package com.jydy.pda.view;

import android.R.color;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jydy.pda.R;

public class DropEditText extends FrameLayout implements View.OnClickListener, OnItemClickListener {
	private TextView mEditText;  // 杈撳叆妗�
	private ImageView mDropImage; // 鍙宠竟鐨勫浘鐗囨寜閽�
	private PopupWindow mPopup; // 鐐瑰嚮鍥剧墖寮瑰嚭popupwindow
	private WrapListView mPopView; // popupwindow鐨勫竷灞�
	
	private int mDrawableLeft;
	private int mDropMode; // flow_parent or wrap_content
	private String mHit;
	
	public DropEditText(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public DropEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		LayoutInflater.from(context).inflate(R.layout.edit_layout, this);
		mPopView = (WrapListView) LayoutInflater.from(context).inflate(R.layout.pop_view, null);
		
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DropEditText, defStyle, 0);
		mDrawableLeft = ta.getResourceId(R.styleable.DropEditText_drawableRight, R.drawable.drop1);
		mDropMode = ta.getInt(R.styleable.DropEditText_dropMode, 0);
		mHit = ta.getString(R.styleable.DropEditText_hint);
		ta.recycle();
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		
		mEditText = (TextView) findViewById(R.id.dropview_edit);
		mDropImage = (ImageView) findViewById(R.id.dropview_image);
		
//		mEditText.setSelectAllOnFocus(true);
//		mEditText.setEnabled(false);
		mDropImage.setImageResource(mDrawableLeft);
	
		if(!TextUtils.isEmpty(mHit)) {
			mEditText.setHint(mHit);
		}
		
		mDropImage.setOnClickListener(this);
		mPopView.setOnItemClickListener(this);
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		// 濡傛灉甯冨眬鍙戠敓鏀�
		// 骞朵笖dropMode鏄痜lower_parent
		// 鍒欒缃甃istView鐨勫搴�
		if(changed && 0 == mDropMode) {
			mPopView.setListWidth(getMeasuredWidth());
		}
	}
	
	/**
	 * 璁剧疆Adapter
	 * @param adapter ListView鐨凙dapter
	 */
	public void setAdapter(BaseAdapter adapter) {
		mPopView.setAdapter(adapter);
		
		mPopup = new PopupWindow(mPopView, LinearLayout.LayoutParams.WRAP_CONTENT, 200);
		mPopup.setBackgroundDrawable(new ColorDrawable(color.transparent));
		mPopup.setFocusable(true); // 璁﹑opwin鑾峰彇鐒︾偣
	}
	
	/**
	 * 鑾峰彇杈撳叆妗嗗唴鐨勫唴瀹�
	 * @return String content
	 */
	public String getText() {
		return mEditText.getText().toString();
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.dropview_image) {
			if(mPopup.isShowing()) {
				mPopup.dismiss();
				return;
			}
			
			mPopup.showAsDropDown(this, 0, 10);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		mEditText.setText(mPopView.getAdapter().getItem(position).toString());
		mPopup.dismiss();
	}
}
