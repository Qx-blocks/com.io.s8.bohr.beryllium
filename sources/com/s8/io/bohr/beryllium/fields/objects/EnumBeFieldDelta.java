package com.s8.io.bohr.beryllium.fields.objects;

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
public class EnumBeFieldDelta extends BeFieldDelta {


	public final EnumBeField field;

	public final Object value;

	public EnumBeFieldDelta(EnumBeField field, Object value) {
		super();
		this.field = field;
		this.value = value;
	}

	@Override
	public void consume(TableS8Object object) throws IllegalArgumentException, IllegalAccessException {
		field.field.set(object, value);
	}


	public @Override BeField getField() { return field; }

	@Override
	public void computeFootprint(MemoryFootprint weight) {
		weight.reportInstance();
		weight.reportBytes(4); // int ordinal
	}

}
