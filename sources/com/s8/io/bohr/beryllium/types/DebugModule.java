package com.s8.io.bohr.beryllium.types;

import java.io.IOException;
import java.io.Writer;

import com.s8.api.objects.table.TableS8Object;
import com.s8.io.bohr.atom.S8ShellStructureException;
import com.s8.io.bohr.beryllium.fields.BeField;


/**
 * 
 *
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
public class DebugModule {


	/**
	 * 
	 */
	private final BeType type;


	/**
	 * 
	 * @param type
	 */
	public DebugModule(BeType type) {
		super();
		this.type = type;
	}




	/**
	 * 
	 * @param object
	 * @param writer
	 * @throws IOException
	 * @throws S8ShellStructureException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	public void print(TableS8Object object, Writer writer) 
			throws IOException, S8ShellStructureException, IllegalArgumentException, IllegalAccessException {
		
		// advertise class
		writer.write('(');
		writer.write(object.getClass().getCanonicalName());
		writer.write(')');

		// adevrtise index
		writer.write(" index="+object.S8_key.toString());
		writer.write(" {\n");

		// loop through fields
		int nFields = type.fields.length;
		for(int i=0; i<nFields; i++) {
			writer.append('\t');
			type.fields[i].print(object, writer);
			writer.append('\n');
		}
		writer.write("}\n");
	}



	/**
	 * 
	 * @param value
	 * @param inflow
	 * @param scope
	 * @throws IOException
	 * @throws S8ShellStructureException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	public void deepCompare(TableS8Object left, TableS8Object right, Writer writer) 
			throws IOException, S8ShellStructureException, IllegalArgumentException, IllegalAccessException {

		if(left!=null && right==null) {
			// advertise class
			writer.write('(');
			writer.write(left.getClass().getCanonicalName());
			writer.write(") is now null");
		}
		else if(left==null && right!=null) {
			// advertise class
			writer.write('(');
			writer.write(right.getClass().getCanonicalName());
			writer.write(") is now non-null");
		}
		else {
			boolean hasDiff = !left.getClass().equals(right.getClass());
			int nFields = type.fields.length, i=0;
			while(!hasDiff && i<nFields) {
				hasDiff = type.fields[i].hasDiff(left, right);
				i++;
			}

			if(hasDiff) {
				// advertise class
				writer.write('(');
				writer.write(left.getClass().getCanonicalName());
				writer.write(')');

				// adevrtise index
				writer.write(" index="+(left.S8_key.toString()));
				writer.write(" {");

				// loop through fields
				for(int i2=0; i2<nFields; i2++) {
					BeField field = type.fields[i2];
					if(field.hasDiff(left, right)) {
						writer.append("\n\t");
						field.print(left, writer);
						writer.append("\n\t");
						field.print(right, writer);
						writer.append('\n');	
					}
				}
				writer.write("}\n");		
			}
		}
	}
}
