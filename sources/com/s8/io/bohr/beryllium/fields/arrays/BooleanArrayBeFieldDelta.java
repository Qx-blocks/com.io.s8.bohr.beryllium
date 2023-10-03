package com.s8.io.bohr.beryllium.fields.arrays;

import com.s8.api.bytes.MemoryFootprint;
import com.s8.api.objects.table.TableS8Object;
import com.s8.io.bohr.beryllium.fields.BeField;
import com.s8.io.bohr.beryllium.fields.BeFieldDelta;


/**
 * later aggregate
 * 
 *
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
public class BooleanArrayBeFieldDelta extends BeFieldDelta {


	public final BooleanArrayBeField field;

	public final boolean[] value;

	public BooleanArrayBeFieldDelta(BooleanArrayBeField field, boolean[] array) {
		super();
		this.field = field;
		this.value = array;
	}

	@Override
	public BeField getField() { 
		return field;
	}


	@Override
	public void consume(TableS8Object object) throws IllegalArgumentException, IllegalAccessException  {
		field.field.set(object, value);
	}

	@Override
	public void computeFootprint(MemoryFootprint weight) {
		if(value!=null) {
			weight.reportInstance(); // the array object itself	
			weight.reportBytes(value.length/8);
		}
	}

}
