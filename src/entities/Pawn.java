package entities;

import com.esotericsoftware.kryo.serializers.FieldSerializer.Optional;

/* This is base class for every creature in game */
public class Pawn extends Entity {
	
	/* If pawn is not updated for this many frames
	 * isUpdated() will return false */
	private static final int UPDATE_FRAMES_STOP = 2;
	
	private int maxHealth = 0;
	private int currentHealth = 0;
	private String subName = "";
	private int normalSpeed = 0;
	private int currentSpeed = 0;
	/* Is this pawn updated in this frame */
	@Optional(value = "")
	private int isUpdated = 0;
	
	public Pawn(){
	}

	/* Calculate animation speed */
	public void calcAnimSpeed(){
		if(getAnimation() != null){
			int curSpeed = currentSpeed;
			int frWidth = getAnimation().getImage(0).getWidth() * 2;
			float animSpeed = (float)curSpeed / frWidth;
			animSpeed *= 10;
			getAnimation().setSpeed(animSpeed - 0.5f);
		}	
	}
	
	public int getMaxHealth() {
		return maxHealth;
	}

	public void setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
	}

	public String getSubName() {
		return subName;
	}

	public void setSubName(String subName) {
		this.subName = subName;
	}

	public int getCurrentHealth() {
		return currentHealth;
	}

	public void setCurrentHealth(int currentHealth) {
		this.currentHealth = currentHealth;
	}

	public int getNormalSpeed() {
		return normalSpeed;
	}

	public void setNormalSpeed(int normalSpeed) {
		this.normalSpeed = normalSpeed;
	}

	public int getCurrentSpeed() {
		return currentSpeed;
	}

	public void setCurrentSpeed(int currentSpeed) {
		this.currentSpeed = currentSpeed;
	}

	/* If pawn is not updated fro */
	public boolean isUpdated() {
		isUpdated --;
		return isUpdated >= 0;
	}

	public void setUpdated() {
		isUpdated = UPDATE_FRAMES_STOP;
	}
}
