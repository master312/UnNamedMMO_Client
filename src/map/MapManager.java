package map;

import java.util.ArrayList;

import org.lwjgl.util.Point;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.Log;
//TODO: Chunks timeount

public class MapManager {
	private class MapTileset{
		private Image image = null;
		private int id = -1;
		
		public MapTileset(Image _image, int _id){
			image = _image;
			id = _id;
		}

		public Image getImage() { return image; }
		public int getId() { return id; }
	}
	
	/* These values will be used if world width and height are not specified */
	private static final int DEFAULT_WORLD_WIDTH = 100;
	private static final int DEFAULT_WORLD_HEIGHT = 100;
	/* These values will be used if chunk width and height are not specified 
	 * Chunk size can be changed on server (When server is done xD)*/
	private static final int DEFAULT_CHUNK_WIDTH = 50;
	private static final int DEFAULT_CHUNK_HEIGHT = 50;
	/* TODO: COMMENT HERE! Move camera when this % of screen is left to end */
	private static final int DEFAULT_CAMERA_SHIFT_X = 25;
	private static final int DEFAULT_CAMERA_SHIFT_Y = 20;
	/* Chunks clear interval. Chunks that are no longer needed are deleted
	 * on this interval (in MS) */
	private static final long CHUNKS_CLEAR_INTERVAL = 15000;
	private long lastClearTime = 0;
	
	
	private int worldWidth = DEFAULT_WORLD_WIDTH;
	private int worldHeight = DEFAULT_WORLD_HEIGHT;
	private int chunkWidth = DEFAULT_CHUNK_WIDTH;
	private int chunkHeight = DEFAULT_CHUNK_HEIGHT;
	
	/* List of tilesets */
	private ArrayList<MapTileset> tilesets = new ArrayList<MapTileset>();
	/* Array of all chunks in the world */
	private MapChunk chunks[][];
	/* List of loaded chunks */
	private ArrayList<Point> chunksLoaded = new ArrayList<Point>();
	
	/* World camera position, in pixels */
	private int worldCamX = 0;
	private int worldCamY = 0;
	/* Coordinates of chunks on world map that should be prepared in memory
	 * And can be drawn any time */
	private Point start = new Point();
	private Point end = new Point();
	
	public MapManager(){ }
	
	public MapManager(int _worldWidth, int _worldHeight){
		worldWidth = _worldWidth;
		worldHeight = _worldHeight;
	}
	
	public void initWorld(){
		chunks = new MapChunk[worldWidth][worldHeight];
		for(int i = 0; i < worldWidth; i++){
			for(int j = 0; j < worldHeight; j++){
				chunks[i][j] = null;
			}
		}
		
		Log.info("MapManager: World initialized\n" + 
				"World size: " + worldWidth + "x" + worldHeight);
	}
	
	public void render(GameContainer container, Graphics g){
		int lookX = 0;
		int lookY = 0;
		for(int i = start.getX(); i <= end.getX(); i++){
			for(int j = start.getY(); j <= end.getY(); j++){
				MapChunk tmpChunk = chunks[i][j];
				if(tmpChunk == null){
					//TODO: Receive map from server... or something like thats
					createNewChunk(i, j, container);
					tmpChunk = chunks[i][j];
				}
				//TODO: Optimize this somehow. Do not call isTilesetLoaded() on every frame; Its slow
				if(!isTilesetLoaded(tmpChunk.getTilesetId())){
					loadTileset(tmpChunk.getTilesetId());
				}
				lookX = worldCamX + (i * chunkWidth * MapChunk.TILE_WIDTH);
				lookY = worldCamY + (j * chunkHeight * MapChunk.TILE_HEIGHT);
				tmpChunk.render(g, getTilesetImage(tmpChunk.getTilesetId()),
						lookX, lookY);
			}
		}
		if(System.currentTimeMillis() - lastClearTime > CHUNKS_CLEAR_INTERVAL){
			clearChunks();
			lastClearTime = System.currentTimeMillis();
		}
	}
	
	/* Calculate camera position, based on player coordinates
	 * Player will always be in the center of screen */
	public void movePlayer(int x, int y, GameContainer container){
		int screenX = x + worldCamX;
		int screenY = y + worldCamY;
		int minW = (container.getWidth() / 100) * DEFAULT_CAMERA_SHIFT_X;
		int minH = (container.getHeight() / 100) * DEFAULT_CAMERA_SHIFT_Y;
		int maxW = container.getWidth() - minW;
		int maxH = container.getHeight() - minH;
		boolean isMoved = false;
		
		if(screenX > maxW){
			//Moves camera to right
			worldCamX += maxW - screenX;
			isMoved = true;
		}else if(screenX < (minW - 50) && worldCamX < 0){
			//Moves camera to left
			worldCamX += (minW - 50) - screenX;
			isMoved = true;
		}
		if(screenY > (maxH - 50)){
			worldCamY += (maxH - 50) - screenY;
			isMoved = true;
		}else if(screenY < minH && worldCamY < 0){
			worldCamY += minH - screenY;
			isMoved = true;
		}
		
		if(isMoved)
			calculateStartEndTiles(container.getWidth(), container.getHeight());
	}
	
