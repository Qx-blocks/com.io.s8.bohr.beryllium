package com.s8.io.bohr.beryllium.fields;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.s8.io.bohr.beryllium.exception.BeBuildException;
import com.s8.io.bohr.beryllium.fields.arrays.BooleanArrayBeField;
import com.s8.io.bohr.beryllium.fields.arrays.DoubleArrayBeField;
import com.s8.io.bohr.beryllium.fields.arrays.FloatArrayBeField;
import com.s8.io.bohr.beryllium.fields.arrays.IntegerArrayBeField;
import com.s8.io.bohr.beryllium.fields.arrays.LongArrayBeField;
import com.s8.io.bohr.beryllium.fields.arrays.ShortArrayBeField;
import com.s8.io.bohr.beryllium.fields.arrays.StringArrayBeField;
import com.s8.io.bohr.beryllium.fields.objects.EnumBeField;
import com.s8.io.bohr.beryllium.fields.objects.S8RefBeField;
import com.s8.io.bohr.beryllium.fields.objects.S8SerializableBeField;
import com.s8.io.bohr.beryllium.fields.primitives.BooleanBeField;
import com.s8.io.bohr.beryllium.fields.primitives.DoubleBeField;
import com.s8.io.bohr.beryllium.fields.primitives.FloatBeField;
import com.s8.io.bohr.beryllium.fields.primitives.IntegerBeField;
import com.s8.io.bohr.beryllium.fields.primitives.LongBeField;
import com.s8.io.bohr.beryllium.fields.primitives.PrimitiveBeField;
import com.s8.io.bohr.beryllium.fields.primitives.ShortBeField;
import com.s8.io.bohr.beryllium.fields.primitives.StringBeField;

/**
 * 
 *
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
public class BeFieldFactory {


	/**
	 * mapped
	 */
	public final static PrimitiveBeField.Prototype[] DEFAULT_PRIMITIVES = new PrimitiveBeField.Prototype[] {

					BooleanBeField.PROTOTYPE,
					BooleanArrayBeField.PROTOTYPE,

					ShortBeField.PROTOTYPE,
					ShortArrayBeField.PROTOTYPE,

					IntegerBeField.PROTOTYPE,
					IntegerArrayBeField.PROTOTYPE,

					LongBeField.PROTOTYPE,
					LongArrayBeField.PROTOTYPE,

					FloatBeField.PROTOTYPE,
					FloatArrayBeField.PROTOTYPE,

					DoubleBeField.PROTOTYPE,
					DoubleArrayBeField.PROTOTYPE,

					StringBeField.PROTOTYPE,
					StringArrayBeField.PROTOTYPE
	};


	public final static BeFieldPrototype[] STANDARDS = new BeFieldPrototype[] {

			S8SerializableBeField.PROTOTYPE,
			
			/* must be tested before S8ObjectGphField */
			S8RefBeField.PROTOTYPE,
			//S8TableGphField.PROTOTYPE,

			/* must be tested before S8Struct */
			//S8ObjectNdField.PROTOTYPE,

			//S8ObjectArrayNdField.PROTOTYPE,
			//S8ObjectListNdField.PROTOTYPE,
			EnumBeField.PROTOTYPE,

			// wildcard
			//InterfaceNdField.PROTOTYPE
	};


	private Map<String, PrimitiveBeField.Prototype> primitivePrototypes;


	/**
	 * 
	 * @param buildables
	 */
	public BeFieldFactory() {
		super();

		// load default primitives
		primitivePrototypes = new HashMap<String, PrimitiveBeField.Prototype>();

		// add default
		for(PrimitiveBeField.Prototype primitive : DEFAULT_PRIMITIVES) {
			primitivePrototypes.put(primitive.getKey(), primitive);
		}
	}


	/**
	 * 
	 * @param field
	 * @return
	 * @throws LithTypeBuildException
	 */
	public BeFieldBuilder captureField(Field field) throws BeBuildException {

		// build key
		Class<?> type = field.getType();
		String key = type.getCanonicalName();

		BeFieldProperties props;

		// try to match primitives
		PrimitiveBeField.Prototype primitivePrototype = primitivePrototypes.get(key);
		if(primitivePrototype!=null && (props = primitivePrototype.captureField(field))!=null) {
			return primitivePrototype.createFieldBuilder(props, field);
		}

		// if not working try other builders
		
		for(BeFieldPrototype prototype : STANDARDS) {
			if((props = prototype.captureField(field))!=null) {
				return prototype.createFieldBuilder(props, field);
			}
		}

		//DEBUG_analyze(field);

		// no prototypes has been able to capture the field
		throw new BeBuildException("Failed to capture the field ", field);
	}



}
