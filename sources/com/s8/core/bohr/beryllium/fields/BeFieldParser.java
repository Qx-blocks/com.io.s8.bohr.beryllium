package com.s8.core.bohr.beryllium.fields;

import java.io.IOException;

import com.s8.api.bytes.ByteInflow;
import com.s8.api.flow.record.objects.RecordS8Object;
import com.s8.core.bohr.atom.protocol.BOHR_Properties;


/**
 * 
 *
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
public abstract class BeFieldParser {
	
	
	public abstract BeField getField();
	
	
	/**
	 * 
	 * @param map
	 * @param object
	 * @param inflow
	 * @param bindings
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws BkException
	 */
	public abstract void parseValue(RecordS8Object object, ByteInflow inflow) throws IOException, IllegalArgumentException, IllegalAccessException;
	

	/**
	 * 
	 * @param inflow
	 * @return
	 * @throws IOException 
	 */
	public abstract BeFieldDelta deserializeDelta(ByteInflow inflow) throws IOException;
	

	
	public static boolean isNonNull(int props) {
		return (props & BOHR_Properties.IS_NON_NULL_PROPERTIES_BIT) == BOHR_Properties.IS_NON_NULL_PROPERTIES_BIT;
	}
}
