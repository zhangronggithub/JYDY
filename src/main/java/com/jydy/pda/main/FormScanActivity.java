package com.jydy.pda.main;

import android.app.Dialog;
import android.content.Intent;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jydy.pda.R;
import com.jydy.pda.config.Constants;
import com.jydy.pda.https.CallWebService;
import com.jydy.pda.https.DecodeXml;
import com.jydy.pda.ui.sc.CJJSActivity;
import com.jydy.pda.ui.sc.CJKSActivity;
import com.jydy.pda.ui.sc.FZJSActivity;
import com.jydy.pda.ui.sc.FZKSActivity;
import com.jydy.pda.ui.sc.JXJSActivity;
import com.jydy.pda.ui.sc.JXKSActivity;
import com.jydy.pda.ui.sc.YDZJSActivity;
import com.jydy.pda.ui.sc.YDZKSActivity;
import com.jydy.pda.utils.Logs;
import com.jydy.pda.view.MyWaitDialog;
import com.jydy.pda.utils.SoundManager;

import java.util.HashMap;
import java.util.Map;

public class FormScanActivity extends BaseActivity implements OnClickListener {

    private String TAG = this.getClass().getSimpleName();

    private Button btnNext;

    private ImageView ivExit, ivSaoma;

    private EditText etGyForm, etGpForm;

    private String GyType, GyID = "", GyName, GP, GD = "",type;

    private String flag, error, GPZT, PL, XZ, XJ, QXCD, QPCDJ, QPCDY, LOTNO,JDLLZ,YDLLZ,JH,JXCS;

    private TextView tvContent, tvGyName;

    private Dialog myWaitDialog;

