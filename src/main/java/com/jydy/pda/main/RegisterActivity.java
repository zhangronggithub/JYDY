package com.jydy.pda.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jydy.pda.R;
import com.jydy.pda.utils.Encrypt;
import com.jydy.pda.utils.Logs;
import com.jydy.pda.utils.SoundManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 23923 on 2016/11/18.
 */

public class RegisterActivity extends Activity implements View.OnClickListener{

    private String TAG = getClass().getSimpleName();

    private ImageView ivExit;

    private EditText  etForm;

    private Button btnNext;

    private TextView tvIMEI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ivExit = (ImageView) findViewById(R.id.ivExit);
        etForm = (EditText) findViewById(R.id.etForm);
        btnNext = (Button) findViewById(R.id.btnNext);
        tvIMEI = (TextView) findViewById(R.id.tvIMEI);
        ivExit.setOnClickListener(this);
        btnNext.setOnClickListener(this);

        SoundManager.getInstance();
        SoundManager.initSounds(getBaseContext());
        SoundManager.loadSounds();

        TelephonyManager tm = (TelephonyManager)this.getSystemService(TELEPHONY_SERVICE);
        String str = tm.getDeviceId();
        String code2 = "";
        for (int i=0;i<Math.floor(str.length()/4);i++){
            code2 = code2+str.substring(i*4,(i+1)*4)+" ";
        }
        code2 = code2 + str.substring((int)Math.floor(str.length()/4)*4,str.length());
        tvIMEI.setText(code2);

    }
    public static String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivExit:
                finish();
                break;
            case R.id.btnNext:
                try{
                    String encrypt = Encrypt.encrypt(RegisterActivity.this);
                    Logs.d(TAG,encrypt);
                    if (encrypt.equals(replaceBlank(etForm.getText().toString()))){
                        SharedPreferences.Editor ZCXX = getSharedPreferences("ZCXX", MODE_PRIVATE).edit();
                        ZCXX.putString("ZCXX", encrypt);
                        ZCXX.commit();
                        Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(i);
                    }else{
                        SoundManager.playSound(2, 1);
                        Toast.makeText(RegisterActivity.this,"无效的注册码！",Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                }
                break;


        }
    }
}
