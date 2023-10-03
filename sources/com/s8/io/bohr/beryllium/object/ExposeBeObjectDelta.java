package com.s8.io.bohr.beryllium.object;

import java.io.IOException;

import com.s8.api.bohr.BOHR_Keywords;
import com.s8.api.bytes.ByteOutflow;
import com.s8.api.bytes.MemoryFootprint;
import com.s8.io.bohr.beryllium.branch.BeOutbound;
import com.s8.io.bohr.beryllium.branch.BeTable;
import com.s8.io.bohr.beryllium.exception.BeIOException;


/**
 * 
 * 
 * 
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
public class ExposeBeObjectDelta extends BeObjectDelta {
	
	public final int slot;


	public ExposeBeObjectDelta(String index, int slot) {
		super(index);
		this.slot = slot;
	}

	@Override
	public void serialize(BeOutbound outbound, ByteOutflow outflow) throws IOException {

		outflow.putUInt8(BOHR_Keywords.EXPOSE_NODE);

		/* define index */
		outflow.putStringUTF8(id);

		/* define slot */
		outflow.putUInt8(slot);
	}



	@Override
	public void consume(BeTable graph) throws BeIOException {
		throw new BeIOException("Not applicable");
	}
	

	@Override
	public void computeFootprint(MemoryFootprint weight) {

		weight.reportInstance();
	}

}

