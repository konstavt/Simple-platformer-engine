import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Game {

    private final String title;

    private int width, height;
    private long window;

    private boolean resize;
    private final boolean vSync;
    private final boolean fullscreen;

    private GLFWFramebufferSizeCallback framebufferSizeCallback;

    public Game(String title, int width, int height, boolean vSync, boolean fullscreen) {
        this.height = height;
        this.title = title;
        this.width = width;
        this.vSync = vSync;
        this.fullscreen = fullscreen;
    }

    private void init() {
        // Set up an error callback so we can see if GLFW fails
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // Window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // Window will be resizable

        boolean maximized = false;
        if (width == 0 || height == 0) {
            width = 100;
            height = 100;
            glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
            maximized = true;
        }

        long monitor = NULL;
        if (fullscreen) {
            monitor = glfwGetMonitors().get(0);
            GLFWVidMode vidMode = glfwGetVideoMode(monitor);
            if (vidMode == null) {
                throw new RuntimeException("Failed to get primary monitor video mode");
            }

            width = vidMode.width();
            height = vidMode.height();

            glfwWindowHint(GLFW_REFRESH_RATE, vidMode.refreshRate());
        }

        // Create the window
        window = glfwCreateWindow(width, height, title, monitor, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        MouseHandler.init(window);

        Input.setInstance(new Input(window));

        glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
            this.width = width;
            this.height = height;
            this.setResize(true);
        });

        if (maximized) {
            glfwMaximizeWindow(window);
        }

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync

        if (isVsync()) {
            glfwSwapInterval(1);

        }

        // Make the window visible
        glfwShowWindow(window);

        GL.createCapabilities();

        // Enable color blending
        glEnable(GL_BLEND);

        // Tell OpenGl exactly how to blend the transparent pixels
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        World.setInstance(new World());

        //setClearColour(173.0f / 255.0f, 216.0f / 255.0f, 230.0f / 255.0f, 1.0f);
        setClearColour(47 / 255.0f, 47 / 255.0f, 47 / 255.0f, 1.0f);
    }

    public void update() {
        glfwSwapBuffers(window);
        glfwPollEvents();
    }

    public void cleanup() {
        glfwDestroyWindow(window);
    }

    public void setClearColour(float r, float g, float b, float a) {
        glClearColor(r, g, b, a);
    }

    public boolean windowShouldClose() {
        return glfwWindowShouldClose(window);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        glfwSetWindowTitle(window, title);
    }

    public boolean isResize() {
        return resize;
    }

    public boolean isVsync() {
        return vSync;
    }

    public void setResize(boolean resize) {
        this.resize = resize;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public long getWindow() {
        return window;
    }

    public static void main(String[] args) {

        float scale = 0.5f;

        Game window = new Game("terve", 800, 600, true, true);

        //Game window = new Game("terve", (int) (60 * 16 * scale), (int) (40 * 16 * scale), true, false);
        Player player = new Player("konsta", 50, 100, 32, 32, 1.3f);
        //Player player2 = new Player("simo", 100, 100, 16, 16, 1);


        window.init();
        Camera camera = new Camera(0, 0, window.width / scale, window.height / scale);

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, window.width / scale, window.height / scale, 0, -1, 1);
        glMatrixMode(GL_MODELVIEW);

        long lastTime = System.nanoTime();

        while (!window.windowShouldClose()) {

            MouseHandler.tick();
            Input.getInstance().updatePreviousState();

            long currentTime = System.nanoTime();
            float deltaTime = (currentTime - lastTime) / 1000000000.0f;
            lastTime = currentTime;

            if (deltaTime > 0.05f) deltaTime = 0.05f;


            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            player.movePlayer();
            //player2.movePlayer();

            player.update(deltaTime);
            //player2.update(deltaTime);

            camera.setPosition(0, 0);
            camera.follow(player);   // or camera.setPosition(...) for a fixed camera
            camera.apply();

            if ((MouseHandler.isButtonJustPressed(Controls.MOUSE_LEFT))) {
                player.x = MouseHandler.getX() / scale + camera.getX();
                player.y = MouseHandler.getY() / scale + camera.getY();
            }

            //if ((MouseHandler.isButtonJustPressed(Controls.MOUSE_RIGHT))) {
            //    player2.x = MouseHandler.getX() / scale;
            //    player2.y = MouseHandler.getY() / scale;
            //}

            if (MouseHandler.isButtonDown(Controls.MOUSE_RIGHT)
                    && Input.getInstance().isPressed(GLFW_KEY_LEFT_CONTROL)) {

                int posx = (int) ((MouseHandler.getX() / scale / 16));
                int posy = (int) ((MouseHandler.getY() / scale / 16));

                System.out.println(posx);
                System.out.println(posy);

                World.getInstance().temporary.removeTile(posx, posy);
            }

            World.getInstance().temporary.render(camera);
            player.renderPlayer();
            //player2.renderPlayer();

            MouseHandler.drawCursor(scale, camera);

            window.update();

            if (Input.getInstance().isPressed(Controls.QUIT)) {
                glfwSetWindowShouldClose(window.getWindow(), true);
            }

            MouseHandler.endFrame();
        }

        window.cleanup();

    }
}
