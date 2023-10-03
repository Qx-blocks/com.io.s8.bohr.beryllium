package com.s8.io.bohr.beryllium.fields.primitives;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;

import com.s8.api.bohr.BOHR_Types;
import com.s8.api.bytes.ByteInflow;
import com.s8.api.bytes.ByteOutflow;
import com.s8.api.bytes.MemoryFootprint;
import com.s8.api.exceptions.S8BuildException;
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
public class BooleanBeField extends PrimitiveBeField {

	public final static PrimitiveBeField.Prototype PROTOTYPE = new Prototype(boolean.class){

		@Override
		public PrimitiveBeField.Builder createFieldBuilder(BeFieldProperties properties, Field handler) {
			return new BooleanBeField.Builder(properties, handler);
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
			return new BooleanBeField(ordinal, properties, field);
		}
	}



	/**
	 * 
	 * @param outboundTypeName
	 * @param handler
	 * @throws S8BuildException 
	 */
	public BooleanBeField(int ordinal, BeFieldProperties properties, Field handler) throws BeBuildException {
		super(ordinal, properties, handler);
	}

	@Override
	public Prototype getPrototype() {
		return PROTOTYPE;
	}


	@Override
	public void computeFootprint(TableS8Object object, MemoryFootprint weight) {
		weight.reportBytes(1);
	}

	@Override
	public void deepClone(TableS8Object origin, TableS8Object clone) throws IllegalArgumentException, IllegalAccessException {
		boolean value = field.getBoolean(origin);
		field.setBoolean(clone, value);
	}

	@Override
	public void DEBUG_print(String indent) {
		System.out.println(indent + name + ": (boolean)");
	}


	@Override
	public boolean hasDiff(TableS8Object base, TableS8Object update) throws IllegalArgumentException, IllegalAccessException {
		boolean baseValue = field.getBoolean(base);
		boolean updateValue = field.getBoolean(update);
		return baseValue != updateValue;
	}


	@Override
	public BeFieldDelta produceDiff(TableS8Object object) throws IllegalArgumentException, IllegalAccessException {
		return new BooleanBeFieldDelta(this, field.getBoolean(object));
	}


	@Override
	protected void printValue(TableS8Object object, Writer writer) throws BeIOException {
		try {
			writer.write(Boolean.toString(field.getBoolean(object)));
		} catch (IllegalArgumentException | IllegalAccessException | IOException e) {
			throw new BeIOException(e.getMessage());
		}
	}







	/* <IO-inflow-section> */


	@Override
	public BeFieldParser createParser(ByteInflow inflow) throws IOException {
		int code = inflow.getUInt8();
		switch(code) {

		case BOHR_Types.BOOL8 : return new Bool8_Inflow();

		default : throw new BeIOException("Failed to find field-inflow for code: "+Integer.toHexString(code));
		}
	}



	private class Bool8_Inflow extends BeFieldParser {

		@Override
		public BooleanBeField getField() {
			return BooleanBeField.this;
		}

		@Override
		public void parseValue(TableS8Object object, ByteInflow inflow) 
				throws IOException, IllegalArgumentException, IllegalAccessException {
			field.setBoolean(object, inflow.getBool8());
		}

		@Override
		public BeFieldDelta deserializeDelta(ByteInflow inflow) throws IOException {
			return new BooleanBeFieldDelta(BooleanBeField.this, inflow.getBool8());
		}
	}

	/* </IO-inflow-section> */


	/* <IO-outflow-section> */



	@Override
	public BeFieldComposer createComposer(int code) throws BeIOException {
		switch(flow) {
		case DEFAULT_FLOW_TAG: case "bool8" : return new Bool8Composer(code);
		default : throw new BeIOException("Failed to find field-outflow for encoding: "+flow);
		}	
	}


	private class Bool8Composer extends BeFieldComposer {

		public Bool8Composer(int code) { super(code); }

		@Override
		public BooleanBeField getField() {
			return BooleanBeField.this;
		}

		@Override
		public void publishFlowEncoding(ByteOutflow outflow) throws IOException {
			outflow.putUInt8(BOHR_Types.BOOL8);
		}

		@Override
		public void composeValue(TableS8Object object, ByteOutflow outflow) 
				throws IOException, IllegalArgumentException, IllegalAccessException {
			outflow.putBool8(field.getBoolean(object));
		}

		@Override
		public void publishValue(BeFieldDelta delta, ByteOutflow outflow) throws IOException {
			outflow.putBool8(((BooleanBeFieldDelta) delta).value);
		}
	}

	/* <IO-outflow-section> */	


}
