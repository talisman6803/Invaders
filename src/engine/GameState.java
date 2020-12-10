package engine;

/**
 * Implements an object that stores the state of the game between levels.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public class GameState {

	/** Current game level. */
	private int level;
	/** Current score. */
	private int score1;
	private int score2;
	/** Lives currently remaining. */
	private int livesRemaining1;
	private int livesRemaining2;
	/** Bullets shot until now. */
	private int bulletsShot1;
	private int bulletsShot2;
	/** Ships destroyed until now. */
	private int shipsDestroyed1;
	private int shipsDestroyed2;

	/**
	 * Constructor.
	 * 
	 * @param level
	 *            Current game level.
	 * @param score
	 *            Current score.
	 * @param livesRemaining
	 *            Lives currently remaining.
	 * @param bulletsShot
	 *            Bullets shot until now.
	 * @param shipsDestroyed
	 *            Ships destroyed until now.
	 */
	public GameState(final int level, final int score1,
			final int livesRemaining1, final int bulletsShot1,
			final int shipsDestroyed1, final int score2, final int livesRemaining2,
					 final int bulletsShot2, final int shipsDestroyed2) {
		this.level = level;

		this.score1 = score1;
		this.livesRemaining1 = livesRemaining1;
		this.bulletsShot1 = bulletsShot1;
		this.shipsDestroyed1 = shipsDestroyed1;

		this.score2= score2;
		this.livesRemaining2 = livesRemaining2;
		this.bulletsShot2 = bulletsShot2;
		this.shipsDestroyed2 = shipsDestroyed2;
	}

	/**
	 * @return the level
	 */
	public final int getLevel() {
		return level;
	}

	/**
	 * @return the score
	 */
	public final int getScore1() {
		return score1;
	}
	public final int getScore2() {
		return score2;
	}
	/**
	 * @return the livesRemaining
	 */
	public final int getLivesRemaining1() {
		return livesRemaining1;
	}
	public final int getLivesRemaining2() {
		return livesRemaining2;
	}
	/**
	 * @return the bulletsShot
	 */
	public final int getBulletsShot1() {
		return bulletsShot1;
	}
	public final int getBulletsShot2() {
		return bulletsShot2;
	}

	/**
	 * @return the shipsDestroyed
	 */
	public final int getShipsDestroyed1() {
		return shipsDestroyed1;
	}
	public final int getShipsDestroyed2() {
		return shipsDestroyed2;
	}
}
