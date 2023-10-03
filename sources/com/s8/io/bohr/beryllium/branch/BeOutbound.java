package com.s8.io.bohr.beryllium.branch;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.s8.api.bohr.BOHR_Keywords;
import com.s8.api.bytes.ByteOutflow;
import com.s8.io.bohr.beryllium.codebase.BeCodebase;
import com.s8.io.bohr.beryllium.exception.BeBuildException;
import com.s8.io.bohr.beryllium.exception.BeIOException;
import com.s8.io.bohr.beryllium.types.BeType;
import com.s8.io.bohr.beryllium.types.BeTypeComposer;

/**
 * 
 *
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
public class BeOutbound {


	private final BeCodebase codebase;

	private long typeCode = 0;

	/**
	 * 
	 */
	private final Map<String, BeTypeComposer> composers;


	public BeOutbound(BeCodebase codebase) {
		super();
		this.codebase = codebase;
		this.composers = new HashMap<>();
	}



	/**
	 * 
	 * @param type
	 * @return
	 * @throws NdIOException
	 */
	public BeTypeComposer getComposer(Class<?> type) throws BeIOException {
		String runtimeName = type.getName();
		return getComposer(runtimeName);
	}

	/**
	 * 
	 * @param type
	 * @return
	 * @throws NdIOException
	 */
	public BeTypeComposer getComposer(String runtimeTypeName) throws BeIOException {
		BeTypeComposer composer = composers.computeIfAbsent(runtimeTypeName, name -> {
			BeType nType = codebase.getTypeByRuntimeName(name);
			try {
				return new BeTypeComposer(nType, ++typeCode);
			} 
			catch (BeBuildException e) {
				e.printStackTrace();
				return null;
			}
		});
		if(composer != null) { return composer; }
		else {
			throw new BeIOException("failed to build composer");
		}
	}



	/**
	 * 
	 * @param outflow
	 * @param deltas
	 * @throws IOException
	 */
	private void composeSequence(ByteOutflow outflow, List<BeBranchDelta> deltas) throws IOException {
		outflow.putUInt8(BOHR_Keywords.OPEN_SEQUENCE);
		for(BeBranchDelta delta : deltas){
			delta.serialize(this, outflow);
		}
		outflow.putUInt8(BOHR_Keywords.CLOSE_SEQUENCE);
	}
	
	

	/**
	 * 
	 * @param outflow
	 * @throws IOException
	 */
	public void pushFrame(ByteOutflow outflow, List<BeBranchDelta> deltas) throws IOException {
		outflow.putByteArray(BOHR_Keywords.FRAME_HEADER);
		composeSequence(outflow, deltas);
		outflow.putByteArray(BOHR_Keywords.FRAME_FOOTER);
	}

}
