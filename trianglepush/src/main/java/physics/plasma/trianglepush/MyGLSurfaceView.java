package physics.plasma.trianglepush;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class MyGLSurfaceView extends GLSurfaceView {

    volatile public float x;
    volatile public float y;

    // Retain old values to progressively calculate previous
    // position, velocity, and acceleration
    private float previousX;
    private float previousY;
    private float dX;
    private float dY;

    private MyGLRenderer mRenderer;

    public MyGLSurfaceView(Context context) {
        super(context);
    }

    public void passRenderer(MyGLRenderer renderer){
        mRenderer = renderer;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e){

        // Store the current touch position.
        x = e.getX();
        y = e.getY();
        mRenderer.setCoords(x,y);

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
}
