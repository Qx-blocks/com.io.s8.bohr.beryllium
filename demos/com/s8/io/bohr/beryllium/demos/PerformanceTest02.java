package com.s8.io.bohr.beryllium.demos;

import java.util.HashMap;
import java.util.Map;

import com.s8.io.bohr.beryllium.demos.examples.MyStorageEntry;

public class PerformanceTest02 {

	public static void main(String[] args) {

		int n = 65536;
		Map<String, MyStorageEntry> base = new HashMap<String, MyStorageEntry>(n);
		for(int i = 0; i < n; i++) {
			MyStorageEntry entry = MyStorageEntry.generateRandom();
			base.put(entry.S8_id, entry);
		}
		
		class Wrapper { public int x = 0; }
		Wrapper wrapper = new Wrapper();
		
		long time = System.nanoTime();
		base.forEach((key, value) ->{
			wrapper.x += value.deepCopy().quantity;
		});
		System.out.println("screen through time : "+(System.nanoTime() - time)/1000000+ "ms");
		System.out.println(wrapper.x);
		
	}

}
