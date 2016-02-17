package com.android.sensor.virtualjax;


public class quaternion {
	
	public double real;
	public double x;
	public double y;
	public double z;
	
	public quaternion(){
		this.real = 0;
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}
	public  quaternion(double real, double x, double y, double z){
		this.real = real;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public void setElement(double real, double x, double y, double z){
		this.real = real;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	//四元数叉乘
	public quaternion Kakezan(quaternion left, quaternion right) 
	{ 
	              quaternion ans = new quaternion();  	  
	              ans.real =  left.real * right.real + (-left.x) * right.x + (-left.y )* right.y + (-left.z) * right.z;                	  
	              ans.x =  left.real * right.x+right.real * left.x+left.y * right.z+ (-left.z) * right.y; 
	              ans.y  =  left.real * right.y+right.real * left.y+left.z * right.x+(-left.x) * right.z; 
	              ans.z = left.real * right.z+right.real * left.z+left.x * right.y+(-left.y) * right.x; 	         
	              return ans; 
	} 
	public quaternion Position_rotated(quaternion target_position){
		quaternion ansQuaternion = new quaternion();
		return ansQuaternion;
	}
	//共轭的四元数，用于旋转
	public void MakeConjugateQuaternion(quaternion src,quaternion target){
		//quaternion ans = new quaternion();
		target.real = src.real;
		target.x = src.x * -1;
		target.y = src.y * -1;
		target.z = src.z * -1;
		//return ans;
	}
	//根据旋转坐标和旋转角，创建四元数
	public quaternion MakeRotationalQuaternion(double radian, double AxisX, double AxisY, double AxisZ) 
	{ 
	              quaternion ans = new quaternion(); 
	              double norm; 
	              double ccc, sss; 
	              
	              ans.real = ans.x = ans.y = ans.z = 0.0; 
	  
	              norm = AxisX *  AxisX +  AxisY *  AxisY +  AxisZ *  AxisZ; 
	              if(norm <= 0.0) return ans; 
	  
	              norm = 1.0 / Math.sqrt(norm); 
	              AxisX *= norm; 
	              AxisY *= norm; 
	              AxisZ *= norm; 
	  
	              ccc = Math.cos(0.5 * radian); 
	              sss = Math.sin(0.5 * radian); 
	  
	              ans.real = ccc; 
	              ans.x = sss * AxisX; 
	              ans.y = sss * AxisY; 
	              ans.z = sss * AxisZ; 
	  
	              return ans; 
	} 
	
	//将一个position转化为四元数
	public quaternion PutXYZToQuaternion(double PosX, double PosY, double PosZ) 
	{ 
	              quaternion ans = new quaternion(); 
	  
	              ans.real = 0.0; 
	              ans.x = PosX; 
	              ans.y = PosY; 
	              ans.z = PosZ; 
	  
	              return ans; 
	}
		
}

