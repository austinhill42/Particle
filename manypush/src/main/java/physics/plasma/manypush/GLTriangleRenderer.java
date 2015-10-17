package physics.plasma.manypush;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * GLTriangleRenderer
 *
 * This is the rendering part of the program. It runs on a thread seperate from the main activity,
 * and should be used almost exclusively to push graphics to the screen.
 */
public class GLTriangleRenderer implements GLSurfaceView.Renderer{

    // TODO Make this particle data with the new Particle class instead
    // Triangle "sprite" to be rendered
    private GLTriangle triangle;

    // Open GL coordinates for positioning triangles
    private float[] coords = new float[16];

    // Names are pretty self evident. Based on orientation though, so they change.
    private float screenWidth;
    private float screenHeight;

    /** This is the data that sets up the buffers passed into OpenGL */
    // How many bytes per float.
    private final int mBytesPerFloat = 4;
    // How many elements per vertex.
    private final int mStrideBytes = 7 * mBytesPerFloat;
    // Offset of the position data.
    private final int mPositionOffset = 0;
    // Size of the position data in elements.
    private final int mPositionDataSize = 3;
    // Offset of the color data.
    private final int mColorOffset = 3;
    // Size of the color data in elements.
    private final int mColorDataSize = 4;

    /**
     * Transformation Matrices:
     * Model * View * Projection = MVP (Combined)
     * object center -> world -> eye -> project on screen
     *
     * The model matrix is made in the Model's class structure,
     * and should be passed in through it's getModelMatrix method
     */
    // Initialize the view matrix. This can be thought of as our camera.
    // This matrix transforms world space to eye space;
    // it positions things relative to our eye.
    private float[] mViewMatrix = new float[16];

    // Initialize the projection matrix.
    // This is used to project the scene onto a 2D screen.
    private float[] mProjectionMatrix = new float[16];

    // Initialize the final combined matrix.
    // This will be passed into the shader program.
    private float[] mMVPMatrix = new float[16];

    // This will be used to pass in the final transformation matrix.
    private int mMVPMatrixHandle;
    // This will be used to pass in model position information.
    private int mPositionHandle;
    // This will be used to pass in model color information.
    private int mColorHandle;

    /**
     * GLTriangleRenderer
     *
     * Main constructor that also spawns the triangle "sprite" that will be drawn to screen.
     */
    public GLTriangleRenderer(){
        triangle = new GLTriangle();
    }

    /**
     * onSurfaceCreated
     *
     * This is called when the rendering surface is created. It sets up the display,
     * and is used to initialize OpenGL's shader subprograms.
     */
    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {

        // TODO Setup a way to change the background into a grid of adaptable size.
        // Set the background clear color to gray.
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);

        // TODO Make a way to change the zoom and to pan around a large field of view.
        // Position the camera behind the origin.
        final float eyeX = 0.0f;
        final float eyeY = 0.0f;
        final float eyeZ = 1.5f;

        // With it looking toward the distance.
        final float lookX = 0.0f;
        final float lookY = 0.0f;
        final float lookZ = -5.0f;

        // Set what direction is up in the view.
        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;

        // Create the assocaited view matrix from the given information.
        // This matrix can be said to represent the camera position.
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

        // Load in the vertex shader and save a handle to it.
        int vertexShaderHandle = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        loadShader(vertexShaderHandle,vertexShader);

        // Load in the fragment shader and save a handle to it.
        int fragmentShaderHandle = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        loadShader(fragmentShaderHandle,fragmentShader);

        // Create a program object and save a handle to it.
        int programHandle = GLES20.glCreateProgram();

        // If the resulting program handle isn't a null reference, we can proceed.
        if (programHandle != 0)
        {
            // Bind the vertex shader to the program.
            GLES20.glAttachShader(programHandle, vertexShaderHandle);

            // Bind the fragment shader to the program.
            GLES20.glAttachShader(programHandle, fragmentShaderHandle);

            // Bind attributes
            GLES20.glBindAttribLocation(programHandle, 0, "a_Position");
            GLES20.glBindAttribLocation(programHandle, 1, "a_Color");

            // Link the two shaders together into the program.
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

        // If for some reason the program handle doesn't get attached
        // we throw an exception and send the user a message
        if (programHandle == 0)
        {
            throw new RuntimeException("Error creating program.");
        }

        // Set program's input handles. These will be used to pass in values to the program.
        mMVPMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_MVPMatrix");
        mPositionHandle = GLES20.glGetAttribLocation(programHandle, "a_Position");
        mColorHandle = GLES20.glGetAttribLocation(programHandle, "a_Color");

        // Tell OpenGL to use the created program when rendering.
        GLES20.glUseProgram(programHandle);
    }

