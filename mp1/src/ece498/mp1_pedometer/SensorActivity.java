package ece498.mp1_pedometer;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.format.Time;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import au.com.bytecode.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class SensorActivity extends Activity implements SensorEventListener {
	private boolean step_flag;
	private int steps;
	private TextView stepsDisplay;
	private TextView directions;
	private TextView wifiView;
	
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
	
	private double directionDegree;
	
	private WifiManager wifiMan;
	private WifiEventReceiver wifiReceiver;
	private ScanResult strongestAP;
	
	private SensorManager senMan;
	private Sensor accel;
	private Sensor gyro;
	private Sensor mag;
	private Sensor light; 
	
	private static AudioRecord sound;
	private static short[] data = new short[100];

	
	private static String compassVal;
	private static double stepSize = 26.5;
	private double displacement = 0;
	
	private static boolean running = false;
	private static Context context;
	private static int turn = 0;
	
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
        stepsDisplay = (TextView)findViewById(R.id.num_steps);
        directions = (TextView)findViewById(R.id.direction);
        wifiView = (TextView)findViewById(R.id.wifi);
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
    	directionDegree = 0;
    	File filedir = context.getExternalFilesDir(null);
    	Time currentTime = new Time();
    	currentTime.setToNow();
    	filename = "yu91_achiang3_" + currentTime.format2445() + ".csv";
    	file = new File(filedir, filename);
    	try {
    		writer = new CSVWriter(new FileWriter(file), ',');
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	senMan = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    	wifiMan = (WifiManager) getSystemService(Context.WIFI_SERVICE);
    	wifiReceiver = new WifiEventReceiver();
    	registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    	accel = senMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    	gyro = senMan.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    	mag = senMan.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    	light = senMan.getDefaultSensor(Sensor.TYPE_LIGHT);
    	sound = new AudioRecord(MediaRecorder.AudioSource.MIC,
                8000, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,2048);
    	final Button myButton = (Button) findViewById(R.id.button1);
    	myButton.setOnClickListener(new View.OnClickListener()
    		{
    			public void onClick(View v){
    				running = running == false ? true : false;
    				if (running == true) {
    					wifiMan.startScan();
    		    		sound.startRecording();
    				}
    				else
    				{
    					sound.stop();
    				}
    			}
    		});

    	final Button wifiScan = (Button) findViewById(R.id.button2);
    	wifiScan.setOnClickListener(new View.OnClickListener()
    		{
    			public void onClick(View v){
    					wifiMan.startScan();
    					turn++;
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
    		sound.read(data, 0, 100);
    		double avg = 0;
    		double total = 0;
    		for(int i = 0; i < data.length; i++)
    		{
    			if(data[i] != 0)
    			{
    				avg = avg+data[i];
    				total++;
    			}
    		}
    		avg = avg/total;
    		float decibel = (float)((20.0*Math.log10((float)(Math.abs((float)(avg)/8000.0)))));
    		//stepsDisplay.setText("Decibel" +" " +  decibel);
	    	float timestamp = event.timestamp * 1000000; // milliseconds
	    	float[] values = event.values;
	        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER ) {
	        	Accel_x = values[0];
	        	Accel_y = values[1];
	        	Accel_z = values[2];
	        	if (Accel_z > 9.8 && step_flag == false) {
	        		step_flag = true;
	        	} else if (Accel_z <= 9.8 && step_flag == true) {
	        		steps++;
	        		displacement = displacement + stepSize;
	        		stepsDisplay.setText("STEPS: " + steps + " Displacement: " + displacement + "inches");
	        		step_flag = false;
	        	}
	        	/*
	        	float geoMag[] = new float[3];
	        	geoMag[0] = Gyro_x;
	        	geoMag[1] = Gyro_y;
	        	geoMag[2] = Gyro_z;
	        	float R[] = new float[9];
	        	float I[] = new float[9];
	        	if (SensorManager.getRotationMatrix(R, I, values, geoMag)) {
	        		float orientation[] = new float[3];
	        		SensorManager.getOrientation(R, orientation);
	        		directionDegree = Math.toDegrees(orientation[0]);
	        		directions.setText("Degrees: " + directionDegree + " Direction: " + degreesToDirection(directionDegree));
	        	}
	        	*/
	        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
	        	Gyro_x = values[0];
	        	Gyro_y = values[1];
	        	Gyro_z = values[2];
	        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
	        	Mag_x = values[0];
	        	Mag_y = values[1];
	        	Mag_z = values[2];
	        	float gravity[] = new float[3];
	        	gravity[0] = 1;
	        	gravity[1] = 1;
	        	gravity[2] = Accel_z;
	        	float R[] = new float[9];
	        	float I[] = new float[9];
	        	if (SensorManager.getRotationMatrix(R, I, gravity, values)) {
	        		float orientation[] = new float[3];
	        		SensorManager.getOrientation(R, orientation);
	        		directionDegree = Math.toDegrees(orientation[0]);
	        		if(directionDegree < 0)
	        		{
	        			directionDegree = directionDegree + 360;
	        		}
	        		compassVal = degreesToDirection(directionDegree);
	        		directions.setText("Degrees: " + directionDegree + " Direction: " + compassVal);
	        	}
	        } else if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
	        	light_intensity = values[0];
	        }
	            String[] entry = new String[1];
	            entry[0] = Float.toString(timestamp) + "," + Float.toString(Accel_x) + ","
	            		+ Float.toString(Accel_y) + "," + Float.toString(Accel_z) + ","
	            		+ Float.toString(Gyro_x) + "," + Float.toString(Gyro_y) + ","
	            		+ Float.toString(Gyro_z) + "," + Float.toString(Mag_x) + ","
	            		+ Float.toString(Mag_y) + "," + Float.toString(Mag_z) + "," + Float.toString(light_intensity) + "," + Float.toString(decibel) + ","
	            		+ compassVal + "," + directionDegree + ","+ (strongestAP != null ? strongestAP.SSID + "," + strongestAP.level : "NULL,NULL") + "," + turn;
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
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }
 
    @Override
    protected void onPause() {
        super.onPause(); 
        senMan.unregisterListener(this);
        unregisterReceiver(wifiReceiver);
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
    
    private String degreesToDirection(double degrees) {
    	if (degrees >= 337.5 || degrees < 22.5)
    		return "N";
    	else if (degrees >= 22.5 && degrees < 67.5)
    		return "NE";
    	else if (degrees >= 67.5 && degrees < 112.5)
    		return "E";
    	else if (degrees >= 112.5 && degrees < 157.5)
    		return "SE";
    	else if (degrees >= 157.5 && degrees < 202.5)
    		return "S";
    	else if (degrees >= 202.5 && degrees < 247.5)
    		return "SW";
    	else if (degrees >= 247.5 && degrees < 292.5)
    		return "W";
    	else if (degrees >= 292.5 && degrees < 337.5)
    		return "NW";
    	else
    		return "X";
    }
    
    class WifiEventReceiver extends BroadcastReceiver {
    	public void onReceive(Context context, Intent intent) {
    		int strongestLevel = -1000;
    		List<ScanResult> results = wifiMan.getScanResults();
    		for (int i = 0; i < results.size(); i++) {
    			if (results.get(i).level > strongestLevel) {
    				strongestLevel = results.get(i).level;
    				strongestAP = results.get(i);
    			}
    		}
    		if (strongestAP != null)
    		{
    			wifiView.setText("Strongest AP: " + strongestAP.SSID + " Level: " + strongestAP.level);
    		}
    	}
    }
}

