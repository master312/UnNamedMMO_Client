package entities;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;

import com.esotericsoftware.kryo.serializers.FieldSerializer.Optional;
/* This is base class for every entity in game */
public class Entity {
	public enum Direction{
		NORTH{ public String toString(){ return "NORTH"; } }, 
		NORTHEAST{ public String toString(){ return "NORTHEAST"; } }, 
		EAST{ public String toString(){ return "EAST";}	}, 
		SOUTHEAST { public String toString() { return "SOUTHEAST"; } }, 
		SOUTH { public String toString() { return "SOUTH"; } }, 
		SOUTHWEST { public String toString() { return "SOUTHWEST"; } }, 
		WEST { public String toString() { return "WEST"; } }, 
		NORTHWEST { public String toString() { return "NORTHWEST"; } }
	}
	public static class NetDirection {
		/* Direction ID's over network */
		public static final int DIRECTION_NORT = 0;
		public static final int DIRECTION_NORTHEAST = 1;
		public static final int DIRECTION_EAST = 2;
		public static final int DIRECTION_SOUTHEAST = 3;
		public static final int DIRECTION_SOUTH = 4;
		public static final int DIRECTION_SOUTHWEST = 5;
		public static final int DIRECTION_WEST = 6;
		public static final int DIRECTION_NORTHWEST = 7;
	}
	public enum EntityType{
		UNDEFINED{ public String toString(){ return "UNDEFINED"; } }, 
		MONSTER{ public String toString(){ return "MONSTER"; } },
		NPC{ public String toString(){ return "NPC"; } },
		PLAYER{ public String toString(){ return "PLAYER"; } }
	}
	
	private EntityType type = EntityType.UNDEFINED;
	private int id = -1;
	private float locX = 0f, locY = 0f; 
	private int width = 0, height = 0;
	private int spriteId = -1;
	@Optional(value = "")	//Tells network serializer to ignore this variable
	private Image staticImg = null;
	@Optional(value = "")
	private Animation animation = null;
	@Optional(value = "")
	private int animFrameStart = 0;	//At what frame animation starts
	@Optional(value = "")
	private int animFrameEnd = 0;	//And and what frame ends
	private String name = "";
	private Direction dir = Direction.NORTH;

	
	public Entity(){
	}
	
	public boolean isMonster() { return type == EntityType.MONSTER; }
	public boolean isNpc() { return type == EntityType.NPC; }
	public boolean isPlayer() { return type == EntityType.PLAYER; }
	public boolean isPawn() { return isMonster() || isNpc() || isPlayer(); }
	public boolean isAnimated() { return animation != null; }
	
	public boolean haveGraphics() { 
		return staticImg != null || animation != null; 
	}
	
	public void move(float x, float y){
		locX += x;
		locY += y;
	}

	public Direction getDir() {
		return dir;
	}

	public void setDir(Direction dir) {
		this.dir = dir;
	}
	
	/* Return direction id to be send over network */
	public short getNetDir(){
		switch(dir){
		case NORTH:
			return NetDirection.DIRECTION_NORT;
		case NORTHEAST:
			return NetDirection.DIRECTION_NORTHEAST;
		case EAST:
			return NetDirection.DIRECTION_EAST;
		case SOUTHEAST:
			return NetDirection.DIRECTION_SOUTHEAST;
		case SOUTH:
			return NetDirection.DIRECTION_SOUTH;
		case SOUTHWEST:
			return NetDirection.DIRECTION_SOUTHWEST;
		case WEST:
			return NetDirection.DIRECTION_WEST;
		case NORTHWEST:
			return NetDirection.DIRECTION_NORTHWEST;
		}
		return -1;
	}
	
	public void setNetDir(short netDir){
		switch(netDir){
		case NetDirection.DIRECTION_NORT:
			dir = Direction.NORTH;
			setAnimFrameStart(0);
			setAnimFrameEnd(3);
			break;
		case NetDirection.DIRECTION_NORTHEAST:
			dir = Direction.NORTHEAST;
			break;
		case NetDirection.DIRECTION_EAST:
			dir = Entity.Direction.EAST;
			setAnimFrameStart(12);
			setAnimFrameEnd(15);
			break;
		case NetDirection.DIRECTION_SOUTHEAST:
			dir = Entity.Direction.SOUTHEAST;
			break;
		case NetDirection.DIRECTION_SOUTH:
			dir = Direction.SOUTH;
			setAnimFrameStart(8);
			setAnimFrameEnd(11);
			break;
		case NetDirection.DIRECTION_SOUTHWEST:
			dir = Direction.SOUTHWEST;
			break;
		case NetDirection.DIRECTION_WEST:
			dir = Direction.WEST;
			setAnimFrameStart(4);
			setAnimFrameEnd(7);
			break;
		case NetDirection.DIRECTION_NORTHWEST:
			dir = Direction.NORTHWEST;
			break;
		}
	}
	
	public EntityType getType() {
		return type;
	}

	public void setType(EntityType type) {
		this.type = type;
	}

	public Image getStaticImg() {
		return staticImg;
	}

	public void setStaticImg(Image staticImg) {
		this.staticImg = staticImg;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public float getLocX() {
		return locX;
	}

	public void setLocX(float locX) {
		this.locX = locX;
	}

	public float getLocY() {
		return locY;
	}

	public void setLocY(float locY) {
		this.locY = locY;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public Animation getAnimation() {
		return animation;
	}

	public void setAnimation(Animation animation) {
		this.animation = animation;
	}

	public int getSpriteId() {
		return spriteId;
	}

	public void setSpriteId(int spriteId) {
		this.spriteId = spriteId;
	}
	
	public boolean equals(Object o){
		return id == ((Entity) o).getId();
	}

	public int getAnimFrameStart() {
		return animFrameStart;
	}

	public void setAnimFrameStart(int animFrameStart) {
		if(animation != null)
			animation.setCurrentFrame(animFrameStart);
		this.animFrameStart = animFrameStart;
	}

	public int getAnimFrameEnd() {
		return animFrameEnd;
	}

	public void setAnimFrameEnd(int animFrameEnd) {
		this.animFrameEnd = animFrameEnd;
	}
	
}