	/* Move camera by this values */
	public void moveCamera(int x, int y){
		Log.debug("MapManager TODO: MoveCamera function");
//		worldCamX += x;
//		worldCamY += y;
//		calculateStartEndTiles();
	}
	
	/* Convert pixels coordinates, to chunk coordinates */
	public Point pixelToChunk(int x, int y){
		Point tmpPoint = pixelToTile(x, y);
		
		tmpPoint.setX(tmpPoint.getX() / chunkWidth);
		tmpPoint.setY(tmpPoint.getY() / chunkHeight);
		
		return tmpPoint;
	}
	
	/* Converts pixel coordinates, to tile coordinates (on world map) */
	public Point pixelToTile(int x, int y){
		return new Point((int)(x / MapChunk.TILE_WIDTH), 
					(int)(y / MapChunk.TILE_HEIGHT));
	}
	
	/* Calculate location of tiles that should be loaded in memory
	 * P - Player; T - Tile;
	 * T T T
	 * T P T
	 * T T T */
	private void calculateStartEndTiles(int scrnW, int scrnH){
		Point tmpPoint = null;
		tmpPoint = pixelToChunk(-worldCamX + scrnW / 2, -worldCamY + scrnH / 2);
		
		start.setLocation(tmpPoint.getX() - 1, tmpPoint.getY() - 1);
		
		if(start.getX() < 0)
			start.setX(0);
		if(start.getY() < 0)
			start.setY(0);
		
		end.setLocation(tmpPoint.getX() + 1, tmpPoint.getY() + 1);
		
		if(end.getX() >= worldWidth){
			end.setX(worldWidth - 1);
		}
		if(end.getY() >= worldHeight){
			end.setY(worldHeight - 1);
		}
		if(start.getX() >= end.getX()){
			start.setX(end.getX() - 2);
		}
		if(start.getY() >= end.getY()){
			start.setY(end.getY() - 2);
		}
		
	}
	
	/* Return whether tileset is loaded or not*/
	private boolean isTilesetLoaded(int id){
		for(int i = 0; i < tilesets.size(); i++){
			if(tilesets.get(i).getId() == id)
				return true;
		}
		return false;
	}
	
	/* Return image of this tileset. isTilesetLoaded(...) should always be 
	 * called before calling this. This function will crush program if
	 * tileset is not loaded */
	private Image getTilesetImage(int id){
		for(int i = 0; i < tilesets.size(); i++){
			if(tilesets.get(i).getId() == id)
				return tilesets.get(i).getImage();
		}
		return null;
	}
	
	/* Loads tileset and adds it to memory */
	private void loadTileset(int id){
		Image tmpImage = null;
		try {
			tmpImage = new Image("data/graphics/tilesets/" + id + ".png");
		} catch (SlickException e) {
			e.printStackTrace();
		}

		tilesets.add(new MapTileset(tmpImage, id));
	}
	
	/* Delete not needed chunks from memory */
	private void clearChunks(){
		boolean isCleared = false;
		for(int i = 0; i < chunksLoaded.size(); i++){
			Point tmpP = chunksLoaded.get(i);
			if(tmpP.getX() >= start.getX() && tmpP.getX() <= end.getX() &&
					tmpP.getY() >= start.getY() && tmpP.getY() <= end.getY()){
				//Chunk is needed. Do not delete id
				continue;
			}else{
				//Chunk is not needed anymore
				chunks[tmpP.getX()][tmpP.getY()] = null;
				chunksLoaded.remove(i);
				isCleared = true;
				i = (i > 0) ? i - 1 : 0;
			}
		}
		if(isCleared)
			System.gc();
	}
	
	/* Creates new chunk at coordinates x:y */
	private void createNewChunk(int x, int y, GameContainer container){
		chunks[x][y] = new MapChunk(chunkWidth, chunkHeight,
				container.getWidth(), container.getHeight());
		chunks[x][y].initEmpty();
		chunksLoaded.add(new Point(x, y));
	}

	public int getWorldCamX() {
		return worldCamX;
	}

	public void setWorldCamX(int worldCamX) {
		this.worldCamX = worldCamX;
	}

	public int getWorldCamY() {
		return worldCamY;
	}

	public void setWorldCamY(int worldCamY) {
		this.worldCamY = worldCamY;
	}
	
	
}
