package game;

import game.KeyBinds.KeyAction;
import entities.Player;
import entities.Entity.Direction;

/* This class handles user input, and character controll */
public class PlayerDriver {
	
	/* In what direction is player currently going */
	private Direction dir = Direction.NORTH;
	private boolean isMoving = false;
	private boolean keyDown[] = new boolean[4];
	private Player entity = null;
	
	public PlayerDriver(){
		keyDown[0] = keyDown[1] = keyDown[2] = keyDown[3] = false;
	}
	
	public void handleKeyDown(int key){
		if(Common.isKeyAction(key, KeyAction.PLAYER_LEFT)){
			keyDown[3] = true;
		}else if(Common.isKeyAction(key, KeyAction.PLAYER_RIGHT)){
			keyDown[1] = true;
		}
		if(Common.isKeyAction(key, KeyAction.PLAYER_UP)){
			keyDown[0] = true;
		}else if(Common.isKeyAction(key, KeyAction.PLAYER_DOWN)){
			keyDown[2] = true;
		}
		calculateDirection();
	}
	
	public void handleKeyUp(int key){
		if(Common.isKeyAction(key, KeyAction.PLAYER_LEFT)){
			keyDown[3] = false;
		}else if(Common.isKeyAction(key, KeyAction.PLAYER_RIGHT)){
			keyDown[1] = false;
		}
		if(Common.isKeyAction(key, KeyAction.PLAYER_UP)){
			keyDown[0] = false;
		}else if(Common.isKeyAction(key, KeyAction.PLAYER_DOWN)){
			keyDown[2] = false;
		}
		calculateDirection();
	}

	/* Returns true if player was moved */
	public boolean update(int delta){
		boolean toReturn = false;
		if(entity == null)
			return toReturn;
		if(isMoving){
			toReturn = true;
			//System.out.println("asdasd");
			int speed = entity.getCurrentSpeed();
			switch(dir){
			case NORTH:
				entity.move(0, -speed);
				break;
			case NORTHEAST:
				entity.move(speed, -speed);
				break;
			case EAST:
				entity.move(speed, 0);
				break;
			case SOUTHEAST:
				entity.move(speed, speed);
				break;
			case SOUTH:
				entity.move(0, speed);
				break;
			case SOUTHWEST:
				entity.move(-speed, speed);
				break;
			case WEST:
				entity.move(-speed, 0);
				break;
			case NORTHWEST:
				entity.move(-speed, -speed);
				break;
			}
		}
		return toReturn;
	}
	
	public void calculateDirection(){
		isMoving = true;
		if(keyDown[0]){	//Up key is down
			if(keyDown[1]){	//Right key is down
				dir = Direction.NORTHEAST;
			}else if(keyDown[3]){ //Left key is down
				dir = Direction.NORTHWEST;
			}else{
				dir = Direction.NORTH;
			}
		}else if(keyDown[2]){	//Down key is down
			if(keyDown[1]){	//Right key is down
				dir = Direction.SOUTHEAST;
			}else if(keyDown[3]){ //Left key is down
				dir = Direction.SOUTHWEST;
			}else{
				dir = Direction.SOUTH;
			}
		}else if(keyDown[1]){	//Right key is down
			dir = Direction.EAST;
		}else if(keyDown[3]){	//Left key is down
			dir = Direction.WEST;
		}else{	//None key is down
			isMoving = false;
		}
	}

	public Direction getDir() {
		return dir;
	}

	public void setDir(Direction dir) {
		this.dir = dir;
	}

	public boolean isMoving() {
		return isMoving;
	}

	public void setMoving(boolean isMoving) {
		this.isMoving = isMoving;
	}

	public boolean[] getKeyDown() {
		return keyDown;
	}

	public void setKeyDown(boolean[] keyDown) {
		this.keyDown = keyDown;
	}

	public Player getEntity() {
		return entity;
	}

	public void setEntity(Player entity) {
		this.entity = (Player)Common.get().initEntityGraphics(entity);
	}
	
	public int getX(){
		return entity.getLocX();
	}
	
	public int getY(){
		return entity.getLocY();
	}
}
