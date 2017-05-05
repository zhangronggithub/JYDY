package com.jydy.pda.config;

import android.os.Environment;

import com.jydy.pda.bean.PowerListBean;

import java.util.ArrayList;


public class Constants {
//	 867762020019623
//	 0379053637476228958728
	//867762020020134
	//0550121203152437681180

	// Http
	public static  String HTTP_ROOT = "http://";
	// Http请求地址
	public static   String HTTP_IP = "10.10.1.21";
	//10.0.2.2
	//命名空间
	public static  String Namespace = "http://127.1.1.0/";
	//主域名
	public static String HTTP_PATH = "/barcode/webservice/JinYu/Service.asmx?op=";
	//用户名
	public static String USERID = "";
	//账套
	public static String DATABASE = "";
	//SN
	public static final String SN = "LPHOON";

	//是否开启测试模块
	public static  Boolean Debug = true;
	//原材料条码长度
	public static String YCLTMCD="15";
	//半成品条码长度
	public static String BCPTMCD;
	//成品条码长度
	public static String CPTMCD="17";
	//库位条码长度
	public static String KWTMCD="10";
	//箱条码长度,15,Z
	public static String XTMCD="15";
	//托盘条码长度
	public static String TPTMCD="15";
	//保存权限后台数据
	public static ArrayList<PowerListBean> powerList = new ArrayList<>() ;

	public static String APKFILE= Environment.getExternalStorageDirectory()+"/jydy.apk";//apk保存地址
	//方法名称
	public static final String ZTMethodName = "Get_ZT";//获取账套
	public static final String LoginMethodName = "CheckLogin";//登录
	public static final String GetPowerListMethodName = "GetPowerList";//权限
	public static final String Get_GPMethodName = "Get_GP";//根据工票条码和工艺条码，获取工票信息
	public static final String Save_GXMethodName = "Save_GX";//保存工艺对应的数据
	public static final String Get_FJMethodName = "Get_FJ";//获取压端子附件信息
	public static final String Get_ZYLXMethodName = "Get_ZYLX";//获取辅助作业类型信息
	public static final String Get_JXLXMethodName = "Get_JXLX";//获取接线类型信息
	public static final String Get_FZLXMethodName = "Get_FZLX";//获取辅助类型信息
	public static final String Get_DZMMethodName = "Get_DZM";//获取端子名信息
	public static final String Save_JXMethodName = "Save_JX";//接线完工

	// 获取地址
	public static String getHttp() 
	{
		return HTTP_ROOT + HTTP_IP + HTTP_PATH;
	}

	// 获取下载apk地址
	public static String APKURL()
	{
		return HTTP_ROOT + HTTP_IP + "/barcode/webservice/Sears/download/WarehouseM.zip";
	}

	//控制按钮的重复点击
	private static long lastClickTime;
	public synchronized static boolean isFastClick() {
		long time = System.currentTimeMillis();
		if ( time - lastClickTime < 500) {
			return true;
		}
		lastClickTime = time;
		return false;
	}


}



