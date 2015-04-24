package game;

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
import org.newdawn.slick.util.Log;

import entities.Entity;
import entities.Player;
import gui.TextField;

public class StateCharacterCreate extends BasicGameState{
	private int stateId = -1;
	private Player player;
	private TextField txtName;
	private boolean isCharacterSend;
	private boolean isCreationError;
	
	public StateCharacterCreate(int _stateId) {
		stateId = _stateId;
	}
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		Entity e = Common.initEntityGraphicsSt(new Player(-1, 0, 0, 30, 1));
		player = (Player)e;
		player.getAnimation().setCurrentFrame(9);
		txtName = new TextField(container, container.getDefaultFont(),
								250, 280, 170, 25);
	}
	
	public void enter(GameContainer container, StateBasedGame game) 
			throws SlickException{
		isCharacterSend = false;
		isCreationError = false;
		txtName.setText("");
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		g.setColor(Color.red);
		g.drawString("Character create", 250, 100);
		g.drawString("Default character: ", 260, 150);
		player.getAnimation().draw(300, 180);
		g.setColor(Color.white);
		g.drawString("Name:", 200, 280);
		txtName.render(container, g);
		g.drawString("Press enter to create character", 200, 320);
		if(isCreationError){
			g.setColor(Color.red);
			g.drawString("Could not create character!", 300, 450);
		}
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		ClientSocket sock = Common.getSocketSt();
		if(!sock.isConnected()){
			game.enterState(1, new FadeOutTransition(), 
					new FadeInTransition());
			return;
		}
		
		if(container.getInput().isKeyDown(Input.KEY_ENTER)){
			if(txtName.getText().length() < 3 || isCharacterSend){
			}else{
				player.setName(txtName.getText());
				NetProtocol.clCreateCharacter(player);
				isCharacterSend = true;
			}
			
		}else if(container.getInput().isKeyDown(Input.KEY_ESCAPE)){
			if(!isCharacterSend){
				game.enterState(stateId - 1, new FadeOutTransition(), 
						new FadeInTransition());
			}
		}
		if(!isCharacterSend)
			return;
		Packet pack = sock.getPacket();
		if(pack != null){
			if(pack.readShort() == OpCodes.SR_CHAR_CREATE_STATUS){
				if(pack.readShort() == 1){
					//Character created successfully
					Log.info("Character created");
					game.enterState(stateId - 1, new FadeOutTransition(), 
							new FadeInTransition());
				}else{
					//Character could not be created
					Log.warn("Character could not be created");
					isCreationError = true;
					isCharacterSend = false;
				}
			}else{
				Log.error("Character creation, invalid opcode received");
				game.enterState(stateId - 1, new FadeOutTransition(), 
						new FadeInTransition());
			}
		}
	}

	@Override
	public int getID() { return stateId; }

}
