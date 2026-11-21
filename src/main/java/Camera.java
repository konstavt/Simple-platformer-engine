public class Camera {
    private float x;
    private float y;
    private float viewportWidth;
    private float viewportHeight;

    public Camera(float x, float y, float viewportWidth, float viewportHeight) {
        this.x = x;
        this.y = y;
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void follow(AbstractEntity target) {
        // Center camera on target
        this.x = target.x + target.hitbox.width / 2.0f - viewportWidth / 2.0f;
        this.y = target.y + target.hitbox.height / 2.0f - viewportHeight / 2.0f;
    }

    public void apply() {
        org.lwjgl.opengl.GL11.glMatrixMode(org.lwjgl.opengl.GL11.GL_MODELVIEW);
        org.lwjgl.opengl.GL11.glLoadIdentity();
        org.lwjgl.opengl.GL11.glTranslatef(-x, -y, 0.0f);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getViewportWidth() {
        return viewportWidth;
    }

    public void setViewportWidth(float viewportWidth) {
        this.viewportWidth = viewportWidth;
    }

    public float getViewportHeight() {
        return viewportHeight;
    }

    public void setViewportHeight(float viewportHeight) {
        this.viewportHeight = viewportHeight;
    }

    public void setViewport(float viewportWidth, float viewportHeight) {
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
    }
}
