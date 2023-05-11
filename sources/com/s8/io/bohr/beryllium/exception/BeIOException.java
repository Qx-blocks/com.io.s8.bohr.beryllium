package com.s8.io.bohr.beryllium.exception;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;


/**
 * 
 * 
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
public class BeIOException extends IOException {
	

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7581262624376703609L;

	private Class<?> type;
	
	private Field field;
	
	private Method method;
	
	private Exception cause;
	
	
	public BeIOException(String message) {
		super(message);
	}
	
	
	/**
	 * 
	 * @param message
	 * @param type
	 */
	public BeIOException(String message, Class<?> type) {
		super(message+", for @type: "+type);
		this.type = type;
	}
	
	
	/**
	 * 
	 * @param message
	 * @param type
	 */
	public BeIOException(String message, Class<?> type, Exception cause) {
		super(message+", due to "+cause.getMessage()+", for @type: "+type);
		this.type = type;
		this.cause = cause;
	}
	

	public BeIOException(String message, Field field, Exception cause) {
		super(message+", due to "+cause.getMessage()+", for outflow @field: "+field);
		this.field = field;
		this.type = field.getDeclaringClass();
		this.cause = cause;
	}
	
	
	public BeIOException(String message, Method method, Exception cause) {
		super(message+", due to "+cause.getMessage()+", for outflow @method: "+method);
		this.method = method;
		this.type = method.getDeclaringClass();
		this.cause = cause;
	}
	
	
	
	public Class<?> getInvolvedType(){
		return type;
	}
	
	public Field getInvolvedField(){
		return field;
	}
	
	public Method getInvolvedMethod(){
		return method;
	}
	
	public Exception getCause(){
		return cause;
	}
}
