package com.jydy.pda.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jydy.pda.R;
import com.jydy.pda.config.Constants;
import com.jydy.pda.utils.Logs;

/**
 * 配置界面
 1.获取Constants中的配置信息展现在配置界面上
 2.更改Constants中的配置信息
 3.将更改后的配置信息保存在sharedpreference
 *
 */
public class ConfigActivity extends Activity {
	
	private EditText etIP,etNameSpace,etHTTP_PATH,etYCLTMCD,etBCPTMCD,etCPTMCD,etKWTMCD,etXTMCD,etTPTMCD;
	
	private Button btnSure;
	
	private ImageView ivBack;
	
	private TextView tvTitle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_debug);
		initView();
		btnSure.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				initData();
				Toast.makeText(ConfigActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
			}
		});
		ivBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(ConfigActivity.this, LoginActivity.class));
				finish();
			}
		});
	}
	private void initView() {
		etIP = (EditText) findViewById(R.id.etIP);
		etNameSpace = (EditText) findViewById(R.id.etNameSpace);
		etHTTP_PATH = (EditText) findViewById(R.id.etHTTP_PATH);
		etYCLTMCD = (EditText) findViewById(R.id.etYCLTMCD);
		etBCPTMCD = (EditText) findViewById(R.id.etBCPTMCD);
		etCPTMCD = (EditText) findViewById(R.id.etCPTMCD);
		etKWTMCD = (EditText) findViewById(R.id.etKWTMCD);
		etXTMCD = (EditText) findViewById(R.id.etXTMCD);
		etTPTMCD = (EditText) findViewById(R.id.etTPTMCD);
		btnSure = (Button) findViewById(R.id.btnSure);
		ivBack = (ImageView) findViewById(R.id.ivBack);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setText("系统配置");
		etIP.setText(Constants.HTTP_IP);
		etNameSpace.setText(Constants.Namespace);
		etHTTP_PATH.setText(Constants.HTTP_PATH);
		etYCLTMCD.setText(Constants.YCLTMCD);
		etBCPTMCD.setText(Constants.BCPTMCD);
		etCPTMCD.setText(Constants.CPTMCD);
		etKWTMCD.setText(Constants.KWTMCD);
		etXTMCD.setText(Constants.XTMCD);
		etTPTMCD.setText(Constants.TPTMCD);
		
	}
	private void initData() {
		SharedPreferences.Editor contans = getSharedPreferences("contans", MODE_PRIVATE).edit();
		if (!etIP.getText().toString().equals("")) {
			Constants.HTTP_IP = etIP.getText().toString().trim();
			contans.putString("HTTP_IP", etIP.getText().toString().trim());
			Logs.d("IP", Constants.HTTP_IP);
		}
		if (!etNameSpace.getText().toString().equals("")) {
			Constants.Namespace = etNameSpace.getText().toString().trim();
			contans.putString("Namespace", etNameSpace.getText().toString().trim());
			Logs.d("NameSpace", Constants.Namespace);
		}
		if (!etHTTP_PATH.getText().toString().equals("")) {
			Constants.HTTP_PATH = etHTTP_PATH.getText().toString().trim();
			contans.putString("HTTP_PATH",etHTTP_PATH.getText().toString().trim());
			Logs.d("HTTP_PATH", Constants.HTTP_PATH);
		}

		if (!etYCLTMCD.getText().toString().equals("")) {
			Constants.YCLTMCD = etYCLTMCD.getText().toString().trim();
			contans.putString("YCLTMCD", etYCLTMCD.getText().toString().trim());
			Logs.d("YCLTMCD", Constants.YCLTMCD);
		}
		if (!etBCPTMCD.getText().toString().equals("")) {
			Constants.BCPTMCD = etBCPTMCD.getText().toString().trim();
			contans.putString("BCPTMCD", etBCPTMCD.getText().toString().trim());
			Logs.d("BCPTMCD", Constants.BCPTMCD);
		}
		if (!etCPTMCD.getText().toString().equals("")) {
			Constants.CPTMCD = etCPTMCD.getText().toString().trim();
			contans.putString("CPTMCD", etCPTMCD.getText().toString().trim());
			Logs.d("CPTMCD", Constants.CPTMCD);
		}
		if (!etKWTMCD.getText().toString().equals("")) {
			Constants.KWTMCD = etKWTMCD.getText().toString().trim();
			contans.putString("KWTMCD", etKWTMCD.getText().toString().trim());
			Logs.d("KWTMCD", Constants.KWTMCD);
		}
		if (!etXTMCD.getText().toString().equals("")) {
			Constants.XTMCD = etXTMCD.getText().toString().trim();
			contans.putString("XTMCD", etXTMCD.getText().toString().trim());
			Logs.d("XTMCD", Constants.XTMCD);
		}
		if (!etTPTMCD.getText().toString().equals("")) {
			Constants.TPTMCD = etTPTMCD.getText().toString().trim();
			contans.putString("TPTMCD", etTPTMCD.getText().toString().trim());
			Logs.d("TPTMCD", Constants.TPTMCD);
		}
		contans.commit();
	}
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		startActivity(new Intent(ConfigActivity.this, LoginActivity.class));
		finish();
	}
}
