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
public class StringBeField extends PrimitiveBeField {


	public final static StringBeField.Prototype PROTOTYPE = new Prototype(String.class){

		@Override
		public PrimitiveBeField.Builder createFieldBuilder(BeFieldProperties properties, Field handler) {
			return new StringBeField.Builder(properties, handler);
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
			return new StringBeField(ordinal, properties, field);
		}		
	}


	/**
	 * 
	 * @param outboundTypeName
	 * @param handler
	 * @throws BeBuildException 
	 */
	public StringBeField(int ordinal, BeFieldProperties properties, Field handler) throws BeBuildException{
		super(ordinal, properties, handler);
	}
	

	@Override
	public Prototype getPrototype() {
		return PROTOTYPE;
	}

	
	
	@Override
	public StringBeFieldDelta produceDiff(TableS8Object object) throws IllegalArgumentException, IllegalAccessException {
		return new StringBeFieldDelta(this, (String) field.get(object));
	}


	@Override
	public void computeFootprint(TableS8Object object, MemoryFootprint weight) 
			throws IllegalArgumentException, IllegalAccessException{
		String value = (String) field.get(object);
		if(value!=null) {
			weight.reportInstance();
			weight.reportBytes(value.length());
		}
	}

	@Override
	public void deepClone(TableS8Object origin, TableS8Object clone) throws IllegalArgumentException, IllegalAccessException {
		String value = (String) field.get(origin);
		field.set(clone, value);
	}


	@Override
	public boolean hasDiff(TableS8Object base, TableS8Object update) throws IllegalArgumentException, IllegalAccessException {
		String baseValue = (String) field.get(base);
		String updateValue = (String) field.get(update);
		if(baseValue==null && updateValue==null) {
			return false;
		}
		else if((baseValue!=null && updateValue==null) || (baseValue==null && updateValue!=null)) {
			return true;
		}
		else {
			return !baseValue.equals(updateValue);	
		}
	}


	@Override
	public void DEBUG_print(String indent) {
		System.out.println(indent+name+": (String)");
	}



	@Override
	protected void printValue(TableS8Object object, Writer writer) 
			throws IOException, IllegalArgumentException, IllegalAccessException {
		String val = (String) field.get(object);
		writer.write(val!=null ? val : "<null>");
	}


	


	/* </delta> */




	/* <IO-inflow-section> */


	@Override
	public BeFieldParser createParser(ByteInflow inflow) throws IOException {
		int code = inflow.getUInt8();
		switch(code) {

		case BOHR_Types.STRING_UTF8 : return new UTF8Parser();

		default : throw new BeIOException("Failed to find field-inflow for code: "+Integer.toHexString(code));
		}
	}



	private class UTF8Parser extends BeFieldParser {

		@Override
		public StringBeField getField() {
			return StringBeField.this;
		}

		@Override
		public void parseValue(TableS8Object object, ByteInflow inflow) 
				throws IOException, IllegalArgumentException, IllegalAccessException {
			field.set(object, inflow.getStringUTF8());
		}

		@Override
		public BeFieldDelta deserializeDelta(ByteInflow inflow) throws IOException {
			return new StringBeFieldDelta(StringBeField.this, inflow.getStringUTF8());
		}


	}

	/* </IO-inflow-section> */


	/* <IO-outflow-section> */

	public BeFieldComposer createComposer(int code) throws BeIOException {
		switch(flow) {
		case DEFAULT_FLOW_TAG:
		case "StringUTF8" : return new UTF8Composer(code);
		default : throw new BeIOException("Failed to find field-outflow for encoding: "+flow);
		}
	}


	private class UTF8Composer extends BeFieldComposer {

		public UTF8Composer(int code) {
			super(code);
		}

		@Override
		public StringBeField getField() {
			return StringBeField.this;
		}

		@Override
		public void publishFlowEncoding(ByteOutflow outflow) throws IOException {
			outflow.putUInt8(BOHR_Types.STRING_UTF8);
		}

		@Override
		public void composeValue(TableS8Object object, ByteOutflow outflow) 
				throws IOException, IllegalArgumentException, IllegalAccessException {
			outflow.putStringUTF8((String) field.get(object));
		}

		@Override
		public void publishValue(BeFieldDelta delta, ByteOutflow outflow) throws IOException {
			outflow.putStringUTF8(((StringBeFieldDelta) delta).value);	
		}
	}


	/* <IO-outflow-section> */	


}
