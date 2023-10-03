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
public class ShortBeField extends PrimitiveBeField {


	public final static PrimitiveBeField.Prototype PROTOTYPE = new Prototype(short.class){

		@Override
		public PrimitiveBeField.Builder createFieldBuilder(BeFieldProperties properties, Field handler) {
			return new ShortBeField.Builder(properties, handler);
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
			return new ShortBeField(ordinal, properties, field);
		}		
	}

	/**
	 * 
	 * @param outboundTypeName
	 * @param handler
	 * @throws BeBuildException 
	 */
	public ShortBeField(int ordinal, BeFieldProperties properties, Field handler) throws BeBuildException{
		super(ordinal, properties, handler);
	}

	@Override
	public Prototype getPrototype() {
		return PROTOTYPE;
	}

	@Override
	public ShortBeFieldDelta produceDiff(TableS8Object object) throws IllegalArgumentException, IllegalAccessException {
		return new ShortBeFieldDelta(this, field.getShort(object));
	}


	@Override
	public void DEBUG_print(String indent) {
		System.out.println(indent+name+": (short)");
	}



	@Override
	protected void printValue(TableS8Object object, Writer writer) 
			throws IllegalArgumentException, IllegalAccessException, IOException{
		writer.write(Short.toString(field.getShort(object)));
	}


	@Override
	public void computeFootprint(TableS8Object object, MemoryFootprint weight) {
		weight.reportBytes(2);
	}


	@Override
	public void deepClone(TableS8Object origin, TableS8Object clone) throws IllegalArgumentException, IllegalAccessException {
		short value = field.getShort(origin);
		field.setShort(clone, value);
	}


	@Override
	public boolean hasDiff(TableS8Object base, TableS8Object update) throws IllegalArgumentException, IllegalAccessException {
		short baseValue = field.getShort(base);
		short updateValue = field.getShort(update);
		return baseValue != updateValue;
	}




	/* <IO-inflow-section> */


	@Override
	public BeFieldParser createParser(ByteInflow inflow) throws IOException {
		int code = inflow.getUInt8();
		switch(code) {

		case BOHR_Types.UINT8 : return new UInt8Parser();
		case BOHR_Types.UINT16 : return new UInt16Parser();

		case BOHR_Types.INT8 : return new Int8Parser();
		case BOHR_Types.INT16 : return new Int16Parser();

		default : throw new BeIOException("Failed to find field-inflow for code: "+Integer.toHexString(code));
		}
	}



	private abstract class BaseParser extends BeFieldParser {

		@Override
		public ShortBeField getField() {
			return ShortBeField.this;
		}

		@Override
		public void parseValue(TableS8Object object, ByteInflow inflow) 
				throws IOException, IllegalArgumentException, IllegalAccessException {
			field.setShort(object, deserialize(inflow));
		}

		@Override
		public BeFieldDelta deserializeDelta(ByteInflow inflow) throws IOException {
			return new ShortBeFieldDelta(ShortBeField.this, deserialize(inflow));
		}

		public abstract short deserialize(ByteInflow inflow) throws IOException;

	}

	private class UInt8Parser extends BaseParser {
		public @Override short deserialize(ByteInflow inflow) throws IOException {
			return (short) inflow.getUInt8();
		}
	}

	private class UInt16Parser extends BaseParser {
		public @Override short deserialize(ByteInflow inflow) throws IOException {
			return (short) inflow.getUInt16();
		}
	}


	private class Int8Parser extends BaseParser {
		public @Override short deserialize(ByteInflow inflow) throws IOException {
			return (short) inflow.getInt8();
		}
	}

	private class Int16Parser extends BaseParser {
		public @Override short deserialize(ByteInflow inflow) throws IOException {
			return inflow.getInt16();
		}
	}

	/* </IO-inflow-section> */


	/* <IO-outflow-section> */
	@Override
	public BeFieldComposer createComposer(int code) throws BeIOException {
		switch(flow) {

		case "uint8" : return new UInt8Composer(code);
		case "uint16" : return new UInt16Composer(code);

		case "int8" : return new Int8Composer(code);
		
		case DEFAULT_FLOW_TAG: case "int16" : return new Int16Composer(code);

		default : throw new BeIOException("Failed to find field-outflow for encoding: "+flow);
		}
	}


	private abstract class BaseComposer extends BeFieldComposer {

		public BaseComposer(int code) { super(code); }


		@Override
		public ShortBeField getField() {
			return ShortBeField.this;
		}


		@Override
		public void composeValue(TableS8Object object, ByteOutflow outflow) 
				throws IOException, IllegalArgumentException, IllegalAccessException {
			serialize(outflow, field.getShort(object));
		}
		
		@Override
		public void publishValue(BeFieldDelta delta, ByteOutflow outflow) throws IOException {
			serialize(outflow, ((ShortBeFieldDelta) delta).value);
		}

		public abstract void serialize(ByteOutflow outflow, short value) throws IOException;
	}


	private class UInt8Composer extends BaseComposer {
		public UInt8Composer(int code) { super(code); }
		public @Override void publishFlowEncoding(ByteOutflow outflow) throws IOException {
			outflow.putUInt8(BOHR_Types.UINT8);
		}
		public @Override void serialize(ByteOutflow outflow, short value) throws IOException {
			outflow.putUInt8((int) value);
		}
	}

	private class UInt16Composer extends BaseComposer {
		public UInt16Composer(int code) { super(code); }
		public @Override void publishFlowEncoding(ByteOutflow outflow) throws IOException {
			outflow.putUInt8(BOHR_Types.UINT16);
		}
		public @Override void serialize(ByteOutflow outflow, short value) throws IOException {
			outflow.putUInt16((int) value);
		}
	}


	private class Int8Composer extends BaseComposer {
		public Int8Composer(int code) { super(code); }
		public @Override void publishFlowEncoding(ByteOutflow outflow) throws IOException {
			outflow.putUInt8(BOHR_Types.INT8);
		}
		public @Override void serialize(ByteOutflow outflow, short value) throws IOException {
			outflow.putInt8((byte) value);
		}
	}

	private class Int16Composer extends BaseComposer {
		public Int16Composer(int code) { super(code); }
		public @Override void publishFlowEncoding(ByteOutflow outflow) throws IOException {
			outflow.putUInt8(BOHR_Types.INT16);
		}
		public @Override void serialize(ByteOutflow outflow, short value) throws IOException {
			outflow.putInt16(value);
		}
	}

	/* <IO-outflow-section> */



}
