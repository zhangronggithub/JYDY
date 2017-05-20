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
import static com.jydy.pda.R.id.etSCZS;
import static com.jydy.pda.R.id.tvBB;
import static com.jydy.pda.R.id.tvBLSL;
import static com.jydy.pda.R.id.tvMJ;
import static com.jydy.pda.R.id.tvWGSL;
import static com.jydy.pda.R.raw.error;

/**
 * Created by 23923 on 2017/2/7.
 */

public class FZJSActivity extends BaseActivity {

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
    @Bind(R.id.tvBLYY)
    TextView tvBLYY;
    @Bind(R.id.etBLSL)
    EditText etBLSL;
    @Bind(R.id.etWGSL)
    EditText etWGSL;
    @Bind(R.id.rb_jcyz_hg)
    RadioButton rbJcyzHg;
    @Bind(R.id.rb_jcyz_bhg)
    RadioButton rbJcyzBhg;
    @Bind(R.id.btnSave)
    Button btnSave;
    String jcyz,flag,error,type,ID,NAME;
    String GD, GP, GY, PL, LOTNO;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_fzjs;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initAction() {
        rbJcyzHg.performClick();
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
        etWGSL.setText(PL);
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
                Toast.makeText(FZJSActivity.this, "请扫描不良原因条码！", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(FZJSActivity.this, "请扫描不良原因条码！", Toast.LENGTH_SHORT).show();
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
    @OnClick({R.id.rb_jcyz_hg, R.id.rb_jcyz_bhg, R.id.btnSave})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rb_jcyz_hg:
                jcyz = "Y";
                break;
            case R.id.rb_jcyz_bhg:
                jcyz = "N";
                break;
            case R.id.btnSave:
                if (TextUtils.isEmpty(tvBLYY.getText().toString())&Float.parseFloat(etBLSL.getText().toString())>0) {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(FZJSActivity.this, "请扫描不良原因！", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(etBLSL.getText().toString())) {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(FZJSActivity.this, "请输入不良数量！", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(etWGSL.getText().toString())) {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(FZJSActivity.this, "请输入完工数量！", Toast.LENGTH_SHORT).show();
                }else{
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
            s_xlm_cs = s_xlm_cs + "<GYLX>" + "7030" + "</GYLX>";
            s_xlm_cs = s_xlm_cs + "<GPZT>" + "2" + "</GPZT>";
            s_xlm_cs = s_xlm_cs + "<GY>" + GY + "</GY>";
            s_xlm_cs = s_xlm_cs + "<GP>" + GP + "</GP>";
            s_xlm_cs = s_xlm_cs + "<GD>" + GD + "</GD>";
            s_xlm_cs = s_xlm_cs + "<BLYY>" + tvBLYY.getText().toString() + "</BLYY>";
            s_xlm_cs = s_xlm_cs + "<BLQTY>" + etBLSL.getText().toString() + "</BLQTY>";
            s_xlm_cs = s_xlm_cs + "<JCYZ>" + jcyz + "</JCYZ>";
            s_xlm_cs = s_xlm_cs + "<WGQTY>" + etWGSL.getText().toString() + "</WGQTY>";
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
                    Toast.makeText(FZJSActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(FZJSActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                SoundManager.playSound(2, 1);
                Toast.makeText(FZJSActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
            }

            Looper.loop();
        }
    };
}
