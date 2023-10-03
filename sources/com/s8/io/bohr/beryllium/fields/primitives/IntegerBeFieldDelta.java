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
public class IntegerBeFieldDelta  extends BeFieldDelta {

	public final IntegerBeField field;
	public final int value;

	public IntegerBeFieldDelta(IntegerBeField field, int value) {
		super();
		this.field = field;
		this.value = value;
	}


	@Override
	public BeField getField() { 
		return field; 
	}

	@Override
	public void consume(TableS8Object object) throws IllegalArgumentException, IllegalAccessException {
		field.field.setInt(object, value);
	}

	@Override
	public void computeFootprint(MemoryFootprint weight) {
		weight.reportBytes(4);
	}
}
