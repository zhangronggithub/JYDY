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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jydy.pda.R;
import com.jydy.pda.https.CallWebService;
import com.jydy.pda.config.Constants;
import com.jydy.pda.https.DecodeXml;
import com.jydy.pda.main.BaseActivity;
import com.jydy.pda.main.LoginActivity;
import com.jydy.pda.utils.Logs;
import com.jydy.pda.utils.SoundManager;
import com.imscs.barcodemanager.BarcodeManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.os.Build.VERSION_CODES.M;

/**
 * Created by 23923 on 2017/2/7.
 */

public class CJKSActivity extends BaseActivity implements View.OnClickListener {

    String TAG = this.getClass().getSimpleName();
    EditText etTM, etSCZS, etJDLLZ, etYDLLZ;
    String GD, GY, GP, PL, XZ, XJ, QXCD, QPCDJ, QPCDY, LOTNO, YZDZJ, YZDZY;//intent传过来的值
    String type, ID, NAME;//扫描模具条码，解析的三个字段。
    String PM, JCQRS;//喷码，检查确认
    String flag, error,MAXSCZS,MINSCZS,MINYZDZJLL,MAXYZDZJLL,MINYZDZYLL,MAXYZDZYLL;
    TextView tvGD, tvGP, tvBB, tvSB, tvMJ, tvPL, tvPH, tvXZ, tvXJ, tvQXCD, tvQPCDJ, tvQPCDY, tvUserID;
    Spinner spDBSL;
    List<String> data_list;
    ArrayAdapter<String> arr_adapter;
    RadioButton rb_jcqrs_hg, rb_jcqrs_bhg, rb_pm_Y, rb_pm_N;
    Button btnSave;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_cjks;
    }

    @Override
    protected void initView() {
        tvUserID = (TextView) findViewById(R.id.tvUserID);
        tvPL = (TextView) findViewById(R.id.tvPL);
        tvPH = (TextView) findViewById(R.id.tvPH);
        tvQXCD = (TextView) findViewById(R.id.tvQXCD);
        tvXZ = (TextView) findViewById(R.id.tvXZ);
        tvXJ = (TextView) findViewById(R.id.tvXJ);
        tvQPCDJ = (TextView) findViewById(R.id.tvQPCDJ);
        tvQPCDY = (TextView) findViewById(R.id.tvQPCDY);
        tvGD = (TextView) findViewById(R.id.tvGD);
        tvGP = (TextView) findViewById(R.id.tvGP);
        tvBB = (TextView) findViewById(R.id.tvBB);
        tvSB = (TextView) findViewById(R.id.tvSB);
        tvMJ = (TextView) findViewById(R.id.tvMJ);
        etTM = (EditText) findViewById(R.id.etTM);
        etJDLLZ = (EditText) findViewById(R.id.etJDLLZ);
        etYDLLZ = (EditText) findViewById(R.id.etYDLLZ);
        spDBSL = (Spinner) findViewById(R.id.spDBSL);
        etSCZS = (EditText) findViewById(R.id.etSCZS);
        btnSave = (Button) findViewById(R.id.btnSave);
        rb_jcqrs_hg = (RadioButton) findViewById(R.id.rb_jcqrs_hg);
        rb_jcqrs_bhg = (RadioButton) findViewById(R.id.rb_jcqrs_bhg);
        rb_pm_Y = (RadioButton) findViewById(R.id.rb_pm_Y);
        rb_pm_N = (RadioButton) findViewById(R.id.rb_pm_N);
    }

    @Override
    protected void initAction() {
        btnSave.setOnClickListener(this);
        rb_jcqrs_hg.setOnClickListener(this);
        rb_jcqrs_bhg.setOnClickListener(this);
        rb_pm_Y.setOnClickListener(this);
        rb_pm_N.setOnClickListener(this);
        rb_pm_Y.performClick();
        rb_jcqrs_hg.performClick();
        //监听产品条码Edittext
        etTM.setOnKeyListener(onKeyListener);

    }

    @Override
    protected void initData() {
        PL = getIntent().getStringExtra("PL");
        XZ = getIntent().getStringExtra("XZ");
        XJ = getIntent().getStringExtra("XJ");
        QXCD = getIntent().getStringExtra("QXCD");
        QPCDJ = getIntent().getStringExtra("QPCDJ");
        QPCDY = getIntent().getStringExtra("QPCDY");
        LOTNO = getIntent().getStringExtra("LOTNO");
        YZDZJ = getIntent().getStringExtra("YZDZJ");
        YZDZY = getIntent().getStringExtra("YZDZY");
        GD = getIntent().getStringExtra("GD");
        GY = getIntent().getStringExtra("GY");
        GP = getIntent().getStringExtra("GP");
        MINSCZS = getIntent().getStringExtra("MINSCZS");
        MAXSCZS = getIntent().getStringExtra("MAXSCZS");
        MINYZDZJLL = getIntent().getStringExtra("MINYZDZJLL");
        MAXYZDZJLL = getIntent().getStringExtra("MAXYZDZJLL");
        MINYZDZYLL = getIntent().getStringExtra("MINYZDZYLL");
        MAXYZDZYLL = getIntent().getStringExtra("MAXYZDZYLL");
        tvUserID.setText(Constants.USERID);
        tvGD.setText(GD);
        tvGP.setText(GP);
        tvPH.setText(LOTNO);
        tvPL.setText(PL);
        tvXZ.setText(XZ);
        tvXJ.setText(XJ);
        tvQXCD.setText(QXCD);
        tvQPCDJ.setText(QPCDJ);
        tvQPCDY.setText(QPCDY);
        data_list = new ArrayList<String>();
        data_list.add("1");
        data_list.add("5");
        data_list.add("10");
        data_list.add("25");
        //适配器
        arr_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data_list);
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDBSL.setAdapter(arr_adapter);
        spDBSL.setSelection(3);
    }

    @Override
    protected void showTM(String tmStr) {
        etTM.setText(tmStr);
        try {
            type = DecodeXml.decodeXml(tmStr, "C001");
            ID = DecodeXml.decodeXml(tmStr, "ID");
            NAME = DecodeXml.decodeXml(tmStr, "NAME");
            if (type.equals("101")) {
                tvBB.setText(ID);
                etTM.getText().clear();
            } else if (type.equals("103")) {
                tvSB.setText(ID);
                etTM.getText().clear();
            } else if (type.equals("104")) {
                tvMJ.setText(ID);
                etTM.getText().clear();
            } else {
                Toast.makeText(CJKSActivity.this, "请扫描班别、设备或模具条码！", Toast.LENGTH_SHORT).show();
                etTM.getText().clear();
            }
        } catch (Exception e) {
            Toast.makeText(CJKSActivity.this, "请扫描班别、设备或模具条码！", Toast.LENGTH_SHORT).show();
            etTM.getText().clear();
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
            case R.id.rb_pm_Y:
                PM = "Y";
                break;
            case R.id.rb_pm_N:
                PM = "N";
                break;
            case R.id.rb_jcqrs_hg:
                JCQRS = "Y";
                break;
            case R.id.rb_jcqrs_bhg:
                JCQRS = "N";
                break;

            case R.id.btnSave:
                if (TextUtils.isEmpty(tvBB.getText().toString())) {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(CJKSActivity.this, "请扫描班组！", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(tvSB.getText().toString())) {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(CJKSActivity.this, "请扫描设备！", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(tvMJ.getText().toString())) {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(CJKSActivity.this, "请扫描模具！", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(etSCZS.getText().toString())) {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(CJKSActivity.this, "请输入实测值始！", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(etJDLLZ.getText().toString())) {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(CJKSActivity.this, "请输入甲端拉力值！", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(etYDLLZ.getText().toString())) {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(CJKSActivity.this, "请输入乙端拉力值！", Toast.LENGTH_SHORT).show();
                }else if (Float.parseFloat(etSCZS.getText().toString().trim())>Float.parseFloat(MAXSCZS)||Float.parseFloat(etSCZS.getText().toString().trim())<Float.parseFloat(MINSCZS)) {
                    SoundManager.playSound(2, 1);
                   Toast.makeText(CJKSActivity.this, "实测值始必须在"+MINSCZS+"~"+MAXSCZS+"范围之类！", Toast.LENGTH_SHORT).show();
                }else if (Float.parseFloat(etJDLLZ.getText().toString().trim())>Float.parseFloat(MAXYZDZJLL)||Float.parseFloat(etJDLLZ.getText().toString().trim())<Float.parseFloat(MINYZDZJLL)) {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(CJKSActivity.this, "实测值始必须在"+MINYZDZJLL+"~"+MAXYZDZJLL+"范围之类！", Toast.LENGTH_SHORT).show();
                }else if (Float.parseFloat(etYDLLZ.getText().toString().trim())>Float.parseFloat(MAXYZDZYLL)||Float.parseFloat(etYDLLZ.getText().toString().trim())<Float.parseFloat(MINYZDZYLL)) {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(CJKSActivity.this, "实测值始必须在"+MINYZDZYLL+"~"+MAXYZDZYLL+"范围之类！", Toast.LENGTH_SHORT).show();
                }else{
                Thread mThread = new Thread(nextRunnable);
                mThread.start();
                }
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
            s_xlm_cs = s_xlm_cs + "<GPZT>" + "1" + "</GPZT>";
            s_xlm_cs = s_xlm_cs + "<GY>" + GY + "</GY>";
            s_xlm_cs = s_xlm_cs + "<GP>" + GP + "</GP>";
            s_xlm_cs = s_xlm_cs + "<GD>" + GD + "</GD>";
            s_xlm_cs = s_xlm_cs + "<BB>" + tvBB.getText().toString() + "</BB>";
            s_xlm_cs = s_xlm_cs + "<SBJXBH>" + tvSB.getText().toString() + "</SBJXBH>";
            s_xlm_cs = s_xlm_cs + "<MJ>" + tvMJ.getText().toString() + "</MJ>";
            s_xlm_cs = s_xlm_cs + "<PL>" + tvPL.getText().toString() + "</PL>";
            s_xlm_cs = s_xlm_cs + "<XZ>" + tvXZ.getText().toString() + "</XZ>";
            s_xlm_cs = s_xlm_cs + "<XJ>" + tvXJ.getText().toString() + "</XJ>";
            s_xlm_cs = s_xlm_cs + "<QXCD>" + tvQXCD.getText().toString() + "</QXCD>";
            s_xlm_cs = s_xlm_cs + "<QPCDJ>" + tvQPCDJ.getText().toString() + "</QPCDJ>";
            s_xlm_cs = s_xlm_cs + "<QPCDY>" + tvQPCDY.getText().toString() + "</QPCDY>";
            s_xlm_cs = s_xlm_cs + "<SCZS>" + etSCZS.getText().toString() + "</SCZS>";
            s_xlm_cs = s_xlm_cs + "<YZDZJLLZ>" + etJDLLZ.getText().toString() + "</YZDZJLLZ>";
            s_xlm_cs = s_xlm_cs + "<YZDZYLLZ>" + etYDLLZ.getText().toString() + "</YZDZYLLZ>";
            s_xlm_cs = s_xlm_cs + "<QTY>" + spDBSL.getSelectedItem().toString() + "</QTY>";
            s_xlm_cs = s_xlm_cs + "<JCQRS>" + JCQRS + "</JCQRS>";
            s_xlm_cs = s_xlm_cs + "<PM>" + PM + "</PM>";
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
                    Toast.makeText(CJKSActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(CJKSActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                SoundManager.playSound(2, 1);
                Toast.makeText(CJKSActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
            }

            Looper.loop();
        }
    };


}
