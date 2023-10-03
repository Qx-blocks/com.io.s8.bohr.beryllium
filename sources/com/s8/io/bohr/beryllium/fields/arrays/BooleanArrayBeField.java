package com.s8.io.bohr.beryllium.fields.arrays;

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
import com.s8.io.bohr.beryllium.fields.primitives.PrimitiveBeField;


/**
 * later aggregate
 * 
 * 
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 *
 */
public class BooleanArrayBeField extends PrimitiveArrayBeField {

	/**
	 * 
	 */
	public final static Prototype PROTOTYPE = new Prototype(boolean[].class){
		@Override
		public Builder createFieldBuilder(BeFieldProperties properties, Field handler) {
			return new Builder(properties, handler);
		}
	};



	/**
	 * 
	 * @author pierreconvert
	 *
	 */
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
			return new BooleanArrayBeField(ordinal, properties, field);
		}		
	}

	@Override
	public Prototype getPrototype() {
		return PROTOTYPE;
	}


	public BooleanArrayBeField(int ordinal, BeFieldProperties properties, Field handler) throws BeBuildException {
		super(ordinal, properties, handler);
	}


	@Override
	public void computeFootprint(TableS8Object object, MemoryFootprint weight) throws IllegalArgumentException, IllegalAccessException {
		boolean[] array = (boolean[]) field.get(object);
		if(array!=null) {
			weight.reportInstance(); // the array object itself	
			weight.reportBytes(array.length*4);
		}
	}


	@Override
	public void deepClone(TableS8Object origin, TableS8Object clone) 
			throws IllegalArgumentException, IllegalAccessException {
		boolean[] array = (boolean[]) field.get(origin);
		field.set(clone, clone(array));
	}

	@Override
	public boolean hasDiff(TableS8Object base, TableS8Object update) 
			throws IllegalArgumentException, IllegalAccessException {
		boolean[] baseValue = (boolean[]) field.get(base);
		boolean[] updateValue = (boolean[]) field.get(update);
		return !areEqual(baseValue, updateValue);
	}

	@Override
	public BeFieldDelta produceDiff(TableS8Object object) 
			throws IllegalArgumentException, IllegalAccessException {
		return new BooleanArrayBeFieldDelta(this, (boolean[]) field.get(object));
	}


	@Override
	public void DEBUG_print(String indent) {
		System.out.println(indent+name+": (boolean[])");
	}


	/**
	 * 
	 * @param base
	 * @return
	 */
	private boolean[] clone(boolean[] base) {
		if(base!=null) {
			int n = base.length;
			boolean[] copy = new boolean[n];
			for(int i=0; i<n; i++) {
				copy[i] = base[i];
			}
			return copy;
		}
		else {
			return null;
		}
	}



	private boolean areEqual(boolean[] array0, boolean[] array1) {

		// check nulls
		if(array0 == null) { return array1==null; }
		if(array1 == null) { return array0==null; }

		// check lengths
		int n0 = array0.length;
		int n1 = array1.length;
		if(n0!=n1) { return false; }

		// check values
		for(int i=0; i<n0; i++) {
			if(array0[i]!=array1[i]) { return false; }
		}
		return true;
	}





	@Override
	protected void printValue(TableS8Object object, Writer writer) 
			throws IOException, IllegalArgumentException, IllegalAccessException {
		boolean[] array = (boolean[]) field.get(object);
		if(array!=null) {
			boolean isInitialized = false;
			writer.write('[');
			int n = array.length;
			for(int i=0; i<n; i++) {
				if(isInitialized) {
					writer.write(" ,");	
				}
				else {
					isInitialized = true;
				}
				writer.write(Boolean.toString(array[i]));
			}
			writer.write(']');
		}
		else {
			writer.write("null");
		}
	}


	
	/* <IO-inflow-section> */
	@Override
	public BeFieldParser createParser(ByteInflow inflow) throws IOException {


		int code = inflow.getUInt8();
		if(code != BOHR_Types.ARRAY) {
			throw new IOException("Only array accepted");
		}

		switch(code = inflow.getUInt8()) {

		case BOHR_Types.BOOL8 : return new BOOL8_Inflow();

		default : throw new BeIOException("Failed to find field-inflow for code: "+Integer.toHexString(code));
		}
	}



	private class BOOL8_Inflow extends BeFieldParser {

		@Override
		public BooleanArrayBeField getField() {
			return BooleanArrayBeField.this;
		}

		@Override
		public void parseValue(TableS8Object object, ByteInflow inflow) 
				throws IOException, IllegalArgumentException, IllegalAccessException {
			field.set(object, deserialize(inflow));
		}

		@Override
		public BeFieldDelta deserializeDelta(ByteInflow inflow) throws IOException {
			return new BooleanArrayBeFieldDelta(BooleanArrayBeField.this, deserialize(inflow));
		}

		public boolean[] deserialize(ByteInflow inflow) throws IOException {
			int length = (int) inflow.getUInt7x();
			if(length >= 0) {
				boolean[] values = new boolean[length];
				for(int i = 0; i<length; i++) { values[i] = inflow.getBool8(); }
				return values;
			}
			else { return null; }
		}
	}



	/* </IO-inflow-section> */


	/* <IO-outflow-section> */

	@Override
	public BeFieldComposer createComposer(int code) throws BeIOException {
		switch(flow) {

		case DEFAULT_FLOW_TAG: case "bool8[]" : return new BOOL8Composer(code);

		default : throw new BeIOException("Failed to find field-outflow for encoding: "+flow);
		}
	}


	private class BOOL8Composer extends BeFieldComposer {

		public BOOL8Composer(int code) { super(code); }

		@Override
		public void publishFlowEncoding(ByteOutflow outflow) throws IOException {
			outflow.putUInt8(BOHR_Types.ARRAY);
			outflow.putUInt8(BOHR_Types.BOOL8);
		}

		@Override
		public BooleanArrayBeField getField() {
			return BooleanArrayBeField.this;
		}


		@Override
		public void composeValue(TableS8Object object, ByteOutflow outflow) throws IOException, IllegalArgumentException, IllegalAccessException {
			serialize(outflow, (boolean[]) field.get(object));
		}
		
		@Override
		public void publishValue(BeFieldDelta delta, ByteOutflow outflow) throws IOException {
			serialize(outflow, ((BooleanArrayBeFieldDelta) delta).value);
		}

		public void serialize(ByteOutflow outflow, boolean[] value) throws IOException {
			if(value != null) {
				int length = value.length;
				outflow.putUInt7x(length);
				for(int i = 0; i<length; i++) { outflow.putBool8(value[i]); }
			}
			else { outflow.putUInt7x(-1); }
		}

	}

	/* <IO-outflow-section> */
}
