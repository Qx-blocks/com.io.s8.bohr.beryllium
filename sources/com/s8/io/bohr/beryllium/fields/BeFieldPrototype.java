package com.s8.io.bohr.beryllium.fields;

import java.lang.reflect.Field;

import com.s8.io.bohr.beryllium.exception.BeBuildException;


/**
 * 
 *
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
public abstract class BeFieldPrototype {

	
	
	
	/**
	 * 
	 * @param field
	 * @return
	 * @throws BeBuildException 
	 */
	public abstract BeFieldProperties captureField(Field field) throws BeBuildException;
	
	
	/**
	 * 
	 * @param properties
	 * @param handler
	 * @return
	 */
	public abstract BeFieldBuilder createFieldBuilder(BeFieldProperties properties, Field field);


}
