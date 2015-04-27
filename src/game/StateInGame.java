package game;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;


public class StateInGame extends BasicGameState{
	private int stateId = -1;
	private GameNetworkHandler netHandler;
	
	public StateInGame(int _id){
		stateId = _id;
	}
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		netHandler = new GameNetworkHandler();
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) 
			throws SlickException {
		//Wait here until receive world size packet
		if(netHandler.handleWorldSize()){
			Log.info("Entered in-game state");
			return;
		}
		Log.error("World size packet not received from server. Quiting program");
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		
		Common.getMapManagerSt().render(g);
		EntityManager em = Common.getEntityManagerSt();
		em.render(g);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		netHandler.handleIncoming();			//Handle incoming data
		Common.getSpriteManagerSt().update();	//Update sprite manager
		Common.getEntityManagerSt().update(delta);
		PlayerDriver plDriver = Common.getPlayerDriverSt();
		
		if(plDriver.update(delta)){
			//If player was moved, move map camera
			Common.getMapManagerSt().movePlayer(plDriver.getX(), plDriver.getY());
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
