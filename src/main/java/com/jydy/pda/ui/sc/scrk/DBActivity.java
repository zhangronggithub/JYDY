package com.jydy.pda.ui.sc.scrk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jydy.pda.R;
import com.jydy.pda.bean.CMSMQ;
import com.jydy.pda.config.Constants;
import com.jydy.pda.https.CallWebService;
import com.jydy.pda.utils.Logs;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBActivity extends Activity implements OnItemClickListener{
	final String TAG = getClass().getSimpleName();
	private String type;
	private CMSMQ cmsmq;
	private List<CMSMQ> cmsmqList;
	private ListView lvCMSMQ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cmsmq);
		initData();
		initView();
		Thread mThread = new Thread(mRunnable);
		mThread.start();
	}
	private void initView() {
		lvCMSMQ = (ListView) findViewById(R.id.lvCMSMQ);
		lvCMSMQ.setOnItemClickListener(this);
	}
	private void initData() {
		type = getIntent().getStringExtra("type");
	}
	Runnable mRunnable = new Runnable() {

		@Override
		public void run() {
			Looper.prepare();
			Map<String, String> params = new HashMap<String, String>();
			String s_xlm_cs ="<?xml version=\"1.0\" encoding=\"GB2312\"?>" ;
			s_xlm_cs = s_xlm_cs + "<ROOT>";
			s_xlm_cs = s_xlm_cs + "<DETAIL>" ;
			s_xlm_cs = s_xlm_cs + "<USERID>" + Constants.USERID + "</USERID>" ;
			s_xlm_cs = s_xlm_cs + "<MQ003>" + type + "</MQ003>" ;
			s_xlm_cs = s_xlm_cs + "<DATABASE>" +Constants.DATABASE + "</DATABASE>" ;
			s_xlm_cs = s_xlm_cs + "<SN>" + Constants.SN + "</SN>" ;
			s_xlm_cs = s_xlm_cs + "</DETAIL>";
			s_xlm_cs = s_xlm_cs + "</ROOT>" ;
			Logs.d(TAG, s_xlm_cs);
			params.put("s_xml_cs", s_xlm_cs);
			try{
			String str = CallWebService.CallWebService(Constants.CMSMQMethodName,Constants.Namespace ,params,Constants.getHttp());
			Logs.d(TAG,str );
			String[] s = str.split("CMSMQ=anyType");
			Logs.d(TAG, s[1]);//{MC001=001       ; MC002=ԭ���ϲ�            ; };
			cmsmqList = new ArrayList<CMSMQ>();
			for (int i = 1; i < s.length; i++) {
				cmsmq = new CMSMQ();
				String[] a = s[i].split("=|;");
				Logs.d(TAG, a[1]);	
				Logs.d(TAG, a[3]);
				cmsmq.setMQ001(a[1]);
				cmsmq.setMQ002(a[3]);
				cmsmqList.add(cmsmq);
			}
			myHandler.sendEmptyMessage(1);
			}catch (Exception e ){
				Toast.makeText(DBActivity.this, "网络异常！", Toast.LENGTH_SHORT).show();
			}
			Looper.loop();
		}
	};
	Handler myHandler = new Handler() {  
		public void handleMessage(Message msg) {   
			switch (msg.what) {   
			case 1:   
				LvCmsmcBaseAdapter adapter = new LvCmsmcBaseAdapter();
				lvCMSMQ.setAdapter(adapter);
				break;   
			}   
			super.handleMessage(msg);   
		}   
	}; 
	class LvCmsmcBaseAdapter extends BaseAdapter{


		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return cmsmqList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return cmsmqList.get(position);
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
				convertView = LayoutInflater.from(DBActivity.this).inflate(
						R.layout.cmsmc_item, parent, false);
				viewHolder.tvMC001= (TextView) convertView.findViewById(R.id.tvMC001);
				viewHolder.tvMC002= (TextView) convertView.findViewById(R.id.tvMC002);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			viewHolder.tvMC001.setText(cmsmqList.get(position).getMQ001());
			viewHolder.tvMC002.setText(cmsmqList.get(position).getMQ002());

			return convertView;
		}


	}
	private class ViewHolder{
		private TextView tvMC001,tvMC002;


	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//		if (type.equals("58")||type.equals("34")) {
			Intent it = getIntent();
			it.putExtra("MQ001", cmsmqList.get(position).getMQ001());
			it.putExtra("MQ002", cmsmqList.get(position).getMQ002());
			setResult(1, it);
			finish();
//		}
//		else if (type.equals("34")) {
//			Intent it = getIntent();
//			Intent intent = new Intent(CMSMQActivity.this,LLXQActivity.class);
//			intent.putExtra("DB", "");
//			intent.putExtra("DH", "");
//			intent.putExtra("MQ010", "");
//			intent.putExtra("type", type);
//			startActivity(intent);
//			finish();
//		}
	}

}
