package com.android.sensor.virtualjax;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.android.sensor.virtualjax.quaternion;
import com.kami.Tools.uplaodTools;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class dataCollectionActivity extends Activity implements SensorEventListener,OnClickListener {
	private static final String TAG = "Sensor Data Collection";

	private BufferedWriter mLogAcc;
//	private BufferedWriter mlogCompass;
//	private BufferedWriter mLogOrientation;
	private BufferedWriter mLogRotation;
	private BufferedWriter mLogposition;

	private int countAcc, countCom, countOrient, countRota;

	private SensorManager mgr;
	private Sensor accel;
//	private Sensor compass;
//	private Sensor orient;
	private Sensor rovectorSensor;
	
	quaternion WPosition;
	quaternion WtPosition;
	quaternion currentQuaternion;
	quaternion conjugateQuaternion;

	private TextView preferred;
	private Button uploadTest;


	private boolean ready = false;

	private float[] accelValues = new float[3];
	private long accTime = 0;
//	private float[] compassValues = new float[3];
//	private long compTime = 0;
	private float[] rotationValues = new float[4];
	private long rotaTime = 0;

//	private float[] inR = new float[9];
//
//	private float[] inclineMatrix = new float[9];
//	//private float[] orientationValues = new float[3];
//	private long orientTime = 0;
//	private float[] prefValues = new float[3];//orientation(computed)

//	private float mAzimuth;
//	private double mInclination;
	private int counter;
	//private int mRotation;

	//private Calendar mcalenderCalendar = Calendar.getInstance();
	private long milliseconds = 0;

	//create the motion accel data
	private float[] gravity = new float[3];//加速度中抽离出重力的部分
	private float[] motion = new float[3];//加速度中抽离出动作的部分
	//private double ratioX,ratioY,ratioZ;//各轴和重力的比例
	//private double mAngleX,mAngleY,mAngleZ;
	private String labelString;
	private String labelTag;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Bundle labelBundle = getIntent().getExtras();
		labelString = labelBundle.getString("label");
		labelTag = labelBundle.getString("labelTag");
		preferred = (TextView)findViewById(R.id.preferred);
		uploadTest =(Button)findViewById(R.id.button2);
		uploadTest.setOnClickListener(this);

		mgr = (SensorManager) this.getSystemService(SENSOR_SERVICE);

		accel = mgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//		compass = mgr.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
//		orient = mgr.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		rovectorSensor = mgr.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
		//chreate the files 
		try {
			SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
			Calendar calendar = Calendar.getInstance();
			String currentTime = dateformat.format(calendar.getTime()).replace(" ", "-");
			
			String filenameacc = Environment.getExternalStorageDirectory().getAbsolutePath() +
					"/sensordata/accel_"+currentTime+"_"+labelString+".log";
//			String filenamecompass = Environment.getExternalStorageDirectory().getAbsolutePath() + 
//					"/sensordata/compass_"+currentTime+"_"+labelString+".log";
//			String filenameoritation = Environment.getExternalStorageDirectory().getAbsolutePath() + 
//					"/sensordata/orientation_"+currentTime+"_"+labelString+".log";
			String filenameRotation = Environment.getExternalStorageDirectory().getAbsolutePath() + 
					"/sensordata/rotation_"+currentTime+"_"+labelString+".log";
			String filenameposition = Environment.getExternalStorageDirectory().getAbsolutePath() + 
					"/sensordata/position_"+currentTime+"_"+labelString+".log";

			mLogposition = new BufferedWriter(new FileWriter(filenameposition,  true));
			mLogAcc = new BufferedWriter(new FileWriter(filenameacc, true));
//			mlogCompass = new BufferedWriter(new FileWriter(filenamecompass, true));
//			mLogOrientation = new BufferedWriter(new FileWriter(filenameoritation, true));
			mLogRotation = new BufferedWriter(new FileWriter(filenameRotation,  true));
		}
		catch(Exception e) {
			Log.e(TAG, "Unable to initialize the logfile");
			e.printStackTrace();
			finish();
		}
		countAcc = countCom = countOrient = countRota = 0;
		WPosition = new quaternion(0,0,1,0);
		currentQuaternion = new quaternion(0,0,0,0);
		conjugateQuaternion = new quaternion(0,0,0,0);
	}

	@Override
	protected void onResume() {
		mgr.registerListener(this, accel, SensorManager.SENSOR_DELAY_GAME);
//		mgr.registerListener(this, compass, SensorManager.SENSOR_DELAY_GAME);
//		mgr.registerListener(this, orient, SensorManager.SENSOR_DELAY_GAME);
		mgr.registerListener(this, rovectorSensor, SensorManager.SENSOR_DELAY_GAME);
		super.onResume();
	}

	@Override
	protected void onPause() {
		mgr.unregisterListener(this, accel);
//		mgr.unregisterListener(this, compass);
//		mgr.unregisterListener(this, orient);
		mgr.unregisterListener(this, rovectorSensor);
		super.onPause();
	}

	@Override
	protected void onStop(){
		//writeLog(milliseconds,"stopping...");
		mgr.unregisterListener(this, accel);
//		mgr.unregisterListener(this, compass);
//		mgr.unregisterListener(this, orient);
		mgr.unregisterListener(this, rovectorSensor);
		try {
			mLogAcc.flush();
//			mlogCompass.flush();
			mLogRotation.flush();
//			mLogOrientation.flush();
			mLogposition.flush();
		} catch (IOException e) {
			// ignore any errors with the logfile
		}
		super.onStop();
	}

	protected void onDestroy() {
		//writeLog("shutting down...");
		try {
			mLogAcc.flush();
			mLogAcc.close();
//			mlogCompass.flush();
//			mlogCompass.close();
			mLogRotation.flush();
			mLogRotation.close();
//			mLogOrientation.flush();
//			mLogOrientation.close();
			mLogposition.close();
			mLogposition.flush();
		}
		catch(Exception e) {
			// ignore any errors with the logfile
		}
		super.onDestroy();
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// ignore
	}

	public void onSensorChanged(SensorEvent event) {
		// Need to get both accelerometer and compass
		// before we can determine our orientationValue
		milliseconds = Calendar.getInstance().getTimeInMillis();
		switch(event.sensor.getType()) {
		case Sensor.TYPE_ACCELEROMETER:
			accTime = milliseconds;
			countAcc++;
			for(int i=0; i<3; i++) {
				accelValues[i] = event.values[i];//for accel
			}

			if(rotationValues[0] != 0)
				ready = true;
//
			for(int i=0; i<3; i++) {
				gravity [i] = (float) (0.1 * event.values[i] + 0.9 * gravity[i]); //for gravity Data
				motion[i] = event.values[i] - gravity[i];// for motion accel data
			}

			// ratio is gravity on the Y axis compared to full gravity
			// should be no more than 1, no less than -1
			//			ratioX = gravity[0]/SensorManager.GRAVITY_EARTH;
			//			ratioY = gravity[1]/SensorManager.GRAVITY_EARTH;
			//			ratioZ = gravity[2]/SensorManager.GRAVITY_EARTH;
			//			if(ratioY > 1.0) ratioY = 1.0;
			//			if(ratioY < -1.0) ratioY = -1.0;
			//			if (ratioX > 1.0) ratioX = 1.0;
			//			if(ratioX < -1.0) ratioX = -1.0;
			//
			//			// convert ratio to radians to degrees, make negative if facing up
			//			mAngleY = Math.toDegrees(Math.acos(ratioY));
			//			mAngleX = Math.toDegrees(Math.acos(ratioX));
			//			if(gravity[2] < 0) {
			//				mAngleY = -mAngleY;
			//				mAngleX = -mAngleX;
			//			}
//			writeLog(mLogAcc, Integer.toString(countAcc) + "  " + Long.toString(accTime) + 
//					"  " + accelValues[0] + "  " + accelValues[1] + "  " + accelValues[2] 
//					);
			//将记录的加速度改为动作加速度
			writeLog(mLogAcc, Integer.toString(countAcc) + "  " + Long.toString(accTime) + 
					"  " + motion[0] + "  " + motion[1] + "  " + motion[2] 
					);
			break;
//		case Sensor.TYPE_MAGNETIC_FIELD:
//			countCom++;
//			compTime = milliseconds;
//			for(int i=0; i<3; i++) {
//				compassValues[i] = event.values[i];
//			}
//			compTime = milliseconds;
//			if(accelValues[2] != 0)
//				ready = true;
//			writeLog(mlogCompass, Integer.toString(countCom) + "  " +Long.toString(compTime) + 
//					"  " + compassValues[0] + "  " + compassValues[1] + "  " + compassValues[2] 
//					);
//			break;
//		case Sensor.TYPE_ORIENTATION:
//			orientTime = milliseconds;
//			countOrient++;
//			//			for(int i=0; i<3; i++) {
//			//				orientationValues[i] = event.values[i];
//			//			}
//			if(!ready)
//				return;
//
//			if(SensorManager.getRotationMatrix(
//					inR, inclineMatrix, accelValues, compassValues)) {
//				// got a good rotation matrix
//
//				mInclination = SensorManager.getInclination(inclineMatrix);
//
//				SensorManager.getOrientation(inR, prefValues);			
//			}
//			mAzimuth = (float) Math.toDegrees(prefValues[0]);
//			if(mAzimuth < 0) {
//				mAzimuth += 360.0f;
//			}
//			writeLog( mLogOrientation, Integer.toString(countOrient) + "  " + Long.toString(orientTime) + 
//					"  " + mAzimuth + "  " + Math.toDegrees(prefValues[1]) + "  " + Math.toDegrees(prefValues[2]) +
//					"  " + Math.toDegrees(mInclination)
//					);
//			break;
		case Sensor.TYPE_ROTATION_VECTOR:
			rotaTime = milliseconds;
			countRota++;
			for (int i = 0; i < 3; i++) {
				rotationValues[i] = event.values[i];					
			}
			if(accelValues[2]!=0){
				ready = true;
			}
			rotationValues[3] = (float)Math.sqrt(1- (Math.pow(rotationValues[0], 2) + 
					Math.pow(rotationValues[1], 2) + Math.pow(rotationValues[2], 2)));
			currentQuaternion.setElement(rotationValues[3], rotationValues[0], rotationValues[1], rotationValues[2]);
			currentQuaternion.MakeConjugateQuaternion(currentQuaternion, conjugateQuaternion);
			//currentQuaternion = currentQuaternion.Kakezan(currentQuaternion, position);
			//position = currentQuaternion.Kakezan(currentQuaternion, conjugateQuaternion);
			WtPosition = currentQuaternion.Kakezan(WPosition, conjugateQuaternion);
			WtPosition = currentQuaternion.Kakezan(currentQuaternion, WtPosition);
//			writeLog(mLogRotation, Integer.toString(countRota) + "  " + Long.toString(rotaTime) + 
//					"  " + rotationValues[0] + "  " + rotationValues[1] + "  " + rotationValues[2] 
//					);
			writeLog(mLogposition, Integer.toString(countRota) + "  " + Long.toString(rotaTime) + 
					"  " + WtPosition.x + "  " + WtPosition.y + "  " + WtPosition.z 
					);

			writeLog(mLogRotation, Integer.toString(countRota) + "  " + Long.toString(rotaTime) + 
					"  " + rotationValues[0] + "  " + rotationValues[1] + "  " + rotationValues[2] 
					);
			break;
		}
		if(counter++ % 10 == 0) {
			doUpdate(null);
			counter = 1;
		}

	}

	public void doUpdate(View view) {
		if(!ready)
			return;
		String msg  =  "Time: " + Long.toString(milliseconds) + "\n" + Integer.toString(countAcc)
				+"\n" + Integer.toString(countCom) + "\n" + Integer.toString(countOrient) 
				+ "\n" + Integer.toString(countRota);
		//		mAzimuth = (float) Math.toDegrees(prefValues[0]);
		//		if(mAzimuth < 0) {
		//			mAzimuth += 360.0f;
		//		}
		//millions = mcalenderCalendar.getTimeInMillis();
		//millions = 0;
		//		String msg = "Time: " + Long.toString(milliseconds) + String.format(
		//				" Preferred:\nazimuth (Z): %7.3f \npitch (X): %7.3f\nroll (Y): %7.3f",
		//				mAzimuth, Math.toDegrees(prefValues[1]),
		//				Math.toDegrees(prefValues[2]));
		//		preferred.setText(msg);

		//        msg = "Time: " + Long.toString(milliseconds) + String.format(
		//        		" Orientation Sensor:\nazimuth (Z): %7.3f\npitch (X): %7.3f\nroll (Y): %7.3f",
		//        		orientationValues[0],
		//        		orientationValues[1],
		//       			orientationValues[2]);
		//        orientation.setText(msg);

		//		msg = "Time: " + Long.toString(milliseconds) + String.format(
		//				" Rotation Vector Sensor:\n x*sin(/2): %7.3f\n y*sin(/2): %7.3f\n z*sin(/2): %7.3f\n cos(/2): %7.3f\n",
		//				rotationValues[0],
		//				rotationValues[1],
		//				rotationValues[2],
		//				rotationValues[3]);
		//		rotationTextView.setText(msg);
		preferred.setText(msg);
		preferred.invalidate();
		//orientation.invalidate();
		//rotationTextView.invalidate();
	}

	private void writeLog(BufferedWriter mLog, String str) {
		try {
			//mLog.write(Long.toString(time));
			//mLog.write(" ");
			mLog.write(str);
			mLog.write("\n");
		}
		catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO 自动生成的方法存根
		// TODO �Զ���ɵķ������
				switch (v.getId()) {
				case R.id.upLoad:
					uplaodTools uplaodTools = new uplaodTools();
					String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() +
					"/sensordata";
					File file = new File(filePath, "accel.log");
					try {
						uplaodTools.uploadFromBySocket(null, "uploadFile", file,
								"accel.log",
								"http://192.168.1.122:8080/strurts2fileupload/uploadAction");
					} catch (IOException e) {
						// TODO �Զ���ɵ� catch ��
						e.printStackTrace();
					}
					break;
				case R.id.button2:
					
				default:
					break;
				}
	}
}