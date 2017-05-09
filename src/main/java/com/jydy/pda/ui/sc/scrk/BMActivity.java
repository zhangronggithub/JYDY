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
import com.jydy.pda.bean.CMSME;
import com.jydy.pda.config.Constants;
import com.jydy.pda.https.CallWebService;
import com.jydy.pda.utils.Logs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BMActivity extends Activity implements OnClickListener,OnItemClickListener{
	
	final String TAG = getClass().getSimpleName();
	
	private EditText etBMBM;
	
	private ImageView ivCX;
	
	private ListView lvBM;
	
	private List<CMSME> cmsmeList;
	
	private CMSME cmsme;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bm);
		initView();
	}
	private void initView() {
		etBMBM = (EditText) findViewById(R.id.etBMBM);
		ivCX = (ImageView) findViewById(R.id.ivCX);
		lvBM = (ListView) findViewById(R.id.lvBM);
		ivCX.setOnClickListener(this);
		lvBM.setOnItemClickListener(this);
		etBMBM.setOnKeyListener(onKeyListener);
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		 Intent it = getIntent();
		 it.putExtra("ME001", cmsmeList.get(position).getME001());
		 it.putExtra("ME002", cmsmeList.get(position).getME002());
		 setResult(2, it);
		 finish();
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
			if (etBMBM.getText().toString().equals("")) {
				ckbm = "%";
			}else {
				ckbm = etBMBM.getText().toString();
			}
			Map<String, String> params = new HashMap<String, String>();
			String s_xlm_cs ="<?xml version=\"1.0\" encoding=\"GB2312\"?>" ;
            s_xlm_cs = s_xlm_cs + "<ROOT>";
            s_xlm_cs = s_xlm_cs + "<DETAIL>" ;
            s_xlm_cs = s_xlm_cs + "<USERID>" + Constants.USERID + "</USERID>" ;
            s_xlm_cs = s_xlm_cs + "<ME001>" + ckbm + "</ME001>" ;
            s_xlm_cs = s_xlm_cs + "<DATABASE>" + Constants.DATABASE+ "</DATABASE>" ;
            s_xlm_cs = s_xlm_cs + "<SN>" + Constants.SN + "</SN>" ;
            s_xlm_cs = s_xlm_cs + "</DETAIL>";
            s_xlm_cs = s_xlm_cs + "</ROOT>" ;
            Logs.d("ddd", s_xlm_cs);
			params.put("s_xml_cs", s_xlm_cs);
			try {
			String str = CallWebService.CallWebService(Constants.GetCMSMEMethodName,Constants.Namespace ,params,Constants.getHttp());
			Logs.d("TAG",str );
			String[] s = str.split("CMSME=anyType");
			if (s.length==1) {
				Logs.d("TAG", "dddddd");
				myHandler.sendEmptyMessage(2);
				return;
			}
			Logs.d(TAG, s[1]);
			cmsmeList = new ArrayList<CMSME>();
				cmsmeList.clear();
			for (int i = 1; i < s.length; i++) {
				cmsme = new CMSME();
				String[] a = s[i].split("=|;");
				Logs.d(TAG, a[1]);	
				Logs.d(TAG, a[3]);
				cmsme.setME001(a[1]);
				cmsme.setME002(a[3]);
				cmsmeList.add(cmsme);
			}
			myHandler.sendEmptyMessage(1);
			} catch (Exception e) {
				// TODO: handle exception
				Toast.makeText(BMActivity.this, "网络异常！", Toast.LENGTH_SHORT).show();
			}
			Looper.loop();
		}
	};
	Handler myHandler = new Handler() {  
        public void handleMessage(Message msg) {   
             switch (msg.what) {   
                  case 1:   
                	  LvCmsmcBaseAdapter adapter = new LvCmsmcBaseAdapter();
          			  lvBM.setAdapter(adapter);
					  adapter.notifyDataSetChanged();
                       break;   
                  case 2:
                	  Toast.makeText(BMActivity.this, "查无数据！", Toast.LENGTH_SHORT).show();
                	  break;
             }   
             super.handleMessage(msg);   
        }   
   }; 
   class LvCmsmcBaseAdapter extends BaseAdapter{
		
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return cmsmeList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return cmsmeList.get(position);
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
				convertView = LayoutInflater.from(BMActivity.this).inflate(
						R.layout.cmsmc_item, parent, false);
				viewHolder.tvMC001= (TextView) convertView.findViewById(R.id.tvMC001);
				viewHolder.tvMC002= (TextView) convertView.findViewById(R.id.tvMC002);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			
				viewHolder.tvMC001.setText(cmsmeList.get(position).getME001());
				viewHolder.tvMC002.setText(cmsmeList.get(position).getME002());
			
			return convertView;
		}
		
	
	}
	private class ViewHolder{
		private TextView tvMC001,tvMC002;
		
		
	}
}
