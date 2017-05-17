package com.jydy.pda.ui.sc;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.jydy.pda.R;
import com.jydy.pda.config.Constants;
import com.jydy.pda.https.CallWebService;
import com.jydy.pda.https.DecodeXml;
import com.jydy.pda.main.BaseActivity;
import com.jydy.pda.utils.Logs;
import com.jydy.pda.utils.SoundManager;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.R.attr.type;
import static android.content.ContentValues.TAG;
import static android.os.Build.ID;
import static com.jydy.pda.R.id.etBLSL;
import static com.jydy.pda.R.id.etSCZZ;
import static com.jydy.pda.R.id.etWGSL;
import static com.jydy.pda.R.id.tvMJ;
import static com.jydy.pda.R.id.tvTJY;
import static com.jydy.pda.R.raw.error;

/**
 * Created by 23923 on 2017/2/7.
 */

public class YDZJSActivity extends BaseActivity {

    String GD, GP, GY, PL, LOTNO,yzqrwg,jcy,flag,error,type,ID,NAME;
    @Bind(R.id.tvTitle)
    TextView tvTitle;
    @Bind(R.id.tvUserID)
    TextView tvUserID;
    @Bind(R.id.tvGP)
    TextView tvGP;
    @Bind(R.id.tvPL)
    TextView tvPL;
    @Bind(R.id.tvGD)
    TextView tvGD;
    @Bind(R.id.tvPH)
    TextView tvPH;
    @Bind(R.id.etTM)
    EditText etTM;
    @Bind(R.id.tvWGSL)
    EditText tvWGSL;
    @Bind(R.id.tvBLSL)
    EditText tvBLSL;
    @Bind(R.id.tvBLYY)
    EditText tvBLYY;
    @Bind(R.id.etYZCSSL)
    EditText etYZCSSL;
    @Bind(R.id.rb_yzqrwg_hg)
    RadioButton rbYzqrwgHg;
    @Bind(R.id.rb_yzqrwg_bhg)
    RadioButton rbYzqrwgBhg;
    @Bind(R.id.rb_jcy_hg)
    RadioButton rbJcyHg;
    @Bind(R.id.rb_jcy_bhg)
    RadioButton rbJcyBhg;
    @Bind(R.id.btnSave)
    Button btnSave;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_ydzjs;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initAction() {
        etTM.setOnKeyListener(onKeyListener);
    }

    @Override
    protected void initData() {
        GD = getIntent().getStringExtra("GD");
        GY = getIntent().getStringExtra("GY");
        GP = getIntent().getStringExtra("GP");
        PL = getIntent().getStringExtra("PL");
        LOTNO = getIntent().getStringExtra("LOTNO");
        tvGP.setText(GP);
        tvPL.setText(PL);
        tvPH.setText(LOTNO);
        tvWGSL.setText(PL);
        tvUserID.setText(Constants.USERID);
        rbJcyHg.performClick();
        rbYzqrwgHg.performClick();

    }

    @Override
    protected void showTM(String tmStr) {
        etTM.setText(tmStr);
        try {
            type = DecodeXml.decodeXml(tmStr, "C001");
            ID = DecodeXml.decodeXml(tmStr, "ID");
            NAME = DecodeXml.decodeXml(tmStr, "NAME");
            if (type.equals("109")) {
                tvBLYY.setText(ID);
                etTM.getText().clear();
            } else {
                Toast.makeText(YDZJSActivity.this, "请扫描不良原因条码！", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(YDZJSActivity.this, "请扫描不良原因条码！", Toast.LENGTH_SHORT).show();
        }

    }
    private View.OnKeyListener onKeyListener = new View.OnKeyListener() {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                /*隐藏软键盘*/
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputMethodManager.isActive()) {
                    inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                }
                showTM(etTM.getText().toString());
                return true;
            }
            return false;
        }
    };

    @OnClick({R.id.rb_yzqrwg_hg, R.id.rb_yzqrwg_bhg, R.id.rb_jcy_hg, R.id.rb_jcy_bhg, R.id.btnSave})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rb_yzqrwg_hg:
                yzqrwg = "Y";
                break;
            case R.id.rb_yzqrwg_bhg:
                yzqrwg = "N";
                break;
            case R.id.rb_jcy_hg:
                jcy = "Y";
                break;
            case R.id.rb_jcy_bhg:
                jcy = "N";
                break;
            case R.id.btnSave:
                if (TextUtils.isEmpty(tvWGSL.getText().toString())) {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(YDZJSActivity.this, "请输入完工数量！", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(tvBLSL.getText().toString())) {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(YDZJSActivity.this, "请输入不良数量！", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(etYZCSSL.getText().toString())) {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(YDZJSActivity.this, "请输入压着次数数量！", Toast.LENGTH_SHORT).show();
                }  else {
                    Thread mThread = new Thread(nextRunnable);
                    mThread.start();
                }
                break;
        }
    }
    Runnable nextRunnable = new Runnable() {

        @Override
        public void run() {
            Looper.prepare();
            Map<String, String> params = new HashMap<String, String>();
            String s_xlm_cs = "<?xml version=\"1.0\" encoding=\"GB2312\"?>";
            s_xlm_cs = s_xlm_cs + "<ROOT>";
            s_xlm_cs = s_xlm_cs + "<DETAIL>";
            s_xlm_cs = s_xlm_cs + "<USERID>" + Constants.USERID + "</USERID>";
            s_xlm_cs = s_xlm_cs + "<DATABASE>" + Constants.DATABASE + "</DATABASE>";
            s_xlm_cs = s_xlm_cs + "<SN>" + Constants.SN + "</SN>";
            s_xlm_cs = s_xlm_cs + "<GYLX>" + "7010" + "</GYLX>";
            s_xlm_cs = s_xlm_cs + "<GPZT>" + "2" + "</GPZT>";
            s_xlm_cs = s_xlm_cs + "<GY>" + GY + "</GY>";
            s_xlm_cs = s_xlm_cs + "<GP>" + GP + "</GP>";
            s_xlm_cs = s_xlm_cs + "<GD>" + GD + "</GD>";
            s_xlm_cs = s_xlm_cs + "<YZCSQTY>" + etYZCSSL.getText().toString() + "</YZCSQTY>";
            s_xlm_cs = s_xlm_cs + "<BLYY>" + tvBLYY.getText().toString() + "</BLYY>";
            s_xlm_cs = s_xlm_cs + "<BLQTY>" + tvBLSL.getText().toString() + "</BLQTY>";
            s_xlm_cs = s_xlm_cs + "<JCYLLZ>" + jcy + "</JCYLLZ>";
            s_xlm_cs = s_xlm_cs + "<WGQTY>" + tvWGSL.getText().toString() + "</WGQTY>";
            s_xlm_cs = s_xlm_cs + "<YZQRWG>" + yzqrwg + "</YZQRWG>";
            s_xlm_cs = s_xlm_cs + "</DETAIL>";
            s_xlm_cs = s_xlm_cs + "</ROOT>";
            params.put("s_xml_cs", s_xlm_cs);
            Logs.d(TAG, s_xlm_cs);
            try {
                String str = CallWebService.CallWebService(Constants.Save_GXMethodName, Constants.Namespace, params, Constants.getHttp());
                Logs.d(TAG, str);
                flag = DecodeXml.decodeXml(str, "FLAG");
                error = DecodeXml.decodeXml(str, "ERROR");

                if (flag.equals("S")) {
                    Toast.makeText(YDZJSActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(YDZJSActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                SoundManager.playSound(2, 1);
                Toast.makeText(YDZJSActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
            }

            Looper.loop();
        }
    };
}
