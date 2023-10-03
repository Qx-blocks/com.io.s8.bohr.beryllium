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
import com.s8.io.bohr.beryllium.object.BeRef;


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

					
					BeFieldProperties properties = new BeFieldProperties(this, null, BeFieldProperties.FIELD);
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
	public void computeFootprint(TableS8Object object, MemoryFootprint weight) 
			throws IllegalArgumentException, IllegalAccessException {
		BeRef value = (BeRef) field.get(object);
		weight.reportBytes(1 + value.repositoryAddress.length() + 8);
	}


	@Override
	public void deepClone(TableS8Object origin, TableS8Object clone) throws IllegalArgumentException, IllegalAccessException {
		field.set(clone, (BeRef) field.get(origin));
	}


	@Override
	public boolean hasDiff(TableS8Object base, TableS8Object update) throws IllegalArgumentException, IllegalAccessException {
		BeRef baseValue = (BeRef) field.get(base);
		BeRef updateValue = (BeRef) field.get(update);
		return !BeRef.areEqual(baseValue, updateValue);
	}


	@Override
	public BeFieldDelta produceDiff(TableS8Object object) throws IllegalArgumentException, IllegalAccessException {
		return new S8RefBeFieldDelta(this, (BeRef) field.get(object));
	}



	@Override
	public void DEBUG_print(String indent) {
		System.out.println(indent+name+": (S8Ref<?>)");
	}




	@Override
	protected void printValue(TableS8Object object, Writer writer) 
			throws IOException, IllegalArgumentException, IllegalAccessException {
		BeRef value = (BeRef) field.get(object);
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
		case BOHR_Types.S8REF : return new DefaultParser();
		default: throw new BeIOException("Unsupported code: "+Integer.toHexString(code));
		}
	}


	private class DefaultParser extends BeFieldParser {

		@Override
		public void parseValue(TableS8Object object, ByteInflow inflow) 
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


		private BeRef deserialize(ByteInflow inflow) throws IOException {
			return BeRef.read(inflow);
		}
	}

	/* </IO-inflow-section> */



	/* <IO-outflow-section> */

	@Override
	public BeFieldComposer createComposer(int code) throws BeIOException {
		switch(flow) {

		case "obj[]" : default: return new DefaultComposer(code);

		}
	}


	private class DefaultComposer extends BeFieldComposer {

		public DefaultComposer(int code) {
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
		public void composeValue(TableS8Object object, ByteOutflow outflow) 
				throws IOException, IllegalArgumentException, IllegalAccessException {
			BeRef value = (BeRef) field.get(object);
			BeRef.write(value, outflow);
		}

		@Override
		public void publishValue(BeFieldDelta delta, ByteOutflow outflow) throws IOException {
			BeRef value = ((S8RefBeFieldDelta) delta).ref;
			BeRef.write(value, outflow);
		}
	}
	/* </IO-outflow-section> */




	@Override
	public boolean isValueResolved(TableS8Object object) throws BeIOException {
		// TODO Auto-generated method stub
		return false;
	}


}


