package map;

import game.Common;

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
	public static final int TILESET_WIDTH = 8;
	
	private int width = 0;
	private int height = 0;
	
	private int tilesetId = 0;
	private Tile tiles[][] = null;
	/* Location on megamap */
	private int locX = 0;
	private int locY = 0;
	
	public MapChunk(){}
	
	public MapChunk(int _width, int _height, int _x, int _y){
		width = _width;
		height = _height;
		locX = _x;
		locY = _y;
	}
	
	public void initEmpty(){
		tiles = new Tile[width][height];

		for(int i = 0; i < width; i++){
			for(int j = 0; j < height; j++){
				tiles[i][j] = new Tile();
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
		if(destX > Common.getScreenWidthSt() + TILE_WIDTH){
			return -1;	//Return from render function
		}else if(destY > Common.getScreenHeightSt() + TILE_HEIGHT
				 || destX < -TILE_WIDTH ){
			return -2;	//Break current loop
		}else if(destY < -TILE_HEIGHT){
			return 0;
		}
		Tile tmpTile = tiles[x][y];
		if(tmpTile.getGround() > -1){
			renderLayer(tmpTile, destX, destY, Layer.GROUND, 0, tileset);
		}
		if(tmpTile.getBottom(0) > -1){
			for(int i = 0; i < Tile.BOTTOM_LAYERS; i++){
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
	
	public Tile[][] getTiles(){ return tiles; }
	
	public int getLocX() { return locX; }
	public void setLocX(int locX) { this.locX = locX; }
	public int getLocY() { return locY; }
	public void setLocY(int locY) { this.locY = locY; }
	
	
	public void setWidth(int width) { this.width = width; }
	public void setHeight(int height) { this.height = height; }
	public int getWidth() { return width; }
	public int getHeight() { return height; }
	public void setTilesetId(int tilesetId) { this.tilesetId = tilesetId; }
	public int getTilesetId() { return tilesetId; }
}
