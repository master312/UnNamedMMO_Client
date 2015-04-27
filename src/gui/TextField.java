package gui;

import java.io.IOException;

import org.lwjgl.Sys;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.GUIContext;

/**
 * A single text field supporting text entry
 *
 * @author kevin
 */
public class TextField extends AbstractComponent {

    private static final int INITIAL_KEY_REPEAT_INTERVAL = 400;
    private static final int KEY_REPEAT_INTERVAL = 50;

    private int width;
    private int height;
    protected int x;
    protected int y;
    private int maxCharacter = 1000;
    private String value = "";
    private Font font;
    private Color border = Color.white;
    private Color text = Color.white;
    private Color background = new Color(0, 0, 0, 0.5f);
    private int cursorPos;
    private boolean visibleCursor = true;
    private int lastKey = -1;
    private char lastChar = 0;
    private long repeatTimer;
    private String oldText;
    private int oldCursorPos;
    private boolean consume = true;
    private GameContainer gc;
    private boolean isEnter = false;
    
    public TextField(GameContainer gc, Font font, int x, int y, int width, int height, ComponentListener listener) {
        this(gc, font, x, y, width, height);
        addListener(listener);
        this.gc = gc;
    }

    public TextField(GameContainer gc, Font font, int x, int y, int width, int height) {
        super(gc);

        this.font = font;

        setLocation(x, y);
        this.width = width;
        this.height = height;
        this.gc = gc;
    }

    public void setConsumeEvents(boolean consume) {
        this.consume = consume;
    }

    public void deactivate() {
        setFocus(false);
    }

    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setBackgroundColor(Color color) {
        background = color;
    }

    public void setBorderColor(Color color) {
        border = color;
    }

    public void setTextColor(Color color) {
        text = color;
    }

    public void render(GameContainer gc, Graphics g) {
        if (lastKey != -1) {
            if (input.isKeyDown(lastKey)) {
                if (repeatTimer < System.currentTimeMillis()) {
                    repeatTimer = System.currentTimeMillis() + KEY_REPEAT_INTERVAL;
                    keyPressed(lastKey, lastChar);
                }
            } else {
                lastKey = -1;
            }
        }
        Rectangle oldClip = g.getClip();
        // g.setWorldClip(x,y,width, height);

        // Someone could have set a color for me to blend...   
        Color clr = g.getColor();

        if (background != null) {
            g.setColor(background.multiply(clr));
            g.fillRect(x, y, width, height);
        }
        g.setColor(text.multiply(clr));

        int cpos = font.getWidth(value.substring(0, cursorPos));
        int tx = 0;
        if (cpos > width) {
            tx = width - cpos - font.getWidth("_");
        }

        g.translate(tx + 2, 0);
        g.setFont(font);
        g.drawString(value, x + 1, y + 1);

        if (hasFocus() && visibleCursor) {
            g.drawString("_", x + 1 + cpos + 2, y + 1);
        }

        g.translate(-tx - 2, 0);

        if (border != null) {
            g.setColor(border.multiply(clr));
            g.drawRect(x, y, width - 1, height - 1);
        }
        g.setColor(clr);
        g.clearWorldClip();
        g.setClip(oldClip);
    }

    /**
     * Get the value in the text field
     *
     * @return The value in the text field
     */
    public String getText() {
        return value;
    }

    /**
     * Set the value to be displayed in the text field
     *
     * @param value The value to be displayed in the text field
     */
    public void setText(String value) {
        this.value = value;
        if (cursorPos > value.length()) {
            cursorPos = value.length();
        }
    }

    /**
     * Set the position of the cursor
     *
     * @param pos The new position of the cursor
     */
    public void setCursorPos(int pos) {
        cursorPos = pos;
        if (cursorPos > value.length()) {
            cursorPos = value.length();
        }
    }

    /**
     * Indicate whether the mouse cursor should be visible or not
     *
     * @param visibleCursor True if the mouse cursor should be visible
     */
    public void setCursorVisible(boolean visibleCursor) {
        this.visibleCursor = visibleCursor;
    }

    /**
     * Set the length of the allowed input
     *
     * @param length The length of the allowed input
     */
    public void setMaxLength(int length) {
        maxCharacter = length;
        if (value.length() > maxCharacter) {
            value = value.substring(0, maxCharacter);
        }
    }

    /**
     * Do the paste into the field, overrideable for custom behaviour
     *
     * @param text The text to be pasted in
     */
    protected void doPaste(String text) {
        recordOldPosition();

        for (int i = 0; i < text.length(); i++) {
            keyPressed(-1, text.charAt(i));
        }
    }

