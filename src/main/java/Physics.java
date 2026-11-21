import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Physics {

    public static final float GRAVITY = 2000.0f;
    public static final float FRICTION = 0.90f;

    private AbstractEntity entity;
    private float velocityX = 0.0f;
    private float velocityY = 0.0f;
    private float accelerationX = 0.0f;
    private float accelerationY = 0.0f;
    public boolean isGrounded = false;

    public Physics(AbstractEntity entity) {
        this.entity = entity;
    }

    public void applyForce(float forceX, float forceY) {
        this.accelerationX += forceX / entity.mass;
        this.accelerationY += forceY / entity.mass;

    }

    public void updatePhysics(float deltaTime) {
        float moveX = velocityX * deltaTime;
        float moveY = velocityY * deltaTime;

        if (!isGrounded) {
            if (velocityY > 0) {
                applyForce(0, 2 * GRAVITY * entity.mass);
            } else applyForce(0, GRAVITY * entity.mass);
        }

        velocityX += (accelerationX * deltaTime);
        velocityY += (accelerationY * deltaTime);

        velocityX *= FRICTION;

        if (Math.abs(velocityX) < 5.0f) velocityX = 0.0f;

        isGrounded = false;
        collisionCheck(moveX, moveY);

        accelerationX = 0;
        accelerationY = 0;
    }

    public void collisionCheck(float moveX, float moveY) {

        // --- X-AXIS COLLISION ---
        entity.x += moveX;
        entity.updateHitbox();

        // Ask the map FOR the block we hit, not just if we hit one
        Rectangle2D.Float hitBlockX = World.getInstance().infinite.getCollidingTile(entity.hitbox);

        if (hitBlockX != null) {
            if (moveX > 0) {
                // Moving Right: Snap the player's right edge to the block's left edge
                entity.x = hitBlockX.x - entity.hitbox.width;

            } else if (moveX < 0) {
                // Moving Left: Snap the player's left edge to the block's right edge
                entity.x = hitBlockX.x + hitBlockX.width;
            }
            velocityX = 0.0f;
            entity.updateHitbox();
        }

        // --- Y-AXIS COLLISION ---
        entity.y += moveY;
        entity.updateHitbox();

        Rectangle2D.Float hitBlockY = World.getInstance().infinite.getCollidingTile(entity.hitbox);

        if (hitBlockY != null) {
            if (moveY > 0) {
                // Moving Down (Falling): Snap player's bottom to the block's top
                entity.y = hitBlockY.y - entity.hitbox.height;
                isGrounded = true;
            } else if (moveY < 0) {
                // Moving Up (Jumping): Snap player's top to the block's bottom
                entity.y = hitBlockY.y + hitBlockY.height;
            }
            velocityY = 0.0f;
            entity.updateHitbox();
        }
    }

    public void jump(float jumpPower) {
        if (isGrounded) {
            this.velocityY = -jumpPower / entity.mass;
            this.isGrounded = false;
        }
    }

    // Called only on the exact frame the key is pressed down
    public void startJump(float jumpPower) {
        if (isGrounded) {
            this.velocityY = -jumpPower / entity.mass;
            this.isGrounded = false;
        }
    }

    // Called on the exact frame the key is released
    public void stopJump() {
        // If the spacebar is released while moving UP (negative Y velocity),
        // cut the velocity so the player starts falling sooner.
        if (this.velocityY < 0) {
            this.velocityY *= 0.5f; // 0.5 is a magic number; tweak it for game feel!
        }
    }

    public void resetPos() {
        entity.x = 100 * entity.mass;
        entity.y = 100;
    }
}


