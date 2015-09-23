package physics.plasma.manypush;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * MainActivity
 *
 * This is the main entry point when the program is started.
 */
public class MainActivity extends AppCompatActivity {

    // Hold a reference in the activity
    // to the main GLSurfaceView and it's MyGLRenderer
    private MainActivity mActivity;
    private MyGLSurfaceView mGLSurfaceView;
    private MyGLRenderer mGLRenderer;

    /**
     * onCreate
     * @param savedInstanceState
     *
     * This method is called as the program starts.
     * It initializes the surface and the rendering parts.     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGLSurfaceView = new MyGLSurfaceView(this);

        // Check if the system supports OpenGL ES 2.0.
        final ActivityManager activityManager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo =
                activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

        if (supportsEs2)
        {
            // Request an OpenGL ES 2.0 compatible context.
            mGLSurfaceView.setEGLContextClientVersion(2);

            // Create and attach the renderer to the surface
            // passing the renderer to the other threads
            mGLRenderer = new MyGLRenderer();
            mGLSurfaceView.passRenderer(mGLRenderer);
            mGLRenderer.passRenderer(mGLRenderer);
            mGLSurfaceView.setRenderer(mGLRenderer);
        }
        else
        {
            // This is where you could create an OpenGL ES 1.x compatible
            // renderer if you wanted to support both ES 1 and ES 2.
            return;
        }

        setContentView(mGLSurfaceView);
    }

    /**
     * getRenderer
     * @return
     *
     * returns the current MyGLRenderer stored in the activity
     */
    public MyGLRenderer getRenderer(){
        return mGLRenderer;
    }

    /**
     * getSurface
     * @return
     *
     * returns the current MyGLSurfaceView stored in the activity
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

    /**
     * onCreateOptionsMenu
     * @param menu
     * @return
     *
     * this sets up the extended menu options you can access by selecting the 3 dots
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     *onOptionsItemSelected
     * @param item
     * @return
     *
     * called when a menu option is selected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
