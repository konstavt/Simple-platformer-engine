import org.lwjgl.system.MemoryStack;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

public class Texture {
    private int textureID;
    private int width;
    private int height;

    public int getId() {
        return this.textureID;
    }

    public Texture(String filepath) {
        // 1. Generate the texture ID on the GPU
        textureID = glGenTextures();

        // 2. Bind the texture so subsequent setup calls affect it
        glBindTexture(GL_TEXTURE_2D, textureID);

        // 3. Set texture parameters (Crucial for 2D games!)
        // GL_NEAREST prevents blurry/buzzy scaling (keeps pixel art crisp)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        // 4. Load the image using STBImage
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            // OpenGL expects the Y-axis to go bottom-to-top, but images are top-to-bottom
            stbi_set_flip_vertically_on_load(false);

            // Load the image into a ByteBuffer
            ByteBuffer image = stbi_load(filepath, w, h, channels, 4);
            if (image == null) {
                throw new RuntimeException("Failed to load texture file: " + filepath + "\n" + stbi_failure_reason());
            }

            this.width = w.get();
            this.height = h.get();

            // 5. Upload the image data to the GPU
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image);

            // 6. Free the system memory (the GPU has its own copy now)
            stbi_image_free(image);
        }
    }
    public int getWidth() { return this.width; }
    public int getHeight() { return this.height; }

    public void bind() { glBindTexture(GL_TEXTURE_2D, textureID); }
    public void unbind() { glBindTexture(GL_TEXTURE_2D, 0); }
}
