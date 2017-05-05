package com.jydy.pda.main;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jydy.pda.R;
import com.jydy.pda.https.CallWebService;
import com.jydy.pda.config.Constants;
import com.jydy.pda.https.DecodeXml;
import com.jydy.pda.utils.Encrypt;
import com.jydy.pda.utils.Logs;
import com.jydy.pda.utils.SoundManager;
import com.jydy.pda.view.DropEditText;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LoginActivity extends Activity  implements View.OnClickListener{

	public final String TAG = getClass().getSimpleName();

	private TextView mBtnLogin,tvConfig,tvVersion,tvProgress;

	private DropEditText drop_edit;

	private List<String> listMB002 = new ArrayList<String>();

	private List<String> listMB001 = new ArrayList<String>();

	private EditText etUserName,etPwd;

	private ImageView ivBack,ivHead;

	private String flag = null;

	private View progress;

	private View mInputLayout;

	private float mWidth, mHeight;

	private LinearLayout mName, mPsw,mdrop;

	public static final int TAKE_PHOTO = 1;

	public static final int CROP_PHOTO = 2;

	private Uri imageUri;

	private String filehead = "head";

	private Bitmap bit;

	private String Version;

	private ProgressDialog pBar;

	ProgressBar mProgress;

	Dialog mDownloadDialog;

	private int progress1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		SoundManager.getInstance();
		SoundManager.initSounds(getBaseContext());
		SoundManager.loadSounds();
		getData();
		Thread mThread = new Thread(mRunnable);
		mThread.start();
		initView();

	}
	private void getData() {
		SharedPreferences pref = getSharedPreferences("contans", MODE_PRIVATE);
		Constants.HTTP_IP = pref.getString("HTTP_IP", Constants.HTTP_IP);
		Constants.Namespace = pref.getString("Namespace", Constants.Namespace);
		Constants.HTTP_PATH = pref.getString("HTTP_PATH", Constants.HTTP_PATH);
		Constants.YCLTMCD = pref.getString("YCLTMCD", Constants.YCLTMCD);
		Constants.BCPTMCD = pref.getString("BCPTMCD", Constants.BCPTMCD);
		Constants.CPTMCD = pref.getString("CPTMCD", Constants.CPTMCD);
		Constants.KWTMCD = pref.getString("KWTMCD", Constants.KWTMCD);
		Constants.XTMCD = pref.getString("XTMCD", Constants.XTMCD);
		Constants.TPTMCD = pref.getString("TPTMCD", Constants.TPTMCD);
	}
	Runnable mRunnable = new Runnable() {

		@Override
		public void run() {
			Looper.prepare();
			Map<String, String> params = new HashMap<String, String>();
			String s_xlm_cs ="<?xml version=\"1.0\" encoding=\"GB2312\"?>" + "<ROOT>" ;
			s_xlm_cs = s_xlm_cs + "<DETAIL>" ;
			s_xlm_cs = s_xlm_cs + "<SN>" + Constants.SN + "</SN>" ;
			s_xlm_cs = s_xlm_cs + "</DETAIL>" ;
			s_xlm_cs = s_xlm_cs + "</ROOT>" ;

			params.put("s_xml_cs", s_xlm_cs);
			Logs.d("URL", Constants.ZTMethodName);
			try {
				String str = CallWebService.CallWebService(Constants.ZTMethodName, Constants.Namespace ,params, Constants.getHttp());
				Logs.d("TAG",str );
				String FLAG = DecodeXml.decodeXml(str,"FLAG");
				String ERROR = DecodeXml.decodeXml(str,"ERROR");
				String MB001 = DecodeXml.decodeXml(str,"MB001");
				String MB002 = DecodeXml.decodeXml(str,"MB002");
				Version = DecodeXml.decodeXml(str,"Version");
				if (FLAG.equals("S")){
					String[] MB1 = MB001.split("\\|");
					String[] MB2 = MB002.split("\\|");
					Logs.d(TAG,MB2.length);
					for (int i = 0; i < MB1.length; i++) {
						Logs.d(TAG, MB2[i]);
						listMB002.add(MB2[i]);
						listMB001.add(MB1[i]);
					}
					Logs.d(TAG, listMB002.get(0).toString());
					mHandler.sendEmptyMessage(2);
				}else{
					SoundManager.playSound(2, 1);
					Toast.makeText(LoginActivity.this,ERROR,Toast.LENGTH_SHORT).show();
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				SoundManager.playSound(2, 1);
				Toast.makeText(LoginActivity.this,"网络异常！",Toast.LENGTH_SHORT).show();
			}
			Looper.loop();
		}
	};
	public class DialogOnKeyListener implements DialogInterface.OnKeyListener {

		@Override
		public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK&& event.getRepeatCount() == 0) {
				dialog.dismiss();
				finish();

			}
			return false;
		}

	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getRepeatCount() == 0) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	private void initView() {
		tvVersion = (TextView) findViewById(R.id.tvVersion);
		mBtnLogin = (TextView) findViewById(R.id.main_btn_login);
		mInputLayout = findViewById(R.id.input_layout);
		mName = (LinearLayout) findViewById(R.id.input_layout_name);
		mPsw = (LinearLayout) findViewById(R.id.input_layout_psw);
		mdrop = (LinearLayout) findViewById(R.id.input_layout_drop);
		etUserName = (EditText) findViewById(R.id.etUserName);
		etPwd = (EditText) findViewById(R.id.etPwd);
		drop_edit = (DropEditText) findViewById(R.id.drop_edit);
		ivBack = (ImageView) findViewById(R.id.ivBack);
		tvConfig = (TextView) findViewById(R.id.tvConfig);
		mBtnLogin.setOnClickListener(this);
		ivBack.setOnClickListener(this);
		tvConfig.setOnClickListener(this);
		SharedPreferences pref = getSharedPreferences("login", MODE_PRIVATE);
		String  username = pref.getString("username", "");
//		String  password = pref.getString("password", "");
//		etPwd.setText(password);
		etUserName.setText(username);

		//app版本号
		// 获取packagemanager的实例
		PackageManager packageManager = getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		try {
			PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(),0);
			String version = packInfo.versionName;
			tvVersion.setText("v"+version);
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//下拉edittext
		if (listMB002!=null) {
			drop_edit.setAdapter(new BaseAdapter() {

				@Override
				public int getCount() {
					return listMB002.size();
				}

				@Override
				public Object getItem(int position) {
					return listMB002.get(position);
				}

				@Override
				public long getItemId(int position) {
					return position;
				}

				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					TextView tv = new TextView(LoginActivity.this);
					tv.setTextColor(Color.rgb(33, 150, 243));
					tv.setText(listMB002.get(position));
					tv.setPadding(10, 5,0, 5);
					return tv;
				}
			}); }
		etUserName.addTextChangedListener(
				new TextWatcher() {
					@Override
					public void beforeTextChanged(CharSequence s, int start, int count, int after) {

					}
					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {
						if (etUserName.getText().toString().equals("00000000")){
							Logs.d(TAG,"000000000");

							tvConfig.setVisibility(View.VISIBLE);
						}
					}

					@Override
					public void afterTextChanged(Editable s) {
					}
				}
		);
	}
