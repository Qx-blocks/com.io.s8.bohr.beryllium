package com.s8.io.bohr.beryllium.demos;

import com.s8.io.bohr.beryllium.codebase.BeCodebase;
import com.s8.io.bohr.beryllium.demos.examples.MyStorageEntry;
import com.s8.io.bohr.beryllium.exception.BeBuildException;

public class BeCodebaseTestUnit00 {

	public static void main(String[] args) throws BeBuildException {

		BeCodebase codebase = BeCodebase.from(MyStorageEntry.class);
	
		System.out.println(codebase);
	}

}
