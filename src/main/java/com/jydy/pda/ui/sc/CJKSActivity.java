package com.jydy.pda.ui.sc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.jydy.pda.view.CustomDialog1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
    TextView tvGD, tvGP, tvBB, tvSB, tvMJ,tvYDMJ, tvPL, tvPH, tvXZ, tvXJ, tvQXCD, tvQPCDJ, tvQPCDY, tvUserID,tvPMLX;
    List<String> data_list;
    ArrayAdapter<String> arr_adapter;
    RadioButton rb_jcqrs_hg, rb_jcqrs_bhg, rb_pm_Y, rb_pm_N;
    Button btnSave;
    LinearLayout llPMLX;
    String[] strPMLX;
    boolean[] selectedPMLX;
    EditText etDBSL,etYZCSSL;
    TextView tvJDFJ,tvYDFJ;
    CustomDialog1 builder;

    String[] strFJ ;
    boolean[] selected ;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_cjks;
    }

    @Override
    protected void initView() {
        llPMLX = (LinearLayout) findViewById(R.id.llPMLX);
        tvUserID = (TextView) findViewById(R.id.tvUserID);
        tvPMLX = (TextView) findViewById(R.id.tvPMLX);
        tvPL = (TextView) findViewById(R.id.tvPL);
        tvPH = (TextView) findViewById(R.id.tvPH);
        tvJDFJ = (TextView) findViewById(R.id.tvJDFJ);
        tvYDFJ = (TextView) findViewById(R.id.tvYDFJ);
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
        tvYDMJ = (TextView) findViewById(R.id.tvYDMJ);
        etTM = (EditText) findViewById(R.id.etTM);
        etDBSL = (EditText) findViewById(R.id.etDBSL);
        etYZCSSL = (EditText) findViewById(R.id.etYZCSSL);
        etJDLLZ = (EditText) findViewById(R.id.etJDLLZ);
        etYDLLZ = (EditText) findViewById(R.id.etYDLLZ);
        etSCZS = (EditText) findViewById(R.id.etSCZS);
        btnSave = (Button) findViewById(R.id.btnSave);
        rb_jcqrs_hg = (RadioButton) findViewById(R.id.rb_jcqrs_hg);
        rb_jcqrs_bhg = (RadioButton) findViewById(R.id.rb_jcqrs_bhg);
        rb_pm_Y = (RadioButton) findViewById(R.id.rb_pm_Y);
        rb_pm_N = (RadioButton) findViewById(R.id.rb_pm_N);
        //获取喷码类型信息
        Thread mThread1 = new Thread(getPMLXRunnable);
        mThread1.start();
        //获取附件信息
        Thread mThread = new Thread(getFJRunnable);
        mThread.start();
    }

    @Override
    protected void initAction() {
        tvPMLX.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        rb_jcqrs_hg.setOnClickListener(this);
        rb_jcqrs_bhg.setOnClickListener(this);
        rb_pm_Y.setOnClickListener(this);
        rb_pm_N.setOnClickListener(this);
        tvJDFJ.setOnClickListener(this);
        tvYDFJ.setOnClickListener(this);
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
        etYZCSSL.setText(PL);

    }

    @Override
    protected void showTM(String tmStr) {
        etTM.setText(tmStr);
        try {
            type = DecodeXml.decodeXml(tmStr, "C001");
            ID = DecodeXml.decodeXml(tmStr, "ID");
            NAME = DecodeXml.decodeXml(tmStr, "NAME");
            if (type.equals("102")) {
                if(tvBB.getText().toString().contains(ID)){
                    Toast.makeText(this, "请不要重复扫描！", Toast.LENGTH_SHORT).show();
                }else{
                tvBB.setText(tvBB.getText().toString()+ID+";");
                }
                etTM.getText().clear();
            } else if (type.equals("103")) {
                tvSB.setText(ID);
                etTM.getText().clear();
            } else if (type.equals("104")) {
                builder = new CustomDialog1(this,
                        R.style.customdialog);
                String str1 = "请选择甲端模具或乙端模具！";
                builder.setmessage(str1);
                builder.setpositiveButton("甲端", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tvMJ.setText(ID);
                        etTM.getText().clear();
                        builder.dismiss();
                    }
                });
                builder.setnegativeButton("乙端", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tvYDMJ.setText(ID);
                        etTM.getText().clear();
                        builder.dismiss();
                    }
                });
                builder.setCancelable(true);
                builder.show();

            } else {
                Toast.makeText(CJKSActivity.this, "请扫描人员、设备或模具条码！", Toast.LENGTH_SHORT).show();
                etTM.getText().clear();
            }
        } catch (Exception e) {
            Toast.makeText(CJKSActivity.this, "请扫描人员、设备或模具条码！", Toast.LENGTH_SHORT).show();
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
                llPMLX.setVisibility(View.VISIBLE);
                break;
            case R.id.rb_pm_N:
                PM = "N";
                llPMLX.setVisibility(View.GONE);
                break;
            case R.id.rb_jcqrs_hg:
                JCQRS = "Y";
                break;
            case R.id.rb_jcqrs_bhg:
                JCQRS = "N";
                break;
            case R.id.tvJDFJ:
                showDialog("甲端附件",strFJ,selected,tvJDFJ);
                break;
            case R.id.tvYDFJ:
                showDialog("乙端附件",strFJ,selected,tvYDFJ);
                break;

            case R.id.btnSave:
                if (TextUtils.isEmpty(tvBB.getText().toString())) {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(CJKSActivity.this, "请扫描人员！", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(tvSB.getText().toString())) {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(CJKSActivity.this, "请扫描设备！", Toast.LENGTH_SHORT).show();
                    return;
                }else if (TextUtils.isEmpty(tvPMLX.getText().toString())&PM.equals("Y")) {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(CJKSActivity.this, "请选择喷码类型！", Toast.LENGTH_SHORT).show();
                    return;
                }else if (TextUtils.isEmpty(etSCZS.getText().toString())&Constants.SCZTYPE.equals("Y")) {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(CJKSActivity.this, "请输入实测值始！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!TextUtils.isEmpty(etSCZS.getText().toString())) {
                    if (Float.parseFloat(etSCZS.getText().toString().trim())>Float.parseFloat(MAXSCZS)||Float.parseFloat(etSCZS.getText().toString().trim())<Float.parseFloat(MINSCZS)){
                        SoundManager.playSound(2, 1);
                        Toast.makeText(CJKSActivity.this, "实测值始不在范围之类！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if (!TextUtils.isEmpty(tvMJ.getText().toString())) {
                    if (TextUtils.isEmpty(etJDLLZ.getText().toString())){
                        Toast.makeText(CJKSActivity.this, "请输入甲端拉力值！", Toast.LENGTH_SHORT).show();
                        return;
                    }
//                    if (TextUtils.isEmpty(tvJDFJ.getText().toString())){
//                        Toast.makeText(CJKSActivity.this, "请选择甲端附件！", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
                }
//                if (!TextUtils.isEmpty(tvJDFJ.getText().toString())) {
//                    if (TextUtils.isEmpty(etJDLLZ.getText().toString())){
//                        Toast.makeText(CJKSActivity.this, "请输入甲端拉力值！", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                    if (TextUtils.isEmpty(tvMJ.getText().toString())){
//                        Toast.makeText(CJKSActivity.this, "请扫描甲端模具！", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                }
                 if (!TextUtils.isEmpty(etJDLLZ.getText().toString())) {
                    if (Float.parseFloat(etJDLLZ.getText().toString().trim())<Float.parseFloat(MINYZDZJLL)){
                        SoundManager.playSound(2, 1);
                        Toast.makeText(CJKSActivity.this, "甲端拉力值不在范围之类！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                     if (TextUtils.isEmpty(tvMJ.getText().toString())){
                         Toast.makeText(CJKSActivity.this, "请扫描甲端模具！", Toast.LENGTH_SHORT).show();
                         return;
                     }
//                     if (TextUtils.isEmpty(tvJDFJ.getText().toString())){
//                         Toast.makeText(CJKSActivity.this, "请选择甲端附件！", Toast.LENGTH_SHORT).show();
//                         return;
//                     }
                }
                if (!TextUtils.isEmpty(tvYDMJ.getText().toString())) {
                    if (TextUtils.isEmpty(etYDLLZ.getText().toString())){
                        Toast.makeText(CJKSActivity.this, "请输入乙端拉力值！", Toast.LENGTH_SHORT).show();
                        return;
                    }
//                    if (TextUtils.isEmpty(tvYDFJ.getText().toString())){
//                        Toast.makeText(CJKSActivity.this, "请选择乙端附件！", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
                }
//                if (!TextUtils.isEmpty(tvYDFJ.getText().toString())) {
//                    if (TextUtils.isEmpty(etYDLLZ.getText().toString())){
//                        Toast.makeText(CJKSActivity.this, "请输入乙端拉力值！", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                    if (TextUtils.isEmpty(tvYDMJ.getText().toString())){
//                        Toast.makeText(CJKSActivity.this, "请扫描乙端模具！", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                }
                 if (!TextUtils.isEmpty(etYDLLZ.getText().toString())) {
                   if (Float.parseFloat(etYDLLZ.getText().toString().trim())<Float.parseFloat(MINYZDZYLL)){
                       SoundManager.playSound(2, 1);
                       Toast.makeText(CJKSActivity.this, "乙端拉力值不在范围之类！", Toast.LENGTH_SHORT).show();
                       return;
                   }
                     if (TextUtils.isEmpty(tvYDMJ.getText().toString())){
                         Toast.makeText(CJKSActivity.this, "请扫描乙端模具！", Toast.LENGTH_SHORT).show();
                         return;
                     }
//                     if (TextUtils.isEmpty(tvYDFJ.getText().toString())){
//                         Toast.makeText(CJKSActivity.this, "请选择乙端附件！", Toast.LENGTH_SHORT).show();
//                         return;
//                     }
                }

                Thread mThread = new Thread(nextRunnable);
                mThread.start();

                break;
            case R.id.tvPMLX:
                showDialog("喷码类型",strPMLX,selectedPMLX,tvPMLX);
                break;
            default:
                break;
        }
    }
    public void showDialog(final String strTitle,final String[] strFJ,final boolean[] selected,final TextView tv){
        for(int i=0; i<selected.length; i++) {
            if(selected[i] == true) {
                selected[i] = false;
            }
        }
        DialogInterface.OnMultiChoiceClickListener mutiListener =
                new DialogInterface.OnMultiChoiceClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface,
                                        int which, boolean isChecked) {
                        selected[which] = isChecked;
                    }
                };
        new  AlertDialog.Builder(this)
                .setTitle(strTitle )
                .setMultiChoiceItems(strFJ,  selected ,  mutiListener)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selectedStr = "";
                        for(int i=0; i<selected.length; i++) {
                            if(selected[i] == true) {
                                selectedStr = selectedStr + strFJ[i]+";";
                            }
                        }
                        tv.setText(selectedStr);
                    }
                })
                .setNegativeButton("取消" ,  null )
                .show();
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
            s_xlm_cs = s_xlm_cs + "<YG>" + tvBB.getText().toString() + "</YG>";
            s_xlm_cs = s_xlm_cs + "<SBJXBH>" + tvSB.getText().toString() + "</SBJXBH>";
            s_xlm_cs = s_xlm_cs + "<MJ>" + tvMJ.getText().toString() + "</MJ>";
            s_xlm_cs = s_xlm_cs + "<YDMJ>" + tvYDMJ.getText().toString() + "</YDMJ>";
            s_xlm_cs = s_xlm_cs + "<PL>" + tvPL.getText().toString() + "</PL>";
            s_xlm_cs = s_xlm_cs + "<XZ>" + tvXZ.getText().toString() + "</XZ>";
            s_xlm_cs = s_xlm_cs + "<XJ>" + tvXJ.getText().toString() + "</XJ>";
            s_xlm_cs = s_xlm_cs + "<QXCD>" + tvQXCD.getText().toString() + "</QXCD>";
            s_xlm_cs = s_xlm_cs + "<PMLB>" + tvPMLX.getText().toString() + "</PMLB>";
            s_xlm_cs = s_xlm_cs + "<QPCDJ>" + tvQPCDJ.getText().toString() + "</QPCDJ>";
            s_xlm_cs = s_xlm_cs + "<QPCDY>" + tvQPCDY.getText().toString() + "</QPCDY>";
            s_xlm_cs = s_xlm_cs + "<SCZS>" + etSCZS.getText().toString() + "</SCZS>";
            s_xlm_cs = s_xlm_cs + "<YZDZJLLZ>" + etJDLLZ.getText().toString() + "</YZDZJLLZ>";
            s_xlm_cs = s_xlm_cs + "<YZDZYLLZ>" + etYDLLZ.getText().toString() + "</YZDZYLLZ>";
            s_xlm_cs = s_xlm_cs + "<QTY>" + etDBSL.getText().toString() + "</QTY>";
            s_xlm_cs = s_xlm_cs + "<JDFJ>" + tvJDFJ.getText().toString() + "</JDFJ>";
            s_xlm_cs = s_xlm_cs + "<YDFJ>" + tvYDFJ.getText().toString() + "</YDFJ>";
            s_xlm_cs = s_xlm_cs + "<YZCSQTY>" + etYZCSSL.getText().toString() + "</YZCSQTY>";
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

    Runnable getPMLXRunnable = new Runnable() {

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
            s_xlm_cs = s_xlm_cs + "</DETAIL>";
            s_xlm_cs = s_xlm_cs + "</ROOT>";
            params.put("s_xml_cs", s_xlm_cs);
            Logs.d(TAG, s_xlm_cs);
            try {
                String str = CallWebService.CallWebService(Constants.Get_PMLBMethodName, Constants.Namespace, params, Constants.getHttp());
                Logs.d(TAG, str);
//                D/----返回的数据----: anyType{schema=anyType{element=anyType{complexType=anyType{choice=anyType{element=anyType{complexType=anyType{sequence=anyType{element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; }; }; }; }; }; }; }; diffgram=anyType{NewDataSet=anyType{TAB_FJ=anyType{FJ001=60001; FJ002=附件01; FJ003=Y; FJ004=anyType{}; }; }; }; }
                ArrayList<Map<String, Object>> list = DecodeXml.decodeDataset(str,"TAB_PMLB");
                strPMLX = new String[list.size()];
                selectedPMLX = new boolean[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    strPMLX[i] = list.get(i).get("PMLB002").toString();
                    selectedPMLX[i] = false;
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
    Runnable getFJRunnable = new Runnable() {

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
            s_xlm_cs = s_xlm_cs + "</DETAIL>";
            s_xlm_cs = s_xlm_cs + "</ROOT>";
            params.put("s_xml_cs", s_xlm_cs);
            Logs.d(TAG, s_xlm_cs);
            try {
                String str = CallWebService.CallWebService(Constants.Get_FJMethodName, Constants.Namespace, params, Constants.getHttp());
                Logs.d(TAG, str);
//                D/----返回的数据----: anyType{schema=anyType{element=anyType{complexType=anyType{choice=anyType{element=anyType{complexType=anyType{sequence=anyType{element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; }; }; }; }; }; }; }; diffgram=anyType{NewDataSet=anyType{TAB_FJ=anyType{FJ001=60001; FJ002=附件01; FJ003=Y; FJ004=anyType{}; }; }; }; }
                ArrayList<Map<String, Object>> list = DecodeXml.decodeDataset(str,"TAB_FJ");
                strFJ = new String[list.size()];
                selected = new boolean[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    strFJ[i] = list.get(i).get("FJ002").toString();
                    selected[i] = false;
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
