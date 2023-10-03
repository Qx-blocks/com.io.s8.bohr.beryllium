package com.s8.io.bohr.beryllium.codebase;

import java.util.ArrayDeque;
import java.util.Queue;

import com.s8.api.objects.annotations.S8ObjectType;
import com.s8.io.bohr.beryllium.exception.BeBuildException;
import com.s8.io.bohr.beryllium.fields.BeFieldFactory;
import com.s8.io.bohr.beryllium.types.BeTypeBuilder;

/**
 *
 *
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 */
public class BeCodebaseBuilder {
	


	/**
	 * factory
	 */
	private BeFieldFactory fieldFactory;
	

	/**
	 * 
	 */
	private BeCodebase codebase;


	/**
	 * 
	 */
	private Queue<BeTypeBuilder> queue;

	
	private boolean isFinished;
	private boolean isBuildingInProgress;

	private boolean isVerbose;

	/**
	 * 
	 */
	public BeCodebaseBuilder(boolean isVerbose) {
		super();

		// init context
		codebase = new BeCodebase(isVerbose);
		
		// factory
		fieldFactory = new BeFieldFactory();
		
		// queue
		queue = new ArrayDeque<BeTypeBuilder>();


		this.isVerbose = isVerbose;
		
		isBuildingInProgress = false;
	}



	/**
	 * 
	 * @return
	 */
	public BeFieldFactory getFieldFactory() {
		return fieldFactory;
	}
	


	public void pushObjectTypes(Class<?>[] types) throws BeBuildException {
		if(isFinished) {
			throw new BeBuildException("codebase construction process is now terminated");
		}
		
		for(Class<?> type : types) {
			pushObjectType(type);
		}
	}
	
	

	/**
	 * 
	 * @param type
	 * @throws LithTypeBuildException
	 * @throws LthSerialException 
	 */
	public void pushObjectType(Class<?> type) throws BeBuildException {
		if(isFinished) {
			throw new BeBuildException("codebase construction process is now terminated");
		}
		
		// must not have already been added
		if(!codebase.isTypeKnown(type)) {
			
			// must be annotated
			if(type.isAnnotationPresent(S8ObjectType.class)) {
				queue.add(new BeTypeBuilder(type, isVerbose));
			}
			else if(isVerbose) {
				System.out.println("Type "+type+" has not been accepted because of lacking annotation");
			}	
		}
		
		if(!isBuildingInProgress) {
			buildBuffered();
		}
	}
	

	public void pushRowType(Class<?> type) throws BeBuildException {
		if(isFinished) {
			throw new BeBuildException("codebase construction process is now terminated");
		}
		
		/*
		try {
			upperLevel.pushRowType(type);
		} 
		catch (IOException e) {
			new BeBuildException(e.getMessage(), type);
		}
		*/
	}
	
	/**
	 * 
	 * @return
	 * @throws LthSerialException
	 * @throws LithTypeBuildException
	 * @throws BohrTypeBuildingException
	 */
	private void buildBuffered() throws BeBuildException {

		isBuildingInProgress = true;

		int count = 0;
		BeTypeBuilder typeBuilder;
		while(!queue.isEmpty() && count<65536) {
			typeBuilder = queue.poll();

		
			// check that not already built
			if(!codebase.isTypeKnown(typeBuilder.getBaseType())) {
				
				// type
				boolean isBuildable = typeBuilder.process(this);

				// register
				if(isBuildable) { codebase.put(typeBuilder.build()); }
			}

			count++;
		}
		
		isBuildingInProgress = false;
	}
	
	
	public BeCodebase build() throws BeBuildException {
		if(isFinished) {
			throw new BeBuildException("codebase construction process is already terminated");
		}
		codebase.nTypes = codebase.typesByRuntimeName.size();
		isFinished = true;
		return codebase;
	}
}
