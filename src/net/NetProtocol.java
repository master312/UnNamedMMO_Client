package net;

import entities.Player;
import game.Common;

public class NetProtocol {
	
	public static void clLogin(String username, String password){
		PacketBuilder pb = new PacketBuilder();
		pb.writeShort(OpCodes.CL_LOGIN);
		pb.writeString(username);
		pb.writeString(password);
		Common.getSocketSt().send(pb.getPacket(), true);
	}
	
	public static void clCreateCharacter(Player character){
		Common.getSocketSt().send(character, true);
	}

	/* Sends enter world packet */
	public static void clEnterWorld(int charId){
		PacketBuilder pb = new PacketBuilder();
		pb.writeShort(OpCodes.CL_ENTER_WORLD);
		pb.writeInt(charId);
		Common.getSocketSt().send(pb.getPacket(), true);
	}
	
	/* Sends move update to server */
	public static void clMove(short direction){
		PacketBuilder pb = new PacketBuilder();
		pb.writeShort(OpCodes.CL_MOVE);
		pb.writeShort(direction);
		Common.getSocketSt().send(pb.getPacket(), true);
	}

}
