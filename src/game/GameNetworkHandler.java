package game;

import org.newdawn.slick.util.Log;

import entities.Entity;
import net.ClientSocket;
import net.OpCodes;
import net.Packet;

/* This class handles networking inGame related networking stuff */
public class GameNetworkHandler {
	
	private ClientSocket sock;
	
	private class EntityUpdates{
		public static final short POSITION = 1;
		public static final short DIRECTION = 2;
	}
	
	public GameNetworkHandler(){
	}
	
	/* Handle incoming packets */
	public void handleIncoming(){
		sock = Common.getSocketSt();
		while(true){
			Packet pack = sock.getPacket();
			if(pack != null){
				handlePacket(pack);
			}else{
				break;
			}
		}
		while(true){
			Entity ent = sock.getEntity();
			if(ent != null){
				handleNewEntity(ent);
			}else{
				break;
			}
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
			//TODO: Request entity form server
			return;
		}
		switch(pack.readShort()){
		case EntityUpdates.POSITION:
			tmpE.setLocX((int)pack.readInt());
			tmpE.setLocY((int)pack.readInt());
			break;
		case EntityUpdates.DIRECTION:
			break;
		}
	}
	
	private void handleNewEntity(Entity e){
		EntityManager em = Common.getEntityManagerSt();
		em.addEntity(e);
	}
	
}
