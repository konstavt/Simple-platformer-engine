import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLineWidth;
import static org.lwjgl.opengl.GL11.glVertex2f;

public class MouseHandler {

    // The single, global instance of this class
    private static MouseHandler instance;

    // Current position
    private double xPos, yPos;
    // Previous position (useful for calculating how far the mouse moved this frame)
    private double lastX, lastY;

    // Track mouse button states
    private final boolean[] mouseButtons = new boolean[8];

    private final Map<Integer, Boolean> wasLastPressed = new HashMap<>();
    private final Map<Integer, Boolean> isJustPressed = new HashMap<>();

    // Store callback references to prevent garbage collection (FIXES LAG)
    private GLFWCursorPosCallback cursorPosCallback;
    private GLFWMouseButtonCallback mouseButtonCallback;

    // Private constructor ensures no one else can create a new instance
    private MouseHandler() {
        xPos = 0.0;
        yPos = 0.0;
        lastX = 0.0;
        lastY = 0.0;
    }

    // Get the global instance
    public static MouseHandler get() {
        if (MouseHandler.instance == null) {
            MouseHandler.instance = new MouseHandler();
        }
        return MouseHandler.instance;
    }

    /**
     * Initializes the mouse callbacks.
     * Call this ONCE during your window setup.
     * * @param windowHandle The ID of your GLFW window
     */
    public static void init(long windowHandle) {
        // Set up the position callback and store reference
        get().cursorPosCallback = GLFWCursorPosCallback.create((window, xpos, ypos) -> {
            get().lastX = get().xPos;
            get().lastY = get().yPos;
            get().xPos = xpos;
            get().yPos = ypos;
        });
        glfwSetCursorPosCallback(windowHandle, get().cursorPosCallback);

        // Set up the mouse button callback and store reference
        get().mouseButtonCallback = GLFWMouseButtonCallback.create((window, button, action, mods) -> {
            // Safety check to ensure the button is within our array bounds
            if (button >= 0 && button < get().mouseButtons.length) {
                if (action == GLFW_PRESS) {
                    get().mouseButtons[button] = true;
                } else if (action == GLFW_RELEASE) {
                    get().mouseButtons[button] = false;
                }
            }
        });
        glfwSetMouseButtonCallback(windowHandle, get().mouseButtonCallback);

        for (int i = 0; i < get().mouseButtons.length; i++) {
            get().wasLastPressed.put(i, false);
        }
    }

    /**
     * Call this at the END of your game loop / update cycle.
     * It resets the last position to the current position,
     * ensuring delta movement is only registered while the mouse is actually moving.
     */
    public static void endFrame() {
        get().lastX = get().xPos;
        get().lastY = get().yPos;
    }

    // --- Getters for your game to use ---

    public static float getX() {
        return (float) get().xPos;
    }

    public static float getY() {
        return (float) get().yPos;
    }

    // Gets how far the mouse moved on the X axis since the last frame
    public static float getDx() {
        return (float) (get().xPos - get().lastX);
    }

    // Gets how far the mouse moved on the Y axis since the last frame
    public static float getDy() {
        return (float) (get().yPos - get().lastY);
    }

    /**
     * Checks if a specific mouse button is currently held down.
     * @param button Use GLFW constants like GLFW_MOUSE_BUTTON_LEFT
     */
    public static boolean isButtonDown(int button) {
        if (button >= 0 && button < get().mouseButtons.length) {
            return get().mouseButtons[button];
        }
        return false;
    }

    public static boolean isButtonJustPressed(int button) {
        return (get().isJustPressed.get(button));
    }

    public static void tick() {

        for (int i = 0; i < get().mouseButtons.length; i++) {
            get().isJustPressed.put(i, false);
            if (!get().wasLastPressed.get(i) && isButtonDown(i)) {
                get().isJustPressed.put(i, true);
            }
            get().wasLastPressed.put(i, isButtonDown(i));
        }
    }

    public static Vector2i getWorldCoords(int x, int y) {
        return new Vector2i();
    }

    public static void drawCursor(float scale, Camera camera) {
        int worldX = (int) (get().xPos / scale + camera.getX());
        int cursorXPos = Math.floorDiv(worldX, World.GRID_SIZE) * World.GRID_SIZE;

        int worldY = (int) (get().yPos / scale + camera.getY());
        int cursorYPos = Math.floorDiv(worldY, World.GRID_SIZE) * World.GRID_SIZE;

        System.out.println("cursor: "+ cursorXPos + ", world: " + worldX);

        glLineWidth(4.0f);
        glBegin(GL_LINE_LOOP);
        glColor3f(0.0f, 0.8f, 0.8f); //
        glVertex2f(cursorXPos, cursorYPos);
        glVertex2f(cursorXPos + 16, cursorYPos);
        glVertex2f(cursorXPos + 16, cursorYPos + 16);
        glVertex2f(cursorXPos, cursorYPos + 16);
        glEnd();
        glColor3f(1.0f, 1.0f, 1.0f);
        glLineWidth(1.0f);

    }
}