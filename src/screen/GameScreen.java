package screen;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

import engine.Cooldown;
import engine.Core;
import engine.GameSettings;
import engine.GameState;
import entity.Bullet;
import entity.BulletPool;
import entity.EnemyShip;
import entity.EnemyShipFormation;
import entity.Entity;
import entity.Ship;

/**
 * Implements the game screen, where the action happens.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 *
 */
public class GameScreen extends Screen {

	/** Milliseconds until the screen accepts user input. */
	private static final int INPUT_DELAY = 6000;
	/** Bonus score for each life remaining at the end of the level. */
	private static final int LIFE_SCORE = 100;
	/** Minimum time between bonus ship's appearances. */
	private static final int BONUS_SHIP_INTERVAL = 20000;
	/** Maximum variance in the time between bonus ship's appearances. */
	private static final int BONUS_SHIP_VARIANCE = 10000;
	/** Time until bonus ship explosion disappears. */
	private static final int BONUS_SHIP_EXPLOSION = 500;
	/** Time from finishing the level to screen change. */
	private static final int SCREEN_CHANGE_INTERVAL = 1500;
	/** Height of the interface separation line. */
	private static final int SEPARATION_LINE_HEIGHT = 40;

	/** Current game difficulty settings. */
	private GameSettings gameSettings;
	/** Current difficulty level number. */
	private int level;
	/** Formation of enemy ships. */
	private EnemyShipFormation enemyShipFormation;
	/** Player's ship. */
	private Ship ship1;
	private Ship ship2;
	/** Bonus enemy ship that appears sometimes. */
	private EnemyShip enemyShipSpecial;
	/** Minimum time between bonus ship appearances. */
	private Cooldown enemyShipSpecialCooldown;
	/** Time until bonus ship explosion disappears. */
	private Cooldown enemyShipSpecialExplosionCooldown;
	/** Time from finishing the level to screen change. */
	private Cooldown screenFinishedCooldown;
	/** Set of all bullets fired by on screen ships. */
	private Set<Bullet> bullets;
	/** Current score. */
	private int score1;
	private int score2;
	/** Player lives left. */
	private int lives1;
	private int lives2;
	/** Total bullets shot by the player. */
	private int bulletsShot1;
	private int bulletsShot2;
	/** Total ships destroyed by the player. */
	private int shipsDestroyed1;
	private int shipsDestroyed2;
	/** Moment the game starts. */
	private long gameStartTime;
	/** Checks if the level is finished. */
	private boolean levelFinished;
	/** Checks if a bonus life is received. */
	private boolean bonusLife1;
	private boolean bonusLife2;

	/**
	 * Constructor, establishes the properties of the screen.
	 *
	 * @param gameState
	 *            Current game state.
	 * @param gameSettings
	 *            Current game settings.
	 * @param bonusLife1
	 *            Checks if a bonus life is awarded this level.
	 * @param width
	 *            Screen width.
	 * @param height
	 *            Screen height.
	 * @param fps
	 *            Frames per second, frame rate at which the game is run.
	 */
	public GameScreen(final GameState gameState,
					  final GameSettings gameSettings, final boolean bonusLife1,
					  final boolean bonusLife2,
					  final int width, final int height, final int fps) {
		super(width, height, fps);

		this.gameSettings = gameSettings;
		this.bonusLife1 = bonusLife1;
		this.bonusLife2 = bonusLife2;
		this.level = gameState.getLevel();
		this.score1 = gameState.getScore1();
		this.score2 = gameState.getScore2();
		this.lives1 = gameState.getLivesRemaining1();
		if (this.bonusLife1)
			this.lives1++;
		this.lives2 = gameState.getLivesRemaining2();
		if (this.bonusLife2)
			this.lives2++;
		this.bulletsShot1 = gameState.getBulletsShot1();
		this.shipsDestroyed1 = gameState.getShipsDestroyed1();

		this.bulletsShot2 = gameState.getBulletsShot2();
		this.shipsDestroyed2 = gameState.getShipsDestroyed2();
	}

	/**
	 * Initializes basic screen properties, and adds necessary elements.
	 */
	public final void initialize() {
		super.initialize();

		enemyShipFormation = new EnemyShipFormation(this.gameSettings);
		enemyShipFormation.attach(this);
		this.ship1 = new Ship(this.width / 2 - 150, this.height - 30, Color.GREEN);
		this.ship2 = new Ship(this.width / 2 + 150, this.height - 30, Color.RED);
		// Appears each 10-30 seconds.
		this.enemyShipSpecialCooldown = Core.getVariableCooldown(
				BONUS_SHIP_INTERVAL, BONUS_SHIP_VARIANCE);
		this.enemyShipSpecialCooldown.reset();
		this.enemyShipSpecialExplosionCooldown = Core
				.getCooldown(BONUS_SHIP_EXPLOSION);
		this.screenFinishedCooldown = Core.getCooldown(SCREEN_CHANGE_INTERVAL);
		this.bullets = new HashSet<Bullet>();

		// Special input delay / countdown.
		this.gameStartTime = System.currentTimeMillis();
		this.inputDelay = Core.getCooldown(INPUT_DELAY);
		this.inputDelay.reset();
	}

