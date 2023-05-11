package com.s8.io.bohr.beryllium.fields;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.s8.io.bohr.beryllium.exception.BeBuildException;
import com.s8.io.bohr.beryllium.fields.primitives.BooleanBeField;
import com.s8.io.bohr.beryllium.fields.primitives.DoubleBeField;
import com.s8.io.bohr.beryllium.fields.primitives.FloatBeField;
import com.s8.io.bohr.beryllium.fields.primitives.IntegerBeField;
import com.s8.io.bohr.beryllium.fields.primitives.LongBeField;
import com.s8.io.bohr.beryllium.fields.primitives.PrimitiveBeField;
import com.s8.io.bohr.beryllium.fields.primitives.ShortBeField;
import com.s8.io.bohr.beryllium.fields.primitives.StringBeField;
import com.s8.io.bohr.neodymium.fields.arrays.BooleanArrayNdField;
import com.s8.io.bohr.neodymium.fields.arrays.DoubleArrayNdField;
import com.s8.io.bohr.neodymium.fields.arrays.FloatArrayNdField;
import com.s8.io.bohr.neodymium.fields.arrays.IntegerArrayNdField;
import com.s8.io.bohr.neodymium.fields.arrays.LongArrayNdField;
import com.s8.io.bohr.neodymium.fields.arrays.ShortArrayNdField;
import com.s8.io.bohr.neodymium.fields.arrays.StringArrayNdField;
import com.s8.io.bohr.neodymium.fields.collections.S8ObjectArrayNdField;
import com.s8.io.bohr.neodymium.fields.collections.S8ObjectListNdField;
import com.s8.io.bohr.neodymium.fields.objects.EnumNdField;
import com.s8.io.bohr.neodymium.fields.objects.InterfaceNdField;
import com.s8.io.bohr.neodymium.fields.objects.S8ObjectNdField;
import com.s8.io.bohr.neodymium.fields.objects.S8RefNdField;
import com.s8.io.bohr.neodymium.fields.objects.S8SerializableNdField;

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
					BooleanArrayNdField.PROTOTYPE,

					ShortBeField.PROTOTYPE,
					ShortArrayNdField.PROTOTYPE,

					IntegerBeField.PROTOTYPE,
					IntegerArrayNdField.PROTOTYPE,

					LongBeField.PROTOTYPE,
					LongArrayNdField.PROTOTYPE,

					FloatBeField.PROTOTYPE,
					FloatArrayNdField.PROTOTYPE,

					DoubleBeField.PROTOTYPE,
					DoubleArrayNdField.PROTOTYPE,

					StringBeField.PROTOTYPE,
					StringArrayNdField.PROTOTYPE
	};


	public final static BeFieldPrototype[] STANDARDS = new BeFieldPrototype[] {

			S8SerializableNdField.PROTOTYPE,
			
			/* must be tested before S8ObjectGphField */
			S8RefNdField.PROTOTYPE,
			//S8TableGphField.PROTOTYPE,

			/* must be tested before S8Struct */
			S8ObjectNdField.PROTOTYPE,

			S8ObjectArrayNdField.PROTOTYPE,
			S8ObjectListNdField.PROTOTYPE,
			EnumNdField.PROTOTYPE,

			// wildcard
			InterfaceNdField.PROTOTYPE
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
