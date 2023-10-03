package com.s8.io.bohr.beryllium.fields.primitives;

import com.s8.api.bytes.MemoryFootprint;
import com.s8.api.objects.table.TableS8Object;
import com.s8.io.bohr.beryllium.fields.BeFieldDelta;


/**
 * 
 *
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
public class StringBeFieldDelta extends BeFieldDelta {


	public final StringBeField field;

	public final String value;

	public StringBeFieldDelta(StringBeField field, String value) {
		super();
		this.field = field;
		this.value = value;
	}

	@Override 
	public StringBeField getField() { 
		return field;
	}


	@Override
	public void consume(TableS8Object object) throws IllegalArgumentException, IllegalAccessException {
		field.field.set(object, value);
	}

	@Override
	public void computeFootprint(MemoryFootprint weight) {
		if(value!=null) {
			weight.reportInstance();
			weight.reportBytes(value.length());
		}
	}

}
