package physics.plasma.manypush;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class MyGLSurfaceView extends GLSurfaceView {

    // renderer on a different thread that needs the coordinates
    GLTriangleRenderer renderer;

    // values to pass across threads
    volatile public float[] coords = new float[8];

    public MyGLSurfaceView(Context context) {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e){

        switch (e.getAction()){

            // when a main touch happens
            case MotionEvent.ACTION_DOWN:

                // Store the main touch position.
                //coords[0] = e.getX();
                //coords[1] = e.getY();
                //renderer.setCoords(coords);

                // set the newest pointer coordinates in the array
                // and send the updates to the renderer
                coords[(2 * e.getPointerId(e.getActionIndex()))] = e.getX(e.getActionIndex());
                coords[(2 * e.getPointerId(e.getActionIndex()))+1] = e.getY(e.getActionIndex());
                renderer.setCoords(coords);

                break;

            // when a secondary touch goes down
            case MotionEvent.ACTION_POINTER_DOWN:

                // set the newest pointer coordinates in the array
                // and send the updates to the renderer
                coords[(2 * e.getPointerId(e.getActionIndex()))] = e.getX(e.getActionIndex());
                coords[(2 * e.getPointerId(e.getActionIndex()))+1] = e.getY(e.getActionIndex());
                renderer.setCoords(coords);

                break;

            // when any touch pointer moves
            case MotionEvent.ACTION_MOVE:

                // update the coordinates and send to the renderer
                for(int i=0;i<e.getPointerCount();i++){
                    coords[2*i] = e.getX(i);
                    coords[(2*i)+1] = e.getY(i);
                }
                renderer.setCoords(coords);

                break;

            // when a secondary touch lifts off
            case MotionEvent.ACTION_POINTER_UP:

                break;

            case MotionEvent.ACTION_UP:

                break;
        }
        return true;
    }

    public void passRenderer(GLTriangleRenderer newRenderer){
        renderer = newRenderer;
    }
}