    /**
     * Record the old position and content
     */
    protected void recordOldPosition() {
        oldText = getText();
        oldCursorPos = cursorPos;
    }

    /**
     * Do the undo of the paste, overrideable for custom behaviour
     *
     * @param oldCursorPos before the paste
     * @param oldText      The text before the last paste
     */
    protected void doUndo(int oldCursorPos, String oldText) {
        if (oldText != null) {
            setText(oldText);
            setCursorPos(oldCursorPos);
        }
    }

    /**
     * @see org.newdawn.slick.gui.AbstractComponent#keyPressed(int, char)
     */
    public void keyPressed(int key, char c) {
        if (hasFocus()) {
            if (key != -1) {
                if ((key == Input.KEY_V) &&
                        ((input.isKeyDown(Input.KEY_LCONTROL)) || (input.isKeyDown(Input.KEY_RCONTROL)))) {
                    String text = Sys.getClipboard();
                    if (text != null) {
                        doPaste(text);
                    }
                    return;
                }
                if ((key == Input.KEY_Z) &&
                        ((input.isKeyDown(Input.KEY_LCONTROL)) || (input.isKeyDown(Input.KEY_RCONTROL)))) {
                    if (oldText != null) {
                        doUndo(oldCursorPos, oldText);
                    }
                    return;
                }

                // alt and control keys don't come through here   
                if (input.isKeyDown(Input.KEY_LCONTROL) || input.isKeyDown(Input.KEY_RCONTROL)) {
                    return;
                }
                if (input.isKeyDown(Input.KEY_LALT) || input.isKeyDown(Input.KEY_RALT)) {
                    return;
                }
            }

            if (lastKey != key) {
                lastKey = key;
                repeatTimer = System.currentTimeMillis() + INITIAL_KEY_REPEAT_INTERVAL;
            } else {
                repeatTimer = System.currentTimeMillis() + KEY_REPEAT_INTERVAL;
            }
            lastChar = c;

			if (key == Input.KEY_LEFT) {
                if (cursorPos > 0) {
                    cursorPos--;
                }
                // Nobody more will be notified   
                if (consume) {
                    gc.getInput().consumeEvent();
                }
            } else if (key == Input.KEY_RIGHT) {
                if (cursorPos < value.length()) {
                    cursorPos++;
                }
                // Nobody more will be notified   
                if (consume) {
                    gc.getInput().consumeEvent();
                }
            } else if (key == Input.KEY_BACK) {
                if ((cursorPos > 0) && (value.length() > 0)) {
                    if (cursorPos < value.length()) {
                        value = value.substring(0, cursorPos - 1)
                                + value.substring(cursorPos);
                    } else {
                        value = value.substring(0, cursorPos - 1);
                    }
                    cursorPos--;
                }
                // Nobody more will be notified   
                if (consume) {
                    gc.getInput().consumeEvent();
                }
            } else if (key == Input.KEY_DELETE) {
                if (value.length() > cursorPos) {
                    value = value.substring(0, cursorPos) + value.substring(cursorPos + 1);
                }
                // Nobody more will be notified   
                if (consume) {
                    gc.getInput().consumeEvent();
                }
            } else if ((c < 127) && (c > 31) && (value.length() < maxCharacter)) {
                if (cursorPos < value.length()) {
                    value = value.substring(0, cursorPos) + c
                            + value.substring(cursorPos);
                } else {
                    value = value.substring(0, cursorPos) + c;
                }
                cursorPos++;
                // Nobody more will be notified   
                if (consume) {
                    gc.getInput().consumeEvent();
                }
            } else if (key == Input.KEY_RETURN) {
                notifyListeners();
                // Nobody more will be notified
                isEnter = true;
                if (consume) {
                    gc.getInput().consumeEvent();
                }
            }

        }
    }

    /**
     * @see org.newdawn.slick.gui.AbstractComponent#setFocus(boolean)
     */
    public void setFocus(boolean focus) {
        lastKey = -1;
        super.setFocus(focus);
    }

	public boolean isRelative() {
		// TODO Auto-generated method stub
		return false;
	}

	protected float poll() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isEnter(){
		if(isEnter){
			isEnter = false;
			return true;
		}
		return false;
	}
	
	@Override
	public void render(GUIContext arg0, Graphics arg1) throws SlickException {
		// TODO Auto-generated method stub
		
	}
}   