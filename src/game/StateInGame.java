package game;

import net.PacketBuilder;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;


public class StateInGame extends BasicGameState{
	private int stateId = -1;
	
	
	public StateInGame(int _id){
		stateId = _id;
	}
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) 
			throws SlickException {
		System.out.println("Entered game state");
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		Common.getMapManagerSt().render(container, g);
		Common.getEntityManagerSt().render(g, container);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		Common.getSpriteManagerSt().update();
		
		PlayerDriver plDriver = Common.getPlayerDriverSt();
		
		if(plDriver.update(delta)){
			//If player was moved, move map camera
			Common.getMapManagerSt().movePlayer(plDriver.getX(), plDriver.getY(), container);
			PacketBuilder pBuild = new PacketBuilder();
			pBuild.writeInt(666);
			pBuild.writeInt(plDriver.getX());
			pBuild.writeInt(plDriver.getY());
			Common.getSocketSt().send(pBuild.getPacket(), true);
		}
	}

	public void keyPressed(int key, char c){
		Common.getPlayerDriverSt().handleKeyDown(key);
	}

	public void keyReleased(int key, char c){
		Common.getPlayerDriverSt().handleKeyUp(key);
	}
	
	@Override
	public int getID() { return stateId; }

}
