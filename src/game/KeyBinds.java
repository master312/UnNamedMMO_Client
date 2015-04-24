package game;

import org.newdawn.slick.Input;

public class KeyBinds {
	public enum KeyAction{
		PLAYER_LEFT, PLAYER_RIGHT, PLAYER_UP, PLAYER_DOWN
	}
	/* Number of elements in KeyActions enum */
	int actionsNum = 4;
	
	int binds[] = new int[actionsNum];
	
	public KeyBinds(){
		binds[getOrdinal(KeyAction.PLAYER_LEFT)] = Input.KEY_LEFT;
		binds[getOrdinal(KeyAction.PLAYER_RIGHT)] = Input.KEY_RIGHT;
		binds[getOrdinal(KeyAction.PLAYER_UP)] = Input.KEY_UP;
		binds[getOrdinal(KeyAction.PLAYER_DOWN)] = Input.KEY_DOWN;
	}
	
	private int getOrdinal(KeyAction action){
		return action.ordinal();
	}
	
	public boolean isAction(int key, KeyAction action){
		return binds[getOrdinal(action)] == key;
	}
	
}
