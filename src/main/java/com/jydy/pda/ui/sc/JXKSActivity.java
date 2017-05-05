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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;

import butterknife.OnClick;


import static android.content.ContentValues.TAG;


/**
 * Created by 23923 on 2017/2/7.
 */

public class JXKSActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.tvTitle)
    TextView tvTitle;
    @Bind(R.id.tvUserID)
    TextView tvUserID;
    @Bind(R.id.tvGP)
    TextView tvGP;
    @Bind(R.id.tvPL)
    TextView tvPL;
    @Bind(R.id.tvJH)
    TextView tvJH;
    @Bind(R.id.tvPH)
    TextView tvPH;
    @Bind(R.id.etTM)
    EditText etTM;
    @Bind(R.id.tvMJ)
    TextView tvMJ;
    @Bind(R.id.tvZYY)
    TextView tvZYY;
    @Bind(R.id.tvJXLX)
    TextView tvJXLX;
    @Bind(R.id.tvFZLX)
    TextView tvFZLX;
    @Bind(R.id.rb_xhjc_hg)
    RadioButton rbXhjcHg;
    @Bind(R.id.rb_xhjc_bhg)
    RadioButton rbXhjcBhg;
    @Bind(R.id.tvDZM)
    TextView tvDZM;
    @Bind(R.id.etSCZS)
    EditText etSCZS;
    @Bind(R.id.btnSave)
    Button btnSave;

    String xhjc,flag,error,type,ID,NAME;
    String GD, GP, GY, PL, LOTNO,JH;
    String[] strJXLX,strFZLX,strDZM;
    boolean[] selectedJXLX, selectedFZLX, selectedDZM;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_jxks;
    }

    @Override
    protected void initView() {
        Thread mThread1 = new Thread(getJXLXRunnable);
        mThread1.start();
        Thread mThread2 = new Thread(getFZLXRunnable);
        mThread2.start();
        Thread mThread3 = new Thread(getDZMRunnable);
        mThread3.start();
    }

    @Override
    protected void initAction() {
        etTM.setOnKeyListener(onKeyListener);

    }

    @Override
    protected void initData() {
        rbXhjcHg.performClick();
        GD = getIntent().getStringExtra("GD");
        GY = getIntent().getStringExtra("GY");
        GP = getIntent().getStringExtra("GP");
        PL = getIntent().getStringExtra("PL");
        JH = getIntent().getStringExtra("JH");
        LOTNO = getIntent().getStringExtra("LOTNO");
        tvGP.setText(GP);
        tvPL.setText(PL);
        tvPH.setText(LOTNO);
        tvJH.setText(JH);
        tvUserID.setText(Constants.USERID);

    }

    @Override
    protected void showTM(String tmStr) {
        etTM.setText(tmStr);
        try {
            type = DecodeXml.decodeXml(tmStr, "C001");
            ID = DecodeXml.decodeXml(tmStr, "ID");
            NAME = DecodeXml.decodeXml(tmStr, "NAME");
            if (type.equals("102")) {
                tvZYY.setText(ID);
                etTM.getText().clear();
            }else if (type.equals("104")) {
                tvMJ.setText(ID);
                etTM.getText().clear();
            } else {
                Toast.makeText(JXKSActivity.this, "请扫描作业员或模具条码！", Toast.LENGTH_SHORT).show();
                etTM.getText().clear();
            }
        } catch (Exception e) {
            Toast.makeText(JXKSActivity.this, "请扫描作业员或模具条码！", Toast.LENGTH_SHORT).show();
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
    @OnClick({R.id.rb_xhjc_hg, R.id.rb_xhjc_bhg, R.id.btnSave, R.id.tvJXLX, R.id.tvFZLX, R.id.tvDZM})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rb_xhjc_hg:
                xhjc = "Y";
                break;
            case R.id.rb_xhjc_bhg:
                xhjc = "N";
                break;
            case R.id.tvJXLX:
                showDialog("接线附件",strJXLX,selectedJXLX,tvJXLX);
                break;
            case R.id.tvFZLX:
                showDialog("辅助附件",strFZLX,selectedFZLX,tvFZLX);
                break;
            case R.id.tvDZM:
                showDialog("端子名",strDZM,selectedDZM,tvDZM);
                break;
            case R.id.btnSave:
                if (TextUtils.isEmpty(tvMJ.getText().toString())) {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(JXKSActivity.this, "请扫描模具！", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(tvZYY.getText().toString())) {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(JXKSActivity.this, "请扫描作业员！", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(tvDZM.getText().toString())) {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(JXKSActivity.this, "请输入端子名！", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(etSCZS.getText().toString())) {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(JXKSActivity.this, "请输入实测值始！", Toast.LENGTH_SHORT).show();
                }else{
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
            s_xlm_cs = s_xlm_cs + "<GYLX>" + "7020" + "</GYLX>";
            s_xlm_cs = s_xlm_cs + "<GPZT>" + "1" + "</GPZT>";
            s_xlm_cs = s_xlm_cs + "<GY>" + GY + "</GY>";
            s_xlm_cs = s_xlm_cs + "<GP>" + GP + "</GP>";
            s_xlm_cs = s_xlm_cs + "<GD>" + GD + "</GD>";
            s_xlm_cs = s_xlm_cs + "<JH>" + JH + "</JH>";
            s_xlm_cs = s_xlm_cs + "<LOTNO>" + tvPH.getText().toString() + "</LOTNO>";
            s_xlm_cs = s_xlm_cs + "<PL>" + tvPL.getText().toString() + "</PL>";
            s_xlm_cs = s_xlm_cs + "<MJ>" + tvMJ.getText().toString() + "</MJ>";
            s_xlm_cs = s_xlm_cs + "<TJY>" + tvZYY.getText().toString() + "</TJY>";
            s_xlm_cs = s_xlm_cs + "<SCZS>" + etSCZS.getText().toString() + "</SCZS>";
            s_xlm_cs = s_xlm_cs + "<XHJC>" + xhjc + "</XHJC>";
            s_xlm_cs = s_xlm_cs + "<JXLX>" + tvJXLX.getText().toString() + "</JXLX>";
            s_xlm_cs = s_xlm_cs + "<FZLX>" + tvFZLX.getText().toString() + "</FZLX>";
            s_xlm_cs = s_xlm_cs + "<DZM>" + tvDZM.getText().toString() + "</DZM>";
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
                    Toast.makeText(JXKSActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(JXKSActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                SoundManager.playSound(2, 1);
                Toast.makeText(JXKSActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
            }

            Looper.loop();
        }
    };
    Runnable getJXLXRunnable = new Runnable() {

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
                String str = CallWebService.CallWebService(Constants.Get_JXLXMethodName, Constants.Namespace, params, Constants.getHttp());
                Logs.d(TAG, str);
//                D/----返回的数据----: anyType{schema=anyType{element=anyType{complexType=anyType{choice=anyType{element=anyType{complexType=anyType{sequence=anyType{element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; }; }; }; }; }; }; }; diffgram=anyType{NewDataSet=anyType{TAB_FJ=anyType{FJ001=60001; FJ002=附件01; FJ003=Y; FJ004=anyType{}; }; }; }; }
                ArrayList<Map<String, Object>> list = DecodeXml.decodeDataset(str,"TAB_JX");
                Logs.d(TAG, list.get(0).get("JX002").toString());
                strJXLX = new String[list.size()];
                selectedJXLX = new boolean[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    strJXLX[i] = list.get(i).get("JX002").toString();
                    selectedJXLX[i] = false;
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                SoundManager.playSound(2, 1);
                Toast.makeText(JXKSActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
            }
            Looper.loop();
        }
    };
    Runnable getFZLXRunnable = new Runnable() {

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
                String str = CallWebService.CallWebService(Constants.Get_FZLXMethodName, Constants.Namespace, params, Constants.getHttp());
                Logs.d(TAG, str);
//                D/----返回的数据----: anyType{schema=anyType{element=anyType{complexType=anyType{choice=anyType{element=anyType{complexType=anyType{sequence=anyType{element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; }; }; }; }; }; }; }; diffgram=anyType{NewDataSet=anyType{TAB_FJ=anyType{FJ001=60001; FJ002=附件01; FJ003=Y; FJ004=anyType{}; }; }; }; }
                ArrayList<Map<String, Object>> list = DecodeXml.decodeDataset(str,"TAB_FZLX");
                Logs.d(TAG, list.get(0).get("FZLX002").toString());
                strFZLX = new String[list.size()];
                selectedFZLX = new boolean[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    strFZLX[i] = list.get(i).get("FZLX002").toString();
                    selectedFZLX[i] = false;
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                SoundManager.playSound(2, 1);
                Toast.makeText(JXKSActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
            }
            Looper.loop();
        }
    };
    Runnable getDZMRunnable = new Runnable() {

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
                String str = CallWebService.CallWebService(Constants.Get_DZMMethodName, Constants.Namespace, params, Constants.getHttp());
                Logs.d(TAG, str);
//                D/----返回的数据----: anyType{schema=anyType{element=anyType{complexType=anyType{choice=anyType{element=anyType{complexType=anyType{sequence=anyType{element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; }; }; }; }; }; }; }; diffgram=anyType{NewDataSet=anyType{TAB_FJ=anyType{FJ001=60001; FJ002=附件01; FJ003=Y; FJ004=anyType{}; }; }; }; }
                ArrayList<Map<String, Object>> list = DecodeXml.decodeDataset(str,"TAB_DZMSD");
                Logs.d(TAG, list.get(0).get("DZMSD002").toString());
                strDZM = new String[list.size()];
                selectedDZM = new boolean[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    strDZM[i] = list.get(i).get("DZMSD002").toString();
                    selectedDZM[i] = false;
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                SoundManager.playSound(2, 1);
                Toast.makeText(JXKSActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
            }
            Looper.loop();
        }
    };
}
