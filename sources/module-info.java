/**
 * 
 */
/**
 * @author pierreconvert
 *
 */
module com.s8.core.bohr.beryllium {
	

	/* <beryllium> */

	exports com.s8.core.bohr.beryllium.branch;
	
	exports com.s8.core.bohr.beryllium.codebase;
	
	exports com.s8.core.bohr.beryllium.exception;
	
	exports com.s8.core.bohr.beryllium.fields;
	exports com.s8.core.bohr.beryllium.fields.primitives;
	exports com.s8.core.bohr.beryllium.fields.arrays;
	exports com.s8.core.bohr.beryllium.fields.objects;

	exports com.s8.core.bohr.beryllium.object;
	
	exports com.s8.core.bohr.beryllium.syntax;
	
	exports com.s8.core.bohr.beryllium.types;
	
	exports com.s8.core.bohr.beryllium.utilities;
	
	
	/* </beryllium> */
	
	requires transitive com.s8.api;
	requires transitive com.s8.core.bohr.atom;
	requires transitive com.s8.core.io.bytes;
	
}