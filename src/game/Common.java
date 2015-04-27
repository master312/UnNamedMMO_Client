package game;

import net.ClientSocket;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;

import data.SpriteManager;
import entities.Entity;
import game.KeyBinds.KeyAction;
import map.MapManager;

/* This class contains all game data */
public class Common {
	private static Common commonClass = null;
	
	/* How many messages will be stored in chat history */
	public static final int MAX_CHAT_HISTORY = 50;
	
	private ClientSocket socket = null;
	private MapManager mapManager = null;
	private EntityManager entityManager = null;
	private KeyBinds keyBinds = null;
	private PlayerDriver playerDriver = null;
	/* Sprite manager. Used to manage all sprites */
	private SpriteManager spriteManager = null;
	private ChatBox chatBox = null;
	/* Screen size */
	private int screenWidth = 0;
	private int screenHeight = 0;
	
	public int testint = -1;
	
	public Common(GameContainer container){
		screenWidth = container.getWidth();
		screenHeight = container.getHeight();
		chatBox = new ChatBox(container);
	}
	
	private void initSubclasses(){
		mapManager = new MapManager();
		entityManager = new EntityManager();
		keyBinds = new KeyBinds();
		playerDriver = new PlayerDriver();
		spriteManager = new SpriteManager();
		socket = new ClientSocket();
	}

	/* Returns entity with graphics initialized 
	 * Sprite ID must be set first, it will just return unchanged entity if not. */
	public Entity initEntityGraphics(Entity e){
		if(e.getSpriteId() < 0 || e.isAnimated() || e.getStaticImg() != null){
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
	
	public int getScreenWidth() {
		return screenWidth;
	}
	
	public static int getScreenWidthSt() {
		return commonClass.getScreenWidth();
	}

	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}

	public int getScreenHeight() {
		return screenHeight;
	}

	public static int getScreenHeightSt(){
		return commonClass.getScreenHeight();
	}
	
	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}
	
	public ChatBox getChatBox() {
		return chatBox;
	}

	public static ChatBox getChatBoxSt(){
		return commonClass.getChatBox();
	}

	public static void initialize(GameContainer container){
		if(commonClass != null)
			return;
		commonClass = new Common(container);
		commonClass.initSubclasses();
	}
	
	public static Common get(){
		return commonClass;
	}
}
