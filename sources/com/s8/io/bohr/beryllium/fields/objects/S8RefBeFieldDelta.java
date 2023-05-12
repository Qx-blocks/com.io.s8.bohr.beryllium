package com.s8.io.bohr.beryllium.fields.objects;

import com.s8.io.bohr.beryllium.fields.BeField;
import com.s8.io.bohr.beryllium.fields.BeFieldDelta;
import com.s8.io.bohr.beryllium.object.BeObject;
import com.s8.io.bohr.beryllium.object.BeRef;
import com.s8.io.bytes.alpha.MemoryFootprint;


/**
 * 
 *
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
public class S8RefBeFieldDelta extends BeFieldDelta {


	public final S8RefBeField field;

	public final BeRef<?> ref;


	/**
	 * 
	 * @param fieldCode
	 * @param field
	 * @param address
	 * @param slot
	 */
	public S8RefBeFieldDelta(S8RefBeField field, BeRef<?> ref) {
		super();
		this.field = field;
		this.ref = ref;
	}

	
	@Override
	public void consume(BeObject object) throws IllegalArgumentException, IllegalAccessException {
		field.field.set(object, ref);
	}

	
	@Override
	public BeField getField() { 
		return field;
	}


	@Override
	public void computeFootprint(MemoryFootprint weight) {
		weight.reportBytes(1 + ref.address.length() + 8);
	}

}


