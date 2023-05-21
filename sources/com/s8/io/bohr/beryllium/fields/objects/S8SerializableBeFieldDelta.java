package com.s8.io.bohr.beryllium.fields.objects;

import com.s8.io.bohr.atom.serial.BohrSerializable;
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
public class S8SerializableBeFieldDelta<T extends BohrSerializable> extends BeFieldDelta {
	
	
	public final S8SerializableBeField<T> field;
	
	public final BohrSerializable value;

	
	
	public S8SerializableBeFieldDelta(S8SerializableBeField<T> field, BohrSerializable value) {
		super();
		this.field = field;
		this.value = value;
	}

	@Override
	public void consume(BeObject object) throws IllegalArgumentException, IllegalAccessException {
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
