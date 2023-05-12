package com.s8.io.bohr.beryllium.fields.arrays;


import java.lang.reflect.Field;

import com.s8.io.bohr.beryllium.exception.BeIOException;
import com.s8.io.bohr.beryllium.fields.BeFieldProperties;
import com.s8.io.bohr.beryllium.fields.primitives.PrimitiveBeField;


/**
 * 
 *
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
public abstract class PrimitiveArrayBeField extends PrimitiveBeField {

	/**
	 * 
	 * @param name
	 * @param handler
	 */
	public PrimitiveArrayBeField(int ordinal, BeFieldProperties properties, Field handler) {
		super(ordinal, properties, handler);
	}

	
	/**
	 * 
	 * @param object
	 * @param array
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws LthSerialException
	 */
	public void setValue(Object object, Object array) throws BeIOException, IllegalArgumentException, IllegalAccessException {
		field.set(object, array);
	}


}
