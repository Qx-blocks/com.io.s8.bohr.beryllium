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
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 *
 */
public class RemoveBeObjectDelta extends BeObjectDelta {

	public RemoveBeObjectDelta(String index) {
		super(index);
	}

	@Override
	public void consume(BeTable table) throws BeIOException {
		table.objects.remove(id);
	}

	@Override
	public void serialize(BeOutbound outbound, ByteOutflow outflow) throws IOException {
		
		/* remove node */
		outflow.putUInt8(BOHR_Keywords.REMOVE_NODE);

		/* define index */
		outflow.putStringUTF8(id);
	}
	

	@Override
	public void computeFootprint(MemoryFootprint weight) {
		// TODO Auto-generated method stub
		
	}

}
