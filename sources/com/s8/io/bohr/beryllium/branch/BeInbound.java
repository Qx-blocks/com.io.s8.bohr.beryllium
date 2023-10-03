package com.s8.io.bohr.beryllium.branch;

import static com.s8.api.bohr.BOHR_Keywords.CLOSE_JUMP;
import static com.s8.api.bohr.BOHR_Keywords.CLOSE_SEQUENCE;
import static com.s8.api.bohr.BOHR_Keywords.CREATE_NODE;
import static com.s8.api.bohr.BOHR_Keywords.DECLARE_TYPE;
import static com.s8.api.bohr.BOHR_Keywords.DEFINE_JUMP_COMMENT;
import static com.s8.api.bohr.BOHR_Keywords.DEFINE_JUMP_TIMESTAMP;
import static com.s8.api.bohr.BOHR_Keywords.EXPOSE_NODE;
import static com.s8.api.bohr.BOHR_Keywords.OPEN_JUMP;
import static com.s8.api.bohr.BOHR_Keywords.OPEN_SEQUENCE;
import static com.s8.api.bohr.BOHR_Keywords.REMOVE_NODE;
import static com.s8.api.bohr.BOHR_Keywords.UPDATE_NODE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.s8.api.bohr.BOHR_Keywords;
import com.s8.api.bytes.ByteInflow;
import com.s8.io.bohr.beryllium.codebase.BeCodebase;
import com.s8.io.bohr.beryllium.exception.BeIOException;
import com.s8.io.bohr.beryllium.fields.BeFieldDelta;
import com.s8.io.bohr.beryllium.fields.BeFieldParser;
import com.s8.io.bohr.beryllium.object.CreateBeObjectDelta;
import com.s8.io.bohr.beryllium.object.ExposeBeObjectDelta;
import com.s8.io.bohr.beryllium.object.RemoveBeObjectDelta;
import com.s8.io.bohr.beryllium.object.UpdateBeObjectDelta;
import com.s8.io.bohr.beryllium.types.BeType;
import com.s8.io.bohr.beryllium.types.BeTypeParser;


