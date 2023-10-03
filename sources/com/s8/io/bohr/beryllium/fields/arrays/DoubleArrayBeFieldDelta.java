package com.s8.io.bohr.beryllium.fields.arrays;

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
public class DoubleArrayBeFieldDelta extends BeFieldDelta {

	public final DoubleArrayBeField field;
	
	public final double[] value;

	public DoubleArrayBeFieldDelta(DoubleArrayBeField field, double[] array) {
		super();
		this.field = field;
		this.value = array;
	}

	public @Override BeField getField() { return field; }

	@Override
	public void consume(TableS8Object object) throws IllegalArgumentException, IllegalAccessException {
		field.field.set(object, value); 
	}


	@Override
	public void computeFootprint(MemoryFootprint weight) {
		if(value!=null) {
			weight.reportInstance();
			weight.reportBytes(value.length*8);
		}
	}

}
