package physics.plasma.particlepush;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

/**
 * TouchSurfaceView
 *
 * This is a touch screen interface that is created by the main program to:
 *      Handle touch input of various kinds
 *      Pass data to Renderer about the associated graphics needed on screen
 */
public class GLTouchSurfaceView extends GLSurfaceView {

    // The attached renderer will be on a different thread
    // We need variable access to it to pass it drawing coordinates
    // and talk to it in general
    GLTriangleRenderer renderer;

    // Coordinate value array that can be accessed across threads
    volatile public float[] coords = new float[16];

    /**
     * TouchSurfaceView
     *
     * This is the default constructor for the class that basically copies
     * the constructor from the GLSurfaceView class we extended to make this setup
     */
    public GLTouchSurfaceView(Context context) {
        super(context);
    }

    /**
     * onTouchEvent
     *
     * This is called every time something on the touch screen changes.
     *
     * Each time it is called there is a motion event passed along with it
     * that contains the coordinates of all the current touch positions,
     * as well as other data about them such as their associated ids, pressure, etc...
     *
     * Note that a motion event is called EVERY TIME something happens,
     * and it contains data for ALL the current pointers, not just the one that changed.
     * So if 2 things happen in rapid succession, 2 separate events will be sent
     * along with the respective states of every pointer at each time of call.
     */
    @Override
    public boolean onTouchEvent(MotionEvent e){

        // TODO Change touch interface to interact with the new Pointer class.
        // Determine what to do based on the current type of action in the gesture
        switch (e.getActionMasked()){

            // This is called when the first pointer goes down;
            // There should be only one pointer in this kind of event.
            case MotionEvent.ACTION_DOWN:

                // Set the coordinates of all the pointers
                // and send the updates to the renderer
                for(int i=0;i<e.getPointerCount() && i<coords.length/2;i++){
                    coords[2*i] = e.getX(i);
                    coords[(2*i)+1] = e.getY(i);
                }
                renderer.setCoords(coords);

                break;

            // This is called when an additional pointer goes down;
            // There should always be at least two pointers in this kind of event.
            case MotionEvent.ACTION_POINTER_DOWN:

                // Set the coordinates of all the pointers
                // and send the updates to the renderer
                for(int i=0;i<e.getPointerCount() && i<coords.length/2;i++){
                    coords[2*i] = e.getX(i);
                    coords[(2*i)+1] = e.getY(i);
                }
                renderer.setCoords(coords);

                break;

            // This is called when any pointer changes position.
            case MotionEvent.ACTION_MOVE:

                // Set the coordinates of all the pointers
                // and send the updates to the renderer
                for(int i=0;i<e.getPointerCount() && i<coords.length/2;i++){
                    coords[2*i] = e.getX(i);
                    coords[(2*i)+1] = e.getY(i);
                }
                renderer.setCoords(coords);

                break;

            // This is called when anything but the main pointer comes up;
            // There should always be at least two pointers in this kind of event,
            // Although it may be that only one still has touch coordinates.
            case MotionEvent.ACTION_POINTER_UP:

                break;

            // This is called when the last pointer comes up;
            // There should only be one pointer in this kind of event,
            // and it should have invalid touch coordinates. (Since it lifted off the screen)
            case MotionEvent.ACTION_UP:

                break;
        }
        return true;
    }

    // TODO Figure out how to reference the renderer without making this method to pass it.
    /**
     * passRenderer
     *
     * This is a weird method I use to pass the renderer as a variable.
     * I can't seem to figure out how else to push coordinates across threads.
     */
    public void passRenderer(GLTriangleRenderer newRenderer){
        renderer = newRenderer;
    }
}
