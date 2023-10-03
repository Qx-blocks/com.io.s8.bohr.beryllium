package com.s8.io.bohr.beryllium.object;

import java.io.IOException;
import java.util.List;

import com.s8.api.bohr.BOHR_Keywords;
import com.s8.api.bytes.ByteOutflow;
import com.s8.api.bytes.MemoryFootprint;
import com.s8.api.exceptions.S8IOException;
import com.s8.api.objects.table.TableS8Object;
import com.s8.io.bohr.beryllium.branch.BeOutbound;
import com.s8.io.bohr.beryllium.branch.BeTable;
import com.s8.io.bohr.beryllium.fields.BeFieldDelta;
import com.s8.io.bohr.beryllium.types.BeType;
import com.s8.io.bohr.beryllium.types.BeTypeComposer;


/**
 * 
 * 
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
public class UpdateBeObjectDelta extends BeObjectDelta {

	
	public final BeType type;

	public final List<BeFieldDelta> deltas;
	
	
	/**
	 * 
	 * @param index
	 * @param type
	 * @param deltas
	 */
	public UpdateBeObjectDelta(String index, BeType type, List<BeFieldDelta> deltas) {
		super(index);
		

		// type
		this.type = type;
		
		// deltas
		this.deltas = deltas;
		
	}


	@Override
	public void serialize(BeOutbound outbound, ByteOutflow outflow) throws IOException {

		
		BeTypeComposer composer = outbound.getComposer(type.getRuntimeName());
		
		/*  advertise diff type: publish a create node */

		/* pass flag */
		outflow.putUInt8(BOHR_Keywords.UPDATE_NODE);

		/* pass index */
		outflow.putStringUTF8(id);

		// produce all diffs
		for(BeFieldDelta delta : deltas) {
			int ordinal = delta.getField().ordinal;
			composer.fieldComposers[ordinal].publish(delta, outflow);
		}

		/* Close node */
		outflow.putUInt8(BOHR_Keywords.CLOSE_NODE);
	}



	@Override
	public void consume(BeTable table) {

		try {
			/* retrieve object */
			TableS8Object object = table.objects.get(id);

			if(object==null) {
				throw new S8IOException("failed to retrieve vertex for index: "+id);
			}
			
			// consume diff
			type.consumeDiff(object, deltas);	

		}
		catch (Exception e) {
			e.printStackTrace();
		}	
	}


	/*
	public static UpdateNdObjectDelta deserialize(LithCodebaseIO codebaseIO, ByteInflow inflow) {
		UpdateNdObjectDelta objectDelta = new UpdateNdObjectDelta();
		objectDelta.deserializeBody(codebaseIO, inflow);
		return objectDelta;
	}
	 */


	@Override
	public void computeFootprint(MemoryFootprint weight) {

		weight.reportInstance();

		// fields
		if(deltas!=null) {
			for(BeFieldDelta delta : deltas) {
				delta.computeFootprint(weight);
			}
		}
	}
}
