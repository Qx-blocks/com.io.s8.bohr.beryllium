package com.s8.io.bohr.beryllium.fields.objects;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;

import com.s8.api.bohr.BOHR_Types;
import com.s8.api.bytes.ByteInflow;
import com.s8.api.bytes.ByteOutflow;
import com.s8.api.bytes.MemoryFootprint;
import com.s8.api.objects.annotations.S8Field;
import com.s8.api.objects.table.TableS8Object;
import com.s8.io.bohr.beryllium.exception.BeBuildException;
import com.s8.io.bohr.beryllium.exception.BeIOException;
import com.s8.io.bohr.beryllium.fields.BeField;
import com.s8.io.bohr.beryllium.fields.BeFieldBuilder;
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
public class EnumBeField extends BeField {



	public final static BeFieldPrototype PROTOTYPE = new BeFieldPrototype() {


		@Override
		public BeFieldProperties captureField(Field field) throws BeBuildException {
			Class<?> baseType = field.getType();
			if(baseType.isEnum()) {
				S8Field annotation = field.getAnnotation(S8Field.class);
				if(annotation != null) {
					BeFieldProperties properties = new BeFieldProperties(this, baseType, BeFieldProperties.FIELD);
					properties.setFieldAnnotation(annotation);
					return properties;	
				}
				else { return null; }
			}
			else { return null; }
		}


		

		@Override
		public BeFieldBuilder createFieldBuilder(BeFieldProperties properties, Field handler) {
			return new Builder(properties, handler);
		}
	};



	private static class Builder extends BeFieldBuilder {

		public Builder(BeFieldProperties properties, Field handler) {
			super(properties, handler);
		}

		@Override
		public BeFieldPrototype getPrototype() {
			return PROTOTYPE;
		}

		@Override
		public BeField build(int ordinal) throws BeBuildException {
			return new EnumBeField(ordinal, properties, field);
		}
	}



	private Class<?> enumType;

	private Object[] values;


	/**
	 * 
	 * @param ordinal
	 * @param properties
	 * @param handler
	 * @throws NdIOException
	 */
	public EnumBeField(int ordinal, BeFieldProperties properties, Field handler) {
		super(ordinal, properties, handler);
		this.enumType = properties.baseType;
		this.values = enumType.getEnumConstants();
	}


	@Override
	public void computeFootprint(TableS8Object object, MemoryFootprint weight) {
		weight.reportInstance();
		weight.reportBytes(4); // int ordinal
	}


	@Override
	public void DEBUG_print(String indent) {
		System.out.println(indent+name+": ("+enumType.getName()+")");
	}



	@Override
	public void deepClone(TableS8Object origin, TableS8Object clone) throws IllegalArgumentException, IllegalAccessException {
		Object value = field.get(origin);
		field.set(clone, value);
	}


	@Override
	public boolean hasDiff(TableS8Object base, TableS8Object update) throws IllegalArgumentException, IllegalAccessException  {
		Object baseValue = field.get(base);
		Object updateValue = field.get(update);
		return (baseValue!=null && !baseValue.equals(updateValue)) 
				|| (baseValue==null && updateValue!=null);
	}


	@Override
	public BeFieldDelta produceDiff(TableS8Object object) throws IllegalArgumentException, IllegalAccessException {
		return new EnumBeFieldDelta(this, field.get(object));
	}

	@Override
	protected void printValue(TableS8Object object, Writer writer) 
			throws IOException, IllegalArgumentException, IllegalAccessException {
		Object value = field.get(object);
		if(value!=null) {
			Enum<?> enumValue = (Enum<?>) value;
			writer.write(enumValue.name());	
		}
		else {
			writer.write("null");
		}
	}


	@Override
	public String printType() {
		return enumType.getCanonicalName();
	}


	@Override
	public boolean isValueResolved(TableS8Object object) {
		return true; // always resolved
	}



	/* <IO-inflow-section> */

	@Override
	public BeFieldParser createParser(ByteInflow inflow) throws IOException {
		int code = inflow.getUInt8();
		switch(code) {
		case BOHR_Types.UINT8 : return new UInt8Parser();
		case BOHR_Types.UINT16 : return new UInt16Parser();
		case BOHR_Types.UINT32 : return new UInt32Parser();
		default : throw new BeIOException("Failed to find field-inflow for code: "+Integer.toHexString(code));
		}
	}


