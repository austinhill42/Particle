package physics.plasma.manypush;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class MyGLSurfaceView extends GLSurfaceView {

    // renderer on a different thread that needs the coordinates
    GLTriangleRenderer renderer;

    // values to pass across threads
    volatile public float[] coords = new float[2];
    volatile public float[] diffs = new float[2];

    // Retain old values to calculate velocity
    private float[] oldCoords = new float[2];

    public MyGLSurfaceView(Context context) {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e){

        switch (e.getAction()){
            case MotionEvent.ACTION_DOWN:

                // Store the current touch position.
                coords[0] = e.getX();
                coords[1] = e.getY();
                renderer.setCoords(coords[0],coords[1]);

                break;

            case MotionEvent.ACTION_MOVE:

                coords[0] = e.getX();
                coords[1] = e.getY();
                renderer.setCoords(coords[0],coords[1]);

                break;
        }

        oldCoords = coords;

        return true;
    }

    public void passRenderer(GLTriangleRenderer newRenderer){
        renderer = newRenderer;
    }
}
