package com.s8.io.bohr.beryllium.object;

import java.io.IOException;

import com.s8.api.bytes.ByteOutflow;
import com.s8.api.bytes.MemoryFootprint;
import com.s8.io.bohr.beryllium.branch.BeOutbound;
import com.s8.io.bohr.beryllium.branch.BeTable;
import com.s8.io.bohr.beryllium.exception.BeIOException;


/**
 * 
 * 
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
public abstract class BeObjectDelta {

	public final String id;



	public BeObjectDelta(String index) {
		super();
		this.id = index;
	}


	/**
	 * 
	 * @param shell
	 * @return
	 * @throws NdIOException 
	 * @throws IOException 
	 */
	public abstract void consume(BeTable table) throws BeIOException;

	
	/**
	 * 
	 * @param outbound
	 * @param outflow
	 * @throws IOException
	 */
	public abstract void serialize(BeOutbound outbound, ByteOutflow outflow) throws IOException;

	
	/**
	 * 
	 * @param weight
	 */
	public abstract void computeFootprint(MemoryFootprint weight);
}
