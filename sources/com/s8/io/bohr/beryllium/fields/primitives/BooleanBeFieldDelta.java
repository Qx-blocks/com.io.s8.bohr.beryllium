package com.s8.io.bohr.beryllium.fields.primitives;

import com.s8.io.bohr.beryllium.fields.BeField;
import com.s8.io.bohr.beryllium.fields.BeFieldDelta;
import com.s8.io.bohr.beryllium.object.BeObject;
import com.s8.io.bytes.alpha.MemoryFootprint;

/**
 * 
 *
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
public class BooleanBeFieldDelta extends BeFieldDelta {


	public final BooleanBeField field;

	public final boolean value;

	public BooleanBeFieldDelta(BooleanBeField field, boolean value) {
		super();
		this.field = field;
		this.value = value;
	}


	@Override
	public void consume(BeObject object) throws IllegalArgumentException, IllegalAccessException {
		field.field.setBoolean(object, value);
	}


	@Override
	public void computeFootprint(MemoryFootprint weight) {
		weight.reportBytes(1);
	}
	
	
	@Override
	public BeField getField() { 
		return field;
	}

}
