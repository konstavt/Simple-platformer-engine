import org.lwjgl.glfw.GLFWKeyCallback;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.*;

public class Input {

    private Set<Integer> controls = new HashSet<Integer>();
    private Set<Integer> previousControls = new HashSet<Integer>();

    private static Input instance;

    private GLFWKeyCallback keyCallback;

    public static Input getInstance() {
        return instance;
    }

    public boolean isPressed(int key) {
        return (controls.contains(key));
    }

    public boolean isJustPressed(int key) {
        return controls.contains(key) && !previousControls.contains(key);
    }

    public boolean isJustReleased(int key) {
        return !controls.contains(key) && previousControls.contains(key);
    }

    public void updatePreviousState() {
        previousControls.clear();
        previousControls.addAll(controls);
    }

    public static void setInstance(Input instance1) {
        instance = instance1;
    }

    public Input(long window) {

        keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (action == GLFW_PRESS) {
                    controls.add(key);
                }
                if (action == GLFW_RELEASE) {
                    controls.remove(key);
                }
            }
        };

        glfwSetKeyCallback(window, keyCallback);
    }
}
