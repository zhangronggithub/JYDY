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
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.R.attr.type;
import static android.content.ContentValues.TAG;


/**
 * Created by 23923 on 2017/2/7.
 */

public class FZKSActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.tvTitle)
    TextView tvTitle;
    @Bind(R.id.tvUserID)
    TextView tvUserID;
    @Bind(R.id.tvGP)
    TextView tvGP;
    @Bind(R.id.tvPL)
    TextView tvPL;
    @Bind(R.id.tvPH)
    TextView tvPH;
    @Bind(R.id.etTM)
    EditText etTM;
    @Bind(R.id.tvZYZ)
    TextView tvZYZ;
    @Bind(R.id.tvZYLX)
    TextView tvZYLX;
    @Bind(R.id.rb_jcqrs_hg)
    RadioButton rbJcqrsHg;
    @Bind(R.id.rb_jcqrs_bhg)
    RadioButton rbJcqrsBhg;
    @Bind(R.id.btnSave)
    Button btnSave;

    String jcqrs,flag,error,type,ID,NAME;

    String GD, GP, GY, PL, LOTNO;

    String[] strZYLX ;
    boolean[] selected ;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_fzks;
    }

    @Override
    protected void initView() {
        Thread mThread = new Thread(getZYLXRunnable);
        mThread.start();
    }

    @Override
    protected void initAction() {
        rbJcqrsHg.performClick();
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
                tvZYZ.setText(ID);
                etTM.getText().clear();
            } else {
                Toast.makeText(FZKSActivity.this, "请扫描作业员条码！", Toast.LENGTH_SHORT).show();
                etTM.getText().clear();
            }
        } catch (Exception e) {
            Toast.makeText(FZKSActivity.this, "请扫描作业员条码！", Toast.LENGTH_SHORT).show();
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


    @OnClick({R.id.rb_jcqrs_hg, R.id.rb_jcqrs_bhg, R.id.btnSave, R.id.tvZYLX})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rb_jcqrs_hg:
                jcqrs = "Y";
                break;
            case R.id.rb_jcqrs_bhg:
                jcqrs = "N";
                break;
            case R.id.tvZYLX:
                showDialog("作业类型",strZYLX,selected,tvZYLX);
                break;
            case R.id.btnSave:
                if (TextUtils.isEmpty(tvZYZ.getText().toString())) {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(FZKSActivity.this, "请扫描作业者！", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(tvZYLX.getText().toString())) {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(FZKSActivity.this, "请选择作业类型！", Toast.LENGTH_SHORT).show();
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
            s_xlm_cs = s_xlm_cs + "<GYLX>" + "7030" + "</GYLX>";
            s_xlm_cs = s_xlm_cs + "<GPZT>" + "1" + "</GPZT>";
            s_xlm_cs = s_xlm_cs + "<GY>" + GY + "</GY>";
            s_xlm_cs = s_xlm_cs + "<GP>" + GP + "</GP>";
            s_xlm_cs = s_xlm_cs + "<GD>" + GD + "</GD>";
            s_xlm_cs = s_xlm_cs + "<LOTNO>" + tvPH.getText().toString() + "</LOTNO>";
            s_xlm_cs = s_xlm_cs + "<PL>" + tvPL.getText().toString() + "</PL>";
            s_xlm_cs = s_xlm_cs + "<ZYLX>" + tvZYLX.getText().toString() + "</ZYLX>";
            s_xlm_cs = s_xlm_cs + "<ZYZ>" + tvZYZ.getText().toString() + "</ZYZ>";
            s_xlm_cs = s_xlm_cs + "<JCYS>" + jcqrs + "</JCYS>";
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
                    Toast.makeText(FZKSActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(FZKSActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                SoundManager.playSound(2, 1);
                Toast.makeText(FZKSActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
            }

            Looper.loop();
        }
    };
    Runnable getZYLXRunnable = new Runnable() {

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
                String str = CallWebService.CallWebService(Constants.Get_ZYLXMethodName, Constants.Namespace, params, Constants.getHttp());
                Logs.d(TAG, str);
//                D/----返回的数据----: anyType{schema=anyType{element=anyType{complexType=anyType{choice=anyType{element=anyType{complexType=anyType{sequence=anyType{element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; }; }; }; }; }; }; }; diffgram=anyType{NewDataSet=anyType{TAB_FJ=anyType{FJ001=60001; FJ002=附件01; FJ003=Y; FJ004=anyType{}; }; }; }; }
                ArrayList<Map<String, Object>> list = DecodeXml.decodeDataset(str,"TAB_ZYLX");
                Logs.d(TAG, list.get(0).get("ZYLX002").toString());
                strZYLX = new String[list.size()];
                selected = new boolean[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    strZYLX[i] = list.get(i).get("ZYLX002").toString();
                    selected[i] = false;
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                SoundManager.playSound(2, 1);
                Toast.makeText(FZKSActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
            }
            Looper.loop();
        }
    };
}
