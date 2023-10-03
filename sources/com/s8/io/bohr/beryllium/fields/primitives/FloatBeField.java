package com.s8.io.bohr.beryllium.fields.primitives;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;

import com.s8.api.bohr.BOHR_Types;
import com.s8.api.bytes.ByteInflow;
import com.s8.api.bytes.ByteOutflow;
import com.s8.api.bytes.MemoryFootprint;
import com.s8.api.objects.table.TableS8Object;
import com.s8.io.bohr.beryllium.exception.BeBuildException;
import com.s8.io.bohr.beryllium.exception.BeIOException;
import com.s8.io.bohr.beryllium.fields.BeField;
import com.s8.io.bohr.beryllium.fields.BeFieldComposer;
import com.s8.io.bohr.beryllium.fields.BeFieldDelta;
import com.s8.io.bohr.beryllium.fields.BeFieldParser;
import com.s8.io.bohr.beryllium.fields.BeFieldProperties;
import com.s8.io.bohr.beryllium.fields.BeFieldPrototype;


/**
 * 
 *
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
public class FloatBeField extends PrimitiveBeField {


	public final static PrimitiveBeField.Prototype PROTOTYPE = new Prototype(float.class){

		@Override
		public PrimitiveBeField.Builder createFieldBuilder(BeFieldProperties properties, Field field) {
			return new FloatBeField.Builder(properties, field);
		}
	};


	private static class Builder extends PrimitiveBeField.Builder {

		public Builder(BeFieldProperties properties, Field handler) {
			super(properties, handler);
		}

		@Override
		public BeFieldPrototype getPrototype() {
			return PROTOTYPE;
		}

		@Override
		public BeField build(int ordinal) throws BeBuildException {
			return new FloatBeField(ordinal, properties, field);
		}		
	}

	/**
	 * 
	 * @param outboundTypeName
	 * @param handler
	 * @throws BeBuildException 
	 */
	public FloatBeField(int ordinal, BeFieldProperties properties, Field handler) throws BeBuildException{
		super(ordinal, properties, handler);
	}

	@Override
	public Prototype getPrototype() {
		return PROTOTYPE;
	}


	@Override
	public void computeFootprint(TableS8Object object, MemoryFootprint weight) {
		weight.reportBytes(4);
	}


	@Override
	public void deepClone(TableS8Object origin, TableS8Object clone) throws IllegalArgumentException, IllegalAccessException {
		float value = field.getFloat(origin);
		field.setFloat(clone, value);
	}


	@Override
	public boolean hasDiff(TableS8Object base, TableS8Object update) throws IllegalArgumentException, IllegalAccessException {
		float baseValue = field.getFloat(base);
		float updateValue = field.getFloat(update);
		return baseValue != updateValue;
	}

	@Override
	public BeFieldDelta produceDiff(TableS8Object object) throws IllegalArgumentException, IllegalAccessException {
		return new FloatBeFieldDelta(this, field.getFloat(object));
	}


	@Override
	public void DEBUG_print(String indent) {
		System.out.println(indent+name+": (float)");
	}


	@Override
	protected void printValue(TableS8Object object, Writer writer) 
			throws IllegalArgumentException, IllegalAccessException, IOException {
		writer.write(Float.toString(field.getFloat(object)));
	}



	/* <IO-inflow-section> */


	@Override
	public BeFieldParser createParser(ByteInflow inflow) throws IOException {
		int code = inflow.getUInt8();
		switch(code) {

		case BOHR_Types.FLOAT32 : return new Float32Parser();
		case BOHR_Types.FLOAT64 : return new Float64Parser();

		default : throw new BeIOException("Failed to find field-inflow for code: "+Integer.toHexString(code));
		}
	}



	private abstract class BaseParser extends BeFieldParser {

		@Override
		public FloatBeField getField() {
			return FloatBeField.this;
		}

		@Override
		public void parseValue(TableS8Object object, ByteInflow inflow) 
				throws IllegalArgumentException, IllegalAccessException, IOException {
			field.setFloat(object, deserialize(inflow));
		}

		@Override
		public BeFieldDelta deserializeDelta(ByteInflow inflow) throws IOException {
			return new FloatBeFieldDelta(FloatBeField.this, deserialize(inflow));
		}

		public abstract float deserialize(ByteInflow inflow) throws IOException;

	}

	private class Float32Parser extends BaseParser {
		public @Override float deserialize(ByteInflow inflow) throws IOException {
			return inflow.getFloat32();
		}
	}

	private class Float64Parser extends BaseParser {
		public @Override float deserialize(ByteInflow inflow) throws IOException {
			return (float) inflow.getFloat64();
		}
	}
	/* </IO-inflow-section> */


	/* <IO-outflow-section> */
	@Override
	public BeFieldComposer createComposer(int code) throws BeIOException {
		switch(flow) {
		case DEFAULT_FLOW_TAG: case "float32" : return new Float32Composer(code);
		case "float64" : return new Float64Composer(code);
		default : throw new BeIOException("Failed to find field-outflow for encoding: "+flow);
		}
	}


	private abstract class BaseComposer extends BeFieldComposer {

		public BaseComposer(int code) { super(code); }

		@Override
		public FloatBeField getField() {
			return FloatBeField.this;
		}


		@Override
		public void composeValue(TableS8Object object, ByteOutflow outflow)
				throws IllegalArgumentException, IllegalAccessException, IOException {
			serialize(outflow, field.getFloat(object));
		}

		@Override
		public void publishValue(BeFieldDelta delta, ByteOutflow outflow) throws IOException {
			serialize(outflow, ((FloatBeFieldDelta) delta).value);
		}

		public abstract void serialize(ByteOutflow outflow, float value) throws IOException;
	}


	private class Float32Composer extends BaseComposer {
		public Float32Composer(int code) { super(code); }
		public @Override void publishFlowEncoding(ByteOutflow outflow) throws IOException {
			outflow.putUInt8(BOHR_Types.FLOAT32);
		}
		public @Override void serialize(ByteOutflow outflow, float value) throws IOException {
			outflow.putFloat32(value);
		}
	}

	private class Float64Composer extends BaseComposer {
		public Float64Composer(int code) { super(code); }
		public @Override void publishFlowEncoding(ByteOutflow outflow) throws IOException {
			outflow.putUInt8(BOHR_Types.FLOAT64);
		}
		public @Override void serialize(ByteOutflow outflow, float value) throws IOException {
			outflow.putFloat64((double) value);
		}
	}

	/* <IO-outflow-section> */

}
