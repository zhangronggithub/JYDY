package com.jydy.pda.ui.sc;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jydy.pda.R;
import com.jydy.pda.config.Constants;
import com.jydy.pda.https.CallWebService;
import com.jydy.pda.https.DecodeXml;
import com.jydy.pda.main.BaseActivity;
import com.jydy.pda.main.FormScanActivity;
import com.jydy.pda.utils.Logs;



import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jydy.pda.R.id.etGpForm;

public class JDCXActivity extends BaseActivity implements OnClickListener {

	private String TAG = getClass().getSimpleName();
	
	private EditText etTM;
	
	private ImageView ivCX,ivBack;
	
	private TextView tvTitle;

	private String GP,GD;

	ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

	private ListView lvkccx;


	LvCmsmcBaseAdapter adapter;

	@Override
	protected int getContentLayout() {
		return R.layout.activity_kccx;
	}

	@Override
	protected void initView() {
		etTM = (EditText) findViewById(R.id.etTM);
		ivCX = (ImageView) findViewById(R.id.ivCX);
		ivBack = (ImageView) findViewById(R.id.ivBack);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		lvkccx = (ListView) findViewById(R.id.lvkccx);
		ivBack.setOnClickListener(this);
		ivCX.setOnClickListener(this);
		//监听产品条码Edittext
		etTM.setOnKeyListener(onKeyListener);
		tvTitle.setText("进度查询");
		adapter = new LvCmsmcBaseAdapter();
		lvkccx.setAdapter(adapter);
	}


	@Override
	protected void initAction() {

	}

	@Override
	protected void initData() {

	}

	@Override
	protected void showTM(String tmStr) {
		try {
			GP = DecodeXml.decodeXml(tmStr, "GP");
			GD = DecodeXml.decodeXml(tmStr, "GD");
		} catch (Exception e) {
			Toast.makeText(this, "工票条码解析错误", Toast.LENGTH_SHORT).show();
		}
		etTM.setText(GP);
		Thread mThread = new Thread(mRunnable);
		mThread.start();
	}

