package physics.plasma.trianglepush;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class MyGLSurfaceView extends GLSurfaceView {

    // Retain old position values to calculate diference from new ones.
    private float mPreviousX;
    private float mPreviousY;

    public MyGLSurfaceView(Context context) {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e){

        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()){
            case MotionEvent.ACTION_BUTTON_PRESS:

            case MotionEvent.ACTION_MOVE:

            case MotionEvent.ACTION_BUTTON_RELEASE:
        }

        return false;
    }
}
