package map;

public class Tile{
	private short ground = -1;
	private short bottomTile[];
	private short topTile[];
	
	public Tile(){
	}
	
	public Tile(short _ground){
		ground = _ground;
	}
	
	/* Return tile on ground layer */
	public short getGround() { return ground; }
	/* Return tile from bottom layer[num] */
	public short getBottom(int num) { return bottomTile[num]; }
	public int getBottomCnt() { return bottomTile.length; }
	/* Return tile from top layer[num] */
	public short getTop(int num) { return topTile[num]; }
	public int getTopCnt() { return bottomTile.length; }
	
	public void setGround(short _tile) { ground = _tile; }
	public void setBottom(int num, short _tile) { bottomTile[num] = _tile; }
	public void setTop(int num, short _tile) { topTile[num] = _tile; }
}