/**
 * 
 */
/**
 * @author pierreconvert
 *
 */
module com.s8.io.bohr.beryllium {
	

	/* <beryllium> */
	exports com.s8.io.bohr.beryllium.codebase;
	
	exports com.s8.io.bohr.beryllium.fields;
	exports com.s8.io.bohr.beryllium.fields.primitives;
	exports com.s8.io.bohr.beryllium.fields.arrays;
	exports com.s8.io.bohr.beryllium.fields.objects;

	exports com.s8.io.bohr.beryllium.object;
	exports com.s8.io.bohr.beryllium.syntax;
	exports com.s8.io.bohr.beryllium.tables;
	exports com.s8.io.bohr.beryllium.types;
	/* </beryllium> */
	
	requires transitive com.s8.io.bohr.atom;
	requires transitive com.s8.io.bytes;
	
}