package ist.meic.cm.bomberman.controller;

import ist.meic.cm.bomberman.InGame;
import ist.meic.cm.bomberman.status.BombStatus;
import ist.meic.cm.bomberman.status.GhostStatus;
import ist.meic.cm.bomberman.status.Status;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class ExplosionThread extends Thread implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -390850487009272286L;
	private int EXPLOSION_DURATION;
	private int EXPLOSION_TIMEOUT;
	private int EXPLOSION_RANGE;
	private MapController mapController;
	private int position;
	private BombStatus bombStatus;
	protected final static int OTHER_LINE_STEP = 21;

	public ExplosionThread(int position, BombStatus bombStatus,
			MapController mapController) {
		this.mapController = mapController;
		this.position = position;
		this.bombStatus = bombStatus;
		EXPLOSION_DURATION = InGame.getExplosionDuration() * 1000;
		EXPLOSION_TIMEOUT = InGame.getExplosionTimeout() * 1000;
		EXPLOSION_RANGE = InGame.getExplosionRange();
	}

	@Override
	public void run() {
		try {
			Thread.sleep(EXPLOSION_DURATION);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		deleteBomb();
		ExplodingThread et = new ExplodingThread(bombStatus, mapController,
				position);
		et.start();

		try {
			Thread.sleep(EXPLOSION_TIMEOUT);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		et.setRunning(false);
		exploded();
	}

	//
	public void bombExplode() {
		
		char[] mapArray = mapController.getMap().toCharArray();
		mapArray[position] = 'E';

		int len = mapArray.length;
		int pos1 = 0, pos2 = 0, pos3 = 0, pos4 = 0;
		for (int i = 1; i <= EXPLOSION_RANGE
				&& (pos1 >= 0 || pos2 < len || pos3 >= 0 || pos4 < len); i++) {

			pos1 = position - i;
			if (pos1 >= 0 && mapArray[pos1] != 'W')
				mapArray[pos1] = 'E';

			pos2 = position + i;
			if (pos2 < len && mapArray[pos2] != 'W')
				mapArray[pos2] = 'E';

			pos3 = position - (i * OTHER_LINE_STEP);
			if (pos3 >= 0 && mapArray[pos3] != 'W')
				mapArray[pos3] = 'E';

			pos4 = position + (i * OTHER_LINE_STEP);
			if (pos4 < len && mapArray[pos4] != 'W')
				mapArray[pos4] = 'E';
		}

		mapController.setMap(new String(mapArray));
	}

	//
	public void deleteBomb() {
		LinkedList<GhostStatus> ghostsStatus = mapController.getGhostsStatus();
		//
		bombStatus.die();
		bombStatus.getBomberman().setCanBomb(true);
		//
		bombExplode();

		for (Status ghost : ghostsStatus)
			if (checkDeathPos(ghost.getI(), position)) {
				ghost.die(); // remove this ghost from the list of ghosts
								// Statuses, no longer exists
				bombStatus.getBomberman().increaseScore(1); // TODO
			}
	}

	//
	private boolean checkDeathPos(int currentPos, int position) {
		return currentPos == position || currentPos == position - 1
				|| currentPos == position + 1
				|| currentPos == position - OTHER_LINE_STEP
				|| currentPos == position + OTHER_LINE_STEP;
	}

	public void exploded() {
		char[] mapArray = mapController.getMap().toCharArray();

		mapArray[position] = '-';

		if (mapArray[position - 1] == 'E')
			mapArray[position - 1] = '-';

		if (mapArray[position + 1] == 'E')
			mapArray[position + 1] = '-';

		if (mapArray[position - OTHER_LINE_STEP] == 'E')
			mapArray[position - OTHER_LINE_STEP] = '-';

		if (mapArray[position + OTHER_LINE_STEP] == 'E')
			mapArray[position + OTHER_LINE_STEP] = '-';

		mapController.setMap(new String(mapArray));
	}

}