	private View.OnKeyListener onKeyListener = new View.OnKeyListener() {

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){
                /*隐藏软键盘*/
				InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if(inputMethodManager.isActive()){
					inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
				}
				Thread mThread = new Thread(mRunnable);
				mThread.start();
				return true;
			}
			return false;
		}
	};
	Runnable mRunnable = new Runnable() {

		@Override
		public void run() {

			Map<String, String> params = new HashMap<String, String>();
			String s_xlm_cs ="<?xml version=\"1.0\" encoding=\"GB2312\"?>" + "<ROOT>" ;
            s_xlm_cs = s_xlm_cs + "<DETAIL>" ;
			s_xlm_cs = s_xlm_cs + "<DATABASE>" + Constants.DATABASE + "</DATABASE>" ;
			s_xlm_cs = s_xlm_cs + "<GP>" + GP + "</GP>" ;
			s_xlm_cs = s_xlm_cs + "<GD>" + GD + "</GD>" ;
            s_xlm_cs = s_xlm_cs + "<SN>" + Constants.SN + "</SN>" ;
            s_xlm_cs = s_xlm_cs + "</DETAIL>" ;
            s_xlm_cs = s_xlm_cs + "</ROOT>" ;
			params.put("s_xml_cs", s_xlm_cs);
			try {

			String str = CallWebService.CallWebService(Constants.Get_GPJDMethodName,Constants.Namespace ,params,Constants.getHttp());
			Logs.d("TAG",str );
			list = DecodeXml.decodeDataset(str,"TAB_GX_SCAN");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				list.clear();
			}
			myHandler.sendEmptyMessage(1);

		}
	};
	Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1:
					lvkccx.setVisibility(View.GONE);
					adapter.notifyDataSetChanged();
					lvkccx.setVisibility(View.VISIBLE);
					break;
			}
			super.handleMessage(msg);
		}
	};
	class LvCmsmcBaseAdapter extends BaseAdapter {


		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size()+1;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(JDCXActivity.this).inflate(
						R.layout.activity_kccx_item, parent, false);
				viewHolder.tvSEQ= (TextView) convertView.findViewById(R.id.tvSEQ);
				viewHolder.tvCK= (TextView) convertView.findViewById(R.id.tvCK);
				viewHolder.tvPH= (TextView) convertView.findViewById(R.id.tvPH);
				viewHolder.tvPM= (TextView) convertView.findViewById(R.id.tvPM);
				viewHolder.tvSMSJ= (TextView) convertView.findViewById(R.id.tvSMSJ);
				viewHolder.tvGG= (TextView) convertView.findViewById(R.id.tvGG);
				viewHolder.tvDW= (TextView) convertView.findViewById(R.id.tvDW);
				viewHolder.tvLOTNO= (TextView) convertView.findViewById(R.id.tvLOTNO);
				viewHolder.tvCPTM= (TextView) convertView.findViewById(R.id.tvCPTM);
				viewHolder.tvKWTM= (TextView) convertView.findViewById(R.id.tvKWTM);
				viewHolder.tvQTY= (TextView) convertView.findViewById(R.id.tvQTY);
				viewHolder.tvLB= (TextView) convertView.findViewById(R.id.tvLB);
				viewHolder.tvJDLL= (TextView) convertView.findViewById(R.id.tvJDLL);
				viewHolder.tvYDLL= (TextView) convertView.findViewById(R.id.tvYDLL);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			if (position==0) {
				viewHolder.tvSEQ.setBackgroundColor(Color.rgb(0, 191, 255));
				viewHolder.tvCK.setBackgroundColor(Color.rgb(0, 191, 255));
				viewHolder.tvPH.setBackgroundColor(Color.rgb(0, 191, 255));
				viewHolder.tvPM.setBackgroundColor(Color.rgb(0, 191, 255));
				viewHolder.tvSMSJ.setBackgroundColor(Color.rgb(0, 191, 255));
				viewHolder.tvDW.setBackgroundColor(Color.rgb(0, 191, 255));
				viewHolder.tvLOTNO.setBackgroundColor(Color.rgb(0, 191, 255));
				viewHolder.tvQTY.setBackgroundColor(Color.rgb(0, 191, 255));
				viewHolder.tvGG.setBackgroundColor(Color.rgb(0, 191, 255));
				viewHolder.tvLB.setBackgroundColor(Color.rgb(0, 191, 255));
				viewHolder.tvKWTM.setBackgroundColor(Color.rgb(0, 191, 255));
				viewHolder.tvCPTM.setBackgroundColor(Color.rgb(0, 191, 255));
				viewHolder.tvJDLL.setBackgroundColor(Color.rgb(0, 191, 255));
				viewHolder.tvYDLL.setBackgroundColor(Color.rgb(0, 191, 255));
				viewHolder.tvSEQ.setText("序号");
				viewHolder.tvCK.setText("工艺名称");
				viewHolder.tvPH.setText("工作票号");
				viewHolder.tvPM.setText("工单单号");
				viewHolder.tvSMSJ.setText("批量");
				viewHolder.tvDW.setText("不良数量");
				viewHolder.tvLOTNO.setText("开始时间");
				viewHolder.tvQTY.setText("结束时间");
				viewHolder.tvGG.setText("工时");
				viewHolder.tvCPTM.setText("完工数量");
				viewHolder.tvKWTM.setText("合格数量");
				viewHolder.tvLB.setText("工单进度");
				viewHolder.tvJDLL.setText("甲端拉力值");
				viewHolder.tvYDLL.setText("乙端拉力值");
			}else {
				viewHolder.tvSEQ.setBackgroundColor(Color.WHITE);
				viewHolder.tvCK.setBackgroundColor(Color.WHITE);
				viewHolder.tvPH.setBackgroundColor(Color.WHITE);
				viewHolder.tvPM.setBackgroundColor(Color.WHITE);
				viewHolder.tvSMSJ.setBackgroundColor(Color.WHITE);
				viewHolder.tvDW.setBackgroundColor(Color.WHITE);
				viewHolder.tvLOTNO.setBackgroundColor(Color.WHITE);
				viewHolder.tvQTY.setBackgroundColor(Color.WHITE);
				viewHolder.tvGG.setBackgroundColor(Color.WHITE);
				viewHolder.tvLB.setBackgroundColor(Color.WHITE);
				viewHolder.tvKWTM.setBackgroundColor(Color.WHITE);
				viewHolder.tvCPTM.setBackgroundColor(Color.WHITE);
				viewHolder.tvJDLL.setBackgroundColor(Color.WHITE);
				viewHolder.tvYDLL.setBackgroundColor(Color.WHITE);
				viewHolder.tvSEQ.setText((String)list.get(position-1).get("SEQ"));
				viewHolder.tvCK.setText((String)list.get(position-1).get("CPGY002"));
				viewHolder.tvPH.setText((String)list.get(position-1).get("GXSCAN003"));
				viewHolder.tvPM.setText((String)list.get(position-1).get("GXSCAN043"));
				viewHolder.tvSMSJ.setText((String)list.get(position-1).get("GXSCAN004"));
				viewHolder.tvDW.setText((String)list.get(position-1).get("GXSCAN036"));
				viewHolder.tvLOTNO.setText((String)list.get(position-1).get("GXSCAN038"));
				viewHolder.tvQTY.setText((String)list.get(position-1).get("GXSCAN040"));
				viewHolder.tvGG.setText((String)list.get(position-1).get("GXSCAN041"));
				viewHolder.tvCPTM.setText((String)list.get(position-1).get("GXSCAN044"));
				viewHolder.tvKWTM.setText((String)list.get(position-1).get("GXSCAN045"));
				viewHolder.tvLB.setText((String)list.get(position-1).get("WCJD"));
				if(list.get(position-1).get("GXSCAN017").equals("anyType{}")){
					viewHolder.tvJDLL.setText("");
				}else {
					viewHolder.tvJDLL.setText((String) list.get(position - 1).get("GXSCAN017"));
				}
				viewHolder.tvYDLL.setText((String)list.get(position-1).get("GXSCAN018"));

			}


			return convertView;
		}


	}
	private class ViewHolder{
		private TextView tvSEQ,tvDW,tvPH,tvPM,tvGG,tvQTY,tvLOTNO,tvCK,tvKWTM,tvCPTM,tvSMSJ,tvLB,tvJDLL,tvYDLL;



	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivCX:
			Thread mThread = new Thread(mRunnable);
			mThread.start();
			break;
		case R.id.ivBack:
			finish();
			break;

		default:
			break;
		}
		
	}

}
