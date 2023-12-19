package com.s8.core.bohr.beryllium.object;

import java.io.IOException;
import java.util.List;

import com.s8.api.bytes.ByteOutflow;
import com.s8.api.bytes.MemoryFootprint;
import com.s8.api.flow.table.objects.RowS8Object;
import com.s8.core.bohr.atom.protocol.BOHR_Keywords;
import com.s8.core.bohr.beryllium.branch.BeOutbound;
import com.s8.core.bohr.beryllium.branch.BeTable;
import com.s8.core.bohr.beryllium.exception.BeIOException;
import com.s8.core.bohr.beryllium.fields.BeFieldDelta;
import com.s8.core.bohr.beryllium.types.BeType;
import com.s8.core.bohr.beryllium.types.BeTypeComposer;


/**
 * 
 * 
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
public class CreateBeObjectDelta extends BeObjectDelta {

	public List<BeFieldDelta> deltas;


	public final BeType type;

	

	public CreateBeObjectDelta(String index, BeType type, List<BeFieldDelta> deltas) {
		super(index);

		this.type = type;
		
		// deltas
		this.deltas = deltas;

	}

	@Override
	public void serialize(BeOutbound outbound, ByteOutflow outflow) throws IOException {

		BeTypeComposer composer = outbound.getComposer(type.getRuntimeName());
		
		/*  advertise diff type: publish a create node */
		composer.publish_CREATE_NODE(outflow, id);

		/* serialize field deltas */
		// produce all diffs
		for(BeFieldDelta delta : deltas) {
			int ordinal = delta.getField().ordinal;
			composer.fieldComposers[ordinal].publish(delta, outflow);
		}

		/* Close node */
		outflow.putUInt8(BOHR_Keywords.CLOSE_NODE);
	}



	@Override
	public void consume(BeTable table) throws BeIOException {

		/* create object and assign object id */
		RowS8Object object = type.createNewInstance(id);

		/* consume diff */
		type.consumeDiff(object, deltas);

		/* retrieve vertex */
		table.objects.put(id, object);
	}


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

