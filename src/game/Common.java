package game;

import net.ClientSocket;

import org.newdawn.slick.Animation;

import data.SpriteManager;
import entities.Entity;
import game.KeyBinds.KeyAction;
import map.MapManager;

/* This class contains all game data */
public class Common {
	private static Common commonClass = null;
	
	private ClientSocket socket = null;
	private MapManager mapManager = null;
	private EntityManager entityManager = null;
	private KeyBinds keyBinds = null;
	private PlayerDriver playerDriver = null;
	/* Sprite manager. Used to manage all sprites */
	private SpriteManager spriteManager = null;
	
	public int testint = -1;
	
	public Common(){
	}
	
	public void initialize(int worldWidth, int worldHeight){
		mapManager = new MapManager(worldWidth, worldHeight);
		mapManager.initWorld();
		entityManager = new EntityManager();
		keyBinds = new KeyBinds();
		playerDriver = new PlayerDriver();
		spriteManager = new SpriteManager();
		socket = new ClientSocket();
	}

	/* Returns entity with graphics initialized 
	 * Sprite ID must be set first, it will just return unchanged entity if not. */
	public Entity initEntityGraphics(Entity e){
		if(e.getSpriteId() < 0){
			return e;
		}
		Animation tmpAnim = null;
		tmpAnim = new Animation(spriteManager.getSprite(e.getSpriteId()), 500);
		if(tmpAnim.getFrameCount() <= 1){
			/* If there is only one frame, do not create animation object
			Create static image instead */
			e.setStaticImg(
					spriteManager.getSprite(e.getSpriteId()).getSprite(0, 0)
					);
		}else{
			tmpAnim.setAutoUpdate(false);
			e.setAnimation(tmpAnim);
		}
		return e;
	}
	
	public static Entity initEntityGraphicsSt(Entity e){
		return commonClass.initEntityGraphics(e);
	}
	
	public MapManager getMapManager() {
		return mapManager;
	}

	public static MapManager getMapManagerSt(){
		return commonClass.getMapManager();
	}
	
	public EntityManager getEntityManager() {
		return entityManager;
	}
	
	public static EntityManager getEntityManagerSt(){
		return commonClass.getEntityManager();
	}
	
	public KeyBinds getKeyBinds(){
		return keyBinds;
	}
	
	public static KeyBinds getKeyBindsSt(){
		return commonClass.getKeyBinds();
	}
	
	public PlayerDriver getPlayerDriver(){
		return playerDriver;
	}
	
	public static PlayerDriver getPlayerDriverSt(){
		return commonClass.getPlayerDriver();
	}
	
	public SpriteManager getSpriteManager(){
		return spriteManager;
	}
	
	public static SpriteManager getSpriteManagerSt(){
		return commonClass.getSpriteManager();
	}
	
	public ClientSocket getSocket(){
		return socket;
	}
	
	public static ClientSocket getSocketSt(){
		return commonClass.getSocket();
	}
	
	public static boolean isKeyAction(int key, KeyAction action){
		return getKeyBindsSt().isAction(key, action);
	}
	
	public static Common get(){
		if(commonClass == null)
			commonClass = new Common();
		return commonClass;
	}
}
