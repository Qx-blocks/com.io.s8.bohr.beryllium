package com.s8.io.bohr.beryllium.types;

import static com.s8.api.bohr.BOHR_Keywords.CLOSE_NODE;
import static com.s8.api.bohr.BOHR_Keywords.DECLARE_FIELD;
import static com.s8.api.bohr.BOHR_Keywords.SET_VALUE;

import java.io.IOException;

import com.s8.api.bytes.ByteInflow;
import com.s8.io.bohr.beryllium.exception.BeIOException;
import com.s8.io.bohr.beryllium.fields.BeField;
import com.s8.io.bohr.beryllium.fields.BeFieldParser;

/**
 * 
 * This object represents the I/O mapping that allows to read a version of an
 * object.
 * <p>
 * For instance, let's say that MyS8Object in codebase version 26.8 has the
 * following field mapping:
 * </p>
 * <ul>
 * <li>0 -> alpha</li>
 * <li>1 -> beta</li>
 * <li>2 -> delta</li>
 * </ul>
 * <p>
 * And assume that current codebase (let's say 26.11) is now declaring fields:
 * alpha, gamma, delta. Then we can still map the obect build in version 26.8 to
 * the 26.11 codebase using an TypeVersionIO with the following mapping:
 * </p>
 * * <ul>
 * <li>0 -> alpha (still valid for 26.11)</li>
 * <li>1 -> beta (<b>dismissed</b> in 26.11)</li>
 * <li>gamma: was not known in 26.8, so cannot exist on Object from this time. Discarded.</li>
 * <li>2 -> delta (still valid for 26.11)</li>
 * </ul>
 * 
 * 
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
public class BeTypeParser {





	public final BeType type;


	/**
	 * 
	 */
	private BeFieldParser[] fields;




	/**
	 * 
	 * @param propertiesMap
	 * @param outflowCode
	 * @param type
	 * @throws LithTypeBuildException 
	 * @throws LthSerialException
	 */
	public BeTypeParser(BeType type) {
		super();
		this.type = type;
		this.fields = new BeFieldParser[type.getNumberOfFields()];
	}


	/**
	 * 
	 * @return
	 */
	public BeType getType() {
		return type;
	}


	public String print(long code) {
		return "[0x"+Long.toHexString(code)+"]: "+type.getRuntimeName();
	}
	
	
	public interface DeltaListener {
		
		public void onFieldValueChange(BeFieldParser parser, ByteInflow inflow) throws IOException;
		
	}
	
	


	/**
	 * 
	 * @param inflow
	 * @throws IOException
	 */
	public void parse(ByteInflow inflow, DeltaListener listener) throws IOException {

		// code, fieldCode
		int code, fieldCode;
		
		// field inflow
		BeFieldParser fieldParser;

		while((code = inflow.getUInt8()) != CLOSE_NODE) {
			switch(code) {

			case DECLARE_FIELD: 

				/* retrieve name for code */
				String name = inflow.getStringUTF8();

				/* retrieve inflow field */
				BeField field = type.getField(name);
				if(field == null) {
					throw new BeIOException("Failed to find field for name = "+name, type.getBaseType());
				}

				/* retrieve inflow */
				fieldParser = field.createParser(inflow);
				
				/* retrieve field code */
				fieldCode = inflow.getUInt8();

				/* assign field inflow for code */
				fields[fieldCode] = fieldParser;

				break;

			case SET_VALUE : 


				/* get field code */
				fieldCode = inflow.getUInt8();

				/* retrieve fieldIO */
				fieldParser = fields[fieldCode];

				// call back to listener
				listener.onFieldValueChange(fieldParser, inflow);

				break;

			default : throw new IOException("Failed to match keyword: "+Integer.toHexString(code));
			}
		}
	}
	
}
