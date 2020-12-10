package engine;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import screen.DifficultyScreen;
import screen.GameScreen;
import screen.HighScoreScreen;
import screen.ScoreScreen;
import screen.ResetScoreScreen;
import screen.Screen;
import screen.TitleScreen;

/**
 * Implements core game logic.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public final class Core {

	/** Width of current screen. */
	private static final int WIDTH = 448;
	/** Height of current screen. */
	private static final int HEIGHT = 520;
	/** Max fps of current screen. */
	private static final int FPS = 60;

	/** Max lives. */
	private static final int MAX_LIVES = 3;
	/** Levels between extra life. */
	private static final int EXTRA_LIFE_FRECUENCY = 3;
	/** Total number of levels. */
	private static final int NUM_LEVELS = 7;
	
	/** Frame to draw the screen on. */
	private static Frame frame;
	/** Screen currently shown. */
	private static Screen currentScreen;
	/** Difficulty settings list. */
	private static List<GameSettings> gameSettings;
	/** Application logger. */
	private static final Logger LOGGER = Logger.getLogger(Core.class
			.getSimpleName());
	/** Logger handler for printing to disk. */
	private static Handler fileHandler;
	/** Logger handler for printing to console. */
	private static ConsoleHandler consoleHandler;


	/**
	 * Test implementation.
	 * 
	 * @param args
	 *            Program args, ignored.
	 */
	public static void main(final String[] args) {
		try {
			LOGGER.setUseParentHandlers(false);

			fileHandler = new FileHandler("log");
			fileHandler.setFormatter(new MinimalFormatter());

			consoleHandler = new ConsoleHandler();
			consoleHandler.setFormatter(new MinimalFormatter());

			LOGGER.addHandler(fileHandler);
			LOGGER.addHandler(consoleHandler);
			LOGGER.setLevel(Level.ALL);

		} catch (Exception e) {
			// TODO handle exception
			e.printStackTrace();
		}

		frame = new Frame(WIDTH, HEIGHT);
		DrawManager.getInstance().setFrame(frame);
		int width = frame.getWidth();
		int height = frame.getHeight();

		gameSettings = new ArrayList<GameSettings>();
		
		GameState gameState;

		int returnCode = 1;
		do {
			gameState = new GameState(1, 0, MAX_LIVES, 0, 0);

			switch (returnCode) {
			case 1:
				// Main menu.
				currentScreen = new TitleScreen(width, height, FPS);
				LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
						+ " title screen at " + FPS + " fps.");
				returnCode = frame.setScreen(currentScreen);
				LOGGER.info("Closing title screen.");
				break;
			case 2:
				// Game & score.
				boolean isDifficultySetting = false;
				do {
					boolean bonusLife = gameState.getLevel()
							% EXTRA_LIFE_FRECUENCY == 0
							&& gameState.getLivesRemaining() < MAX_LIVES;
					// One extra live every few levels.
					if(isDifficultySetting == false) {
						currentScreen = new DifficultyScreen(width, height, FPS);
						LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
								+ " difficulty screen at " + FPS + " fps.");
						returnCode = frame.setScreen(currentScreen);
						LOGGER.info("Difficulty screen.");
						if (returnCode == 5) {
							LOGGER.info("EASY MODE");
							gameSettingsByDifficulty(-1,-1,0,400);
							bonusLife = true;
							isDifficultySetting = true;
						} else if (returnCode == 6) {
							LOGGER.info("MEDIUM MODE");
							gameSettingsByDifficulty(0,1,20,250);
							isDifficultySetting = true;
						} else if (returnCode == 7) {
							LOGGER.info("HARD MODE");
							gameSettingsByDifficulty(1,2,50,100);
							isDifficultySetting = true;
							
						} else continue;
					}
					
					
					currentScreen = new GameScreen(gameState,
							gameSettings.get(gameState.getLevel() - 1),
							bonusLife, width, height, FPS);
					LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
							+ " game screen at " + FPS + " fps.");
					frame.setScreen(currentScreen);
					LOGGER.info("Closing game screen.");

					gameState = ((GameScreen) currentScreen).getGameState();

					gameState = new GameState(gameState.getLevel() + 1,
							gameState.getScore(),
							gameState.getLivesRemaining(),
							gameState.getBulletsShot(),
							gameState.getShipsDestroyed());

				} while (gameState.getLivesRemaining() > 0
						&& gameState.getLevel() <= NUM_LEVELS);

				LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
						+ " score screen at " + FPS + " fps, with a score of "
						+ gameState.getScore() + ", "
						+ gameState.getLivesRemaining() + " lives remaining, "
						+ gameState.getBulletsShot() + " bullets shot and "
						+ gameState.getShipsDestroyed() + " ships destroyed.");
				currentScreen = new ScoreScreen(width, height, FPS, gameState);
				returnCode = frame.setScreen(currentScreen);
				LOGGER.info("Closing score screen.");
				break;
			case 3:
				// High scores.
				currentScreen = new HighScoreScreen(width, height, FPS);
				LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
						+ " high score screen at " + FPS + " fps.");
				returnCode = frame.setScreen(currentScreen);
				LOGGER.info("Closing high score screen.");
				break;
			case 4:
				// Reset scores menu.
				currentScreen = new ResetScoreScreen(width, height, FPS);
				LOGGER.info("Starting "+ WIDTH + "x" + HEIGHT
						+ "Reset score screen at " + FPS + "fps.");
				returnCode = frame.setScreen(currentScreen);
				LOGGER.info("Closing title screen.");
				break;
			case 5:
				// Reset Score data.
				LOGGER.info("reset scores.");
				FileManager.resetScores();
				returnCode = 1;
				LOGGER.info("Closing title screen.");
				break;
			default:
				break;
			}

		} while (returnCode != 0);

		fileHandler.flush();
		fileHandler.close();
		System.exit(0);
	}

	/**
	 * Constructor, not called.
	 */
	private Core() {

	}

	/**
	 * Controls access to the logger.
	 * 
	 * @return Application logger.
	 */
	public static Logger getLogger() {
		return LOGGER;
	}

	/**
	 * Controls access to the drawing manager.
	 * 
	 * @return Application draw manager.
	 */
	public static DrawManager getDrawManager() {
		return DrawManager.getInstance();
	}

	/**
	 * Controls access to the input manager.
	 * 
	 * @return Application input manager.
	 */
	public static InputManager getInputManager() {
		return InputManager.getInstance();
	}

	/**
	 * Controls access to the file manager.
	 * 
	 * @return Application file manager.
	 */
	public static FileManager getFileManager() {
		return FileManager.getInstance();
	}

	/**
	 * Controls creation of new cooldowns.
	 * 
	 * @param milliseconds
	 *            Duration of the cooldown.
	 * @return A new cooldown.
	 */
	public static Cooldown getCooldown(final int milliseconds) {
		return new Cooldown(milliseconds);
	}

	/**
	 * Controls creation of new cooldowns with variance.
	 * 
	 * @param milliseconds
	 *            Duration of the cooldown.
	 * @param variance
	 *            Variation in the cooldown duration.
	 * @return A new cooldown with variance.
	 */
	public static Cooldown getVariableCooldown(final int milliseconds,
			final int variance) {
		return new Cooldown(milliseconds, variance);
	}

	/**
	 * Setting Level of Game
	 * 
	 * @param width_weight
	 *            Add enemy formation width
	 * @param height_weight
	 *            Add enemy formation height
	 * @param baseSpeed_weight
	 *            Add speed of the enemies
	 * @param shootingFrequency_weight
	 *            Add Frequency of enemy shootings
	 */
	private static void gameSettingsByDifficulty(int width_weight, int height_weight, int baseSpeed_weight, int shootingFrequency_weight) {
		/** Difficulty settings for level 1. */
		final GameSettings SETTINGS_LEVEL_1 =
		new GameSettings(4+width_weight, 4+height_weight, 60+baseSpeed_weight, 2500+shootingFrequency_weight);
		/** Difficulty settings for level 2. */
		final GameSettings SETTINGS_LEVEL_2 =
		new GameSettings(5+width_weight, 5+height_weight, 50+baseSpeed_weight, 2000+shootingFrequency_weight);
		/** Difficulty settings for level 3. */
		final GameSettings SETTINGS_LEVEL_3 =
		new GameSettings(6+width_weight, 5+height_weight, 40+baseSpeed_weight, 1500+shootingFrequency_weight);
		/** Difficulty settings for level 4. */
		final GameSettings SETTINGS_LEVEL_4 =
		new GameSettings(6+width_weight, 6+height_weight, 30+baseSpeed_weight, 1000+shootingFrequency_weight);
		/** Difficulty settings for level 5. */
		final GameSettings SETTINGS_LEVEL_5 =
		new GameSettings(7+width_weight, 6+height_weight, 20+baseSpeed_weight, 500+shootingFrequency_weight);
		/** Difficulty settings for level 6. */
		final GameSettings SETTINGS_LEVEL_6 = 
		new GameSettings(7+width_weight, 7+height_weight, 10+baseSpeed_weight, 250+shootingFrequency_weight);
		/** Difficulty settings for level 7. */
		final GameSettings SETTINGS_LEVEL_7 =
		new GameSettings(8+width_weight, 7+height_weight, 2+baseSpeed_weight, 100+shootingFrequency_weight);
		
		gameSettings.add(SETTINGS_LEVEL_1);
		gameSettings.add(SETTINGS_LEVEL_2);
		gameSettings.add(SETTINGS_LEVEL_3);
		gameSettings.add(SETTINGS_LEVEL_4);
		gameSettings.add(SETTINGS_LEVEL_5);
		gameSettings.add(SETTINGS_LEVEL_6);
		gameSettings.add(SETTINGS_LEVEL_7);
		
	}
}