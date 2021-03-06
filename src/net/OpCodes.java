package net;

/* SR_xxx = Server to client OpCodes 
 * CL_xxx = Client to server OpCodes */
public class OpCodes {
	/* ********************** Server to client OpCodes ***********************/
	
	/* Disconnects client from server, and let him know reason 
	 * SR_KICK (short)reasonCode */
	public static final short SR_KICK = 0x0000;
	/* Lets client know that server is ready to receive login informations*/
	public static final short SR_LOGIN_READY = 0x0001;
	/* Lets client know if login is OK, or FAIL
	 * SR_LOGIN_STATUS (short) 1 = OK, 2 = Fail, 3 = ban ... */
	public static final short SR_LOGIN_STATUS = 0x0002;
	/* Number of characters on account
	 * SR_CHAR_COUNT (short)num */
	public static final short SR_CHAR_COUNT = 0x0003;
	/* Character creation status 
	 * SR_CHAR_CREATE_STATUS (short)statusId 1=OK, 2=Fail...*/
	public static final short SR_CHAR_CREATE_STATUS = 0x0004;
	/* Update pawn stat 
	 * SR_PAWN_UPDATE (int)entityId (short)statId (...)stat */
	public static final short SR_PAWN_UPDATE = 0x0005;
	/* Remove entity from player sight 
	 * SP_ENT_REMOVE (int)entetyId*/
	public static final short SR_ENT_REMOVE = 0x0006;
	/* Sends world size(in chunks) to client 
	 * SR_WORLD_SIZE (int)worldWidth (int)worldHeight (int)chunkWidht (int)cHeight*/
	public static final short SR_WORLD_SIZE = 0x0007;
	/* Sends text message to player. Used for chat messages 
	 * SR_TEXT_MSG (short)type (string)sender (String)message */
	public static final short SR_TEXT_MSG = 0x0008;
	/* Sends map chunk to client */
	public static final short SR_MAP_CHUNK = 0x0009;
	
	/* ********************* Client to server OpCodes ************************/
	
	/* Sends login info to server
	 * CL_LOGIN (str)UserName (str)Password */
	public static final short CL_LOGIN = 0x0001;
	/* Sends character id selected for entering game 
	 * CL_ENTER_WORLD (int)charId*/
	public static final short CL_ENTER_WORLD = 0x0002;
	/* Lets server know that client has moved 
	 * CL_MOVE (short)Dir 0=up;2=right;4=down;6=left*/
	public static final short CL_MOVE = 0x0003;
	/* Request pawn info 
	 * Example: Used when receive SR_PAWN_UPDATE, but no pawn with that id
	 * is on client's entities list 
	 * CL_REQUEST_PAWN (int)pawnId*/
	public static final short CL_REQUEST_PAWN = 0x0004;
	/* Request map chunk 
	 * CL_REQUEST_MAP (int)chunkX (int)chunkY */
	public static final short CL_REQUEST_MAP = 0x0005;
	/* Client sends text message 
	 * CL_CHAT_MSG (short)msgType (string) message*/
	public static final short CL_CHAT_MSG = 0x0006;
}