	/**
	 * Starts the action.
	 *
	 * @return Next screen code.
	 */
	public final int run() {
		super.run();

		this.score1 += LIFE_SCORE * (this.lives1 - 1);
		this.score2 += LIFE_SCORE * (this.lives2 - 1);

		this.logger.info("Screen cleared with a score of " + this.score1);
		this.logger.info("Screen cleared with a score of " + this.score2);

		return this.returnCode;
	}

	/**
	 * Updates the elements on screen and checks for events.
	 */
	protected final void update() {
		super.update();

		if (this.inputDelay.checkFinished() && !this.levelFinished) {

			if (!this.ship1.isDestroyed() && this.lives1 > 0) {
				boolean moveRight1 = inputManager.isKeyDown(KeyEvent.VK_D);
				boolean moveLeft1 = inputManager.isKeyDown(KeyEvent.VK_A);

				boolean isRightBorder1 = this.ship1.getPositionX()
						+ this.ship1.getWidth() + this.ship1.getSpeed() > this.width - 1;
				boolean isLeftBorder1 = this.ship1.getPositionX()
						- this.ship1.getSpeed() < 1;

				//ship1 move, shoot
				if (moveRight1 && !isRightBorder1) {
					this.ship1.moveRight();
				}
				if (moveLeft1 && !isLeftBorder1) {
					this.ship1.moveLeft();
				}
				if (inputManager.isKeyDown(KeyEvent.VK_SHIFT))
					if (this.ship1.shoot(this.bullets))
						this.bulletsShot1++;

				//ship2 move, shoot

			}
			if(!this.ship2.isDestroyed() && this.lives2 > 0){
				boolean moveRight2 = inputManager.isKeyDown(KeyEvent.VK_RIGHT);
				boolean moveLeft2 = inputManager.isKeyDown(KeyEvent.VK_LEFT);

				boolean isRightBorder2 = this.ship2.getPositionX()
						+ this.ship2.getWidth() + this.ship2.getSpeed() > this.width - 1;
				boolean isLeftBorder2 = this.ship2.getPositionX()
						- this.ship2.getSpeed() < 1;

				if (moveRight2 && !isRightBorder2) {
					this.ship2.moveRight();
				}
				if (moveLeft2 && !isLeftBorder2) {
					this.ship2.moveLeft();
				}
				if (inputManager.isKeyDown(KeyEvent.VK_SPACE))
					if (this.ship2.shoot(this.bullets))
						this.bulletsShot2++;

			}
			if (this.enemyShipSpecial != null) {
				if (!this.enemyShipSpecial.isDestroyed())
					this.enemyShipSpecial.move(2, 0);
				else if (this.enemyShipSpecialExplosionCooldown.checkFinished())
					this.enemyShipSpecial = null;

			}
			if (this.enemyShipSpecial == null
					&& this.enemyShipSpecialCooldown.checkFinished()) {
				this.enemyShipSpecial = new EnemyShip();
				this.enemyShipSpecialCooldown.reset();
				this.logger.info("A special ship appears");
			}
			if (this.enemyShipSpecial != null
					&& this.enemyShipSpecial.getPositionX() > this.width) {
				this.enemyShipSpecial = null;
				this.logger.info("The special ship has escaped");
			}

			this.ship1.update();
			this.ship2.update();
			this.enemyShipFormation.update();
			this.enemyShipFormation.shoot(this.bullets);
		}

		manageCollisions();
		cleanBullets();
		draw();

		if ((this.enemyShipFormation.isEmpty() || (this.lives1 == 0 && this.lives2 == 0))
				&& !this.levelFinished) {
			this.levelFinished = true;
			this.screenFinishedCooldown.reset();
		}

		if (this.levelFinished && this.screenFinishedCooldown.checkFinished())
			this.isRunning = false;

	}

	/**
	 * Draws the elements associated with the screen.
	 */
	private void draw() {
		drawManager.initDrawing(this);
		drawManager.drawEntity(this.ship1, this.ship1.getPositionX(),
					this.ship1.getPositionY());

		drawManager.drawEntity(this.ship2, this.ship2.getPositionX(),
					this.ship2.getPositionY());


		if (this.enemyShipSpecial != null)
			drawManager.drawEntity(this.enemyShipSpecial,
					this.enemyShipSpecial.getPositionX(),
					this.enemyShipSpecial.getPositionY());

		enemyShipFormation.draw();

		for (Bullet bullet : this.bullets)
			drawManager.drawEntity(bullet, bullet.getPositionX(),
					bullet.getPositionY());

		// Interface.
		drawManager.drawScore(this, this.score1, this.score2);
		drawManager.drawLives(this, this.lives1, this.lives2);
		drawManager.drawHorizontalLine(this, SEPARATION_LINE_HEIGHT - 1);

		// Countdown to game start.
		if (!this.inputDelay.checkFinished()) {
			int countdown = (int) ((INPUT_DELAY
					- (System.currentTimeMillis()
					- this.gameStartTime)) / 1000);
			drawManager.drawCountDown(this, this.level, countdown,
					this.bonusLife1, this.bonusLife2);
			drawManager.drawHorizontalLine(this, this.height / 2 - this.height
					/ 12);
			drawManager.drawHorizontalLine(this, this.height / 2 + this.height
					/ 12);
		}

		drawManager.completeDrawing(this);
	}

