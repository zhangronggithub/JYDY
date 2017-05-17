package com.jydy.pda.ui.sc;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import butterknife.Bind;

import butterknife.OnClick;

import static android.content.ContentValues.TAG;
import static com.jydy.pda.R.id.etJDLLZ;
import static com.jydy.pda.R.id.etYDLLZ;


/**
 * Created by 23923 on 2017/2/7.
 */

public class YDZKSActivity extends BaseActivity {

    String JDLLZ, YDLLZ, GD, GP, GY, LOTNO, PL, JCY, flag, error, type, ID, NAME,MAXSCZS,MINSCZS,MINYZDZJLL,MAXYZDZJLL,MINYZDZYLL,MAXYZDZYLL,MAXLL,MINLL;
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
    @Bind(R.id.tvTJY)
    TextView tvTJY;
    @Bind(R.id.tvMJ)
    TextView tvMJ;
    @Bind(R.id.etLL)
    EditText etLL;
    @Bind(R.id.tvJDFJ)
    TextView tvJDFJ;
    @Bind(R.id.tvYDFJ)
    TextView tvYDFJ;
    @Bind(R.id.etSCZS)
    EditText etSCZS;
    @Bind(R.id.tvJDLLZ)
    EditText tvJDLLZ;
    @Bind(R.id.tvYDLLZ)
    EditText tvYDLLZ;
    @Bind(R.id.rb_jcy_hg)
    RadioButton rbJcyHg;
    @Bind(R.id.rb_jcy_bhg)
    RadioButton rbJcyBhg;
    @Bind(R.id.btnSave)
    Button btnSave;

    String[] strFJ ;
    boolean[] selected ;
    @Override
    protected int getContentLayout() {
        return R.layout.activity_ydzks;
    }

    @Override
    protected void initView() {
        Thread mThread = new Thread(getFJRunnable);
        mThread.start();
    }

    @Override
    protected void initAction() {
        etTM.setOnKeyListener(onKeyListener);

    }

    @Override
    protected void initData() {
        JDLLZ = getIntent().getStringExtra("JDLLZ");
        YDLLZ = getIntent().getStringExtra("YDLLZ");
        GD = getIntent().getStringExtra("GD");
        GP = getIntent().getStringExtra("GP");
        GY = getIntent().getStringExtra("GY");
        LOTNO = getIntent().getStringExtra("LOTNO");
        PL = getIntent().getStringExtra("PL");
        MINSCZS = getIntent().getStringExtra("MINSCZS");
        MAXSCZS = getIntent().getStringExtra("MAXSCZS");
        MINYZDZJLL = getIntent().getStringExtra("MINYZDZJLL");
        MAXYZDZJLL = getIntent().getStringExtra("MAXYZDZJLL");
        MINYZDZYLL = getIntent().getStringExtra("MINYZDZYLL");
        MAXYZDZYLL = getIntent().getStringExtra("MAXYZDZYLL");
        MAXLL = getIntent().getStringExtra("MAXLL");
        MINLL = getIntent().getStringExtra("MINLL");
        tvJDLLZ.setText(JDLLZ);
        tvYDLLZ.setText(YDLLZ);
        if (!JDLLZ.equals("")){
            tvJDLLZ.setEnabled(false);
        }
        if (!YDLLZ.equals("")){
            tvYDLLZ.setEnabled(false);
        }
        tvGP.setText(GP);
        tvPL.setText(PL);
        tvPH.setText(LOTNO);
        tvUserID.setText(Constants.USERID);
        rbJcyHg.performClick();
    }