    /**
     * onSurfaceChanged
     *
     * This gets called when the screen dimensions or orientation change.
     * It's where we account for the change in touch coordinates and screen size.
     */
    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {

        // TODO Orientation changes will have to work with any camera zoom or pan.

        //Store the width and height for further use.
        screenWidth = width;
        screenHeight = height;

        // Set the OpenGL viewport to the same size as the surface.
        GLES20.glViewport(0, 0, width, height);

        // Create a new perspective projection matrix.
        // The smallest dimension will always have maximum coordinates of +-1
        // While the largest will always have max coordinates that are
        // the ratio of the largest side to the smallest side: +-(max/min)
        float ratio;
        float left = -1.0f;
        float right = 1.0f;
        float bottom = -1.0f;
        float top = 1.0f;
        float near = 1.0f;
        float far = 10.0f;

        // Portrait Orientation
        if(width<=height){
            ratio = (float) width / height;
            left = -ratio;
            right = ratio;
            bottom = -1.0f;
            top = 1.0f;
        }

        // Landscape Orientation
        else if(height<width){
            ratio = (float) height/width;
            left = -1.0f;
            right = 1.0f;
            bottom = -ratio;
            top = ratio;
        }

        // This sets up the projection matrix to scale our objects from a 3d surface to a 2d screen.
        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);

    }

    /**
     * onDrawFrame
     *
     * This is called every time the system asks the renderer to draw a frame.
     * It can be used to update time counters, but should mainly be
     * for actually drawing objects on the screen.
     */
    @Override
    public void onDrawFrame(GL10 gl10) {

        // TODO onDrawFrame Needs to be changed to render particles using the System Manager.

        // I don't quite know what this line does. Clear the buffer for a new one to be made?
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        // Draw a complete rotation every 10 seconds.
        long time = SystemClock.uptimeMillis() % 10000L;
        float angleInDegrees = (360.0f / 10000.0f) * ((int) time);

        // Draw a triangle at every coordinated set in the stored array,
        // with the given orientation.
        for(int i=0;(2*i)<coords.length;i++){
            drawTriangle(triangle.getModelBuffer(coords[2*i],coords[(2*i)+1],angleInDegrees));
        }
    }

    // TODO The actual rendering methods should be moved into the Particle class if possible
    /**
     * drawTriangle
     *
     * Draws a triangle from the given vertex data in the buffer.
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

        // This multiplies the view matrix by the model matrix,
        // and stores the result in the MVP matrix
        // (which then contains model * view).
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, triangle.getModelMatrix(), 0);

        // This multiplies the modelview matrix by the projection matrix,
        // and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);

    }

    // TODO This coordinate conversion method will be moved into the System Manager.
    /**
     * setCoords
     *
     * This converts the touch coordinates from androids coordinate system
     * into OpenGL's rendering coordinate system.
     */
    public void setCoords(float[] in){

        // Portrait View
        if(screenWidth<=screenHeight){
            for(int i=0;(2*i)<in.length;i++){
                coords[2*i] = (in[2*i]-(screenWidth/2))*(2/screenWidth);
                coords[(2*i)+1] = ((screenHeight/2)-in[(2*i)+1])*(2/screenHeight)*(screenHeight/screenWidth);
            }
        }

        // Landscape View
        else if(screenHeight<screenWidth){
            for(int i=0;(2*i)<in.length;i++){
                coords[2*i] = (in[2*i]-(screenWidth/2))*(2/screenWidth)*(screenWidth/screenHeight);
                coords[(2*i)+1] = ((screenHeight/2)-in[(2*i)+1])*(2/screenHeight);
            }
        }

    }

    /**
     * loadShader
     *
     * This takes a string that contains a OpenGL shader program and builds the program,
     * attaching the resulting handle to the given variable.
     */
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

        // Throws an exception and sends appropriate message if the build fails.
        if (shaderHandle == 0)
        {
            throw new RuntimeException("Error creating vertex shader.");
        }
    }
}
