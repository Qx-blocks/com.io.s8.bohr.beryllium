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
public class DoubleBeFieldDelta extends BeFieldDelta {



	public final DoubleBeField field;

	public final double value;

	public DoubleBeFieldDelta(DoubleBeField field, double value) {
		super();
		this.field = field;
		this.value = value;
	}
	

	@Override
	public void consume(BeObject object) throws IllegalArgumentException, IllegalAccessException {
		field.field.setDouble(object, value);
	}


	@Override
	public void computeFootprint(MemoryFootprint weight) {
		weight.reportBytes(8);
	}


	@Override
	public BeField getField() { 
		return field;
	}


}