    @Override
    protected void showTM(String tmStr) {
        etTM.setText(tmStr);
        try {
            type = DecodeXml.decodeXml(tmStr, "C001");
            ID = DecodeXml.decodeXml(tmStr, "ID");
            NAME = DecodeXml.decodeXml(tmStr, "NAME");
            if (type.equals("102")) {
                tvTJY.setText(ID);
                etTM.getText().clear();
            } else if (type.equals("104")) {
                tvMJ.setText(ID);
                etTM.getText().clear();
            } else {
                Toast.makeText(YDZKSActivity.this, "请扫描调机员或模具条码！", Toast.LENGTH_SHORT).show();
                etTM.getText().clear();
            }
        } catch (Exception e) {
            Toast.makeText(YDZKSActivity.this, "请扫描调机员或模具条码！", Toast.LENGTH_SHORT).show();
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

    @OnClick({R.id.rb_jcy_hg, R.id.rb_jcy_bhg, R.id.btnSave, R.id.tvJDFJ, R.id.tvYDFJ})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rb_jcy_hg:
                JCY = "Y";
                break;
            case R.id.rb_jcy_bhg:
                JCY = "N";
                break;
            case R.id.tvJDFJ:
                showDialog("甲端附件",strFJ,selected,tvJDFJ);
                break;
            case R.id.tvYDFJ:
                showDialog("乙端附件",strFJ,selected,tvYDFJ);
                break;
            case R.id.btnSave:
                if (TextUtils.isEmpty(tvTJY.getText().toString())) {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(YDZKSActivity.this, "请扫调机员！", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(tvMJ.getText().toString())) {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(YDZKSActivity.this, "请扫描模具！", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(etLL.getText().toString())) {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(YDZKSActivity.this, "请输入拉力值！", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(tvJDLLZ.getText().toString())) {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(YDZKSActivity.this, "请输入甲端拉力值！", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(tvYDLLZ.getText().toString())) {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(YDZKSActivity.this, "请输入乙端拉力值！", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(etSCZS.getText().toString())) {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(YDZKSActivity.this, "请输入实测值始！", Toast.LENGTH_SHORT).show();
                }else if (Float.parseFloat(etLL.getText().toString().trim())>Float.parseFloat(MAXLL)||Float.parseFloat(etLL.getText().toString().trim())<Float.parseFloat(MINLL)) {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(YDZKSActivity.this, "拉力值不在范围之类！", Toast.LENGTH_SHORT).show();
                } else if (Float.parseFloat(etSCZS.getText().toString().trim())>Float.parseFloat(MAXSCZS)||Float.parseFloat(etSCZS.getText().toString().trim())<Float.parseFloat(MINSCZS)) {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(YDZKSActivity.this, "实测值始不在范围之类！", Toast.LENGTH_SHORT).show();
                }else if (Float.parseFloat(tvJDLLZ.getText().toString().trim())>Float.parseFloat(MAXYZDZJLL)||Float.parseFloat(tvJDLLZ.getText().toString().trim())<Float.parseFloat(MINYZDZJLL)) {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(YDZKSActivity.this, "甲端拉力值不在范围之类！", Toast.LENGTH_SHORT).show();
                }else if (Float.parseFloat(tvYDLLZ.getText().toString().trim())>Float.parseFloat(MAXYZDZYLL)||Float.parseFloat(tvYDLLZ.getText().toString().trim())<Float.parseFloat(MINYZDZYLL)) {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(YDZKSActivity.this, "乙端拉力值不在范围之类！", Toast.LENGTH_SHORT).show();
                }else {
                    Thread mThread = new Thread(nextRunnable);
                    mThread.start();
                }
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
            s_xlm_cs = s_xlm_cs + "<GYLX>" + "7010" + "</GYLX>";
            s_xlm_cs = s_xlm_cs + "<GPZT>" + "1" + "</GPZT>";
            s_xlm_cs = s_xlm_cs + "<GY>" + GY + "</GY>";
            s_xlm_cs = s_xlm_cs + "<GP>" + GP + "</GP>";
            s_xlm_cs = s_xlm_cs + "<GD>" + GD + "</GD>";
            s_xlm_cs = s_xlm_cs + "<LOTNO>" + tvPH.getText().toString() + "</LOTNO>";
            s_xlm_cs = s_xlm_cs + "<MJ>" + tvMJ.getText().toString() + "</MJ>";
            s_xlm_cs = s_xlm_cs + "<PL>" + tvPL.getText().toString() + "</PL>";
            s_xlm_cs = s_xlm_cs + "<JDFJ>" + tvJDFJ.getText().toString() + "</JDFJ>";
            s_xlm_cs = s_xlm_cs + "<YDFJ>" + tvYDFJ.getText().toString() + "</YDFJ>";
            s_xlm_cs = s_xlm_cs + "<TJY>" + tvTJY.getText().toString() + "</TJY>";
            s_xlm_cs = s_xlm_cs + "<SCZS>" + etSCZS.getText().toString() + "</SCZS>";
            s_xlm_cs = s_xlm_cs + "<YZDZJ>" + tvJDLLZ.getText().toString() + "</YZDZJ>";
            s_xlm_cs = s_xlm_cs + "<YZDZY>" + tvYDLLZ.getText().toString() + "</YZDZY>";
            s_xlm_cs = s_xlm_cs + "<JCYLLS>" + JCY + "</JCYLLS>";
            s_xlm_cs = s_xlm_cs + "<LL>" + etLL.getText().toString() + "</LL>";
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
                    Toast.makeText(YDZKSActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(YDZKSActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                SoundManager.playSound(2, 1);
                Toast.makeText(YDZKSActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
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
                Logs.d(TAG, list.get(0).get("FJ002").toString());
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
                Toast.makeText(YDZKSActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
            }
            Looper.loop();
        }
    };


}
