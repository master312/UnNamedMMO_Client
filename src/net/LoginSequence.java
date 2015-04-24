package net;

import game.Common;

/* TODO: Comment */
public class LoginSequence {
	public enum LoginState{
		NOTHING, LOGIN_SEND, LOGIN_OK, LOGIN_FAIL
	}
	
	private LoginState loginState = LoginState.NOTHING;
	
	/* Login sequence will start at the moment class is created */
	public LoginSequence(String username, String password){
		NetProtocol.clLogin(username, password);
		loginState = LoginState.LOGIN_SEND;
	}
	
	public LoginState update(){
		Packet pack = Common.getSocketSt().getPacket();
		if(pack != null){
			if(pack.readShort() == OpCodes.SR_LOGIN_STATUS){
				//Login status packet received
				switch(pack.readShort()){
				case 1:
					//Login ok
					loginState = LoginState.LOGIN_OK;
					break;
				case 2:
					//Login fail
					loginState = LoginState.LOGIN_FAIL;
					break;
				}
			}
		}
		return loginState;
	}
}