	/**
	 * Cleans bullets that go off screen.
	 */
	private void cleanBullets() {
		Set<Bullet> recyclable = new HashSet<Bullet>();
		for (Bullet bullet : this.bullets) {
			bullet.update();
			if (bullet.getPositionY() < SEPARATION_LINE_HEIGHT
					|| bullet.getPositionY() > this.height)
				recyclable.add(bullet);
		}
		this.bullets.removeAll(recyclable);
		BulletPool.recycle(recyclable);
	}

	/**
	 * Manages collisions between bullets and ships.
	 */
	private void manageCollisions() {
		Set<Bullet> recyclable = new HashSet<Bullet>();
		for (Bullet bullet : this.bullets)
			if (bullet.getSpeed() > 0) {
				if (checkCollision(bullet, this.ship1) && !this.levelFinished) {
					recyclable.add(bullet);
					if (!this.ship1.isDestroyed()) {
						this.ship1.destroy();
						this.lives1--;
						this.logger.info("Hit on player ship, " + this.lives1
								+ " lives remaining.");
						if(this.lives1 == 0){
							this.ship1.setPositionX(-1);
							this.ship1.setPositionY(-1);
						}
					}
				}
				else if (checkCollision(bullet, this.ship2) && !this.levelFinished) {
					recyclable.add(bullet);
					if (!this.ship2.isDestroyed()) {
						this.ship2.destroy();
						this.lives2--;
						this.logger.info("Hit on player ship, " + this.lives2
								+ " lives remaining.");
						if(this.lives2 == 0){
							this.ship2.setPositionX(-1);
							this.ship2.setPositionY(-1);
						}
					}
				}


			} else {
				for (EnemyShip enemyShip : this.enemyShipFormation)
					if (!enemyShip.isDestroyed()
							&& checkCollision(bullet, enemyShip)) {
						if(bullet.getShip() == ship1){
							this.score1 += enemyShip.getPointValue();
							this.shipsDestroyed1++;
							this.enemyShipFormation.destroy(enemyShip);
							recyclable.add(bullet);
						}
						else if(bullet.getShip() == ship2){
							this.score2 += enemyShip.getPointValue();
							this.shipsDestroyed2++;
							this.enemyShipFormation.destroy(enemyShip);
							recyclable.add(bullet);
						}

					}
				if (this.enemyShipSpecial != null
						&& !this.enemyShipSpecial.isDestroyed()
						&& checkCollision(bullet, this.enemyShipSpecial)) {
					if(bullet.getShip() == ship1){
						this.score1 += this.enemyShipSpecial.getPointValue();
						this.shipsDestroyed1++;
						this.enemyShipSpecial.destroy();
						this.enemyShipSpecialExplosionCooldown.reset();
						recyclable.add(bullet);
					}
					else if(bullet.getShip() == ship2){
						this.score2 += this.enemyShipSpecial.getPointValue();
						this.shipsDestroyed2++;
						this.enemyShipSpecial.destroy();
						this.enemyShipSpecialExplosionCooldown.reset();
						recyclable.add(bullet);
					}
				}
			}
		this.bullets.removeAll(recyclable);
		BulletPool.recycle(recyclable);
	}

	/**
	 * Checks if two entities are colliding.
	 *
	 * @param a
	 *            First entity, the bullet.
	 * @param b
	 *            Second entity, the ship.
	 * @return Result of the collision test.
	 */
	private boolean checkCollision(final Entity a, final Entity b) {
		// Calculate center point of the entities in both axis.
		int centerAX = a.getPositionX() + a.getWidth() / 2;
		int centerAY = a.getPositionY() + a.getHeight() / 2;
		int centerBX = b.getPositionX() + b.getWidth() / 2;
		int centerBY = b.getPositionY() + b.getHeight() / 2;
		// Calculate maximum distance without collision.
		int maxDistanceX = a.getWidth() / 2 + b.getWidth() / 2;
		int maxDistanceY = a.getHeight() / 2 + b.getHeight() / 2;
		// Calculates distance.
		int distanceX = Math.abs(centerAX - centerBX);
		int distanceY = Math.abs(centerAY - centerBY);

		return distanceX < maxDistanceX && distanceY < maxDistanceY;
	}

	/**
	 * Returns a GameState object representing the status of the game.
	 *
	 * @return Current game state.
	 */
	public final GameState getGameState() {
		return new GameState(this.level, this.score1, this.lives1,
				this.bulletsShot1, this.shipsDestroyed1, this.score2,
				this.lives2, this.bulletsShot2, this.shipsDestroyed2);
	}
}