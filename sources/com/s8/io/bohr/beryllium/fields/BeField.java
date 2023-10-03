package com.s8.io.bohr.beryllium.fields;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;

import com.s8.api.bytes.ByteInflow;
import com.s8.api.bytes.MemoryFootprint;
import com.s8.api.objects.table.TableS8Object;
import com.s8.io.bohr.atom.S8ShellStructureException;
import com.s8.io.bohr.beryllium.exception.BeIOException;

/**
 * 
 *
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
public abstract class BeField {

	public final static String DEFAULT_FLOW_TAG = "(default)";

	
	public final int ordinal;
	
	
	
	public final Field field;

	/**
	 * 
	 */
	public final String name;

	/**
	 * 
	 */
	public final String flow;

	/**
	 * 
	 */
	public final long mask;

	/**
	 * 
	 */
	public final long flags;
	
	/**
	 * 
	 * @param properties
	 * @param handler
	 */
	public BeField(int ordinal, BeFieldProperties properties, Field field) {
		super();
		
		this.ordinal = ordinal;
		
		/* <field-properties> */
		this.name = properties.getName();
		this.flow = properties.getExportFormat();
		this.mask = properties.getMask();
		this.flags = properties.getFlags();
		/* </field-properties> */
		
		/* <handler> */
		this.field = field;
		/* </handler> */
	}


	
	
	protected Field getHandler() {
		return field;
	}

	
	
	
	/**
	 * 
	 * @param flow (null = use flow define in field)
	 * @return
	 * @throws  
	 */
	public abstract BeFieldComposer createComposer(int code) throws BeIOException;
	
	
	public abstract BeFieldParser createParser(ByteInflow inflow) throws IOException;
	
	


	public abstract void computeFootprint(TableS8Object object, MemoryFootprint weight) 
			throws IllegalArgumentException, IllegalAccessException;

	
	

	/**
	 * Print field for debugging purposes
	 * 
	 * @param indent
	 */
	public abstract void DEBUG_print(String indent);

	/**
	 * 
	 * @param clone
	 * @param bindings
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws  
	 * @throws LthSerialException
	 */
	public abstract void deepClone(TableS8Object origin, TableS8Object clone) throws IllegalArgumentException, IllegalAccessException;

	/**
	 * 
	 * @param base
	 * @param update
	 * @param outflow
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws LthSerialException
	 * @throws IOException
	 */
	public abstract boolean hasDiff(TableS8Object base, TableS8Object update) throws IllegalArgumentException, IllegalAccessException;


	/**
	 * <p>
	 * <b>IMPORTANT NOTICE<b>: It is often a good idea to track change AND remap at
	 * the same time, that's why we take advantage of the scope
	 * </p>
	 * 
	 * @param object
	 * @param scope
	 * @return
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws IOException
	 */
	public abstract BeFieldDelta produceDiff(TableS8Object object) throws IllegalArgumentException, IllegalAccessException;

	

	public void print(TableS8Object object, Writer writer) 
			throws IOException, S8ShellStructureException, IllegalArgumentException, IllegalAccessException {
		writer.append("(");
		writer.append(printType());
		writer.append(") ");
		writer.append(name);
		writer.append(": ");
		printValue(object, writer);
	}
	

	/**
	 * print standard name of field type
	 */
	public abstract String printType();


	protected abstract void printValue(TableS8Object object, Writer writer) 
			throws BeIOException, IllegalArgumentException, IllegalAccessException, IOException;

	

	/**
	 * 
	 * @param object
	 * @return true is the object has been resolved, false otherwise
	 * @throws LthSerialException 
	 */
	public abstract boolean isValueResolved(TableS8Object object) throws BeIOException;

	
}
