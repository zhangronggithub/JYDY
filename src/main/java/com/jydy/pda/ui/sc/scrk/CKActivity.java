package com.jydy.pda.ui.sc.scrk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.jydy.pda.R;
import com.jydy.pda.bean.CMSMC;
import com.jydy.pda.config.Constants;
import com.jydy.pda.https.CallWebService;
import com.jydy.pda.utils.Logs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CKActivity extends Activity implements OnClickListener,OnItemClickListener{
	
	final String TAG = getClass().getSimpleName();
	
	private ImageView ivCX;
	
	private EditText etCKBM;
	
	private String type;
	
	private CMSMC cmsmc;
	
	private List<CMSMC> cmsmcList ;
	
	private ListView lvCK;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ckxq);
		
		type = getIntent().getStringExtra("type");
		initView();
	}
	private void initView() {
		ivCX = (ImageView) findViewById(R.id.ivCX);
		
		etCKBM = (EditText) findViewById(R.id.etCKBM);
		lvCK = (ListView) findViewById(R.id.lvCK);
		ivCX.setOnClickListener(this);
		lvCK.setOnItemClickListener(this);
		etCKBM.setOnKeyListener(onKeyListener);
		
		
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
				Thread mThread = new Thread(nextRunnable);
				mThread.start();
				return true;
			}
			return false;
		}
	};
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivCX:
			Thread mThread = new Thread(nextRunnable);
			mThread.start();
			break;

		default:
			break;
		}
		
	}
	Runnable nextRunnable = new Runnable() {

		@Override
		public void run() {
			Looper.prepare();
			String ckbm ;
			if (etCKBM.getText().toString().equals("")) {
				ckbm = "%";
			}else {
				ckbm = etCKBM.getText().toString();
			}
			Map<String, String> params = new HashMap<String, String>();
			String s_xlm_cs ="<?xml version=\"1.0\" encoding=\"GB2312\"?>" ;
            s_xlm_cs = s_xlm_cs + "<ROOT>";
            s_xlm_cs = s_xlm_cs + "<DETAIL>" ;
            s_xlm_cs = s_xlm_cs + "<USERID>" + Constants.USERID + "</USERID>" ;
            s_xlm_cs = s_xlm_cs + "<DJTYPE>" + type + "</DJTYPE>" ;
            s_xlm_cs = s_xlm_cs + "<MC001>" + ckbm + "</MC001>" ;
            s_xlm_cs = s_xlm_cs + "<DATABASE>" + Constants.DATABASE+ "</DATABASE>" ;
            s_xlm_cs = s_xlm_cs + "<SN>" + Constants.SN + "</SN>" ;
            s_xlm_cs = s_xlm_cs + "</DETAIL>";
            s_xlm_cs = s_xlm_cs + "</ROOT>" ;
            Logs.d(TAG, s_xlm_cs);
			params.put("s_xml_cs", s_xlm_cs);
			try {
			String str = CallWebService.CallWebService(Constants.CMSMCMethodName,Constants.Namespace ,params,Constants.getHttp());
			Logs.d(TAG,str );
			String[] s = str.split("CMSMC=anyType");
			if (s.length==1) {
				Logs.d(TAG, "dddddd");
				myHandler.sendEmptyMessage(2);
				Toast.makeText(CKActivity.this, "查无数据！", Toast.LENGTH_SHORT).show();
				return;
			}
			Logs.d(TAG, s[1]);//{MC001=001       ; MC002=ԭ���ϲ�            ; };
			cmsmcList = new ArrayList<CMSMC>();
				cmsmcList.clear();
			for (int i = 1; i < s.length; i++) {
				cmsmc = new CMSMC();
				String[] a = s[i].split("=|;");
				Logs.d(TAG, a[1]);	
				Logs.d(TAG, a[3]);
				cmsmc.setMC001(a[1]);
				cmsmc.setMC002(a[3]);
				cmsmcList.add(cmsmc);
			}
			myHandler.sendEmptyMessage(1);
			} catch (Exception e) {
				// TODO: handle exception
				Toast.makeText(CKActivity.this, "网络异常！", Toast.LENGTH_SHORT).show();
			}
			
			Looper.loop();
		}
	};
	Handler myHandler = new Handler() {  
        public void handleMessage(Message msg) {   
             switch (msg.what) {   
                  case 1:   
                	  LvCmsmcBaseAdapter adapter = new LvCmsmcBaseAdapter();
                	  lvCK.setAdapter(adapter);
					  adapter.notifyDataSetChanged();
                       break;   
                  case 2:
                	  Toast.makeText(CKActivity.this, "查无数据！", Toast.LENGTH_SHORT).show();
                	  break;
             }   
             super.handleMessage(msg);   
        }   
   }; 
	class LvCmsmcBaseAdapter extends BaseAdapter{
		
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return cmsmcList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return cmsmcList.get(position);
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
				convertView = LayoutInflater.from(CKActivity.this).inflate(
						R.layout.cmsmc_item, parent, false);
				viewHolder.tvMC001= (TextView) convertView.findViewById(R.id.tvMC001);
				viewHolder.tvMC002= (TextView) convertView.findViewById(R.id.tvMC002);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			
				viewHolder.tvMC001.setText(cmsmcList.get(position).getMC001());
				viewHolder.tvMC002.setText(cmsmcList.get(position).getMC002());
			
			return convertView;
		}
		
	
	}
	private class ViewHolder{
		private TextView tvMC001,tvMC002;
		
		
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		 Intent it = getIntent();
		 it.putExtra("MC001", cmsmcList.get(position).getMC001());
		 it.putExtra("MC002", cmsmcList.get(position).getMC002());
		 setResult(2, it);
		 finish();
	}
}
