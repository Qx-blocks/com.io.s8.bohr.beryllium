package com.s8.io.bohr.beryllium.demos;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import com.s8.io.bohr.beryllium.branch.BeBranch;
import com.s8.io.bohr.beryllium.branch.BeBranchDelta;
import com.s8.io.bohr.beryllium.codebase.BeCodebase;
import com.s8.io.bohr.beryllium.demos.examples.MyExtendedStorageEntry;
import com.s8.io.bohr.beryllium.demos.examples.MyStorageEntry;
import com.s8.io.bohr.beryllium.exception.BeBuildException;
import com.s8.io.bohr.beryllium.exception.BeIOException;
import com.s8.io.bohr.beryllium.utilities.BeUtilities;

public class BeBranchTest02 {

	
	/**
	 * 
	 * @param args
	 * @throws BeBuildException
	 * @throws IOException
	 */
	public static void main(String[] args) throws BeBuildException, IOException {
		
		BeCodebase codebase = BeCodebase.from(MyStorageEntry.class);


		BeBranch branch = new BeBranch(codebase);
		
		int n = 256;
		List<String> identifiers = new ArrayList<>();
		
		MyStorageEntry entry;
		for(int i = 0; i<n; i++) {
			branch.put(entry = MyStorageEntry.generateRandom());
			identifiers.add(entry.S8_key);
			
			branch.put(entry = MyExtendedStorageEntry.generateRandom());
			identifiers.add(entry.S8_key);
			
		}
		
		List<BeBranchDelta> deltas = branch.pullDeltas();
		
		

		BeBranch branchCopy = new BeBranch(codebase);
		deltas.forEach(delta -> {
			try {
				branchCopy.pushDelta(delta);
			} catch (BeIOException e) {
				e.printStackTrace();
			}
		});
		
		Writer writer = new PrintWriter(System.out);
		BeUtilities.deepCompare(branch, branchCopy, writer);
		writer.close();
		
		System.out.println("hello");
		
	}

}
