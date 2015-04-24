package gui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.GUIContext;

public class Button extends AbstractComponent {
	private enum ButtonState{
		DISABLED, NORMAL, OVER, DOWN
	}
	
    private int width;
    private int height;
    protected int x;
    protected int y;
    private Font font;
    private GameContainer gc;
    private String text;
    private ButtonState state;
    private ActionHandler action = null;
    
    public Button(String text, GameContainer gc, Font font, int x, int y, 
    		int width, int height) {
    	super(gc);
    	this.text = text;
        this.font = font;
        setLocation(x, y);
        this.width = width;
        this.height = height;
        this.gc = gc;
        state = ButtonState.NORMAL;
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
		return x;
	}

	@Override
	public int getY() {
		return y;
	}
	
	public String getText(){
		return text;
	}

	public void setText(String str){
		text = str;
	}
	
	public void render(GameContainer gc, Graphics g) {
		if(state == ButtonState.NORMAL)
			g.setColor(Color.white);
		else
			g.setColor(Color.gray);
		g.fillRect(x, y, width, height);
		g.setColor(Color.black);
		g.drawString(text, x + 2, y + 5);
	}

	@Override
	public void setLocation(int _x, int _y) {
		x = _x;
		y = _y;
	}

	public void mouseMoved(int odlx, int oldY, int newX, int newY){
		if(isMoseOver(newX, newY)){
			state = ButtonState.OVER;
		}else{
			state = ButtonState.NORMAL;
		}
	}
	
	public void mousePressed(int button, int newX, int newY){
		if(isMoseOver(newX, newY)){
			if(action != null)
				action.onAction();
            // Nobody more will be notified
			gc.getInput().consumeEvent();
		}
	}
	
	private boolean isMoseOver(int mX, int mY){
		return mX > x && mX < x + width && mY > y && mY < y + height;
	}
	
	public Font getFont(){
		return font;
	}
	
	public void setActionHandler(ActionHandler ah){
		action = ah;
	}
	
	@Override
	public void render(GUIContext arg0, Graphics arg1) throws SlickException {
		// TODO Auto-generated method stub
		
	}
}
