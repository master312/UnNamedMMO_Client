package data;

import java.util.ArrayList;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.xml.XMLElement;
import org.newdawn.slick.util.xml.XMLElementList;
import org.newdawn.slick.util.xml.XMLParser;
 /* This class loads, and manages all entity sprites.
  * Dose not manage map graphics */
public class SpriteManager {
	private class Sprite{
		/* Sprite id */
		private int id = -1;
		/* Actual sprite sheet */
		private SpriteSheet sprite = null;
		/* Number of objects using this sprite
		 * It this number is zero, this sprite can be deleted */
		private int usage = 0;
		/* If sprite is not used for some amount of time, it will be deleted
		 * This variable holds time when was this sprite last used */
		private long timeout = 0;
		public Sprite(int _id, SpriteSheet _sprite){
			id = _id;
			sprite = _sprite;
		}
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public SpriteSheet getSprite() {
			return sprite;
		}
		public void setSprite(SpriteSheet sprite) {
			this.sprite = sprite;
		}
		public int getUsage() {
			return usage;
		}
		public void setUsage(int usage) {
			this.usage = usage;
		}		
		public long getTimeout() {
			return timeout;
		}
		public void setTimeout(long timeout) {
			this.timeout = timeout;
		}
		
		public void addUsage() { 
			usage ++; 
			timeout = 0;
		}
		public void removeUsage() { usage --; }
	}
	
	/* If sprite is not used for this amount if time, 
	 * it will be deleted from memory */
	private static final long SPRITE_TIMEOUT = 30000;
	/* Interval on what to check for sprites timeout */
	private static final long TIMEOUT_CHECK_INTERVAL = 10000;
	private long lastTimeoutCheck = 0;
	private ArrayList<Sprite> sprites = new ArrayList<Sprite>();

	public SpriteManager(){
		lastTimeoutCheck = System.currentTimeMillis();
	}
	
	public void update(){
		//Check timeout of sprites
		if(System.currentTimeMillis() - lastTimeoutCheck > TIMEOUT_CHECK_INTERVAL){
			Sprite tmpSprt = null;
			boolean isRemoved = false;
			for(int i = 0; i < sprites.size(); i++){
				tmpSprt = sprites.get(i);
				if(tmpSprt.getUsage() <= 0){
					//Sprite is not in usage
					if(tmpSprt.getTimeout() == 0){
						//Starting sprite timeout count
						sprites.get(i).setTimeout(System.currentTimeMillis());
					}else{
						//Timeout is already started
						if(System.currentTimeMillis() - tmpSprt.getTimeout() > 
							SPRITE_TIMEOUT){
							//Sprite is timeouted
							sprites.remove(i);
							isRemoved = true;
							i = (i > 0) ? i - 1 : 0;
						}
					}
				}
			}
			if(isRemoved){
				System.gc();
			}
		}
	}
	
	/* Returns sprite. If sprite with id is not loaded, then loads it*/
	public SpriteSheet getSprite(int id){
		int tmpI = isLoaded(id);
		if(tmpI == -1){
			//Sprite is not loaded. Load and return it.
			Sprite tmpSprt = null;
			try {
				tmpSprt = loadSprite(id);
			} catch (SlickException e) {
				e.printStackTrace();
			}
			tmpSprt.addUsage();
			return tmpSprt.getSprite();
		}
		sprites.get(tmpI).addUsage();
		return sprites.get(tmpI).getSprite();
	}
	
	/* In order to delete unused sprite from memory
	 * this functions must be called when done using sprite*/
	public void freeSprite(int id){
		int tmpI = isLoaded(id);
		if(tmpI == -1){
			Log.warn("freeSprite() tryed to unload non-existing sprite." +
					" This will probably crush the program");
			return;
		}
		sprites.get(tmpI).removeUsage();
	}
	
	public int isLoaded(int id){
		for(int i = 0; i < sprites.size(); i++){
			if(sprites.get(i).getId() == id){
				return i;
			}
		}
		return -1;
	}
	
	public Sprite loadSprite(int id) throws SlickException{
		//Parsing XML
		XMLParser parser = new XMLParser(); 
		XMLElement parsed = null;
		parsed = parser.parse("/data/sprites/" + id + ".xml");
	
		XMLElementList children = parsed.getChildren();
		XMLElement tmpE = children.get(0);
		
		String filename = tmpE.getContent();

		tmpE = children.get(1);
		boolean isStatic = tmpE.getBooleanAttribute("enabled");
		
		tmpE = children.get(2);
		int frames = tmpE.getIntAttribute("count");
		
		tmpE = children.get(3);
		boolean isBgTransparent = tmpE.getBooleanAttribute("transparency");
		int r, g, b;
		if(!isBgTransparent){
			r = tmpE.getIntAttribute("red");
			g = tmpE.getIntAttribute("green");
			b = tmpE.getIntAttribute("blue");
			Log.warn("SpriteManager: Color transparency not yet supported");
			isBgTransparent = true;
		}
		
		tmpE = children.get(4);
		Rectangle rect = new Rectangle(0, 0, 0, 0);
		rect.setX(tmpE.getIntAttribute("x"));
		rect.setY(tmpE.getIntAttribute("y"));
		rect.setWidth(tmpE.getIntAttribute("width"));
		rect.setHeight(tmpE.getIntAttribute("height"));
		//End of XML parsing
		Image img = new Image("/data/" + filename);
		int frameW = img.getWidth();
		int frameH = img.getHeight();
		if(!isStatic){
			frameW = img.getWidth() / frames;
			frameH = img.getHeight() / 4;
		}
		SpriteSheet tmpSprite = null;
		if(isBgTransparent){
			tmpSprite = new SpriteSheet(img, frameW, frameH);
		}else{
			Log.warn("SpriteManager: Color transparency not yet supported");
		}
		sprites.add(new Sprite(id, tmpSprite));
		Log.info("SpriteManager Sprite " + id + " loaded");
		return sprites.get(sprites.size() - 1);
	}
}
