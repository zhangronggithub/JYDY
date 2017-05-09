package com.jydy.pda.ui.sc;

import android.app.Activity;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import static com.jydy.pda.R.id.etQTY;
import static com.jydy.pda.R.id.tvCK1;
import static com.jydy.pda.R.id.tvCPTM;
import static com.jydy.pda.R.id.tvDB1;
import static com.jydy.pda.R.id.tvKWTM;

/**
 * Created by 23923 on 2017/2/7.
 */

public class SCRKActivity extends BaseActivity {

    @Bind(R.id.tvTitle)
    TextView tvTitle;
    @Bind(R.id.tvUserID)
    TextView tvUserID;
    @Bind(R.id.tvBM)
    TextView tvBM;
    @Bind(R.id.tvDB)
    TextView tvDB;
    @Bind(R.id.etTM)
    EditText etTM;
    @Bind(R.id.tvPM)
    TextView tvPM;
    @Bind(R.id.tvQTY)
    TextView tvQTY;
    @Bind(R.id.btnSave)
    Button btnSave;
    @Bind(R.id.btnCommit)
    Button btnCommit;

    String TAG = getClass().getSimpleName();

    String ERROR,FLAG;

    @OnClick({R.id.btnSave, R.id.btnCommit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSave:
                break;
            case R.id.btnCommit:
                Thread mThread1 = new Thread(saveRunnable);
                mThread1.start();
                break;
        }
    }
    Runnable saveRunnable = new Runnable() {

        @Override
        public void run() {
            Looper.prepare();
            Map<String, String> params = new HashMap<String, String>();
            String s_xlm_cs = "<?xml version=\"1.0\" encoding=\"GB2312\"?>" + "<ROOT>";
            s_xlm_cs = s_xlm_cs + "<DETAIL>";
            s_xlm_cs = s_xlm_cs + "<DATABASE>" + Constants.DATABASE + "</DATABASE>";
            s_xlm_cs = s_xlm_cs + "<SN>" + Constants.SN + "</SN>" ;
            s_xlm_cs = s_xlm_cs + "<USERID>" + Constants.USERID + "</USERID>";
            s_xlm_cs = s_xlm_cs + "</DETAIL>";
            s_xlm_cs = s_xlm_cs + "</ROOT>" ;
            Logs.d(TAG, s_xlm_cs);
            params.put("s_xml_cs", s_xlm_cs);
            try {
                String str = CallWebService.CallWebService(Constants.Save_DJMethodName,Constants.Namespace ,params,Constants.getHttp());
                Logs.d(TAG,str );
                FLAG = DecodeXml.decodeXml(str,"FLAG");
                ERROR = DecodeXml.decodeXml(str,"ERROR");
                if (FLAG.equals("S")) {
                    Toast.makeText(SCRKActivity.this, "保存成功", Toast.LENGTH_SHORT).show();




                }else {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(SCRKActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                SoundManager.playSound(2, 1);
                Toast.makeText(SCRKActivity.this, "连接超时", Toast.LENGTH_SHORT).show();
            }


            Looper.loop();
        }
    };
    @Override
    protected int getContentLayout() {
        return R.layout.activity_scrk;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initAction() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void showTM(String tmStr) {

    }
}
