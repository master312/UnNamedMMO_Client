package map;

import java.util.Random;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
/* TODO:
 * Optimize RenderTile(...) function
 */
import org.newdawn.slick.geom.Rectangle;

public class MapChunk {
	public enum Layer{
		GROUND, BOTTOM, TOP
	}
	/* Dimensions of one tile, in pixels */
	public static final int TILE_WIDTH = 32;
	public static final int TILE_HEIGHT = 32;
	/* Width of tileset image, in tiles */
	private static final int TILESET_WIDTH = 8;
	/* Number of bottom layers, excluding ground - Must be at least one
	 * (Layers that are drawn under player) */
	private static final int BOTTOM_LAYERS = 3;
	/* Number of top layers - Must be at least one
	 * (Layers that are drawn over player) */
	private static final int TOP_LAYERS = 2;
	
	private class Tile{
		private short ground = -1;
		private short bottomTile[];
		private short topTile[];
		
		public Tile(){
			initLayers();
		}
		
		public Tile(short _ground){
			ground = _ground;
			initLayers();
		}
		
		private void initLayers(){
			bottomTile = new short[BOTTOM_LAYERS];
			topTile = new short[TOP_LAYERS];
			for(int i = 0; i < BOTTOM_LAYERS; i++){
				bottomTile[i] = -1;
			}
			for(int i = 0; i < TOP_LAYERS; i++){
				topTile[i] = -1;
			}
		}
		
		/* Return tile on ground layer */
		public short getGround() { return ground; }
		/* Return tile from bottom layer[num] */
		public short getBottom(int num) { return bottomTile[num]; }
		/* Return tile from top layer[num] */
		public short getTop(int num) { return topTile[num]; }
		
		public void setGround(short _tile) { ground = _tile; }
		public void setBottom(int num, short _tile) { bottomTile[num] = _tile; }
		public void setTop(int num, short _tile) { topTile[num] = _tile; }
	}
	
	private int width = 0;
	private int height = 0;
	private int tilesetId = 0;
	private Tile tiles[][] = null;
	
	/* Size of game window. Used for optimization */
	private int windowWidth = 0;
	private int windowHeight = 0;
	
	public MapChunk(){}
	
	public MapChunk(int _width, int _height, 
			int _windowWidth, int _windowHeight){
		width = _width;
		height = _height;
		windowWidth = _windowWidth + TILE_WIDTH;
		windowHeight = _windowHeight + TILE_HEIGHT;
	}
	
	public void initEmpty(){
		tiles = new Tile[width][height];

		for(int i = 0; i < width; i++){
			for(int j = 0; j < height; j++){
				if(i == 0 || j == 0){
					tiles[i][j] = new Tile();
				}else{
					tiles[i][j] = new Tile((short)8);
				}
			}
		}
	}
	
	public void render(Graphics g, Image tileset, int camX, int camY){
		int tmpInt = 0;
		for(int i = 0; i < width; i++){
			for(int j = 0; j < height; j++){
				tmpInt = renderTile(i, j, tileset, camX, camY);
				if(tmpInt == -1){
					return;
				}else if(tmpInt == -2){
					break;
				}
			}
		}
	}
	
	/* Draws tile on screen.
	 * If return -1, return from render function
	 * If return -2 break current loop 
	 * If return 0, continue*/
	public int renderTile(int x, int y, Image tileset, int camX, int camY){
		int destX = (x * TILE_WIDTH) + camX;
		int destY = (y * TILE_HEIGHT) + camY;
		if(destX > windowWidth){
			return -1;	//Return from render function
		}else if(destY > windowHeight || destX < -TILE_WIDTH ){
			return -2;	//Break current loop
		}else if(destY < -TILE_HEIGHT){
			return 0;
		}
		Tile tmpTile = tiles[x][y];
		if(tmpTile.getGround() > -1){
			renderLayer(tmpTile, destX, destY, Layer.GROUND, 0, tileset);
		}
		if(tmpTile.getBottom(0) > -1){
			for(int i = 0; i < BOTTOM_LAYERS; i++){
				if(tmpTile.getBottom(i) < 0)
					break;
				renderLayer(tmpTile, destX, destY, Layer.BOTTOM, i, tileset);
			}
		}
		return 0;
		//TODO: Top layer rendering
	}
	
	private void renderLayer(Tile tile, int destX, int destY, Layer layerType, 
			int layerNumber, Image tileset){
		Rectangle tmpRecet = null;
		
		switch(layerType){
		case GROUND:
			tmpRecet = calculateSourceCoordinates(tile.getGround());
			break;
		case BOTTOM:
			tmpRecet = calculateSourceCoordinates(tile.getBottom(layerNumber));
			break;
		case TOP:
			tmpRecet = calculateSourceCoordinates(tile.getTop(layerNumber));
			break;
		}
		
		tileset.draw(destX, destY, destX + TILE_WIDTH, destY + TILE_HEIGHT, 
				tmpRecet.getX(), tmpRecet.getY(), 
				tmpRecet.getWidth(), tmpRecet.getHeight());
	}
	
	private Rectangle calculateSourceCoordinates(int tile){
		Rectangle tmpRect = new Rectangle(0, tile / TILESET_WIDTH, 0, 0);
		
		tmpRect.setX((tile - (tmpRect.getY() * TILESET_WIDTH)) * TILE_WIDTH);
		tmpRect.setY(tmpRect.getY() * TILE_HEIGHT);
		
		tmpRect.setWidth(tmpRect.getX() + TILE_WIDTH);
		tmpRect.setHeight(tmpRect.getY() + TILE_HEIGHT);
		
		return tmpRect;
	}
	
	public void fillLayer(int tile, Layer layer, int layerNumber){
		
	}
	
	public void setWindowWidth(int w) { windowWidth = w; }
	public void setWindowHeight(int h) { windowHeight = h; }
	
	public int getWidth() { return width; }
	public int getHeight() { return height; }
	public int getTilesetId() { return tilesetId; }
}
