package com.s8.io.bohr.beryllium.fields.objects;

import com.s8.api.bytes.MemoryFootprint;
import com.s8.api.objects.serial.BohrSerializable;
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
public class S8SerializableBeFieldDelta<T extends BohrSerializable> extends BeFieldDelta {
	
	
	public final S8SerializableBeField<T> field;
	
	public final BohrSerializable value;

	
	
	public S8SerializableBeFieldDelta(S8SerializableBeField<T> field, BohrSerializable value) {
		super();
		this.field = field;
		this.value = value;
	}

	@Override
	public void consume(TableS8Object object) throws IllegalArgumentException, IllegalAccessException {
		field.field.set(object, value);
	}
	
	
	@Override
	public BeField getField() { 
		return field;
	}
	
	
	@Override
	public void computeFootprint(MemoryFootprint weight) {
		if(value!=null) {
			weight.reportInstance();
			weight.reportBytes(value.computeFootprint());	
		}
	}

}
