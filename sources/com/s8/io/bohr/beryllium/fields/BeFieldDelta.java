package com.s8.io.bohr.beryllium.fields;


import com.s8.io.bohr.beryllium.object.BeObject;
import com.s8.io.bytes.alpha.MemoryFootprint;


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
	public abstract void consume(BeObject object) throws IllegalArgumentException, IllegalAccessException;
	
	
	public abstract void computeFootprint(MemoryFootprint weight);
	
	
	public abstract BeField getField();
}
