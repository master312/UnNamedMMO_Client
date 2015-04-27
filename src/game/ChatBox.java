package game;

import gui.MultiLineTextField;
import gui.TextField;
import net.NetProtocol;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

public class ChatBox {
	
	public class ChatType{
		public static final short SAY = 0;
		public static final short YEL = 1;
	}
	
	/* How many presents of screen will chat box take */
	private static final int PR_WIDTH = 45;
	private static final int PR_HEIGHT = 25;
	
	private MultiLineTextField chatBox = null;
	private TextField textInput = null;
	
	public ChatBox(GameContainer container){
		int tmpW = (container.getWidth() / 100) * PR_WIDTH;
		int tmpH = (container.getHeight() / 100) * PR_HEIGHT;
		int tmpX = 0;
		int tmpY = container.getHeight() - tmpH - 30;
		chatBox = new MultiLineTextField(container, tmpX, tmpY, tmpW, tmpH - 1);
		textInput = new TextField(container, container.getDefaultFont(), 
				tmpX, tmpY + tmpH, tmpW + 1, 30);
		
	}
	
	public void render(GameContainer container, Graphics g){
		chatBox.render(g);
		textInput.render(container, g);
		
		if(textInput.hasFocus() && textInput.isEnter()){
			if(textInput.getText() != "")
				NetProtocol.clChatMsg(ChatType.SAY, textInput.getText());
			textInput.setFocus(false);
			textInput.setText("");
		}
	}
	
	public void push(String msg){
		chatBox.addLine(msg);
	}
	
	public void startFocusInput(){
		textInput.setFocus(true);
	}
	
	public boolean isFocused(){
		return textInput.hasFocus();
	}
}
