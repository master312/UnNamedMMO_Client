package game;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class Main extends StateBasedGame {
	
	public static final int SCREEN_WIDTH = 1024;
	public static final int SCREEN_HEIGHT = 768;
	public static final int TARGET_FPS = 60;
	public static final float VERSION = 0.283f;
	
	public static final int SPLASHSCREEN = 0;
	public static final int MAINMENU     = 1;
	public static final int CHARACTERS   = 2;
	public static final int CHARCREATE	 = 3;
	public static final int GAME         = 4;
	
	public Main(String title) {
		super(title);
	}

    public void initStatesList(GameContainer gc) throws SlickException {
        // The first state added will be the one that is loaded first, when the application is launched
    	addState(new StateMainMenu(MAINMENU));
    	addState(new StateCharacterScreen(CHARACTERS));
    	addState(new StateCharacterCreate(CHARCREATE));
    	addState(new StateInGame(GAME));
    }

    public static void main(String[] args) {
    	try{
			AppGameContainer appgc;
			appgc = new AppGameContainer(new Main("2dMMO " + VERSION));
			appgc.setAlwaysRender(true);
			appgc.setTargetFrameRate(TARGET_FPS);	//Sets maximum FPS
			appgc.setMinimumLogicUpdateInterval(30);
			appgc.setMaximumLogicUpdateInterval(30);
			appgc.setDisplayMode(SCREEN_WIDTH, SCREEN_HEIGHT, false);
			appgc.start();
		}
		catch (SlickException ex){
			ex.printStackTrace();
		}

	}

}