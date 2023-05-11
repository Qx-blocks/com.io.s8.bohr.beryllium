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
public abstract class BeFieldBuilder {
	
	
	
	/**
	 * 
	 * @return
	 */
	public BeFieldProperties properties;
	
	
	public Field field;
	

	/**
	 * 
	 * @param field
	 */
	public BeFieldBuilder(BeFieldProperties properties, Field field) {
		super();
		this.properties = properties;
		this.field = field;
	}

	
	/**
	 * 
	 * @param field
	 * @throws BeBuildException
	 */
	public void attachField(Field field) throws BeBuildException {
		BeFieldProperties props = this.getPrototype().captureField(field);
		if (props != null) {
			this.properties.merge(props);
			this.field = field;
		}
		else {
			throw new BeBuildException("Field type is not matching field builder", field);
		}
	}
	

	
	/**
	 * 
	 * @return
	 */
	public abstract BeFieldPrototype getPrototype();
	
	

	/**
	 * 
	 * @return
	 * @throws BeBuildException 
	 */
	public abstract BeField build(int ordinal) throws BeBuildException;


}
