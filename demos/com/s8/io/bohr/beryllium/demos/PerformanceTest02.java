package com.s8.io.bohr.beryllium.demos;

import com.s8.io.bohr.beryllium.branch.BeBranch;
import com.s8.io.bohr.beryllium.codebase.BeCodebase;
import com.s8.io.bohr.beryllium.demos.examples.MyExtendedStorageEntry;
import com.s8.io.bohr.beryllium.demos.examples.MyStorageEntry;
import com.s8.io.bohr.beryllium.exception.BeBuildException;
import com.s8.io.bohr.beryllium.exception.BeIOException;

public class PerformanceTest02 {

	public static void main(String[] args) throws BeIOException, BeBuildException {

		
		BeCodebase codebase = BeCodebase.from(MyStorageEntry.class);


		BeBranch branch = new BeBranch(codebase);
		
		int n = 65536;
		MyStorageEntry entry;
		for(int i = 0; i<n; i++) {
			if(Math.random()<0.6) {
				entry = MyStorageEntry.generateRandom();	
			}
			else {
				entry = MyExtendedStorageEntry.generateRandom();	
			}
			branch.put(entry);
		}
		
		
		class Wrapper { public double x = 0; }
		
		Wrapper wrapper = new Wrapper();
		long t = System.nanoTime();
		branch.forEach(e -> {
			wrapper.x += ((MyStorageEntry) e).lattitude;
		});
		long dt = System.nanoTime() - t;
		
		System.out.println(dt);
		System.out.println(wrapper.x);
		
	}

}