//	/**
//	 * 是否更新提示窗口
//	 */
//	private void showNoticeDialog() {
//		AlertDialog.Builder builder = new AlertDialog.Builder(this);
//		builder.setTitle("软件更新");
//		builder.setMessage("检测到新版本，是否更新？");
//		builder.setPositiveButton("更新",
//				new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						dialog.dismiss();
//						showDownloadDialog();
//					}
//				});
//
//		builder.setNegativeButton("取消",
//				new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						dialog.dismiss();
//						finish();
//					}
//				});
//		Dialog noticeDialog = builder.create();
//		noticeDialog.show();
//		noticeDialog.setOnKeyListener(new DialogOnKeyListener());
//	}
//
//	/**
//	 * 下载等待窗口
//	 */
//	private void showDownloadDialog() {
//		AlertDialog.Builder builder = new AlertDialog.Builder(this);
//		builder.setTitle("正在更新");
//		final LayoutInflater inflater = LayoutInflater.from(this);
//		View v = inflater.inflate(R.layout.softupdate_progress, null);
//		mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
//		tvProgress = (TextView) v.findViewById(R.id.tvProgress);
//		builder.setView(v);
////		builder.setNegativeButton("取消下载",
////				new DialogInterface.OnClickListener() {
////					@Override
////					public void onClick(DialogInterface dialog, int which) {
////						dialog.dismiss();
////						cancelUpdate = true;
////					}
////				});
//		mDownloadDialog = builder.create();
//		mDownloadDialog.show();
//		downloadApk();
//	}
//
//	private void downloadApk() {
//		new downloadApkThread().start();
//	}
//
//
//	/**
//	 * 下载程序
//	 */
//	private class downloadApkThread extends Thread {
//		@Override
//		public void run() {
//			try {
//				if (Environment.getExternalStorageState().equals(
//						Environment.MEDIA_MOUNTED)) {
//					Logs.d("DDFDF",Constants.APKURL());
//					URL url = new URL(Constants.APKURL());
//					HttpURLConnection conn = (HttpURLConnection) url
//							.openConnection();
//					conn.connect();
//					int length = conn.getContentLength();
//					InputStream is = conn.getInputStream();
////
////					File file = new File(Constants.APKFILE);
////					if (!file.exists()) {
////						file.mkdir();
////					}
//					File apkFile = new File(Constants.APKFILE);
//					FileOutputStream fos = new FileOutputStream(apkFile);
//					int count = 0;
//					byte buf[] = new byte[1024];
//					do {
//						int numread = is.read(buf);
//						count += numread;
//						 progress1 = (int) (((float) count / length) * 100);
//						mHandler.sendEmptyMessage(0);
//						if (numread <= 0) {
//							mHandler.sendEmptyMessage(1);
//							break;
//						}
//
//						fos.write(buf, 0, numread);
//					} while (true);
//					fos.close();
//					is.close();
//				}
//			} catch (MalformedURLException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//
//			mDownloadDialog.dismiss();
//		}
//	};

	/**
	 * 安装apk
	 */
	private void installApk() {
		File apkfile = new File(Constants.APKFILE);
		if (!apkfile.exists()) {
			return;
		}

		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		this.startActivity(i);
	}
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 2:
					try {
						PackageManager packageManager = getPackageManager();
						PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(),0);
						String version = packInfo.versionName;
						Logs.d(TAG,Version+"JJJJJ");
						if (Float.parseFloat(Version)>Float.parseFloat(version)){
//							showNoticeDialog();
						}
					}catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;

				case 1:
					installApk();
					break;
				case 0:
					tvProgress.setText(progress1+"");
					mProgress.setProgress(progress1);
					break;
				default:
					break;
			}
		};
	};
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.main_btn_login:
				if (TextUtils.isEmpty(etUserName.getText().toString())) {
					SoundManager.playSound(2, 1);
					Toast.makeText(LoginActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
				}else if(TextUtils.isEmpty(etPwd.getText().toString())){
					SoundManager.playSound(2, 1);
					Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
				}else if (TextUtils.isEmpty(drop_edit.getText().toString())) {
					SoundManager.playSound(2, 1);
					Toast.makeText(LoginActivity.this, "请选择账套", Toast.LENGTH_SHORT).show();
				}else {
					Thread mThread = new Thread(loginRunnable);
					mThread.start();
				}
				break;
			case R.id.ivBack:
				finish();
				break;

			case R.id.tvConfig:
				startActivity(new Intent(LoginActivity.this, ConfigActivity.class));
				finish();
				break;
			default:
				break;
		}
	}

	Runnable loginRunnable = new Runnable() {

		@Override
		public void run() {
			Looper.prepare();
			Map<String, String> params = new HashMap<String, String>();
			String s_xlm_cs ="<?xml version=\"1.0\" encoding=\"GB2312\"?>" + "<ROOT>" ;
			s_xlm_cs = s_xlm_cs + "<DETAIL>" ;
			s_xlm_cs = s_xlm_cs + "<USERID>" + etUserName.getText() + "</USERID>" ;
			s_xlm_cs = s_xlm_cs + "<PASSWORD>" + etPwd.getText()+ "</PASSWORD>" ;
			s_xlm_cs = s_xlm_cs + "<SN>" + Constants.SN + "</SN>" ;
			s_xlm_cs = s_xlm_cs + "</DETAIL>";
			s_xlm_cs = s_xlm_cs + "</ROOT>" ;
			params.put("s_xml_cs", s_xlm_cs);
			Logs.d("TAG", s_xlm_cs);
			try {
				String str = CallWebService.CallWebService(Constants.LoginMethodName, Constants.Namespace ,params, Constants.getHttp());
				flag = DecodeXml.decodeXml(str, "FLAG");
				if (flag.equals("S")) {
					Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
					Constants.USERID =  etUserName.getText().toString();
					for (int i = 0; i < listMB002.size(); i++) {
						if ((drop_edit.getText().toString()).equals(listMB002.get(i))) {
							Constants.DATABASE = listMB001.get(i);
						}
					}
					SharedPreferences.Editor login = getSharedPreferences("login", MODE_PRIVATE).edit();
					login.putString("username",etUserName.getText().toString() );
					login.putString("password",etPwd.getText().toString() );
					login.commit();
					SharedPreferences pref = getSharedPreferences("ZCXX", MODE_PRIVATE);
					String  zcxx = pref.getString("ZCXX", "");
					Log.d(TAG, Encrypt.encrypt(LoginActivity.this));
					if (zcxx.equals(Encrypt.encrypt(LoginActivity.this))){
						Intent i = new Intent(LoginActivity.this, MainActivity.class);
						startActivity(i);
					}else{
						Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
						startActivity(i);
					}
				}else {
					SoundManager.playSound(2, 1);
					Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				SoundManager.playSound(2, 1);
				Toast.makeText(LoginActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
			}

			Looper.loop();
		}
	};


}
