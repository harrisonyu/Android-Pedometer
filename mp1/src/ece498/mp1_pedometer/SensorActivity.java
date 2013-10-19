package ece498.mp1_pedometer;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import au.com.bytecode.opencsv.CSVWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class SensorActivity extends Activity implements SensorEventListener {
	private boolean step_flag;
	private int steps;
	private TextView view;
	
	private float Accel_x;
	private float Accel_y;
	private float Accel_z;
	private float Gyro_x;
	private float Gyro_y;
	private float Gyro_z;
	private float Mag_x;
	private float Mag_y;
	private float Mag_z;
	private float light_intensity;
	
	private SensorManager senMan;
	private Sensor accel;
	private Sensor gyro;
	private Sensor mag;
	private Sensor light; 
	
	private static boolean running = false;
	private static Context context;
	
	String filename = "mp1_stage1.csv";
	File file;
	CSVWriter writer;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_main);
        steps = 0;
        step_flag = false;
        view = (TextView)findViewById(R.id.num_steps);
    	Accel_x = 0;
    	Accel_y = 0;
    	Accel_z = 0;
    	Gyro_x = 0;
    	Gyro_y = 0;
    	Gyro_z = 0;
    	Mag_x = 0;
    	Mag_y = 0;
    	Mag_z = 0;
    	light_intensity = 0;
    	File filedir = context.getExternalFilesDir(null);
    	file = new File(filedir, filename);
    	try {
    		writer = new CSVWriter(new FileWriter(file), ',');
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	senMan = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    	accel = senMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    	gyro = senMan.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    	mag = senMan.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    	light = senMan.getDefaultSensor(Sensor.TYPE_LIGHT);
    	final Button myButton = (Button) findViewById(R.id.button1);
    	myButton.setOnClickListener(new View.OnClickListener()
    		{
    			public void onClick(View v){
    				if(running == false)
    				{
    					running = true;
    				}
    				else
    				{
    					running = false;
    				}
    					
    			}
    		});
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    
    @Override
    public void onSensorChanged(SensorEvent event)
    {
    	if(running == true)
    	{
	    	float timestamp = event.timestamp * 1000000; // milliseconds
	    	float[] values = event.values;
	        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER ) {
	        	Accel_x = values[0];
	        	Accel_y = values[1];
	        	Accel_z = values[2];
	        	if (Accel_z > 9.8 && step_flag == false) {
	        		step_flag = true;
	        	} else if (Accel_z < 9.8 && step_flag == true) {
	        		steps++;
	        		view.setText("STEPS: " + steps);
	        		step_flag = false;
	        	}
	        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
	        	Gyro_x = values[0];
	        	Gyro_y = values[1];
	        	Gyro_z = values[2];
	        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
	        	Mag_x = values[0];
	        	Mag_y = values[1];
	        	Mag_z = values[2];
	        } else if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
	        	light_intensity = values[0];
	        }
	            String[] entry = new String[1];
	            entry[0] = Float.toString(timestamp) + "," + Float.toString(Accel_x) + ","
	            		+ Float.toString(Accel_y) + "," + Float.toString(Accel_z) + ","
	            		+ Float.toString(Gyro_x) + "," + Float.toString(Gyro_y) + ","
	            		+ Float.toString(Gyro_z) + "," + Float.toString(Mag_x) + ","
	            		+ Float.toString(Mag_y) + "," + Float.toString(Mag_z) + "," + Float.toString(light_intensity);
	            writer.writeNext(entry);
	            System.out.println("Writing: " + file);
    	}
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        senMan.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);
        senMan.registerListener(this, gyro, SensorManager.SENSOR_DELAY_NORMAL);
        senMan.registerListener(this, mag, SensorManager.SENSOR_DELAY_NORMAL);
        senMan.registerListener(this, light, SensorManager.SENSOR_DELAY_NORMAL);
    }
 
    @Override
    protected void onPause() {
        super.onPause(); 
        senMan.unregisterListener(this);
    }
   
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	try {
    		writer.close();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }
}
