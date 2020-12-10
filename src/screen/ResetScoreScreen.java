package screen;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.List;

import engine.Core;
import engine.Cooldown;
import engine.Score;

public class ResetScoreScreen extends Screen{

    private List<Score> highScores;
    private static final int SELECTION_TIME = 200;
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
     */
    public ResetScoreScreen(final int width, final int height, final int fps){
        super(width, height, fps);

        this.returnCode = 6;
        this.selectionCooldown = Core.getCooldown(SELECTION_TIME);
        this.selectionCooldown.reset();

        try {
            this.highScores = Core.getFileManager().loadHighScores();
        } catch (NumberFormatException | IOException e) {
            logger.warning("Couldn't load scores!");
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
    protected final void update() {
        super.update();

        draw();
        if (this.selectionCooldown.checkFinished()
                && this.inputDelay.checkFinished()) {
            if (inputManager.isKeyDown(KeyEvent.VK_UP)
                    || inputManager.isKeyDown(KeyEvent.VK_W)) {
                previousMenuItem();
                this.selectionCooldown.reset();
            }
            if (inputManager.isKeyDown(KeyEvent.VK_DOWN)
                    || inputManager.isKeyDown(KeyEvent.VK_S)) {
                nextMenuItem();
                this.selectionCooldown.reset();
            }
            if (inputManager.isKeyDown(KeyEvent.VK_SPACE))
                this.isRunning = false;
        }
    }
    /**
     * returnCode 5: Yes (reset Scores)
     * returnCode 6: No (Do not reset)
    */
    private void nextMenuItem() {
        if (this.returnCode == 6)
            this.returnCode = 5;
        else if (this.returnCode == 5)
            this.returnCode = 6;
    }

    /**
     * Shifts the focus to the previous menu item.
     */
    private void previousMenuItem() {
        if (this.returnCode == 5)
            this.returnCode = 6;
        else if (this.returnCode == 6)
            this.returnCode = 5;
    }

    private void draw() {
        drawManager.initDrawing(this);

        drawManager.drawResetScoreMenu(this);
        drawManager.drawChoice(this, this.returnCode);

        drawManager.completeDrawing(this);
    }
}
