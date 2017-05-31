package com.jydy.pda.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.jydy.pda.R;

public class CustomDialog1 extends Dialog{
	private Context ctx;
	private TextView txt_message,tv_sure, tv_cancel;


	public CustomDialog1(Context context, int theme) {
		super(context,theme);
		this.ctx = context;
		initGUI();
	}

	
	
	public void setpositiveButton(String btnmsg,View.OnClickListener listener){
		tv_sure.setText(btnmsg);
		tv_sure.setVisibility(View.VISIBLE);
		tv_sure.setOnClickListener((View.OnClickListener)listener);
	}
	public void setnegativeButton(String btnmsg,View.OnClickListener listener){
		tv_cancel.setText(btnmsg);
		tv_cancel.setVisibility(View.VISIBLE);
		tv_cancel.setOnClickListener((View.OnClickListener) listener);
	}
	
	public void setmessage(String msg){
		txt_message.setText(msg);
	}
	private void initGUI() {
		LayoutInflater inflater = LayoutInflater.from(ctx);
		View view = inflater.inflate(R.layout.dialog_custom1, null);
		getWindow().setContentView(view);
		tv_sure = (TextView) view.findViewById(R.id.tv_sure);
		tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
		txt_message = (TextView) view.findViewById(R.id.txt_message);
	}
	
}
