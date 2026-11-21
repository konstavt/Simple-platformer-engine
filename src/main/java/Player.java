import java.awt.*;
import java.awt.geom.Rectangle2D;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2f;

public class Player extends AbstractEntity {

    private String name;
    private Physics physics;
    private float speed = 4000;
    private float jumpPower = 800;
    private float mass = 1;

    public Player(String name, int posX, int posY, int width, int height, float mass) {
        super(posX, posY, width, height, mass);
        this.name = name;
        this.physics = new Physics(this);
    }

    @Override
    public void update(float deltaTime) {
        physics.updatePhysics(deltaTime);
    }

    public void renderPlayer() {

        glLineWidth(4.0f);
        glBegin(GL_LINE_LOOP);
        glColor3f(0.4f, 0.9f, 0.3f); // Blue color


        glVertex2f(hitbox.x, hitbox.y);
        glVertex2f(hitbox.x + hitbox.width, hitbox.y);
        glVertex2f(hitbox.x + hitbox.width, hitbox.y + hitbox.height);
        glVertex2f(hitbox.x, hitbox.y + hitbox.height);
        glEnd();
        glColor3f(1.0f, 1.0f, 1.0f);
        glLineWidth(1.0f);

    }

    public void movePlayer() {

        // Requires an input manager that can distinguish between "just pressed" and "just released"
        if (Input.getInstance().isJustPressed(Controls.JUMP)) physics.startJump(jumpPower);
        if (Input.getInstance().isJustReleased(Controls.JUMP)) physics.stopJump();
        if (Input.getInstance().isPressed(Controls.LEFT)) physics.applyForce(-speed, 0.0f);
        if (Input.getInstance().isPressed(Controls.RIGHT)) physics.applyForce(speed, 0.0f);
        if (Input.getInstance().isPressed(Controls.DOWN)) physics.resetPos();
    }
}