/**
 * 
 *
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
public class BeInbound {
	
	public interface DeltaConsumer {
		
		public void onNewDelta(BeBranchDelta delta) throws IOException;
	}
	
	

	/**
	 * code base
	 */
	private final BeCodebase codebase;



	/**
	 * 
	 */
	private final Map<Long, BeTypeParser> mapByCode;



	private final Map<String, BeTypeParser> mapByVertex = new HashMap<>();



	private long version;

	/**
	 * 
	 * @param codebase
	 * @param graph
	 * @param isVerbose
	 */
	public BeInbound(BeCodebase codebase) {
		super();
		this.codebase = codebase;
		mapByCode = new HashMap<Long, BeTypeParser>();
	}



	/**
	 * 
	 * @param inflow
	 * @throws IOException
	 */
	public void pullFrame(ByteInflow inflow, DeltaConsumer consumer) throws IOException {

		// check opening
		if(!inflow.matches(BOHR_Keywords.FRAME_HEADER)) { throw new IOException("DO NOT MATCH HEADER"); }
		parseSequence(inflow, consumer);
		if(!inflow.matches(BOHR_Keywords.FRAME_FOOTER)) { throw new IOException("DO NOT MATCH FOOTER"); }
	}
	
	/**
	 * 
	 * @param inflow
	 * @throws IOException
	 */
	private void parseSequence(ByteInflow inflow, DeltaConsumer consumer) throws IOException {

		int code;

		if((code = inflow.getUInt8()) != OPEN_SEQUENCE) {
			throw new IOException("Sequence mist startwith an open sequecne tag");
		}

		while((code = inflow.getUInt8()) != CLOSE_SEQUENCE) {
			switch(code) {

			case OPEN_JUMP: parseBranchDelta(inflow, consumer); break;

			default : throw new IOException("Unsupported BOHR keyword code: "+Integer.toHexString(code));
			}
		}
	}

	/**
	 * 
	 * @param inflow
	 * @throws IOException
	 */
	public BeTypeParser onDeclareType(ByteInflow inflow) throws IOException {

		/* retrieve name of this newly declared type */
		String typeName = inflow.getStringUTF8();

		/* retrieve code assigned to this newly declared type */
		long typeCode = inflow.getUInt7x();

		/* check that this type code has not already been assigned */
		if(mapByCode.containsKey(typeCode)) {
			throw new BeIOException("A type has already defined for code: "+typeCode+"->"+typeName
					+". See "+mapByCode.get(typeCode).print(typeCode));
		}
		
		/* find corresponding type */
		BeType type = codebase.getTypeBySerialName(typeName);
		if(type == null) {
			throw new BeIOException("Failed to find type for name: "+typeName);
		}

		/* create typeInflow*/
		BeTypeParser typeParser = new BeTypeParser(type);

		/* store this typeInflow for later use */
		mapByCode.put(typeCode, typeParser);
		//typeInflowsByClass.put(type.getRuntimeName(), typeInflow);

		return typeParser;
	}


	/**
	 * 
	 * @param code
	 * @return
	 * @throws NdIOException
	 */
	public BeTypeParser getTypeParserByCode(long code) throws BeIOException {
		BeTypeParser typeParser = mapByCode.get(code);
		if(typeParser == null) {
			throw new BeIOException("Failed to find typeInflow for code: "+Long.toHexString(code));
		}
		return typeParser;
	}	



	
	/**
	 * 
	 * @param graph
	 * @param inflow
	 * @throws IOException
	 */
	private void parseBranchDelta(ByteInflow inflow, DeltaConsumer consumer) throws IOException {


		int code;

		long version = inflow.getUInt64();
		
		BeBranchDelta delta = new BeBranchDelta(version);

		while((code = inflow.getUInt8()) != CLOSE_JUMP) {
			switch(code) {
			
			case DEFINE_JUMP_COMMENT: onDefineComment(inflow.getStringUTF8(), delta); break;
			case DEFINE_JUMP_TIMESTAMP: onDefineTimestamp(inflow.getUInt64(), delta); break;

			case DECLARE_TYPE: onDeclareType(inflow); break;
			
			case CREATE_NODE: onCreateNode(inflow, delta); break;
			case UPDATE_NODE: onUpdateNode(inflow, delta); break;
			case EXPOSE_NODE: onExposeNode(inflow, delta); break;
			case REMOVE_NODE: onRemoveNode(inflow, delta); break;

			default : throw new IOException("Unsupported BOHR keyword code: "+Integer.toHexString(code));
			}
		}

		consumer.onNewDelta(delta);
	}

	

	public class N6ObjectListener implements BeTypeParser.DeltaListener {

		private List<BeFieldDelta> deltas;
		
		public N6ObjectListener(List<BeFieldDelta> deltas) {
			super();
			this.deltas = deltas;
		}

		@Override
		public void onFieldValueChange(BeFieldParser parser, ByteInflow inflow) throws IOException {

			/* read value */
			deltas.add(parser.deserializeDelta(inflow));
		}

	}



	
	
	private void onDefineComment(String comment, BeBranchDelta delta) throws BeIOException {
		delta.setComment(comment);
	}

	
	private void onDefineTimestamp(long timestamp, BeBranchDelta delta) throws BeIOException {
		delta.setTimestamp(timestamp);
	}
	
	
	private void onCreateNode(ByteInflow inflow, BeBranchDelta delta) throws IOException {

		if(version < 0) { throw new IOException("Version is not defined"); }
		
		long typeCode = inflow.getUInt7x();
		
		BeTypeParser typeParser = getTypeParserByCode(typeCode);
		
		String index = inflow.getStringUTF8();
		
		/* create vertex */
		mapByVertex.put(index, typeParser);

		// object delta
		CreateBeObjectDelta objectDelta = new CreateBeObjectDelta(index, typeParser.getType(), new ArrayList<BeFieldDelta>());
		
		N6ObjectListener objectListener = new N6ObjectListener(objectDelta.deltas);

		// type parser
		typeParser.parse(inflow, objectListener);

		delta.objectDeltas.add(objectDelta);
	}

	


	private void onUpdateNode(ByteInflow inflow, BeBranchDelta delta) throws IOException {

		/* index */
		String index = inflow.getStringUTF8();
		
		/* retrieve vertex */
		BeTypeParser typeParser = mapByVertex.get(index);
		
		if(typeParser == null) { throw new IOException("Out of context exception"); }


		/* create update object delta */
		UpdateBeObjectDelta objectDelta = new UpdateBeObjectDelta(index, typeParser.type, new ArrayList<BeFieldDelta>());

		/* listener */
		N6ObjectListener objectListener = new N6ObjectListener(objectDelta.deltas);
		
		// type parser
		typeParser.parse(inflow, objectListener);
		
		/* append */
		delta.appendObjectDelta(objectDelta);
	}


	

	/**
	 * 
	 * @param inflow
	 * @throws IOException
	 */
	private void onExposeNode(ByteInflow inflow, BeBranchDelta delta) throws IOException {
		
		/* index */
		String index = inflow.getStringUTF8();

		/* retrieve slot */
		int slot = inflow.getUInt8();

		delta.appendObjectDelta(new ExposeBeObjectDelta(index, slot));
	}
	
	
	/**
	 * 
	 * @param inflow
	 * @throws IOException
	 */
	private void onRemoveNode(ByteInflow inflow, BeBranchDelta delta) throws IOException {
		/* index */
		String index = inflow.getStringUTF8();

		delta.appendObjectDelta(new RemoveBeObjectDelta(index));
	}

}