	private abstract class BaseParser extends BeFieldParser {

		@Override
		public EnumBeField getField() {
			return EnumBeField.this;
		}

		@Override
		public void parseValue(TableS8Object object, ByteInflow inflow) 
				throws IOException, IllegalArgumentException, IllegalAccessException {
			field.set(object, deserialize(inflow));
		}

		@Override
		public BeFieldDelta deserializeDelta(ByteInflow inflow) throws IOException {
			return new EnumBeFieldDelta(EnumBeField.this, deserialize(inflow));
		}

		public abstract Object deserialize(ByteInflow inflow) throws IOException;

	}

	private class UInt8Parser extends BaseParser {
		public @Override Object deserialize(ByteInflow inflow) throws IOException {
			return values[(int) inflow.getUInt8()];
		}
	}

	private class UInt16Parser extends BaseParser {
		public @Override Object deserialize(ByteInflow inflow) throws IOException {
			return values[inflow.getUInt16()];
		}
	}

	private class UInt32Parser extends BaseParser {
		public @Override Object deserialize(ByteInflow inflow) throws IOException {
			return values[inflow.getUInt32()];
		}
	}

	/* </IO-inflow-section> */


	/* <IO-outflow-section> */
	@Override
	public BeFieldComposer createComposer(int code) throws BeIOException {
		switch(flow) {
		case "uint8" : return new UInt8Composer(code);
		case "uint16" : return new UInt16Composer(code);
		case "uint32" : return new UInt32Composer(code);
		case DEFAULT_FLOW_TAG: 
			if(values.length<=0xff) {
				return new UInt8Composer(code);
			}
			else if(values.length<=0xffff) {
				return new UInt16Composer(code);
			}
			else {
				return new UInt32Composer(code);
			}
		default : throw new BeIOException("Failed to find field-outflow for encoding: "+flow);
		}
	}


	private abstract class BaseComposer extends BeFieldComposer {

		public BaseComposer(int code) {
			super(code);
		}


		@Override
		public EnumBeField getField() {
			return EnumBeField.this;
		}


		@Override
		public void composeValue(TableS8Object object, ByteOutflow outflow) 
				throws IOException, IllegalArgumentException, IllegalAccessException {
			serialize(outflow, field.get(object));
		}
		
		@Override
		public void publishValue(BeFieldDelta delta, ByteOutflow outflow) throws IOException {
			serialize(outflow, ((EnumBeFieldDelta) delta).value);
		}

		public abstract void serialize(ByteOutflow outflow, Object value) throws IOException;
	}


	private class UInt8Composer extends BaseComposer {
		public UInt8Composer(int code) {
			super(code);
		}
		public @Override void publishFlowEncoding(ByteOutflow outflow) throws IOException {
			outflow.putUInt8(BOHR_Types.UINT8);
		}
		public @Override void serialize(ByteOutflow outflow, Object value) throws IOException {
			int code = ((Enum<?>) value).ordinal();
			outflow.putUInt8(code);
		}
	}

	private class UInt16Composer extends BaseComposer {
		public UInt16Composer(int code) {
			super(code);
		}
		public @Override void publishFlowEncoding(ByteOutflow outflow) throws IOException {
			outflow.putUInt8(BOHR_Types.UINT16);
		}
		public @Override void serialize(ByteOutflow outflow, Object value) throws IOException {
			int code = ((Enum<?>) value).ordinal();
			outflow.putUInt16(code);
		}
	}

	private class UInt32Composer extends BaseComposer {
		public UInt32Composer(int code) {
			super(code);
		}
		public @Override void publishFlowEncoding(ByteOutflow outflow) throws IOException {
			outflow.putUInt8(BOHR_Types.UINT32);
		}
		public @Override void serialize(ByteOutflow outflow, Object value) throws IOException {
			int code = ((Enum<?>) value).ordinal();
			outflow.putUInt32(code);
		}
	}

	/* <IO-outflow-section> */
}
