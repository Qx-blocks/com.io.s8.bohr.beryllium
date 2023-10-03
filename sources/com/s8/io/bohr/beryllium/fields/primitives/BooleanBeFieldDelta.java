package com.s8.io.bohr.beryllium.fields.primitives;

import com.s8.api.bytes.MemoryFootprint;
import com.s8.api.objects.table.TableS8Object;
import com.s8.io.bohr.beryllium.fields.BeField;
import com.s8.io.bohr.beryllium.fields.BeFieldDelta;

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
	public void consume(TableS8Object object) throws IllegalArgumentException, IllegalAccessException {
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
