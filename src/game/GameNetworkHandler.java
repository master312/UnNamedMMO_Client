package game;

import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import org.newdawn.slick.util.Log;

import entities.Entity;
import entities.Pawn;
import map.MapChunk;
import map.MapManager;
import map.Tile;
import net.ClientSocket;
import net.NetProtocol;
import net.OpCodes;
import net.Packet;

/* This class handles networking inGame related networking stuff */
public class GameNetworkHandler {
	private ClientSocket sock;
	
	/* ID's for entity stat update packet */
	private class EntityUpdates{
		public static final short POSITION = 1;
		public static final short DIRECTION = 2;
		public static final short POS_DIR = 3;
	}
	
	public GameNetworkHandler(){
		sock = Common.getSocketSt();
	}
	
	/* Wait and handle world size packet
	 * If returns false, packet was never received */
	public boolean handleWorldSize(){
		for(int i = 0; i < 10; i++){
			Packet tmpPack =sock.getPacket();
			if(tmpPack != null){
				if(tmpPack.readShort() == OpCodes.SR_WORLD_SIZE){
					int worldW = tmpPack.readInt();
					int worldH = tmpPack.readInt();
					Common.getMapManagerSt().setChunkWidth(tmpPack.readInt());
					Common.getMapManagerSt().setChunkHeight(tmpPack.readInt());
					Common.getMapManagerSt().initWorld(worldW, worldH);
					return true;
				}
			}
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	/* Handle incoming packets */
	public void handleIncoming(){
		while(true){
			Packet pack = sock.getPacket();
			if(pack == null){
				break;
			}
			handlePacket(pack);
		}
		while(true){
			Entity ent = sock.getEntity();
			if(ent == null){
				break;
			}
			handleNewEntity(ent);
		}
	}
	
	private void handlePacket(Packet pack){
		int opCode = pack.readShort();
		switch(opCode){
		case OpCodes.SR_PAWN_UPDATE:
			handleEntityUpdate(pack);
			break;
		case OpCodes.SR_ENT_REMOVE:
			Common.getEntityManagerSt().removeEntity(pack.readInt());
			break;
		case OpCodes.SR_TEXT_MSG:
			handleChat(pack);
			break;
		case OpCodes.SR_MAP_CHUNK:
			handleNewMapChunk(pack);
			break;
		default:
			Log.warn("GameNetworkHandler: Invalid opcode " + opCode);
			break;
		}
	}
	
	private void handleEntityUpdate(Packet pack){
		int tmpId = pack.readInt();
		Entity tmpE = null;
		if(tmpId == Common.getPlayerDriverSt().getEntity().getId()){
			//Update is received for self
			tmpE = Common.getPlayerDriverSt().getEntity();
		}else{
			tmpE = Common.getEntityManagerSt().getEntity(tmpId);
		}
		if(tmpE == null){
			//Request pawn info from server
			NetProtocol.clRequestPawn(tmpId);
			return;
		}
		if(tmpE.isPawn()){
			((Pawn) tmpE).setUpdated();
		}
		switch(pack.readShort()){
		case EntityUpdates.POSITION:
			tmpE.setLocX((int)pack.readInt());
			tmpE.setLocY((int)pack.readInt());
			break;
		case EntityUpdates.DIRECTION:
			break;
		case EntityUpdates.POS_DIR:
			tmpE.setLocX((int)pack.readInt());
			tmpE.setLocY((int)pack.readInt());
			tmpE.setNetDir(pack.readShort());
			break;
		}
	}
	
	/* Handles creation of new entity */
	private void handleNewEntity(Entity e){
		EntityManager em = Common.getEntityManagerSt();
		em.addEntity(e);
	}
	
	/* Handle incoming chat message */
	private void handleChat(Packet pack){
		short type = pack.readShort();
		String sender = pack.readString();
		String msg = pack.readString();
		Common.getChatBoxSt().push(sender + ": " + msg);
	}
	
	private void handleNewMapChunk(Packet pack){
	    /* Decompress chunk data */
		Inflater decompresser = new Inflater();
		decompresser.setInput(pack.data, 2, pack.data.length - 2);
		MapManager m = Common.getMapManagerSt();
		int chunkInBytes = (m.getChunkWidth() * m.getChunkWidth()) * 
							((Tile.BOTTOM_LAYERS + Tile.TOP_LAYERS + 1) * 2) + 20;
		byte[] result = new byte[chunkInBytes];
		try {
			int resultLength = decompresser.inflate(result);
		} catch (DataFormatException e) {
			e.printStackTrace();
		}
		decompresser.end();
	    /* Create chunk */
		Packet tmpPack = new Packet();
		tmpPack.data = result;
		m.createChunk(tmpPack);
	}
}
