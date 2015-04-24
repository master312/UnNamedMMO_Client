package game;

import java.util.ArrayList;

import net.ClientSocket;
import net.NetProtocol;
import net.OpCodes;
import net.Packet;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import com.esotericsoftware.minlog.Log;

import entities.Entity;
import entities.Player;
import gui.ActionHandler;
import gui.Button;

public class StateCharacterScreen extends BasicGameState{
	private int stateId = -1;
	/* Number of characters on this account */
	private int charCount;
	private ArrayList<Player> characters = new ArrayList<Player>();
	private Button buttons[];
	
	
	public StateCharacterScreen(int _stateId){
		stateId = _stateId;
		charCount = -1;
		buttons = new Button[5];
	}
	
	public void enter(GameContainer container, StateBasedGame game) 
			throws SlickException {
		charCount = -1;
		characters.clear();
	}
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		final StateBasedGame g = game;
		buttons[0] = new Button("Create 1", container, null, 100, 400, 80, 30);
		buttons[1] = new Button("Create 2", container, null, 200, 400, 80, 30);
		buttons[2] = new Button("Create 3", container, null, 300, 400, 80, 30);
		buttons[3] = new Button("Create 4", container, null, 400, 400, 80, 30);
		buttons[4] = new Button("Create 5", container, null, 500, 400, 80, 30);
		for(int i = 0; i < 5; i++){
			buttons[i].setActionHandler(new ActionHandler(){
				public void onAction(){
					g.enterState(stateId + 1, new FadeOutTransition(), 
							new FadeInTransition());
				}
			});
		}
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		g.setColor(Color.white);
		g.drawString("Characters screen is up!", 150, 150);
		
		for(int i = 0; i < 5; i++){
			buttons[i].render(container, g);
		}
		for(int i = 0; i < characters.size(); i++){
			Player tmpChar = characters.get(i);
			tmpChar.getAnimation().draw((i * 100) + 120, 350);
			g.setColor(Color.white);
			g.drawString(tmpChar.getName(), (i * 100) + 100, 320);
		}
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		ClientSocket sock = Common.getSocketSt();
		if(!sock.isConnected()){
			Log.info("Lost connection to server");
			game.enterState(stateId - 1, new FadeOutTransition(), 
							new FadeInTransition());
			return;
		}
		if(charCount < 0){
			//Character count packet not received yet
			Packet tmpPack = sock.getPacket();
			if(tmpPack != null){
				short tt = tmpPack.readShort();
				if(tt == OpCodes.SR_CHAR_COUNT){
					charCount = (int)tmpPack.readShort();
					if(charCount == 0)
						return;
					for(int i = 0; i < charCount; i++){
						buttons[i].setText("Select");
					}
					Log.info("Characters count: " + charCount);
				}else{
					Log.error("01 Invalid packet received from server");
				}
			}
		}else if(charCount != characters.size()){
			//Not all character objects are received yet
			Entity tmpE = sock.getEntity();
			if(tmpE != null){
				receiveCharacter(tmpE, game);
			}
		}else{
			if(container.getInput().isKeyDown(Input.KEY_ENTER) && charCount > 0){
				buttons[0].forceAction();
			}
		}
	}

	private void receiveCharacter(Entity entity, StateBasedGame game){		
		final StateBasedGame g = game;
		final Player tmpPlayer = (Player)Common.initEntityGraphicsSt(entity);
		/* Change animation frame, so that character is facing north */
		tmpPlayer.getAnimation().setCurrentFrame(9);
		characters.add(tmpPlayer);
		
		System.out.println("New char " + tmpPlayer.getName() + " " + tmpPlayer.getId());
		
		final int charId = tmpPlayer.getId();
		buttons[characters.size() - 1].
			setActionHandler(new ActionHandler(){
			public void onAction(){
				Common.getPlayerDriverSt().setEntity(tmpPlayer);
				g.enterState(stateId + 2, new FadeOutTransition(), 
						new FadeInTransition());
				NetProtocol.clEnterWorld(charId);
				try {
					//Adds a little delay
					Thread.sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	@Override
	public int getID() { return stateId; }

}
