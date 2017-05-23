package com.jydy.pda.ui.sc;

import android.app.Activity;
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
import static com.jydy.pda.R.id.etJDLLZ;
import static com.jydy.pda.R.id.etSCZS;
import static com.jydy.pda.R.id.etWGSL;
import static com.jydy.pda.R.id.etYDLLZ;
import static com.jydy.pda.R.id.tvBB;
import static com.jydy.pda.R.id.tvBLYY;
import static com.jydy.pda.R.id.tvMJ;
import static com.jydy.pda.R.id.tvPH;
import static com.jydy.pda.R.id.tvQXCD;
import static com.jydy.pda.R.id.tvSB;
import static com.jydy.pda.R.id.tvXJ;
import static com.jydy.pda.R.id.tvXZ;
import static com.jydy.pda.R.raw.error;

/**
 * Created by 23923 on 2017/2/7.
 */

public class JXJSActivity extends BaseActivity {

    @Bind(R.id.tvTitle)
    TextView tvTitle;
    @Bind(R.id.tvUserID)
    TextView tvUserID;
    @Bind(R.id.tvGP)
    TextView tvGP;
    @Bind(R.id.tvPL)
    TextView tvPL;
    @Bind(R.id.etTM)
    EditText etTM;
    @Bind(R.id.tvJCY)
    TextView tvJCY;
    @Bind(R.id.etLL)
    EditText etLL;
    @Bind(R.id.etSCZZ)
    EditText etSCZZ;
    @Bind(R.id.etYZSL)
    EditText etYZSL;
    @Bind(R.id.rb_yzwgqr_hg)
    RadioButton rbYzwgqrHg;
    @Bind(R.id.rb_yzwgqr_bhg)
    RadioButton rbYzwgqrBhg;
    @Bind(R.id.btnSave)
    Button btnSave;

    String yzwgqr,flag,error,type,ID,NAME;

    String GD, GP, GY, PL,MAXSCZZ2,MINSCZZ2;
    @Override
    protected int getContentLayout() {
        return R.layout.activity_jxjs;
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
        rbYzwgqrHg.performClick();
        GD = getIntent().getStringExtra("GD");
        GY = getIntent().getStringExtra("GY");
        GP = getIntent().getStringExtra("GP");
        PL = getIntent().getStringExtra("PL");
        MAXSCZZ2 = getIntent().getStringExtra("MAXSCZZ2");
        MINSCZZ2 = getIntent().getStringExtra("MINSCZZ2");
        tvGP.setText(GP);
        tvPL.setText(PL);
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
                if(tvJCY.getText().toString().contains(ID)){
                    Toast.makeText(this, "请不要重复扫描！", Toast.LENGTH_SHORT).show();
                }else {
                    tvJCY.setText(tvJCY.getText().toString() + ID + ";");
                }
                etTM.getText().clear();
            } else {
                Toast.makeText(JXJSActivity.this, "请扫描检查员条码！", Toast.LENGTH_SHORT).show();
                etTM.getText().clear();
            }
        } catch (Exception e) {
            Toast.makeText(JXJSActivity.this, "请扫描检查员条码！", Toast.LENGTH_SHORT).show();
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


    @OnClick({R.id.rb_yzwgqr_hg, R.id.rb_yzwgqr_bhg, R.id.btnSave})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rb_yzwgqr_hg:
                yzwgqr = "Y";
                break;
            case R.id.rb_yzwgqr_bhg:
                yzwgqr = "N";
                break;
            case R.id.btnSave:
                if (TextUtils.isEmpty(tvJCY.getText().toString())) {
                    SoundManager.playSound(2, 1);
                   Toast.makeText( JXJSActivity.this, "请扫描检查员！", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(etLL.getText().toString())) {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(JXJSActivity.this, "请输入拉力！", Toast.LENGTH_SHORT).show();
                    return;
                }else if (TextUtils.isEmpty(etSCZZ.getText().toString())&Constants.SCZTYPE.equals("Y")) {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(JXJSActivity.this, "请输入实测值终！", Toast.LENGTH_SHORT).show();
                    return;
                }else if (TextUtils.isEmpty(etYZSL.getText().toString())) {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(JXJSActivity.this, "请输入压着数量！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!TextUtils.isEmpty(etSCZZ.getText().toString())) {
                    if (Float.parseFloat(etSCZZ.getText().toString().trim())>Float.parseFloat(MAXSCZZ2)||Float.parseFloat(etSCZZ.getText().toString().trim())<Float.parseFloat(MINSCZZ2)){
                        SoundManager.playSound(2, 1);
                        Toast.makeText(JXJSActivity.this, "实测值终不在范围之类！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                    Thread mThread = new Thread(nextRunnable);
                    mThread.start();

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
            s_xlm_cs = s_xlm_cs + "<GYLX>" + "7020" + "</GYLX>";
            s_xlm_cs = s_xlm_cs + "<GPZT>" + "2" + "</GPZT>";
            s_xlm_cs = s_xlm_cs + "<GY>" + GY + "</GY>";
            s_xlm_cs = s_xlm_cs + "<GP>" + GP + "</GP>";
            s_xlm_cs = s_xlm_cs + "<GD>" + GD + "</GD>";
            s_xlm_cs = s_xlm_cs + "<YZQRWG>" + yzwgqr + "</YZQRWG>";
            s_xlm_cs = s_xlm_cs + "<LL>" + etLL.getText().toString() + "</LL>";
            s_xlm_cs = s_xlm_cs + "<SCZZ>" + etSCZZ.getText().toString() + "</SCZZ>";
            s_xlm_cs = s_xlm_cs + "<YZCSQTY>" + etYZSL.getText().toString() + "</YZCSQTY>";
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
                    Toast.makeText(JXJSActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(JXJSActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                SoundManager.playSound(2, 1);
                Toast.makeText(JXJSActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
            }

            Looper.loop();
        }
    };

}
