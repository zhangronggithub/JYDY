package com.jydy.pda.ui.sc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.jydy.pda.R;
import com.jydy.pda.https.CallWebService;
import com.jydy.pda.config.Constants;
import com.jydy.pda.https.DecodeXml;
import com.jydy.pda.main.BaseActivity;
import com.jydy.pda.utils.Logs;
import com.jydy.pda.utils.SoundManager;
import com.imscs.barcodemanager.BarcodeManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.jydy.pda.R.id.etSCZS;
import static com.jydy.pda.R.id.tvBB;
import static com.jydy.pda.R.id.tvSB;

/**
 * Created by 23923 on 2017/2/7.
 */

public class CJJSActivity extends BaseActivity implements View.OnClickListener {

    String TAG = this.getClass().getSimpleName();

    RadioButton rb_yzxg_hg, rb_yzxg_bhg, rb_jcqrz_hg, rb_jcqrz_bhg;

    EditText etTM, etWGSL, etBLSL, etSCZZ;

    String jcqrz, yzxg, type, ID, NAME, flag, error, GP, GD, GY, PL,MAXSCZZ2,MINSCZZ2,SCZS;

    TextView tvBLYY, tvPL, tvUserID, tvGP;

    Button btnSave;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_cjjs;
    }

    @Override
    protected void initView() {
        rb_jcqrz_bhg = (RadioButton) findViewById(R.id.rb_jcqrz_bhg);
        rb_jcqrz_hg = (RadioButton) findViewById(R.id.rb_jcqrz_hg);
        rb_yzxg_hg = (RadioButton) findViewById(R.id.rb_yzxg_hg);
        rb_yzxg_bhg = (RadioButton) findViewById(R.id.rb_yzxg_bhg);
        etTM = (EditText) findViewById(R.id.etTM);
        etWGSL = (EditText) findViewById(R.id.etWGSL);
        etBLSL = (EditText) findViewById(R.id.etBLSL);
        etSCZZ = (EditText) findViewById(R.id.etSCZZ);
        tvBLYY = (TextView) findViewById(R.id.tvBLYY);
        tvPL = (TextView) findViewById(R.id.tvPL);
        tvGP = (TextView) findViewById(R.id.tvGP);
        tvUserID = (TextView) findViewById(R.id.tvUserID);
        btnSave = (Button) findViewById(R.id.btnSave);
    }

    @Override
    protected void initAction() {
        rb_jcqrz_bhg.setOnClickListener(this);
        rb_jcqrz_hg.setOnClickListener(this);
        rb_yzxg_hg.setOnClickListener(this);
        rb_yzxg_bhg.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        rb_jcqrz_hg.performClick();
        rb_yzxg_hg.performClick();
        //监听产品条码Edittext
        etTM.setOnKeyListener(onKeyListener);
    }

    @Override
    protected void initData() {
        GD = getIntent().getStringExtra("GD");
        GP = getIntent().getStringExtra("GP");
        GY = getIntent().getStringExtra("GY");
        PL = getIntent().getStringExtra("PL");
        MAXSCZZ2 = getIntent().getStringExtra("MAXSCZZ2");
        MINSCZZ2 = getIntent().getStringExtra("MINSCZZ2");
        SCZS = getIntent().getStringExtra("SCZS");
        tvUserID.setText(Constants.USERID);
        etWGSL.setText(PL);
        tvGP.setText(GP);
        tvPL.setText(PL);
        etSCZZ.setText(SCZS);

    }

    @Override
    protected void showTM(String tmStr) {
        etTM.setText(tmStr);
        try {
            type = DecodeXml.decodeXml(tmStr, "C001");
            ID = DecodeXml.decodeXml(tmStr, "ID");
            NAME = DecodeXml.decodeXml(tmStr, "NAME");
            if (type.equals("109")) {
                if(tvBLYY.getText().toString().contains(ID)){
                    Toast.makeText(this, "请不要重复扫描！", Toast.LENGTH_SHORT).show();
                }else {
                    tvBLYY.setText(tvBLYY.getText().toString() + ID + ";");
                }
                etTM.getText().clear();
            } else {
                Toast.makeText(CJJSActivity.this, "请扫描不良原因条码！", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(CJJSActivity.this, "请扫描不良原因条码！", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rb_jcqrz_bhg:
                jcqrz = "N";
                break;
            case R.id.rb_jcqrz_hg:
                jcqrz = "Y";
                break;
            case R.id.rb_yzxg_bhg:
                yzxg = "N";
                break;
            case R.id.rb_yzxg_hg:
                yzxg = "Y";
                break;
            case R.id.btnSave:
                if (TextUtils.isEmpty(etWGSL.getText().toString())) {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(CJJSActivity.this, "请输入完工数量！", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(etBLSL.getText().toString())) {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(CJJSActivity.this, "请输入不良数量！", Toast.LENGTH_SHORT).show();
                    return;
                }else if (TextUtils.isEmpty(tvBLYY.getText().toString())&Float.parseFloat(etBLSL.getText().toString())>0) {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(CJJSActivity.this, "请扫描不良原因！", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(etSCZZ.getText().toString())&Constants.SCZTYPE.equals("Y")) {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(CJJSActivity.this, "请输入实测值终！", Toast.LENGTH_SHORT).show();
                    return;
                } else if (Double.parseDouble(etWGSL.getText().toString()) > Double.parseDouble(tvPL.getText().toString())) {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(CJJSActivity.this, "请输入比批量小的完工数量！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!TextUtils.isEmpty(etSCZZ.getText().toString())) {
                    if (Float.parseFloat(etSCZZ.getText().toString().trim())>Float.parseFloat(MAXSCZZ2)||Float.parseFloat(etSCZZ.getText().toString().trim())<Float.parseFloat(MINSCZZ2)){
                        SoundManager.playSound(2, 1);
                        Toast.makeText(CJJSActivity.this, "实测值终不在范围之类！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
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
            Map<String, String> params = new HashMap<String, String>();
            String s_xlm_cs = "<?xml version=\"1.0\" encoding=\"GB2312\"?>";
            s_xlm_cs = s_xlm_cs + "<ROOT>";
            s_xlm_cs = s_xlm_cs + "<DETAIL>";
            s_xlm_cs = s_xlm_cs + "<USERID>" + Constants.USERID + "</USERID>";
            s_xlm_cs = s_xlm_cs + "<DATABASE>" + Constants.DATABASE + "</DATABASE>";
            s_xlm_cs = s_xlm_cs + "<SN>" + Constants.SN + "</SN>";
            s_xlm_cs = s_xlm_cs + "<GYLX>" + "7001" + "</GYLX>";
            s_xlm_cs = s_xlm_cs + "<GPZT>" + "2" + "</GPZT>";
            s_xlm_cs = s_xlm_cs + "<GY>" + GY + "</GY>";
            s_xlm_cs = s_xlm_cs + "<GP>" + GP + "</GP>";
            s_xlm_cs = s_xlm_cs + "<GD>" + GD + "</GD>";
            s_xlm_cs = s_xlm_cs + "<YZXG>" + yzxg + "</YZXG>";
            s_xlm_cs = s_xlm_cs + "<BLYY>" + tvBLYY.getText().toString() + "</BLYY>";
            s_xlm_cs = s_xlm_cs + "<BLQTY>" + etBLSL.getText().toString() + "</BLQTY>";
            s_xlm_cs = s_xlm_cs + "<JCQRZ>" + jcqrz + "</JCQRZ>";
            s_xlm_cs = s_xlm_cs + "<WGQTY>" + etWGSL.getText().toString() + "</WGQTY>";
            s_xlm_cs = s_xlm_cs + "<SCZZ>" + etSCZZ.getText().toString() + "</SCZZ>";
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
                    Toast.makeText(CJJSActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(CJJSActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                SoundManager.playSound(2, 1);
                Toast.makeText(CJJSActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
            }

            Looper.loop();
        }
    };

}
