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
public class StringArrayBeFieldDelta extends BeFieldDelta {


	public final StringArrayBeField field;

	public final String[] value;

	public StringArrayBeFieldDelta(StringArrayBeField field, String[] array) {
		super();
		this.field = field;
		this.value = array;
	}

	@Override
	public BeField getField() { 
		return field; 
	}


	@Override
	public void consume(TableS8Object object) throws IllegalArgumentException, IllegalAccessException {
		field.field.set(object, value);
	}


	@Override
	public void computeFootprint(MemoryFootprint weight) {
		if(value!=null) {
			weight.reportInstances(1+value.length); // the array object itself	
			for(String str : value) {
				weight.reportBytes(str.length()); // proxy for UTF8 ~....
			}
		}
	}


}
