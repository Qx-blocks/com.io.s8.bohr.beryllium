package com.s8.io.bohr.beryllium.demos.examples;

import com.s8.api.objects.annotations.S8Field;
import com.s8.api.objects.annotations.S8ObjectType;


/**
 * 
 * @author pierreconvert
 *
 */
@S8ObjectType(name = "extended-storage-entry")
public class MyExtendedStorageEntry extends MyStorageEntry {

	
	
	
	@S8Field(name = "subscriptions") 
	public String[] subscriptions;
	
	
	
	/**
	 * 
	 */
	public MyExtendedStorageEntry(String id) {
		super(id);
	}
	
	
	/**
	 * 
	 * @return
	 */
	public static MyExtendedStorageEntry generateRandom() {
		String id = "CODE_TRGE_"+System.nanoTime()+ ((int) (1880298098 * Math.random()));
		MyExtendedStorageEntry entry = new MyExtendedStorageEntry(id);
		entry.shuffle();
		return entry;
	}
	
	
	
	@Override
	public void shuffle() {
		super.shuffle();
		subscriptions = new String[] { Long.toHexString(System.nanoTime()) };
	}


}
