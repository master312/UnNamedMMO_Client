package game;

import java.util.ArrayList;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Point;

import entities.*;

public class EntityManager {

	/* List of all entities. entities[0] is player's character */
	private ArrayList<Entity> entities = new ArrayList<Entity>();
	/* Temporary location. Used for entity rendering */
	private Point tmpEntityLoc = new Point(0, 0);

	public EntityManager(){
	}
	
	public void update(int delta){
	}
	
	public void render(Graphics g){
		Entity tmpE = null;
		for(int i = 0; i < entities.size(); i++){
			tmpE = entities.get(i);
			if(isOnScreen(tmpE)){
				renderEntity(tmpE, g);
			}
		}
		
		tmpE = Common.getPlayerDriverSt().getEntity();
		if(isOnScreen(tmpE)){
			renderEntity(tmpE, g);
		}
	}
	
	/* Gives new entity to manager 
	 * Entity must be initialized before calling this function
	 * This function will handle graphics initializing for entity. */
	public void addEntity(Entity e){
		entities.add(Common.get().initEntityGraphics(e));
	}
	
	/* Remove entity with id from manager */
	public void removeEntity(int id){
		for(int i = 0; i < entities.size(); i++){
			if(entities.get(i).getId() == id){
				entities.remove(i);
				return;
			}
		}
	}
	
	public void removeEntity(Entity e){
		entities.remove(e);
	}
	
	/* Returns whether this entity is on screen.
	 * If true, store its 'on screen' to tmpEntityLoc variable */
	private boolean isOnScreen(Entity e){
		tmpEntityLoc.setX(e.getLocX() + Common.getMapManagerSt().getWorldCamX());
		tmpEntityLoc.setY(e.getLocY() + Common.getMapManagerSt().getWorldCamY());
		if(tmpEntityLoc.getX() < -e.getWidth() ||
			tmpEntityLoc.getX() > Common.getScreenWidthSt() ||
			tmpEntityLoc.getY() < -e.getHeight() ||
			tmpEntityLoc.getY() > Common.getScreenHeightSt()){
			//Entity is not on screen
			return false;
		}
		return true;
	}
	
	/* Renders entity on screen */
	private void renderEntity(Entity e, Graphics g){
		if(!e.haveGraphics()) //This entity dose not have graphics to be drawn
			return;
		if(e.isAnimated()){
			e.getAnimation().draw(tmpEntityLoc.getX(), tmpEntityLoc.getY());
		}else{
			//Entity is not animated. It uses static image
			g.drawImage(e.getStaticImg(), 
				tmpEntityLoc.getX(), tmpEntityLoc.getY());
		}
		
		if(e.isPawn()){
			//If entity is pawn, draw its name
			g.drawString(e.getName(), 
					tmpEntityLoc.getX() - 5, 
					tmpEntityLoc.getY() - 20);
		}
	}
	
	/* Return entity with ID, or NULL if not found */
	public Entity getEntity(int id){
		for(int i = 0; i < entities.size(); i++){
			if(entities.get(i).getId() == id){
				return entities.get(i);
			}
		}
		return null;
	}
}
