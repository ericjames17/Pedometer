package au.edu.uq.eric.pedometer;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private long lastUpdate = 0;
    private long stepCount = 0;
    private long maxAcclCount = 0;
    private double maxAccl = 1;
    private double thresholdAccl = 0.5;

    private double previousAccl = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        .setAction("Action", null).show();
        }
        });
        */

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    private void setSupportActionBar(Toolbar toolbar) {
    }

    private double[] gravity = new double[]
            { 0, 0, 0 };

    /*
    private float[] linearAcceleration = new float[]
    { 0, 0, 0 };
    */

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public final void onSensorChanged(SensorEvent event){

        final double alpha = 0.8;

        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

        double accelerationX = event.values[0] - gravity[0];
        double accelerationY = event.values[1] - gravity[1];
        double accelerationZ = event.values[2] - gravity[2];

        final TextView textView4 = (TextView) findViewById(R.id.step_count);
        final TextView textView5 = (TextView) findViewById(R.id.max_accl);
        final TextView textView6 = (TextView) findViewById(R.id.linear_accl);

        final double acceleration = Math.sqrt(Math.pow(accelerationX, 2) + Math.pow(accelerationZ, 2) + Math.pow(accelerationY, 2));

        final Button button = (Button) findViewById(R.id.startTrace);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                stepCount = 0;
                maxAcclCount = 0;
                maxAccl = 1;
                thresholdAccl = 0.9;
                textView4.setText(String.valueOf(stepCount));
                textView5.setText(String.valueOf(thresholdAccl));
                //textView6.setText(String.valueOf(acceleration));
                textView6.setText("Move, now!");
            }
        });

        final TextView textView1 = (TextView) findViewById(R.id.acceleration_x);
        textView1.setText(String.valueOf(accelerationX));
        final TextView textView2 = (TextView) findViewById(R.id.acceleration_y);
        textView2.setText(String.valueOf(accelerationY));
        final TextView textView3 = (TextView) findViewById(R.id.acceleration_z);
        textView3.setText(String.valueOf(accelerationZ));

        long realTime = event.timestamp;

        if (acceleration > 5){
            textView6.setText("Shaking");
        }/* else{
            if (acceleration > 0.8) {
                textView6.setText("Walking");
            } else {
                textView6.setText("Move, now");
            }
        }*/


        if ((acceleration > 0.7*maxAccl) && (realTime - lastUpdate > 300000000)){
            maxAccl = (maxAccl*maxAcclCount + acceleration)/(maxAcclCount+1);
            maxAcclCount = maxAcclCount + 1;
            if (maxAccl > 1.8) {
                thresholdAccl = 0.5 * maxAccl;    //may need to adujust the threshold
            }
        }

        /*if (realTime - lastUpdate > 500000000) {    //500,000,000ns = 0.5s
            final TextView textView1 = (TextView) findViewById(R.id.acceleration_x);
            textView1.setText(String.valueOf(accelerationX));
            final TextView textView2 = (TextView) findViewById(R.id.acceleration_y);
            textView2.setText(String.valueOf(accelerationY));
            final TextView textView3 = (TextView) findViewById(R.id.acceleration_z);
            textView3.setText(String.valueOf(accelerationZ));
        */

        if ((acceleration > thresholdAccl) && (realTime - lastUpdate > 300000000)){
        //if (Math.abs(acceleration - previousAccl) > 2){
            stepCount += 1;

            //final TextView textView4 = (TextView) findViewById(R.id.step_count);
            textView4.setText(String.valueOf(stepCount));
            //final TextView textView5 = (TextView) findViewById(R.id.max_accl);
            textView5.setText(String.valueOf(thresholdAccl));
            //final TextView textView6 = (TextView) findViewById(R.id.linear_accl);
            //textView6.setText(String.valueOf(acceleration));

            lastUpdate = realTime;
            previousAccl = acceleration;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
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
