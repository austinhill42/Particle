package physics.plasma.manypush;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLTriangleRenderer implements GLSurfaceView.Renderer{

    // Triangle to be rendered
    private GLTriangle triangle;

    // Open GL coordinates for positioning triangles
    private float[] coords = new float[8];
    private float x;
    private float y;

    private float screenWidth;
    private float screenHeight;

    /** How many bytes per float. */
    private final int mBytesPerFloat = 4;

    /** How many elements per vertex. */
    private final int mStrideBytes = 7 * mBytesPerFloat;

    /** Offset of the position data. */
    private final int mPositionOffset = 0;

    /** Size of the position data in elements. */
    private final int mPositionDataSize = 3;

    /** Offset of the color data. */
    private final int mColorOffset = 3;

    /** Size of the color data in elements. */
    private final int mColorDataSize = 4;

    //Transformation Matrices: Model, View, Projection, MVP (Combined)
    //      object center -> world -> eye -> project on screen

    /**
     * Initialize the view matrix. This can be thought of as our camera.
     * This matrix transforms world space to eye space;
     * it positions things relative to our eye.
     */
    private float[] mViewMatrix = new float[16];

    /**
     * Initialize the projection matrix. This is used to project the scene
     * onto a 2D viewport.
     */
    private float[] mProjectionMatrix = new float[16];

    /**
     * Initialize the final combined matrix.
     * This will be passed into the shader program.
     */
    private float[] mMVPMatrix = new float[16];

    /** This will be used to pass in the transformation matrix. */
    private int mMVPMatrixHandle;

    /** This will be used to pass in model position information. */
    private int mPositionHandle;

    /** This will be used to pass in model color information. */
    private int mColorHandle;

    /**
     * Main constructor for rendering the shape data.
     */
    public GLTriangleRenderer(){
        triangle = new GLTriangle();
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {

        // Set the background clear color to gray.
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);

        // Position the eye behind the origin.
        final float eyeX = 0.0f;
        final float eyeY = 0.0f;
        final float eyeZ = 1.5f;

        // We are looking toward the distance
        final float lookX = 0.0f;
        final float lookY = 0.0f;
        final float lookZ = -5.0f;

        // Set our up vector. This is where our head would be pointing were we holding the camera.
        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;

        // Set the view matrix. This matrix can be said to represent the camera position.
        // NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
        // view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

        final String vertexShader =
          "uniform mat4 u_MVPMatrix;      \n"		// A constant representing the combined model/view/projection matrix.

        + "attribute vec4 a_Position;     \n"		// Per-vertex position information we will pass in.
        + "attribute vec4 a_Color;        \n"		// Per-vertex color information we will pass in.

        + "varying vec4 v_Color;          \n"		// This will be passed into the fragment shader.

        + "void main()                    \n"		// The entry point for our vertex shader.
        + "{                              \n"
        + "   v_Color = a_Color;          \n"		// Pass the color through to the fragment shader.
                                                    // It will be interpolated across the triangle.
        + "   gl_Position = u_MVPMatrix   \n" 	    // gl_Position is a special variable used to store the final position.
        + "               * a_Position;   \n"       // Multiply the vertex by the matrix to get the final point in
        + "}                              \n";      // normalized screen coordinates.

        final String fragmentShader =
          "precision mediump float;       \n"		// Set the default precision to medium. We don't need as high of a
                                                    // precision in the fragment shader.
        + "varying vec4 v_Color;          \n"		// This is the color from the vertex shader interpolated across the
                                                    // triangle per fragment.
        + "void main()                    \n"		// The entry point for our fragment shader.
        + "{                              \n"
        + "   gl_FragColor = v_Color;     \n"		// Pass the color directly through the pipeline.
        + "}                              \n";

        // Load in the vertex shader.
        int vertexShaderHandle = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        loadShader(vertexShaderHandle,vertexShader);

        // Load in the fragment shader shader.
        int fragmentShaderHandle = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        loadShader(fragmentShaderHandle,fragmentShader);

        // Create a program object and store the handle to it.
        int programHandle = GLES20.glCreateProgram();

        if (programHandle != 0)
        {
            // Bind the vertex shader to the program.
            GLES20.glAttachShader(programHandle, vertexShaderHandle);

            // Bind the fragment shader to the program.
            GLES20.glAttachShader(programHandle, fragmentShaderHandle);

            // Bind attributes
            GLES20.glBindAttribLocation(programHandle, 0, "a_Position");
            GLES20.glBindAttribLocation(programHandle, 1, "a_Color");

            // Link the two shaders together into a program.
            GLES20.glLinkProgram(programHandle);

            // Get the link status.
            final int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);

            // If the link failed, delete the program.
            if (linkStatus[0] == 0)
            {
                GLES20.glDeleteProgram(programHandle);
                programHandle = 0;
            }
        }

        if (programHandle == 0)
        {
            throw new RuntimeException("Error creating program.");
        }

        // Set program handles. These will later be used to pass in values to the program.
        mMVPMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_MVPMatrix");
        mPositionHandle = GLES20.glGetAttribLocation(programHandle, "a_Position");
        mColorHandle = GLES20.glGetAttribLocation(programHandle, "a_Color");

        // Tell OpenGL to use this program when rendering.
        GLES20.glUseProgram(programHandle);
    }



    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {

        //Store the width and height for further use
        screenWidth = width;
        screenHeight = height;

        // Set the OpenGL viewport to the same size as the surface.
        GLES20.glViewport(0, 0, width, height);

        // Create a new perspective projection matrix. The height will stay the same
        // while the width will vary as per aspect ratio.
        //final float ratio = (float) width / height;

        float ratio = 1.0f;
        float left = -1.0f;
        float right = 1.0f;
        float bottom = -1.0f;
        float top = 1.0f;
        float near = 1.0f;
        float far = 10.0f;

        if(width<=height){
            ratio = (float) width / height;
            left = -ratio;
            right = ratio;
            bottom = -1.0f;
            top = 1.0f;
        }else if(height<width){
            ratio = (float) height/width;
            left = -1.0f;
            right = 1.0f;
            bottom = -ratio;
            top = ratio;
        }

        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);

    }

    @Override
    public void onDrawFrame(GL10 gl10) {

        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        // Do a complete rotation every 10 seconds.
        long time = SystemClock.uptimeMillis() % 10000L;
        float angleInDegrees = (360.0f / 10000.0f) * ((int) time);

        for(int i=0;(2*i)<coords.length;i++){
            drawTriangle(triangle.drawBuffer(coords[2*i],coords[(2*i)+1], angleInDegrees));
        }
    }

    /**
     * Draws a triangle from the given vertex data.
     *
     * @param aTriangleBuffer The buffer containing the vertex data.
     */
    public void drawTriangle(final FloatBuffer aTriangleBuffer){

        // Pass in the position information
        aTriangleBuffer.position(mPositionOffset);
        GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false,
                mStrideBytes, aTriangleBuffer);

        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Pass in the color information
        aTriangleBuffer.position(mColorOffset);
        GLES20.glVertexAttribPointer(mColorHandle, mColorDataSize, GLES20.GL_FLOAT, false,
                mStrideBytes, aTriangleBuffer);

        GLES20.glEnableVertexAttribArray(mColorHandle);

        // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
        // (which then contains model * view).
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, triangle.getModelMatrix(), 0);

        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);

    }

    /**
     * Set the touch variables for use in placing the objects
     */
    public void setCoords(float xIn,float yIn){
        if(screenWidth<=screenHeight){
            x = (xIn-(screenWidth/2))*(2/screenWidth);
            y = ((screenHeight/2)-yIn)*(2/screenHeight)*(screenHeight/screenWidth);
        }else if(screenHeight<screenWidth){
            x = (xIn-(screenWidth/2))*(2/screenWidth)*(screenWidth/screenHeight);
            y = ((screenHeight/2)-yIn)*(2/screenHeight);
        }

    }

    /**
     * Set the touch variables for use in placing the objects
     */
    public void setCoords(float[] in){
        if(screenWidth<=screenHeight){
            for(int i=0;(i+2)<in.length;i+=2){
                coords[i] = (in[i]-(screenWidth/2))*(2/screenWidth);
                coords[i+1] = ((screenHeight/2)-in[i+1])*(2/screenHeight)*(screenHeight/screenWidth);
            }
        }else if(screenHeight<screenWidth){
            for(int i=0;(i+2)<in.length;i+=2){
                coords[i] = (in[i]-(screenWidth/2))*(2/screenWidth)*(screenWidth/screenHeight);
                coords[i+1] = ((screenHeight/2)-in[i+1])*(2/screenHeight);
            }
        }

    }

    private void loadShader(int shaderHandle, String shader) {
        if (shaderHandle != 0)
        {
            // Pass in the shader source.
            GLES20.glShaderSource(shaderHandle, shader);

            // Compile the shader.
            GLES20.glCompileShader(shaderHandle);

            // Get the compilation status.
            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

            // If the compilation failed, delete the shader.
            if (compileStatus[0] == 0)
            {
                GLES20.glDeleteShader(shaderHandle);
                shaderHandle = 0;
            }
        }

        if (shaderHandle == 0)
        {
            throw new RuntimeException("Error creating vertex shader.");
        }
    }
}
