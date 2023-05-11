package com.s8.io.bohr.beryllium.object;

/**
 * 
 * @author pierreconvert
 *
 */
public abstract class BeObject {

	public final String S8_id;
	
	
	public BeObject(String id) {
		super();
		this.S8_id = id;
	}


	public abstract BeObject deepCopy();
	
}
