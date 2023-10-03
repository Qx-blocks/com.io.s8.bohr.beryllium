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
public class ShortArrayBeFieldDelta extends BeFieldDelta {


	public final ShortArrayBeField field;
	
	public final short[] value;

	public ShortArrayBeFieldDelta(ShortArrayBeField field, short[] array) {
		super();
		this.field = field;
		this.value = array;
	}


	public @Override BeField getField() { return field; }

	@Override
	public void consume(TableS8Object object) throws IllegalArgumentException, IllegalAccessException  {
		field.field.set(object, value);
	}


	@Override
	public void computeFootprint(MemoryFootprint weight) {
		if(value!=null) {
			weight.reportInstance(); // the array object itself	
			weight.reportBytes(value.length*2);
		}
	}
}
