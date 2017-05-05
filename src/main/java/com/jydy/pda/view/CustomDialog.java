package com.jydy.pda.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jydy.pda.R;


public class CustomDialog extends Dialog{
	private Context ctx;
	private TextView tv1,tv2,tv3,tv4;
	private LinearLayout ll1,ll2,ll3,ll4;

	public CustomDialog(Context context, int theme) {
		super(context,theme);
		this.ctx = context;
		initGUI();
	}

	
	
	public void settv1(String btnmsg,View.OnClickListener listener){
		tv1.setText(btnmsg);
		ll1.setVisibility(View.VISIBLE);
		ll1.setOnClickListener((View.OnClickListener)listener);
	}
	public void settv2(String btnmsg,View.OnClickListener listener){
		tv2.setText(btnmsg);
		ll2.setVisibility(View.VISIBLE);
		ll2.setOnClickListener((View.OnClickListener) listener);
	}
	public void settv3(String btnmsg,View.OnClickListener listener){
		ll3.setVisibility(View.VISIBLE);
		tv3.setText(btnmsg);
		ll3.setOnClickListener((View.OnClickListener) listener);
	}
	public void settv4(String btnmsg,View.OnClickListener listener){
		ll4.setVisibility(View.VISIBLE);
		tv4.setText(btnmsg);
		ll4.setOnClickListener((View.OnClickListener) listener);
	}

	
	private void initGUI() {
		LayoutInflater inflater = LayoutInflater.from(ctx);
		View view = inflater.inflate(R.layout.dialog_custom, null);
		getWindow().setContentView(view);
		tv1 =  (TextView) view.findViewById(R.id.tv1);
		tv2 =  (TextView) view.findViewById(R.id.tv2);
		tv3 =  (TextView) view.findViewById(R.id.tv3);
		tv4 =  (TextView) view.findViewById(R.id.tv4);
		ll1 = (LinearLayout) findViewById(R.id.ll1);
		ll2 = (LinearLayout) findViewById(R.id.ll2);
		ll3 = (LinearLayout) findViewById(R.id.ll3);
		ll4 = (LinearLayout) findViewById(R.id.ll4);
	}
	
}
