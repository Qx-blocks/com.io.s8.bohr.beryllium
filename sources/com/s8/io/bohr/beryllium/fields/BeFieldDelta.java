package com.s8.io.bohr.beryllium.fields;


import com.s8.api.bytes.MemoryFootprint;
import com.s8.api.objects.table.TableS8Object;


/**
 * <p><code>NdFieldDelta</code> are immutable!</p>
 *
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
public abstract class BeFieldDelta {
		
	
	public BeFieldDelta() {
		super();
	}
	
	/**
	 * 
	 * @param object
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws LthSerialException 
	 */
	public abstract void consume(TableS8Object object) throws IllegalArgumentException, IllegalAccessException;
	
	
	public abstract void computeFootprint(MemoryFootprint weight);
	
	
	public abstract BeField getField();
}
