package physics.plasma.manypush;

import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * GLTriangle
 *
 * This is the triangle object that holds the vertex and color data used to create
 * and render traingles on the OpenGL surface
 */
public class GLTriangle {

    // Define vertex points for a red, green, and blue equilateral triangle.
    private final float[] vertexDataArray = {
            // X, Y, Z,
            // R, G, B, A
            -0.5f, -0.25f, 0.0f,
            1.0f, 0.0f, 0.0f, 1.0f,

            0.5f, -0.25f, 0.0f,
            0.0f, 0.0f, 1.0f, 1.0f,

            0.0f, 0.559016994f, 0.0f,
            0.0f, 1.0f, 0.0f, 1.0f};

    // Store our model's vertex data in a float buffer.
    private final FloatBuffer vertexBuffer;

    // How many bytes per float.
    private final int mBytesPerFloat = 4;

    // Initialize the model matrix. This matrix is used to move models
    // from object space (where each model can be thought of being
    // located at the center of the universe) to world space.
    private float[] mModelMatrix = new float[16];

    /**
     * GLTriangle
     *
     * This is the constructor for the triangle object.
     */
    public GLTriangle(){

        // Initialize the buffers.
        vertexBuffer = ByteBuffer.allocateDirect(vertexDataArray.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(vertexDataArray).position(0);

    }

    /**
     * getModelBuffer
     *
     * Takes the given coordinate location and creates the buffer needed
     * to render a triangle at that location.
     */
    public FloatBuffer getModelBuffer(float x, float y, float angleInDegrees){

        // Draw the triangle facing straight on
        // with the given rotation and coordinates
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, x, y, 0.0f);
        Matrix.rotateM(mModelMatrix, 0, angleInDegrees, 0.0f, 0.0f, 1.0f);

        return vertexBuffer;
    }

    /**
     * getModelMatrix
     *
     * Used to get the coordinates of the triangle vertices.
     */
    public float[] getModelMatrix() {
        return mModelMatrix;
    }
}