    private String MINSCZS,MAXSCZS,MINYZDZJLL,MAXYZDZJLL,MINYZDZYLL,MAXYZDZYLL;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_form_scan;
    }

    @Override
    protected void initView() {
        ivExit = (ImageView) findViewById(R.id.ivExit);
        btnNext = (Button) findViewById(R.id.btnNext);
        tvContent = (TextView) findViewById(R.id.tvContent);
        tvGyName = (TextView) findViewById(R.id.tvGyName);
        etGyForm = (EditText) findViewById(R.id.etGyForm);
        etGpForm = (EditText) findViewById(R.id.etGpForm);
    }

    @Override
    protected void initAction() {
        ivExit.setOnClickListener(this);
        btnNext.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        type = getIntent().getStringExtra("type");
    }

    @Override
    protected void showTM(String tmStr) {
        if (tmStr.contains("C001")) {
            try {
                GyType = DecodeXml.decodeXml(tmStr, "C001");
                GyID = DecodeXml.decodeXml(tmStr, "ID");
                GyName = DecodeXml.decodeXml(tmStr, "NAME");
            } catch (Exception e) {
                Toast.makeText(FormScanActivity.this, "工艺条码解析错误", Toast.LENGTH_SHORT).show();
            }
            etGyForm.setText(GyID);
            tvGyName.setText(GyName);
        } else {
            if (TextUtils.isEmpty(etGyForm.getText().toString())) {
                SoundManager.playSound(2, 1);
                Toast.makeText(FormScanActivity.this, "请先扫描工艺条码", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    GP = DecodeXml.decodeXml(tmStr, "GP");
                    GD = DecodeXml.decodeXml(tmStr, "GD");
                } catch (Exception e) {
                    Toast.makeText(FormScanActivity.this, "工票条码解析错误", Toast.LENGTH_SHORT).show();
                }
                etGpForm.setText(GP);
                next();
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivExit:
                finish();
                break;
            case R.id.btnNext:
                 next();
                break;

            default:
                break;
        }

    }

    private void next() {
        if (TextUtils.isEmpty(etGyForm.getText().toString())) {
            SoundManager.playSound(2, 1);
            Toast.makeText(FormScanActivity.this, "请扫描工艺条码", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(etGpForm.getText().toString())||GD.equals("")) {
            SoundManager.playSound(2, 1);
            Toast.makeText(FormScanActivity.this, "请扫描工票条码", Toast.LENGTH_SHORT).show();
        }
        else if (!etGyForm.getText().toString().trim().equals("7001")&&!etGyForm.getText().toString().trim().equals("7010")&&!etGyForm.getText().toString().trim().equals("7020")&&!etGyForm.getText().toString().trim().equals("7030")) {
            SoundManager.playSound(2, 1);
            Toast.makeText(FormScanActivity.this, "请扫描正确的工艺条码", Toast.LENGTH_SHORT).show();
        }
        else {
        myWaitDialog = MyWaitDialog.createLoadingDialog(FormScanActivity.this,"加载中，请稍后~");
        myWaitDialog.show();
            if (type.equals("0")) {
                Thread mThread = new Thread(nextRunnable);
                mThread.start();
            } else {
                Thread mThread = new Thread(JXWGRunnable);
                mThread.start();
            }

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
            s_xlm_cs = s_xlm_cs + "<GY>" + etGyForm.getText().toString() + "</GY>";
            s_xlm_cs = s_xlm_cs + "<GP>" + etGpForm.getText().toString() + "</GP>";
            s_xlm_cs = s_xlm_cs + "<GD>" + GD + "</GD>";
            s_xlm_cs = s_xlm_cs + "<GYLX>" + etGyForm.getText().toString() + "</GYLX>";
            s_xlm_cs = s_xlm_cs + "</DETAIL>";
            s_xlm_cs = s_xlm_cs + "</ROOT>";
            params.put("s_xml_cs", s_xlm_cs);
            Logs.d(TAG, s_xlm_cs);
            try {
                String str = CallWebService.CallWebService(Constants.Get_GPMethodName, Constants.Namespace, params, Constants.getHttp());
                Logs.d(TAG, str);
                flag = DecodeXml.decodeXml(str, "FLAG");
                error = DecodeXml.decodeXml(str, "ERROR");
                GPZT = DecodeXml.decodeXml(str, "GPZT");
                PL = DecodeXml.decodeXml(str, "PL");
                XZ = DecodeXml.decodeXml(str, "XZ");
                XJ = DecodeXml.decodeXml(str, "XJ");
                QXCD = DecodeXml.decodeXml(str, "QXCD");
                QPCDJ = DecodeXml.decodeXml(str, "QPCDJ");
                QPCDY = DecodeXml.decodeXml(str, "QPCDY");
                LOTNO = DecodeXml.decodeXml(str, "LOTNO");
                JDLLZ = DecodeXml.decodeXml(str, "YZDZJ");
                YDLLZ = DecodeXml.decodeXml(str, "YZDZY");
                JH = DecodeXml.decodeXml(str, "PM");
                JXCS = DecodeXml.decodeXml(str, "JXCS");
                MINSCZS = DecodeXml.decodeXml(str, "MINSCZS");
                MAXSCZS = DecodeXml.decodeXml(str, "MAXSCZS");
                MINYZDZJLL = DecodeXml.decodeXml(str, "MINYZDZJLL");
                MAXYZDZJLL = DecodeXml.decodeXml(str, "MAXYZDZJLL");
                MINYZDZYLL = DecodeXml.decodeXml(str, "MINYZDZYLL");
                MAXYZDZYLL = DecodeXml.decodeXml(str, "MAXYZDZYLL");


                if (flag.equals("S")) {
                    Logs.d(TAG, "111111");
                    if (etGyForm.getText().toString().equals("7001")) {
                        Logs.d(TAG, "22222");
                        if (GPZT.equals("1")) {
                            Logs.d(TAG, "33333");
                            Intent intent = new Intent(FormScanActivity.this, CJKSActivity.class);
                            intent.putExtra("GD", GD);
                            intent.putExtra("GY", etGyForm.getText().toString());
                            intent.putExtra("GP", etGpForm.getText().toString());
                            intent.putExtra("PL", PL);
                            intent.putExtra("XZ", XZ);
                            intent.putExtra("XJ", XJ);
                            intent.putExtra("QXCD", QXCD);
                            intent.putExtra("QPCDJ", QPCDJ);
                            intent.putExtra("QPCDY", QPCDY);
                            intent.putExtra("MINSCZS", MINSCZS);
                            intent.putExtra("MAXSCZS", MAXSCZS);
                            intent.putExtra("MINYZDZJLL", MINYZDZJLL);
                            intent.putExtra("MAXYZDZJLL", MAXYZDZJLL);
                            intent.putExtra("MINYZDZYLL", MINYZDZYLL);
                            intent.putExtra("MAXYZDZYLL", MAXYZDZYLL);
                            startActivity(intent);
                        } else if (GPZT.equals("2")) {
                            Intent intent = new Intent(FormScanActivity.this, CJJSActivity.class);
                            intent.putExtra("GD", GD);
                            intent.putExtra("GY", etGyForm.getText().toString());
                            intent.putExtra("GP", etGpForm.getText().toString());
                            intent.putExtra("PL", PL);
                            startActivity(intent);
                        }else if(GPZT.equals("3")) {
                            Toast.makeText(FormScanActivity.this, "此工票已完工！", Toast.LENGTH_SHORT).show();
                        }
                    } else if (etGyForm.getText().toString().equals("7010")) {
                        if (GPZT.equals("1")) {
                            Intent intent = new Intent(FormScanActivity.this, YDZKSActivity.class);
                            intent.putExtra("GD", GD);
                            intent.putExtra("GY", etGyForm.getText().toString());
                            intent.putExtra("GP", etGpForm.getText().toString());
                            intent.putExtra("PL", PL);
                            intent.putExtra("LOTNO", LOTNO);
                            intent.putExtra("JDLLZ", JDLLZ);
                            intent.putExtra("YDLLZ", YDLLZ);
                            startActivity(intent);
                        } else if (GPZT.equals("2")) {
                            Intent intent = new Intent(FormScanActivity.this, YDZJSActivity.class);
                            intent.putExtra("GD", GD);
                            intent.putExtra("GY", etGyForm.getText().toString());
                            intent.putExtra("GP", etGpForm.getText().toString());
                            intent.putExtra("PL", PL);
                            intent.putExtra("LOTNO", LOTNO);
                            startActivity(intent);
                        }else if(GPZT.equals("3")) {
                            Toast.makeText(FormScanActivity.this, "此工票已完工！", Toast.LENGTH_SHORT).show();
                        }
                    } else if (etGyForm.getText().toString().equals("7020")) {
                        if (JXCS.equals("N")){
                            Toast.makeText(FormScanActivity.this, "此工单工艺扫描已超过两次！", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (GPZT.equals("1")) {
                            Intent intent = new Intent(FormScanActivity.this, JXKSActivity.class);
                            intent.putExtra("GD", GD);
                            intent.putExtra("GY", etGyForm.getText().toString());
                            intent.putExtra("GP", etGpForm.getText().toString());
                            intent.putExtra("PL", PL);
                            intent.putExtra("JH", JH);
                            intent.putExtra("LOTNO", LOTNO);

                            startActivity(intent);
                        } else if (GPZT.equals("2")) {
                            Intent intent = new Intent(FormScanActivity.this, JXJSActivity.class);
                            intent.putExtra("GD", GD);
                            intent.putExtra("GY", etGyForm.getText().toString());
                            intent.putExtra("GP", etGpForm.getText().toString());
                            intent.putExtra("PL", PL);
                            intent.putExtra("LOTNO", LOTNO);
                            startActivity(intent);
                        }else if(GPZT.equals("3")) {
                            Toast.makeText(FormScanActivity.this, "此工票已完工！", Toast.LENGTH_SHORT).show();
                        }
                    } else if (etGyForm.getText().toString().equals("7030")) {
                        if (GPZT.equals("1")) {
                            Intent intent = new Intent(FormScanActivity.this, FZKSActivity.class);
                            intent.putExtra("GD", GD);
                            intent.putExtra("GY", etGyForm.getText().toString());
                            intent.putExtra("GP", etGpForm.getText().toString());
                            intent.putExtra("PL", PL);
                            intent.putExtra("LOTNO", LOTNO);
                            startActivity(intent);
                        } else if (GPZT.equals("2")) {
                            Intent intent = new Intent(FormScanActivity.this, FZJSActivity.class);
                            intent.putExtra("GD", GD);
                            intent.putExtra("GY", etGyForm.getText().toString());
                            intent.putExtra("GP", etGpForm.getText().toString());
                            intent.putExtra("PL", PL);
                            intent.putExtra("LOTNO", LOTNO);
                            startActivity(intent);
                        }else if(GPZT.equals("3")) {
                            Toast.makeText(FormScanActivity.this, "此工票已完工！", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(FormScanActivity.this, "请扫描工艺条码！", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(FormScanActivity.this, error, Toast.LENGTH_SHORT).show();

                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                SoundManager.playSound(2, 1);
                Toast.makeText(FormScanActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
            }
            myWaitDialog.dismiss();
            Looper.loop();
        }
    };

    Runnable JXWGRunnable = new Runnable() {

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
            s_xlm_cs = s_xlm_cs + "<GY>" + etGyForm.getText().toString() + "</GY>";
            s_xlm_cs = s_xlm_cs + "<GP>" + etGpForm.getText().toString() + "</GP>";
            s_xlm_cs = s_xlm_cs + "<GD>" + GD + "</GD>";
            s_xlm_cs = s_xlm_cs + "<GYLX>" + etGyForm.getText().toString() + "</GYLX>";
            s_xlm_cs = s_xlm_cs + "</DETAIL>";
            s_xlm_cs = s_xlm_cs + "</ROOT>";
            params.put("s_xml_cs", s_xlm_cs);
            Logs.d(TAG, s_xlm_cs);
            try {
                String str = CallWebService.CallWebService(Constants.Save_JXMethodName, Constants.Namespace, params, Constants.getHttp());
                Logs.d(TAG, str);
                flag = DecodeXml.decodeXml(str, "FLAG");
                error = DecodeXml.decodeXml(str, "ERROR");
                if (flag.equals("S")) {
                    Toast.makeText(FormScanActivity.this, "接线完工！", Toast.LENGTH_SHORT).show();
                } else {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(FormScanActivity.this, error, Toast.LENGTH_SHORT).show();

                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                SoundManager.playSound(2, 1);
                Toast.makeText(FormScanActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
            }
            myWaitDialog.dismiss();
            Looper.loop();
        }
    };
}
