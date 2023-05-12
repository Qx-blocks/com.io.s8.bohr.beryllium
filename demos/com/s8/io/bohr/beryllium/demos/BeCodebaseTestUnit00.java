package com.s8.io.bohr.beryllium.demos;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import com.s8.io.bohr.beryllium.codebase.BeCodebase;
import com.s8.io.bohr.beryllium.demos.examples.MyStorageEntry;
import com.s8.io.bohr.beryllium.exception.BeBuildException;
import com.s8.io.bohr.beryllium.fields.BeField;
import com.s8.io.bohr.beryllium.types.BeType;

public class BeCodebaseTestUnit00 {

	public static void main(String[] args) throws BeBuildException, IOException, IllegalArgumentException, IllegalAccessException {

		BeCodebase codebase = BeCodebase.from(MyStorageEntry.class);



		MyStorageEntry e00 = new MyStorageEntry("id0x0023");
		e00.shuffle();

		MyStorageEntry e01 = (MyStorageEntry) codebase.getType(e00).deepClone(e00);
		e01.quantity = 24;
		e01.longitude = 240.8979;

		BeType type = codebase.getType(e00);

		Writer writer = new PrintWriter(System.out);
		type.deepCompare(e00, e01, writer);
		writer.close();

		
		

		System.out.println(codebase);
		
		BeField field = type.getField("quantity");
		if(field.hasDiff(e00, e01)) {
			System.out.println(field.produceDiff(e01).toString());
		}
		
		
		System.out.close();
		
	}
	

}
