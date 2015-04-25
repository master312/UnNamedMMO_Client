package net;

import entities.Player;
import game.Common;

public class NetProtocol {
	
	public static void clLogin(String username, String password){
		PacketBuilder pb = new PacketBuilder();
		pb.writeShort(OpCodes.CL_LOGIN);
		pb.writeString(username);
		pb.writeString(password);
		Packet pack = pb.getPacket();
		Common.getSocketSt().send(pack, true);
		pack.clear();
	}
	
	public static void clCreateCharacter(Player character){
		Common.getSocketSt().send(character, true);
	}

	/* Sends enter world packet */
	public static void clEnterWorld(int charId){
		PacketBuilder pb = new PacketBuilder();
		pb.writeShort(OpCodes.CL_ENTER_WORLD);
		pb.writeInt(charId);
		Packet pack = pb.getPacket();
		Common.getSocketSt().send(pack, true);
		pack.clear();
	}
	
	/* Sends move update to server */
	public static void clMove(short direction){
		PacketBuilder pb = new PacketBuilder();
		pb.writeShort(OpCodes.CL_MOVE);
		pb.writeShort(direction);
		Packet pack = pb.getPacket();
		Common.getSocketSt().send(pack, true);
		pack.clear();
	}

}
