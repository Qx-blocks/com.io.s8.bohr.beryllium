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
public class LongBeFieldDelta extends BeFieldDelta {


	public final LongBeField field;

	public final long value;

	public LongBeFieldDelta(LongBeField field, long value) {
		super();
		this.field = field;
		this.value = value;
	}

	public @Override LongBeField getField() { return field; }

	@Override
	public void consume(TableS8Object object) throws IllegalArgumentException, IllegalAccessException {
		field.field.setLong(object, value);
	}

	@Override
	public void computeFootprint(MemoryFootprint weight) {
		weight.reportBytes(8);
	}
}

