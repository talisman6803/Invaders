package screen;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import engine.Cooldown;
import engine.Core;
import engine.GameState;
import engine.Score;

/**
 * Implements the score screen.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public class ScoreScreen extends Screen {

	/** Milliseconds between changes in user selection. */
	private static final int SELECTION_TIME = 200;
	/** Maximum number of high scores. */
	private static final int MAX_HIGH_SCORE_NUM = 7;
	/** Code of first mayus character. */
	private static final int FIRST_CHAR = 65;
	/** Code of last mayus character. */
	private static final int LAST_CHAR = 90;

	/** Current score. */
	private int score1;
	private int score2;
	/** Player lives left. */
	private int livesRemaining1;
	private int livesRemaining2;
	/** Total bullets shot by the player. */
	private int bulletsShot1;
	private int bulletsShot2;
	/** Total ships destroyed by the player. */
	private int shipsDestroyed1;
	private int shipsDestroyed2;
	/** List of past high scores. */
	private List<Score> highScores;
	/** Checks if current score is a new high score. */
	private boolean isNewRecord1;
	private boolean isNewRecord2;
	/** Player name for record input. */
	private char[] name1;
	private char[] name2;
	/** Character of players name selected for change. */
	private int nameCharSelected1;
	private int nameCharSelected2;
	/** Time between changes in user selection. */
	private Cooldown selectionCooldown;

	/**
	 * Constructor, establishes the properties of the screen.
	 * 
	 * @param width
	 *            Screen width.
	 * @param height
	 *            Screen height.
	 * @param fps
	 *            Frames per second, frame rate at which the game is run.
	 * @param gameState
	 *            Current game state.
	 */
	public ScoreScreen(final int width, final int height, final int fps,
			final GameState gameState) {
		super(width, height, fps);

		this.score1 = gameState.getScore1();
		this.livesRemaining1 = gameState.getLivesRemaining1();
		this.bulletsShot1 = gameState.getBulletsShot1();
		this.shipsDestroyed1 = gameState.getShipsDestroyed1();

		this.score2 = gameState.getScore2();
		this.livesRemaining2 = gameState.getLivesRemaining2();
		this.bulletsShot2 = gameState.getBulletsShot2();
		this.shipsDestroyed2 = gameState.getShipsDestroyed2();

		this.isNewRecord1 = false;
		this.isNewRecord2 = false;
		this.name1 = "AAA".toCharArray();
		this.name2 = "AAA".toCharArray();
		this.nameCharSelected1 = 0;
		this.nameCharSelected2 = 0;
		this.selectionCooldown = Core.getCooldown(SELECTION_TIME);
		this.selectionCooldown.reset();

		try {
			this.highScores = Core.getFileManager().loadHighScores();
			if (highScores.size() < MAX_HIGH_SCORE_NUM
					|| highScores.get(highScores.size() - 1).getScore()
					< this.score1)
				this.isNewRecord1 = true;

			if (highScores.size() < MAX_HIGH_SCORE_NUM
					|| highScores.get(highScores.size() - 1).getScore()
					< this.score2)
				this.isNewRecord2 = true;
		} catch (IOException e) {
			logger.warning("Couldn't load high scores!");
		}
	}

	/**
	 * Starts the action.
	 * 
	 * @return Next screen code.
	 */
	public final int run() {
		super.run();

		return this.returnCode;
	}

	/**
	 * Updates the elements on screen and checks for events.
	 */
	protected final void update() {
		super.update();

		draw();
		if (this.inputDelay.checkFinished()) {
			if (inputManager.isKeyDown(KeyEvent.VK_ESCAPE)) {
				// Return to main menu.
				this.returnCode = 1;
				this.isRunning = false;
				if (this.isNewRecord1)
					saveScore(name1, score1);
				if (this.isNewRecord2)
					saveScore(name2, score2);
			} else if (inputManager.isKeyDown(KeyEvent.VK_SPACE)) {
				// Play again.
				this.returnCode = 2;
				this.isRunning = false;
				if (this.isNewRecord1)
					saveScore(name1, score1);
				if (this.isNewRecord2)
					saveScore(name2, score2);
			}
			// 둘중 한 명만 신기록 세워도 둘다 기록할 수 있음(추후 수정)
			if ((this.isNewRecord1 || this.isNewRecord2) && this.selectionCooldown.checkFinished()) {
				//--------------------player1
				if (inputManager.isKeyDown(KeyEvent.VK_D)) {
					this.nameCharSelected1 = this.nameCharSelected1 == 2 ? 0
							: this.nameCharSelected1 + 1;
					this.selectionCooldown.reset();
				}
				if (inputManager.isKeyDown(KeyEvent.VK_A)) {
					this.nameCharSelected1 = this.nameCharSelected1 == 0 ? 2
							: this.nameCharSelected1 - 1;
					this.selectionCooldown.reset();
				}
				if (inputManager.isKeyDown(KeyEvent.VK_W)) {
					this.name1[this.nameCharSelected1] =
							(char) (this.name1[this.nameCharSelected1]
									== LAST_CHAR ? FIRST_CHAR
							: this.name1[this.nameCharSelected1] + 1);
					this.selectionCooldown.reset();
				}
				if (inputManager.isKeyDown(KeyEvent.VK_S)) {
					this.name1[this.nameCharSelected1] =
							(char) (this.name1[this.nameCharSelected1]
									== FIRST_CHAR ? LAST_CHAR
							: this.name1[this.nameCharSelected1] - 1);
					this.selectionCooldown.reset();
				}
				//--------------------player2
				if (inputManager.isKeyDown(KeyEvent.VK_RIGHT)) {
					this.nameCharSelected2 = this.nameCharSelected2 == 2 ? 0
							: this.nameCharSelected2 + 1;
					this.selectionCooldown.reset();
				}
				if (inputManager.isKeyDown(KeyEvent.VK_LEFT)) {
					this.nameCharSelected2 = this.nameCharSelected2 == 0 ? 2
							: this.nameCharSelected2 - 1;
					this.selectionCooldown.reset();
				}
				if (inputManager.isKeyDown(KeyEvent.VK_UP)) {
					this.name2[this.nameCharSelected2] =
							(char) (this.name2[this.nameCharSelected2]
									== LAST_CHAR ? FIRST_CHAR
									: this.name2[this.nameCharSelected2] + 1);
					this.selectionCooldown.reset();
				}
				if (inputManager.isKeyDown(KeyEvent.VK_DOWN)) {
					this.name2[this.nameCharSelected2] =
							(char) (this.name2[this.nameCharSelected2]
									== FIRST_CHAR ? LAST_CHAR
									: this.name2[this.nameCharSelected2] - 1);
					this.selectionCooldown.reset();
				}

			}
		}

	}

	/**
	 * Saves the score as a high score.
	 */
	private void saveScore(char[] name, int score) {
		highScores.add(new Score(new String(name), score));
		Collections.sort(highScores);
		if (highScores.size() > MAX_HIGH_SCORE_NUM)
			highScores.remove(highScores.size() - 2);

		try {
			Core.getFileManager().saveHighScores(highScores);
		} catch (IOException e) {
			logger.warning("Couldn't load high scores!");
		}
	}

	/**
	 * Draws the elements associated with the screen.
	 */

	//기능상 1p임 (추후수정)
	private void draw() {
		drawManager.initDrawing(this);

		drawManager.drawGameOver(this, this.inputDelay.checkFinished(),
				this.isNewRecord1, this.isNewRecord2);
		drawManager.drawResults(this, this.score1, this.livesRemaining1,
				this.shipsDestroyed1, (float) this.shipsDestroyed1
						/ this.bulletsShot1, this.isNewRecord1);

		if (this.isNewRecord1)
			drawManager.drawNameInput(this, this.name1, this.nameCharSelected1);

		drawManager.completeDrawing(this);
	}
}
