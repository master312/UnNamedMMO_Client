package game;

import java.io.IOException;

import net.ClientSocket;
import net.LoginSequence;
import net.LoginSequence.LoginState;
import net.OpCodes;
import net.Packet;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import com.esotericsoftware.minlog.Log;

import gui.TextField;

public class StateMainMenu extends BasicGameState{	
	private int stateId = -1;
	
	private static String SERVER_IP = "127.0.0.1";
	private static int SERVER_PORT_TCP = 1234;
	private static int SERVER_PORT_UDP = 1235;
	
	/* Is server ready to proceed */
	private boolean isServerReady = false;
	private LoginSequence loginSequence = null;
	/* Has there been any unsuccessful login attempts */
	private boolean isLoginFailed = false;
	/* Ready to connect */
	private boolean connectReady = true;
	
	private TextField txtUsername = null;
	private TextField txtPassword = null;

	public StateMainMenu(int _id) {
		stateId = _id;
	}

	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		//Creates common class
		Common.get();
		Common.get().initialize(100, 100);
		txtUsername = new TextField(container, container.getDefaultFont(),
				200, 200, 300, 30);
		txtUsername.setText("master312");
		txtPassword = new TextField(container, container.getDefaultFont(),
				200, 250, 300, 30);
		txtPassword.setText("milenium");
	}

	public void enter(GameContainer container, StateBasedGame game) 
			throws SlickException {
		Common.getSocketSt().disconnect();
		isServerReady = false;
		loginSequence = null;
		isLoginFailed = false;
		connectReady = true;
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		txtUsername.render(container, g);
		txtPassword.render(container, g);
		
		g.drawString("Username:", 100, 200);
		g.drawString("Password:", 100, 255);
		
		g.drawString("The UnNamed MMO", 260, 165);
		
		if(!Common.getSocketSt().isConnected()){
			//Not connected to server
			if(!isLoginFailed){
				g.drawString("Press enter to login", 200, 300);
			}else{
				g.drawString("Login invalid. Try again", 200, 300);
				g.drawString("Press enter to login", 200, 350);
			}
		}else if(loginSequence == null){
			g.drawString("Connecting to server", 200, 300);
		}else if(loginSequence != null){
			g.drawString("Logging into server", 200, 300);
		}
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		ClientSocket sock = Common.getSocketSt();

		if(!sock.isConnected()){
		//Not connected to server
			connectReady = true;
			if(container.getInput().isKeyDown(Input.KEY_ENTER)){
				if(txtUsername.getText().length() < 3 ||
					txtPassword.getText().length() < 3){
					return;
				}
				try {
					sock.connect(SERVER_IP, SERVER_PORT_TCP, SERVER_PORT_UDP);
				} catch (IOException e) {
					Log.error("Could not connect to server");
				}
			}
			return;
		}else if(!connectReady){
			return;
		}
		//Connected to server
		if(!isServerReady){
			Packet tmpPack = sock.getPacket();
			if(tmpPack != null){
				if(tmpPack.readShort() == OpCodes.SR_LOGIN_READY){
					isServerReady = true;
					Log.debug("Server is ready");
				}else{
					sock.disconnect();
				}
			}
		}else if(loginSequence == null){
			//Server is ready for login
			loginSequence = new LoginSequence(txtUsername.getText(),
											txtPassword.getText());
		}else{
			LoginState lState = loginSequence.update();
			switch(lState){
			case LOGIN_OK:
				Log.info("Logged in!");
				game.enterState(stateId + 1, new FadeOutTransition(), 
								new FadeInTransition());
				break;
			case LOGIN_FAIL:
				Log.error("Invalid login");
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				sock.disconnect();
				isServerReady = false;
				loginSequence = null;
				isLoginFailed = true;
				connectReady = false;
				break;
			default:
				break;
			}
		}
		////
	}
	@Override
	public int getID() { return stateId; }

}
