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
 * 
 *
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
public class IntegerArrayBeField extends PrimitiveArrayBeField {


	/**
	 * 
	 */
	public final static Prototype PROTOTYPE = new Prototype(int[].class){
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
			return new IntegerArrayBeField(ordinal, properties, field);
		}		
	}

	@Override
	public Prototype getPrototype() {
		return PROTOTYPE;
	}

	
	/**
	 * 
	 * @param properties
	 * @param handler
	 * @throws NdBuildException 
	 */
	public IntegerArrayBeField(int ordinal, BeFieldProperties properties, Field handler) throws BeBuildException {
		super(ordinal, properties, handler);
	}


	@Override
	public void computeFootprint(TableS8Object object, MemoryFootprint weight) throws IllegalArgumentException, IllegalAccessException {
		int[] array = (int[]) field.get(object);
		if(array!=null) {
			weight.reportInstance(); // the array object itself	
			weight.reportBytes(array.length*4);
		}
	}


	@Override
	public void deepClone(TableS8Object origin, TableS8Object clone) throws IllegalArgumentException, IllegalAccessException {
		int[] array = (int[]) field.get(origin);
		field.set(clone, clone(array));
	}

	@Override
	public boolean hasDiff(TableS8Object base, TableS8Object update) throws IllegalArgumentException, IllegalAccessException {
		int[] baseValue = (int[]) field.get(base);
		int[] updateValue = (int[]) field.get(update);
		return !areEqual(baseValue, updateValue);
	}

	@Override
	public IntegerArrayBeFieldDelta produceDiff(TableS8Object object) throws IllegalArgumentException, IllegalAccessException  {
		return new IntegerArrayBeFieldDelta(this, (int[]) field.get(object));
	}


	@Override
	public void DEBUG_print(String indent) {
		System.out.println(indent+name+": (long[])");
	}


	/**
	 * 
	 * @param base
	 * @return
	 */
	private int[] clone(int[] base) {
		if(base!=null) {
			int n = base.length;
			int[] copy = new int[n];
			for(int i=0; i<n; i++) {
				copy[i] = base[i];
			}
			return copy;
		}
		else {
			return null;
		}
	}



	private boolean areEqual(int[] array0, int[] array1) {

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
		int[] array = (int[]) field.get(object);
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
				writer.write(Integer.toString(array[i]));
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

		case BOHR_Types.UINT8 : return new UInt8Parser();
		case BOHR_Types.UINT16 : return new UInt16Parser();
		case BOHR_Types.UINT32 : return new UInt32Parser();

		case BOHR_Types.INT8 : return new Int8Parser();
		case BOHR_Types.INT16 : return new Int16Parser();
		case BOHR_Types.INT32 : return new Int32Parser();

		default : throw new BeIOException("Failed to find field-inflow for code: "+Integer.toHexString(code));
		}
	}



	private abstract class BaseParser extends BeFieldParser {

		@Override
		public IntegerArrayBeField getField() {
			return IntegerArrayBeField.this;
		}

		@Override
		public void parseValue(TableS8Object object, ByteInflow inflow) 
				throws IOException, IllegalArgumentException, IllegalAccessException {
			field.set(object, deserialize(inflow));
		}

		@Override
		public BeFieldDelta deserializeDelta(ByteInflow inflow) throws IOException {
			return new IntegerArrayBeFieldDelta(IntegerArrayBeField.this, deserialize(inflow));
		}

		public abstract int[] deserialize(ByteInflow inflow) throws IOException;

	}

	private class UInt8Parser extends BaseParser {
		public @Override int[] deserialize(ByteInflow inflow) throws IOException {
			int length = (int) inflow.getUInt7x();
			if(length >= 0) {
				int[] values = new int[length];
				for(int i = 0; i<length; i++) { values[i] = inflow.getUInt8(); }
				return values;
			}
			else { return null; }
		}
	}

	private class UInt16Parser extends BaseParser {
		public @Override int[] deserialize(ByteInflow inflow) throws IOException {
			int length = (int) inflow.getUInt7x();
			if(length >= 0) {
				int[] values = new int[length];
				for(int i = 0; i<length; i++) { values[i] = inflow.getUInt16(); }
				return values;
			}
			else { return null; }
		}
	}

	private class UInt32Parser extends BaseParser {
		public @Override int[] deserialize(ByteInflow inflow) throws IOException {
			int length = (int) inflow.getUInt7x();
			if(length >= 0) {
				int[] values = new int[length];
				for(int i = 0; i<length; i++) { values[i] = inflow.getUInt32(); }
				return values;
			}
			else { return null; }
		}
	}



	private class Int8Parser extends BaseParser {
		public @Override int[] deserialize(ByteInflow inflow) throws IOException {
			int length = (int) inflow.getUInt7x();
			if(length >= 0) {
				int[] values = new int[length];
				for(int i = 0; i<length; i++) { values[i] = inflow.getInt8(); }
				return values;
			}
			else { return null; }
		}
	}

	private class Int16Parser extends BaseParser {
		public @Override int[] deserialize(ByteInflow inflow) throws IOException {
			int length = (int) inflow.getUInt7x();
			if(length >= 0) {
				int[] values = new int[length];
				for(int i = 0; i<length; i++) { values[i] = inflow.getInt16(); }
				return values;
			}
			else { return null; }
		}
	}

	private class Int32Parser extends BaseParser {
		public @Override int[] deserialize(ByteInflow inflow) throws IOException {
			int length = (int) inflow.getUInt7x();
			if(length >= 0) {
				int[] values = new int[length];
				for(int i = 0; i<length; i++) { values[i] = inflow.getInt32(); }
				return values;
			}
			else { return null; }
		}
	}


	/* </IO-inflow-section> */


	/* <IO-outflow-section> */


	public BeFieldComposer createComposer(int code) throws BeIOException {
		switch(flow) {

		case "uint8[]" : return new UInt8Composer(code);
		case "uint16[]" : return new UInt16Composer(code);
		case "uint32[]" : return new UInt32Composer(code);

		case "int8[]" : return new Int8Composer(code);
		case "int16[]" : return new Int16Composer(code);
		case DEFAULT_FLOW_TAG: case "int32[]" : return new Int32_Outflow(code);

		default : throw new BeIOException("Failed to find field-outflow for encoding: "+flow);
		}
	}


	private abstract class BaseComposer extends BeFieldComposer {
		public BaseComposer(int code) { super(code); }

		@Override
		public IntegerArrayBeField getField() {
			return IntegerArrayBeField.this;
		}


		@Override
		public void composeValue(TableS8Object object, ByteOutflow outflow) 
				throws IOException, IllegalArgumentException, IllegalAccessException {
			serialize(outflow, (int[]) field.get(object));
		}
		

		@Override
		public void publishValue(BeFieldDelta delta, ByteOutflow outflow) throws IOException {
			serialize(outflow, ((IntegerArrayBeFieldDelta) delta).value);
		}

		public abstract void serialize(ByteOutflow outflow, int[] value) throws IOException;
	}


	private class UInt8Composer extends BaseComposer {
		public UInt8Composer(int code) { super(code); }
		public @Override void publishFlowEncoding(ByteOutflow outflow) throws IOException {
			outflow.putUInt8(BOHR_Types.ARRAY);
			outflow.putUInt8(BOHR_Types.UINT8);
		}
		public @Override void serialize(ByteOutflow outflow, int[] value) throws IOException {
			if(value != null) {
				int length = value.length;
				outflow.putUInt7x(length);
				for(int i = 0; i<length; i++) { outflow.putUInt8(value[i]); }
			}
			else { outflow.putUInt7x(-1); }
		}
	}

	private class UInt16Composer extends BaseComposer {
		public UInt16Composer(int code) { super(code); }
		public @Override void publishFlowEncoding(ByteOutflow outflow) throws IOException {
			outflow.putUInt8(BOHR_Types.ARRAY);
			outflow.putUInt8(BOHR_Types.UINT16);
		}
		public @Override void serialize(ByteOutflow outflow, int[] value) throws IOException {
			if(value != null) {
				int length = value.length;
				outflow.putUInt7x(length);
				for(int i = 0; i<length; i++) { outflow.putUInt16(value[i]); }
			}
			else { outflow.putUInt7x(-1); }
		}
	}

	private class UInt32Composer extends BaseComposer {
		public UInt32Composer(int code) { super(code); }
		public @Override void publishFlowEncoding(ByteOutflow outflow) throws IOException {
			outflow.putUInt8(BOHR_Types.ARRAY);
			outflow.putUInt8(BOHR_Types.UINT32);
		}
		public @Override void serialize(ByteOutflow outflow, int[] value) throws IOException {
			if(value != null) {
				int length = value.length;
				outflow.putUInt7x(length);
				for(int i = 0; i<length; i++) { outflow.putUInt32(value[i]); }
			}
			else { outflow.putUInt7x(-1); }
		}
	}


	private class Int8Composer extends BaseComposer {
		public Int8Composer(int code) { super(code); }
		public @Override void publishFlowEncoding(ByteOutflow outflow) throws IOException {
			outflow.putUInt8(BOHR_Types.ARRAY);
			outflow.putUInt8(BOHR_Types.INT8);
		}
		public @Override void serialize(ByteOutflow outflow, int[] value) throws IOException {
			if(value != null) {
				int length = value.length;
				outflow.putUInt7x(length);
				for(int i = 0; i<length; i++) { outflow.putInt8((byte) value[i]); }
			}
			else { outflow.putUInt7x(-1); }
		}
	}

	private class Int16Composer extends BaseComposer {
		public Int16Composer(int code) { super(code); }
		public @Override void publishFlowEncoding(ByteOutflow outflow) throws IOException {
			outflow.putUInt8(BOHR_Types.ARRAY);
			outflow.putUInt8(BOHR_Types.INT16);
		}
		public @Override void serialize(ByteOutflow outflow, int[] value) throws IOException {
			if(value != null) {
				int length = value.length;
				outflow.putUInt7x(length);
				for(int i = 0; i<length; i++) { outflow.putInt16((short) value[i]); }
			}
			else { outflow.putUInt7x(-1); }
		}
	}

	private class Int32_Outflow extends BaseComposer {
		public Int32_Outflow(int code) { super(code); }
		public @Override void publishFlowEncoding(ByteOutflow outflow) throws IOException {
			outflow.putUInt8(BOHR_Types.ARRAY);
			outflow.putUInt8(BOHR_Types.INT32);
		}
		public @Override void serialize(ByteOutflow outflow, int[] value) throws IOException {
			if(value != null) {
				int length = value.length;
				outflow.putUInt7x(length);
				for(int i = 0; i<length; i++) { outflow.putInt32(value[i]); }
			}
			else { outflow.putUInt7x(-1); }
		}
	}

	/* <IO-outflow-section> */
}
