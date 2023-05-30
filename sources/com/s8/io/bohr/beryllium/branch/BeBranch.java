package com.s8.io.bohr.beryllium.branch;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import com.s8.arch.fluor.S8Filter;
import com.s8.io.bohr.beryllium.codebase.BeCodebase;
import com.s8.io.bohr.beryllium.exception.BeIOException;
import com.s8.io.bohr.beryllium.fields.BeField;
import com.s8.io.bohr.beryllium.fields.BeFieldDelta;
import com.s8.io.bohr.beryllium.object.BeObject;
import com.s8.io.bohr.beryllium.object.CreateBeObjectDelta;
import com.s8.io.bohr.beryllium.object.RemoveBeObjectDelta;
import com.s8.io.bohr.beryllium.object.UpdateBeObjectDelta;
import com.s8.io.bohr.beryllium.types.BeType;


/**
 * 
 * @author pierreconvert
 *
 */
public class BeBranch {


	public final BeCodebase codebase;


	/**
	 * last known state of table
	 */
	public final BeTable table = new BeTable();


	private long version;

	private BeBranchDelta branchDelta = null;



	/**
	 * 
	 * @param codebase
	 */
	public BeBranch(BeCodebase codebase) {
		super();
		this.codebase = codebase;
	}



	/**
	 * 
	 * @param delta
	 * @throws BeIOException
	 */
	public void pushDelta(BeBranchDelta delta) throws BeIOException {
		delta.consume(table);
	}

	public void pushDelta(List<BeBranchDelta> deltas) throws BeIOException {
		for(BeBranchDelta delta : deltas) { delta.consume(table); }
	}


	/**
	 * 
	 * @param object
	 * @throws BeIOException
	 */
	public void set(BeObject object) throws BeIOException {
		if(branchDelta == null) {
			branchDelta = new BeBranchDelta(version);
		}


		String id = object.S8_key;


		BeType type = codebase.getType(object);

		BeObject previous = table.objects.get(id);
		if(previous != null) {
			BeType previousType = codebase.getType(previous);


			/* REMOVE and CREATE */
			if(type != previousType) {
				publishRemove(id);
				publishCreate(id, type, object);
			}

			/* UPDATE object */
			else {
				publishUpdate(id, type, previous, object);


			}
		}
		else { /* only CREATE */
			publishCreate(id, type, object);
		}

		BeObject objectClone = type.deepClone(object);

		table.objects.put(id, objectClone);
	}


	private void publishCreate(String id, BeType type, BeObject object) throws BeIOException {
		List<BeFieldDelta> fieldDeltas = new ArrayList<BeFieldDelta>();
		BeField[] fields = type.fields;
		int n = fields.length;
		for(int i=0; i<n; i++) {
			try {
				fieldDeltas.add(fields[i].produceDiff(object));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new BeIOException(e.getMessage());
			}
		}
		branchDelta.objectDeltas.add(new CreateBeObjectDelta(id, type, fieldDeltas));
	}



	private void publishUpdate(String id, BeType type, BeObject previous, BeObject object) throws BeIOException {
		boolean hasDelta = false;

		List<BeFieldDelta> fieldDeltas = new ArrayList<BeFieldDelta>();
		BeField[] fields = type.fields;
		int n = fields.length;
		BeField field;
		for(int i=0; i<n; i++) {

			try {
				if((field = fields[i]).hasDiff(previous, object)) {
					if(!hasDelta) {
						hasDelta = true;
					}
					fieldDeltas.add(field.produceDiff(object));
				}
			} 
			catch (IllegalArgumentException | IllegalAccessException e) {
				throw new BeIOException(e.getMessage());
			}
		}

		if(hasDelta) {
			branchDelta.objectDeltas.add(new UpdateBeObjectDelta(id, type, fieldDeltas));
		}
	}

	private void publishRemove(String id) {
		branchDelta.objectDeltas.add(new RemoveBeObjectDelta(id));
	}


	/**
	 * 
	 * @return
	 */
	public List<BeBranchDelta> pullDeltas() {

		List<BeBranchDelta> sequence = new ArrayList<>();
		sequence.add(branchDelta);
		branchDelta = null;

		return sequence;
	}

	
	
	


	/**
	 * 
	 * @param id
	 * @return
	 * @throws BeIOException
	 */
	public boolean hasEntry(String id) throws BeIOException {
		return table.objects.get(id) != null;
	}

	
	

	/**
	 * 
	 * @param id
	 * @return
	 * @throws BeIOException
	 */
	public BeObject get(String id) throws BeIOException {
		BeObject origin = table.objects.get(id);


		if(origin != null) {
			BeType type = codebase.getType(origin);

			BeObject object = type.deepClone(origin);

			return object;
		}
		else {
			return null;
		}
	}



	/**
	 * 
	 * @param id
	 * @return
	 * @throws BeIOException
	 */
	public void put(String id, BeObject object) throws BeIOException {
		if(object == null) {
			throw new BeIOException("NULL : Null objects are not allowed in beryllium paradigm");
		}
		
		BeType type = codebase.getType(object);
		BeObject clone = type.deepClone(object);
		table.objects.put(id, clone);	
	}
	
	
	@SuppressWarnings("unchecked")
	public <T> List<T> select(S8Filter<T> filter) throws BeIOException{
		if(filter == null) { throw new BeIOException("NULL : Filter must be defined"); }
		
		List<T> selection = new ArrayList<T>();
		table.objects.forEach((key, value) -> {
			
			if(filter.isSelected((T) value)) {
				try {
					// type
					BeType type = codebase.getType(value);

					// object
					BeObject object = type.deepClone(value);
					
					// item
					selection.add((T) object);
					
				} 
				catch (BeIOException e) {
					e.printStackTrace();
				}
			}
		});
		
		/* selection */
		return selection;
	}
	


	/**
	 * 
	 * @param id
	 * @return
	 * @throws BeIOException
	 */
	public void remove(String id) throws BeIOException {
		table.objects.remove(id);
	}



	/**
	 * 
	 * @param consumer
	 */
	public void forEach(Consumer<BeObject> consumer) {
		table.objects.forEach((key, object) -> {

			BeType type = codebase.getType(object);

			try {
				consumer.accept(type.deepClone(object));
			} 
			catch (BeIOException e) {
				e.printStackTrace();
			}
		});
	}


	public Set<String> getKeySet(){
		return table.objects.keySet();
	}

}
