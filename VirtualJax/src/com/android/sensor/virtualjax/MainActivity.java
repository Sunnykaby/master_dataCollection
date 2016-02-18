package com.android.sensor.virtualjax;

import java.io.File;
import java.io.IOException;

import com.kami.Tools.uplaodTools;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;


public class MainActivity extends Activity implements OnClickListener,OnCheckedChangeListener{
	private static final String TAG = "Sensor Data Collection";
	private EditText labelEditText;
	private Button startButton;
	private RadioGroup activityGroup;
	private RadioButton smokingButton;
	private RadioButton drinkingButton;
	private RadioButton scratchingButton;
	private Button uploadTest;
	private String lableTag;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		labelEditText = (EditText)findViewById(R.id.editText1);
		startButton = (Button)findViewById(R.id.button1);
		startButton.setOnClickListener(this);
		uploadTest =(Button)findViewById(R.id.button2);
		uploadTest.setOnClickListener(this);
		activityGroup = (RadioGroup)findViewById(R.id.activityGroup);
		smokingButton = (RadioButton)findViewById(R.id.smoking);
		drinkingButton =(RadioButton)findViewById(R.id.drinking);
		scratchingButton = (RadioButton)findViewById(R.id.Scratching);
		activityGroup.setOnCheckedChangeListener(this);
		
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop(){
		super.onStop();
	}

	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// TODO �Զ���ɵķ������
		switch (v.getId()) {
		case R.id.button1:
			if (lableTag == null) {
				new AlertDialog.Builder(MainActivity.this).setTitle("系统提示")//设置对话框标题  
				  
			     .setMessage("请确认选择一个动作标签！")//设置显示的内容  
			  
			     .setPositiveButton("确定",new DialogInterface.OnClickListener() {//添加确定按钮  
			  
			          
			  
			         @Override  
			  
			         public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件  
			  
			             // TODO Auto-generated method stub  
			  
			             finish();  
			  
			         }  
			  
			     }).setNegativeButton("返回",new DialogInterface.OnClickListener() {//添加返回按钮  
			  
			          
			  
			         @Override  
			  
			         public void onClick(DialogInterface dialog, int which) {//响应事件  
			  
			             // TODO Auto-generated method stub  
			  
			             Log.i("alertdialog"," 请选择动作标签！");  
			  
			         }  
			  
			     }).show();//在按键响应事件中显示此对话框
			}
			Intent labelIntent = new Intent(MainActivity.this, dataCollectionActivity.class);
			Bundle labelBundle = new Bundle();
			labelBundle.putString("label", labelEditText.getText().toString());
			labelBundle.putString("labelTag", lableTag);
			labelIntent.putExtras(labelBundle);
			startActivity(labelIntent);
			break;
		case R.id.button2:
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
		default:
			break;
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO 自动生成的方法存根
		 if(checkedId == smokingButton.getId()){
			 lableTag = "1";
	        }else if(checkedId == drinkingButton.getId()){
	        	lableTag = "2";
	        }else if (checkedId == scratchingButton.getId()){
	        	lableTag = "3";
	        }
	}

}