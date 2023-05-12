package com.s8.io.bohr.beryllium.fields.objects;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.s8.io.bohr.atom.BOHR_Types;
import com.s8.io.bohr.atom.annotations.S8Field;
import com.s8.io.bohr.beryllium.exception.BeBuildException;
import com.s8.io.bohr.beryllium.exception.BeIOException;
import com.s8.io.bohr.beryllium.fields.BeField;
import com.s8.io.bohr.beryllium.fields.BeFieldBuilder;
import com.s8.io.bohr.beryllium.fields.BeFieldComposer;
import com.s8.io.bohr.beryllium.fields.BeFieldDelta;
import com.s8.io.bohr.beryllium.fields.BeFieldParser;
import com.s8.io.bohr.beryllium.fields.BeFieldProperties;
import com.s8.io.bohr.beryllium.fields.BeFieldPrototype;
import com.s8.io.bohr.beryllium.object.BeObject;
import com.s8.io.bohr.beryllium.object.BeRef;
import com.s8.io.bytes.alpha.ByteInflow;
import com.s8.io.bytes.alpha.ByteOutflow;
import com.s8.io.bytes.alpha.MemoryFootprint;


/**
 * 
 *
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
public class S8RefBeField extends BeField {


	public final static BeFieldPrototype PROTOTYPE = new BeFieldPrototype() {


		@Override
		public BeFieldProperties captureField(Field field) throws BeBuildException {
			Class<?> fieldType = field.getType();
			if(BeRef.class.equals(fieldType)) {
				S8Field annotation = field.getAnnotation(S8Field.class);
				if(annotation != null) {

					Type parameterType = field.getGenericType();
					ParameterizedType parameterizedType = (ParameterizedType) parameterType; 
					Class<?> typeArgument = (Class<?>) parameterizedType.getActualTypeArguments()[0];

					BeFieldProperties properties = new BeFieldProperties(this, typeArgument, BeFieldProperties.FIELD);
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
			return new S8RefBeField(ordinal, properties, field);
		}
	}




	/**
	 * 
	 * @param properties
	 * @param handler
	 * @throws NdBuildException 
	 */
	public S8RefBeField(int ordinal, BeFieldProperties properties, Field handler) throws BeBuildException {
		super(ordinal, properties, handler);
	}



	@Override
	public void computeFootprint(BeObject object, MemoryFootprint weight) 
			throws IllegalArgumentException, IllegalAccessException {
		BeRef<?> value = (BeRef<?>) field.get(object);
		weight.reportBytes(1 + value.address.length() + 8);
	}


	@Override
	public void deepClone(BeObject origin, BeObject clone) throws IllegalArgumentException, IllegalAccessException {
		field.set(clone, (BeRef<?>) field.get(origin));
	}


	@Override
	public boolean hasDiff(BeObject base, BeObject update) throws IllegalArgumentException, IllegalAccessException {
		BeRef<?> baseValue = (BeRef<?>) field.get(base);
		BeRef<?> updateValue = (BeRef<?>) field.get(update);
		return !BeRef.areEqual(baseValue, updateValue);
	}


	@Override
	public BeFieldDelta produceDiff(BeObject object) throws IllegalArgumentException, IllegalAccessException {
		return new S8RefBeFieldDelta(this, (BeRef<?>) field.get(object));
	}



	@Override
	public void DEBUG_print(String indent) {
		System.out.println(indent+name+": (S8Ref<?>)");
	}




	@Override
	protected void printValue(BeObject object, Writer writer) 
			throws IOException, IllegalArgumentException, IllegalAccessException {
		BeRef<?> value = (BeRef<?>) field.get(object);
		if(value!=null) {
			writer.write(value.toString());
		}
		else {
			writer.write("null");
		}
	}

	@Override
	public String printType() {
		return "S8Ref<?>";
	}



	




	/* <IO-inflow-section> */

	@Override
	public BeFieldParser createParser(ByteInflow inflow) throws IOException {
		int code;
		switch((code = inflow.getUInt8())){
		case BOHR_Types.S8REF : return new Inflow();
		default: throw new BeIOException("Unsupported code: "+Integer.toHexString(code));
		}
	}


	private class Inflow extends BeFieldParser {

		@Override
		public void parseValue(BeObject object, ByteInflow inflow) 
				throws IOException, IllegalArgumentException, IllegalAccessException {
			field.set(object, deserialize(inflow));
		}


		@Override
		public S8RefBeField getField() {
			return S8RefBeField.this;
		}

		@Override
		public BeFieldDelta deserializeDelta(ByteInflow inflow) throws IOException {
			return new S8RefBeFieldDelta(S8RefBeField.this, deserialize(inflow));
		}


		private BeRef<?> deserialize(ByteInflow inflow) throws IOException {
			int length = (int) inflow.getUInt7x();
			if(length > 0) {
				//byte[] bytes = inflow.getByteArray(length);
				return null;
			}
			else {
				return null;
			}
		}
	}

	/* </IO-inflow-section> */



	/* <IO-outflow-section> */

	@Override
	public BeFieldComposer createComposer(int code) throws BeIOException {
		switch(flow) {

		case "obj[]" : return new Outflow(code);

		default : throw new BeIOException("Impossible to match IO type for flow: "+flow);
		}
	}


	private class Outflow extends BeFieldComposer {

		public Outflow(int code) {
			super(code);
		}

		@Override
		public BeField getField() {
			return S8RefBeField.this;
		}

		@Override
		public void publishFlowEncoding(ByteOutflow outflow) throws IOException {
			outflow.putUInt8(BOHR_Types.S8REF);
		}

		@Override
		public void composeValue(BeObject object, ByteOutflow outflow) 
				throws IOException, IllegalArgumentException, IllegalAccessException {
			BeRef<?> value = (BeRef<?>) field.get(object);
			BeRef.write(value, outflow);
		}

		@Override
		public void publishValue(BeFieldDelta delta, ByteOutflow outflow) throws IOException {
			BeRef<?> value = ((S8RefBeFieldDelta) delta).ref;
			BeRef.write(value, outflow);
		}
	}
	/* </IO-outflow-section> */




	@Override
	public boolean isValueResolved(BeObject object) throws BeIOException {
		// TODO Auto-generated method stub
		return false;
	}


}


