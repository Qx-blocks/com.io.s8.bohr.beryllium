package com.s8.io.bohr.beryllium.fields.primitives;

import java.lang.reflect.Field;

import com.s8.api.objects.annotations.S8Field;
import com.s8.api.objects.table.TableS8Object;
import com.s8.io.bohr.beryllium.fields.BeField;
import com.s8.io.bohr.beryllium.fields.BeFieldBuilder;
import com.s8.io.bohr.beryllium.fields.BeFieldProperties;
import com.s8.io.bohr.beryllium.fields.BeFieldPrototype;

/**
 * 
 *
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
public abstract class PrimitiveBeField extends BeField {
	
	

	public static abstract class Prototype extends BeFieldPrototype {

		private Class<?> fieldType;

		public Prototype(Class<?> fieldType) {
			super();
			this.fieldType = fieldType;
		}

		public Class<?> getType(){
			return fieldType;
		}

		public String getKey() {
			return fieldType.getCanonicalName();
		}
		
		
		@Override
		public BeFieldProperties captureField(Field field) {
			if(fieldType.equals(field.getType())) {
				S8Field annotation = field.getAnnotation(S8Field.class);
				if(annotation != null) {
					
					// types
					BeFieldProperties props = new BeFieldProperties(this, fieldType, BeFieldProperties.FIELD);
					props.setFieldAnnotation(annotation);
					return props;
				}
				else {
					return null;
				}
			}
			else {
				return null;  // type is not matching
			}
		}
		
		

		

		@Override
		public abstract PrimitiveBeField.Builder createFieldBuilder(BeFieldProperties props, Field field);
		
	}
	
	
	public static abstract class Builder extends BeFieldBuilder {
		
		public Builder(BeFieldProperties properties, Field field) {
			super(properties, field);
		}
	}
	
	

	/**
	 * 
	 * @param code
	 * @param field
	 * @param value
	 */
	public PrimitiveBeField(int ordinal, BeFieldProperties properties, Field field) {
		super(ordinal, properties, field);
	}

	
	public abstract Prototype getPrototype();
	


	
	@Override
	public String printType() {
		return getPrototype().getKey();
	}
	
	@Override
	public boolean isValueResolved(TableS8Object object) {
		return true; // always resolved
	}
	
}
