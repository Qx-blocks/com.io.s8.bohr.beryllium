package com.s8.io.bohr.beryllium.types;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import com.s8.api.objects.annotations.S8Field;
import com.s8.api.objects.annotations.S8ObjectType;
import com.s8.api.objects.table.TableS8Object;
import com.s8.io.bohr.beryllium.codebase.BeCodebaseBuilder;
import com.s8.io.bohr.beryllium.exception.BeBuildException;
import com.s8.io.bohr.beryllium.fields.BeField;
import com.s8.io.bohr.beryllium.fields.BeFieldBuilder;

/**
 * 
 *
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
public class BeTypeBuilder {



	private final Class<?> baseType;

	private final boolean isVerbose;




	private boolean isBuildable = false;


	private Map<String, BeFieldBuilder> fieldBuildersByName;



	private Constructor<?> constructor;



	private String name;



	private int nVertexReferences;




	private S8ObjectType typeAnnotation;







	public BeTypeBuilder(Class<?> type, boolean isVerbose) {
		super();


		this.baseType = type;
		this.isVerbose = isVerbose;
	}







	/**
	 * 
	 * @param name
	 * @return
	 */
	public BeFieldBuilder getFieldBuilder(String name) {
		return fieldBuildersByName.get(name);
	}

	/**
	 * 
	 * @param name
	 * @param fieldBuilder
	 */
	public void setFieldBuilder(String name, BeFieldBuilder fieldBuilder) {
		fieldBuildersByName.put(name, fieldBuilder);
	}




	/**
	 * 
	 * <h1>CALLED SECOND</h1>
	 * @param level
	 * @param factory
	 * @throws BohrTypeBuildingException 
	 * @throws BkException
	 * @throws Exception 
	 */
	public boolean process(BeCodebaseBuilder codebaseBuilder) throws BeBuildException {

		// retrieve typeAnnotation once and for all
		typeAnnotation = baseType.getAnnotation(S8ObjectType.class);

		// check status
		check();

		resolveName();

		initializeAttributes();

		/* <constructor> */
		retrieveConstructor();
		/* </constructor> */

		// recursive extraction
		crawl(baseType, codebaseBuilder);
		/* </fields> */

		return isBuildable;
	}





	private void check() {

		int mods = baseType.getModifiers();

		/**
		 * Determine if abstract
		 */


		// is static ?
		if(!baseType.isMemberClass() || (baseType.isMemberClass() && Modifier.isStatic(mods))) {
			if(TableS8Object.class.isAssignableFrom(baseType)) {
				isBuildable = true;
			}
			else if(isVerbose) {
				System.out.println("Type "+baseType+" is not a valid subType for S8Object");
			}
		}
		else if(isVerbose) {
			System.out.println("Type "+baseType+" rejected as not static");
		}
	}



	/**
	 * <h1>CALLED FIRST</h1>
	 * <p>Reslve name by checking direct case and nested class case.</p>
	 * @throws LithTypeBuildException
	 */
	private void resolveName() throws BeBuildException {
		if(isBuildable) {
			/* <code> */

			if(typeAnnotation==null) {
				throw new BeBuildException("Missing type annotation", baseType);
			}

			Class<?> enclosingType = baseType.getEnclosingClass();
			if(enclosingType!=null) {
				S8ObjectType enclosingTypeAnnotation = enclosingType.getAnnotation(S8ObjectType.class);
				if(enclosingTypeAnnotation==null) {
					throw new BeBuildException("Missing enclosing type annotation", baseType);
				}	
				name = typeAnnotation.name()+'@'+enclosingTypeAnnotation.name();
			}
			else {
				name = typeAnnotation.name();
			}
		}

	}



	/**
	 * <p>
	 * Initialize all attributes:
	 * </p>
	 * <ul>
	 * <li>fields</li>
	 * </ul>
	 */
	public void initializeAttributes() {
		/* <fields> */
		if(isBuildable) {
			fieldBuildersByName = new HashMap<String, BeFieldBuilder>();	
		}
	}


	/**
	 * lookUp this level 
	 * 
	 * @param type
	 * @param factory
	 * @throws LithTypeBuildException
	 * @throws BohrTypeBuildingException 
	 * @throws LthSerialException 
	 */
	public void crawl(Class<?> type, BeCodebaseBuilder codebaseBuilder) throws BeBuildException {

		/* <sub-types> */

		/* --> Always to be done, even if not buildable */
		Class<?>[] subTypes = typeAnnotation.sub();
		if(subTypes!=null) {
			for(Class<?> subType : subTypes) {
				codebaseBuilder.pushObjectType(subType);
			}
		}
		/* </sub-types> */


		/* <nested-types> */

		/* --> Always to be done, even if not buildable */
		Class<?>[] nestedTypes = type.getNestMembers();
		int nNestedTypes = nestedTypes.length;
		for(int i=1; i<nNestedTypes; i++) {
			codebaseBuilder.pushObjectType(nestedTypes[i]);
		}
		/* </nested-types> */



		// search superclass
		/* --> Always to be done, even if not buildable */
		Class<?> superType = type.getSuperclass();
		if(superType!=null && superType.isAnnotationPresent(S8ObjectType.class)) {

			// append superclass to type to be built and added to the Mapping
			codebaseBuilder.pushObjectType(superType);

			// perform extract on supertype
			crawl(superType, codebaseBuilder);
		}

		// search trough all declared field at this level
		if(isBuildable) {
			/* <fields> */
			Field[] declaredFields = type.getDeclaredFields();
			if(declaredFields!=null) {
				for(Field field : declaredFields) { onField(codebaseBuilder, field); }	
			}
			/* </fields> */
		}

		/* </subTypes> */
	}


	/**
	 * 
	 * @return
	 */
	public Class<?> getBaseType() {
		return baseType;
	}



	public void retrieveConstructor() throws BeBuildException {
		if(isBuildable) {
			try {
				/*
				 * retrieve constructor with no parameters 
				 */
				constructor = baseType.getConstructor(new Class<?>[]{String.class});

			} 
			catch (NoSuchMethodException | SecurityException e) {
				throw new BeBuildException("missing public constructor with one id parameter", baseType);
			}
			catch (ClassCastException e) {
				throw new BeBuildException("Must inherit DkObject", baseType);
			}	
		}
	}



	public void filter(Field field) {
		/*
		// check ownership
		S8Owner ownerAnnotation = field.getAnnotation(S8Owner.class);
		if(ownerAnnotation!=null) {
			// TODO objectType.
		}
		 */
	}



	/**
	 * <p>
	 * append field
	 * </p>
	 * <p>
	 * <b>Note that testing if field must be collected with <code>isCollected</code>
	 * must be performed prior to using this method</b>
	 * </p>
	 * 
	 * @param field
	 * @param factory
	 * @throws LthSerialException 
	 * @throws BkException
	 */
	private void onField(BeCodebaseBuilder contextBuilder, Field field) throws BeBuildException {

		S8Field fieldAnnotation = field.getAnnotation(S8Field.class);
		if(fieldAnnotation!=null) {
			String name = fieldAnnotation.name();


			// non-mixing check

			BeFieldBuilder fieldBuilder = fieldBuildersByName.get(name);


			// aggregate with an already existing field
			if(fieldBuilder!=null) {

				fieldBuilder.attachField(field);
			}
			// create new field
			else {
				fieldBuilder = contextBuilder.getFieldFactory().captureField(field);
				
				// apply filter before appending fields
				filter(field);

				// add fields
				fieldBuildersByName.put(name, fieldBuilder);	
			}
		}
		// else: do nothing (field is skipped)
	}



	


	/**
	 * Post build
	 * @throws BeBuildException 
	 */
	public BeType build() throws BeBuildException {

		if(!isBuildable) {
			throw new BeBuildException("This type is not buildable");
		}

		// generate type
		BeType type = new BeType(baseType);


		type.name = name;

		type.constructor = constructor;

		type.nVertexReferences = nVertexReferences;

		// finalize build
		fieldBuildersByName.forEach((name, builder) -> {
			//builder.build2(this);
		});


		// retrieve nb fields
		int nFields = fieldBuildersByName.size();


		// build map
		Map<String, BeField> fieldsByName = new HashMap<String, BeField>(nFields);
		AtomicInteger ordinator = new AtomicInteger(0);
		fieldBuildersByName.forEach((name, builder) -> {
			try {
				fieldsByName.put(name, builder.build(ordinator.getAndIncrement()));
			} catch (BeBuildException e) {
				if(isVerbose) {
					e.printStackTrace();	
				}
			}
		});
		type.fieldsByName = fieldsByName;


		// build array
		BeField[] fields = new BeField[nFields];
		BeField field;
		for(Entry<String, BeField> entry : fieldsByName.entrySet()) {
			field = entry.getValue();
			fields[field.ordinal] = field;
		}
		type.fields = fields;

		return type;
	}



	public int onVertexReferenced() {
		return this.nVertexReferences++;
	}



	public void subDiscover() {

	}



}
