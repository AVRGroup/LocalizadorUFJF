package com.example.localizador;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.content.Context;

public class Giroscopio extends Activity implements SensorEventListener {

	private SensorManager mSensorManager;
	Sensor accelerometer;
	Sensor magnetometer;
	boolean orientok;
	Context context;

	float[] mGravity;
	float[] mGeomagnetic;

	public Giroscopio(Context context) {
		this.context = context;
		mSensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
		accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

		orientok = mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_UI);

		// test if orientation sensor exist on the device
		if (!orientok) {
			mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION));
		}

	}

	public void onSensorChanged(SensorEvent event) {
//		 switch (event.sensor.getType()) {
//		 case Sensor.TYPE_ORIENTATION:
//		 onOrientChanged(event);
//		 break;
//		 }
		
		float azimut;
		
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
			mGravity = event.values;
		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
			mGeomagnetic = event.values;
		if (mGravity != null && mGeomagnetic != null) {
			float R[] = new float[9];
			float I[] = new float[9];
			boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
			if (success) {
				float orientation[] = new float[3];
				SensorManager.getOrientation(R, orientation);
				azimut = orientation[0]; // orientation contains: azimut, pitch
											// and roll
				
				Log.i("AZIMUTH", String.valueOf(azimut)); // 0 ~ 360
				//Log.i("PITCH", String.valueOf(azimut[1])); // -180 ~ 180
				//Log.i("ROLL", String.valueOf(azimut[2])); // -80 ~ 80
				
			}
		}
	}

	 public void onOrientChanged(SensorEvent event) {
	 float azimuth, pitch, roll;
	 azimuth = event.values[0];
	 pitch = event.values[1];
	 roll = event.values[2];
	 //Log.i("AZIMUTH", String.valueOf(azimuth)); // 0 ~ 360
	 // Log.i("PITCH", String.valueOf(pitch)); // -180 ~ 180
	 // Log.i("ROLL", String.valueOf(roll)); // -80 ~ 80
	 }

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}
}
