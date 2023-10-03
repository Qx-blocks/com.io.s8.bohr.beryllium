package com.s8.io.bohr.beryllium.types;


import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.s8.api.bytes.MemoryFootprint;
import com.s8.api.objects.table.TableS8Object;
import com.s8.io.bohr.atom.S8ShellStructureException;
import com.s8.io.bohr.beryllium.exception.BeIOException;
import com.s8.io.bohr.beryllium.fields.BeField;
import com.s8.io.bohr.beryllium.fields.BeFieldDelta;


/**
 * 
 *
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
public class BeType {


	/**
	 * 
	 */
	protected final Class<?> baseType;



	/**
	 * 
	 */
	Constructor<?> constructor;





	/**
	 * <p><b>/!\ WARNING </b></p>
	 * code for serialization / deserialization
	 */
	String name;




	/**
	 * fields
	 */
	public Map<String, BeField> fieldsByName;


	/**
	 * fields by ordinal number
	 */
	public BeField[] fields;




	private final DebugModule debugModule;



	/**
	 * The number of references to be stored in vertex
	 */
	int nVertexReferences;


	public BeType(Class<?> baseType) {
		super();
		this.baseType = baseType;

		nVertexReferences = 0;

		// modules
		debugModule = new DebugModule(this);
	}




	public int getNumberOfFields() {
		return fields.length;
	}




	public Class<?> getBaseType(){
		return baseType;
	}

	public String getSerialName() {
		return name;
	}

	public String getRuntimeName() {
		return baseType.getName();
	}



	/**
	 * 
	 * @param object
	 * @param footprint
	 */
	public void computeFootprint(TableS8Object object, MemoryFootprint footprint) {
		footprint.reportInstance();
		fieldsByName.forEach((name, handler) -> {  
			try {
				footprint.reportEntry();

				handler.computeFootprint(object, footprint);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			} 
		});
	}



	/**
	 * 
	 * @return
	 * @throws LthSerialException
	 */
	public TableS8Object createNewInstance(String id) throws BeIOException {
		try {
			return (TableS8Object) constructor.newInstance(new Object[]{id});
		}
		catch (InstantiationException 
				| IllegalAccessException 
				| IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
			throw new BeIOException("instance creation failed due to constructor call error", baseType, e);
		}
	}

	public final static byte[] CODEBASE_ENTRY = "<e/>".getBytes(StandardCharsets.US_ASCII);






	/**
	 * Field accessor
	 * 
	 * @param name
	 * @return
	 */
	public BeField getField(String name) {
		return fieldsByName.get(name);
	}



	/**
	 * 
	 * @param origin
	 * @param context
	 * @return
	 * @throws LthSerialException
	 */
	public TableS8Object deepClone(TableS8Object origin) throws BeIOException {
		try {
			TableS8Object clone = createNewInstance(origin.S8_key);
			for(BeField field : fields) {

				field.deepClone(origin, clone);

			}
			return clone;
		} 
		catch (IllegalArgumentException | IllegalAccessException e) {
			throw new BeIOException(e.getMessage());
		}
	}






	/**
	 * 
	 * @param indent
	 */
	public void DEBUG_print(String indent) {
		System.out.println(indent+"S8Object Type, name = "+getSerialName());
		System.out.println(indent+" fields:{");
		fieldsByName.forEach((name, field) -> { field.DEBUG_print(indent+"   "); });
		System.out.println(indent+" }\n");
	}

	/**
	 * 
	 * @param object
	 * @param inflow
	 * @param context
	 * @throws IOException
	 * @throws S8ShellStructureException 
	 */
	public void print(TableS8Object object, Writer writer) throws BeIOException {
		try {
			debugModule.print(object, writer);
		} 
		catch (IllegalArgumentException | IllegalAccessException | IOException | S8ShellStructureException e) {
			e.printStackTrace();
			throw new BeIOException(e.getMessage());
		}
	}


	/**
	 * 
	 * @param value
	 * @param inflow
	 * @param context
	 * @throws IOException
	 * @throws S8ShellStructureException 
	 */
	public void deepCompare(TableS8Object left, TableS8Object right, Writer writer) throws BeIOException {
		try {
			debugModule.deepCompare(left, right, writer);
		} 
		catch (IllegalArgumentException | IllegalAccessException | IOException | S8ShellStructureException e) {
			e.printStackTrace();
			throw new BeIOException(e.getMessage());
		}
	}


	public boolean equals(BeType right) {
		return getSerialName().equals(right.getSerialName());
	}

	public int getNumberOfVertexReferences() {
		return nVertexReferences;
	}



	/**
	 * 
	 * @param object
	 * @param inflow
	 * @param scope
	 * @throws IOException
	 */
	public void consumeDiff(TableS8Object object, List<BeFieldDelta> deltas) throws BeIOException {
		for(BeFieldDelta delta : deltas) {
			try {
				delta.consume(object);
			} 
			catch (IllegalArgumentException | IllegalAccessException e) {
				throw new BeIOException(e.getMessage());
			}
		}
	}






	/**
	 * 
	 * @param index
	 * @param object
	 * @param scope
	 * @return
	 * @throws IOException
	 */
	public List<BeFieldDelta> produceCreateDeltas(TableS8Object object) throws IOException {
		int n = fields.length;
		List<BeFieldDelta> deltas = new ArrayList<BeFieldDelta>(n);
		for(int i=0; i<n; i++) {
			try {
				deltas.add(fields[i].produceDiff(object));
			} 
			catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
				throw new BeIOException(e.getMessage());
			}
		}
		return deltas;
	}
	
	
}
