package ist.meic.cm.bomberman.status;

import java.io.Serializable;

public class BombermanStatus extends Status implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7540473135344364993L;
	private boolean canBomb;
	private int id;
	
	public BombermanStatus(int id, int i, int x, int y, char[] charArray) {
		super(i, x, y, charArray);
		this.id = id;
		setCanBomb(true);
		// TODO Auto-generated constructor stub
	}

	public int getId() {
		return id;
	}

	public boolean isCanBomb() {
		return canBomb;
	}

	public void setCanBomb(boolean canBomb) {
		this.canBomb = canBomb;
	}

}
