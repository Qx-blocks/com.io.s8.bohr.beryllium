package com.s8.io.bohr.beryllium.fields.objects;

import com.s8.api.bytes.MemoryFootprint;
import com.s8.api.objects.table.TableS8Object;
import com.s8.io.bohr.beryllium.fields.BeField;
import com.s8.io.bohr.beryllium.fields.BeFieldDelta;
import com.s8.io.bohr.beryllium.object.BeRef;


/**
 * 
 *
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
public class S8RefBeFieldDelta extends BeFieldDelta {


	public final S8RefBeField field;

	public final BeRef ref;


	/**
	 * 
	 * @param fieldCode
	 * @param field
	 * @param repositoryAddress
	 * @param slot
	 */
	public S8RefBeFieldDelta(S8RefBeField field, BeRef ref) {
		super();
		this.field = field;
		this.ref = ref;
	}

	
	@Override
	public void consume(TableS8Object object) throws IllegalArgumentException, IllegalAccessException {
		field.field.set(object, ref);
	}

	
	@Override
	public BeField getField() { 
		return field;
	}


	@Override
	public void computeFootprint(MemoryFootprint weight) {
		weight.reportBytes(1 + ref.repositoryAddress.length() + 8);
	}

}


