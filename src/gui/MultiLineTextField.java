package gui;

import game.Common;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.GUIContext;

public class MultiLineTextField extends AbstractComponent {

	private int locX = 0;
	private int locY = 0;
	private int width = 0;
	private int height = 0;
    private Color background = new Color(0, 0, 0, 0.5f);
	private ArrayList<String> lines = new ArrayList<String>();
	private int lineHeight = 0;	//Height of one text line
	private int maxLines = 0;	//How many lines of text can be drawn without scrolling
	
	public MultiLineTextField(GameContainer container, int x, int y, int w, int h) {
		super(container);
		locX = x;
		locY = y;
		width = w;
		height = h;
		lineHeight = container.getDefaultFont().getLineHeight();
		maxLines = (int)height / lineHeight;
	}

	public void render(Graphics g) {
        if (background != null) {
        	Color clr = g.getColor();
            g.setColor(background.multiply(clr));
            g.fillRect(locX, locY, width, height);
        }
		g.setColor(Color.white);
		g.drawRect(locX, locY, width, height);
		int start = 0;
		if(lines.size() > maxLines){
			if(lines.size() > Common.MAX_CHAT_HISTORY){
				//Removes chat history if maximum number of messages is reached
				for(int i = 0; i < lines.size() - Common.MAX_CHAT_HISTORY; i++){
					lines.remove(i);
				}
			}
			start = lines.size() - maxLines;
		}
		int destX = locX + 1;
		int destY = locY + 1;
		for(int i = start; i < lines.size(); i++){
			g.drawString(lines.get(i), destX, destY);
			destY += lineHeight + 1;
		}
	}
	
	public void addLine(String line){
		lines.add(line);
	}
	
	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getX() {
		return locX;
	}

	@Override
	public int getY() {
		return locY;
	}

	@Override
	public void render(GUIContext arg0, Graphics arg1) throws SlickException {
		// TODO Auto-generated method stub
	}

	@Override
	public void setLocation(int x, int y) {
		locX = x;
		locY = y;
	}

}
