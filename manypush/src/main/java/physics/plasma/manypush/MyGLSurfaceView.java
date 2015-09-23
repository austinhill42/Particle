package physics.plasma.manypush;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

/**
 * MyGLSurfaceView
 *
 * An extention of GLSurfaceView that is used to interface with touch events.
 */
public class MyGLSurfaceView extends GLSurfaceView{

    // Hold a reference in the surface
    // to the main GLSurfaceView and it's MyGLRenderer
    private MyGLSurfaceView mGLSurfaceView;
    private MyGLRenderer mGLRenderer;

    // Retain old values to progressively calculate previous
    // position, velocity, and acceleration
    private float previousX;
    private float previousY;
    private float dX;
    private float dY;

    // Temporarily store the touch position to pass between threads
    volatile public float x;
    volatile public float y;

    /**
     * MyGLSurfaceView
     * @param context
     *
     * Constructor for our extended GLSurfaceView
     */
    public MyGLSurfaceView(Context context) {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e){

        // Store the current touch position.
        x = e.getX();
        y = e.getY();
        mGLRenderer.setCoords(x,y);

        switch (e.getAction()){
            case MotionEvent.ACTION_MOVE:

                // Calculate the difference of the current position from the previous position
                // and store in the first derivative slot.
                dX = x - previousX;
                dY = y - previousY;

        }

        previousX = x;
        previousY = y;

        return true;
    }

    /**
     * getRenderer
     * @return
     *
     * returns the current MyGLRenderer stored in the surface
     */
    public MyGLRenderer getRenderer(){
        return mGLRenderer;
    }

    /**
     * getSurface
     * @return
     *
     * returns the current MyGLSurfaceView stored in the surface
     */
    public MyGLSurfaceView getSurface(){
        return mGLSurfaceView;
    }

    /**
     * passRenderer
     * @param renderer
     *
     * sets the stored MyGLRenderer to the given input
     */
    public void passRenderer(MyGLRenderer renderer){
        mGLRenderer = renderer;
    }

    /**
     * passSurface
     * @param surface
     *
     * sets the stored MyGLSurfaceView to the given input
     */
    public void passSurface(MyGLSurfaceView surface){
        mGLSurfaceView = surface;
    }
}
