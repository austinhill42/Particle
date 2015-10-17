package physics.plasma.particlepush;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * MainActivity
 *
 * This is where the program starts.
 * You can set up the first activity to be launched when the program opens
 * by changing the manifest file. This is the first activity created,
 * so onCreate is the insertion point for the program.
 */
public class MainActivity extends Activity {

    // Hold a reference to the main GLSurfaceView
    // and it's GLTriangleRenderer
    private GLTouchSurfaceView mGLSurfaceView;
    public GLTriangleRenderer mGLRenderer;

    // Called when the program creates the activity.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if the system supports OpenGL ES 2.0.
        final ActivityManager activityManager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo =
                activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

        // If the device supports OpenGL ES 2.x we can set things up as normal.
        if (supportsEs2)
        {

            // Create a surface view to manage the touch screen and assign it to our variable
            mGLSurfaceView = new GLTouchSurfaceView(this);

            // Request an OpenGL ES 2.0 compatible context.
            mGLSurfaceView.setEGLContextClientVersion(2);

            // Create a basic triangle renderer to do the bulk work of
            // filling in between vertices (polygons can always be segmented into triangles)
            mGLRenderer = new GLTriangleRenderer();

            // Tell the surface which renderer we're assigning to it, and then do so.
            mGLSurfaceView.passRenderer(mGLRenderer);
            mGLSurfaceView.setRenderer(mGLRenderer);
        }
        else
        {
            // This is where you could create an OpenGL ES 1.x compatible
            // renderer if you wanted to support both ES 1 and ES 2.
            return;
        }

        // Put the surface view in charge of the touchscreen.
        setContentView(mGLSurfaceView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

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
