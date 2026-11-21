import java.awt.*;
import java.awt.geom.Rectangle2D;

// Abstract class
abstract class AbstractEntity {

    public Rectangle2D.Float hitbox;
    public float x = 0.0f;
    public float y = 0.0f;
    public float mass = 1.0f;

    public AbstractEntity() {}

    public AbstractEntity(float startX, float startY, float width, float height, float mass) {
        this.x = startX;
        this.y = startY;
        this.mass = mass;
        this.hitbox = new Rectangle2D.Float(startX, startY, width, height);
    }

    public Rectangle2D.Float getHitbox() {
        return this.hitbox;
    }

    public void setHitbox(Rectangle2D.Float hitbox) {
        this.hitbox = hitbox;
    }

    public void updateHitbox() {
        this.hitbox.x = x;
        this.hitbox.y = y;
    }

    public abstract void update(float deltaTime);

}




