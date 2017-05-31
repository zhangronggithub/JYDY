package com.jydy.pda.ui.sc.scrk;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jydy.pda.R;
import com.jydy.pda.config.Constants;
import com.jydy.pda.https.CallWebService;
import com.jydy.pda.https.DecodeXml;
import com.jydy.pda.main.BaseActivity;
import com.jydy.pda.main.FormScanActivity;
import com.jydy.pda.utils.Logs;
import com.jydy.pda.utils.SoundManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.R.attr.type;


/**
 * Created by 23923 on 2017/2/7.
 */

public class SCRKActivity extends BaseActivity {

    String TAG = getClass().getSimpleName();

    @Bind(R.id.tvTitle)
    TextView tvTitle;
    @Bind(R.id.tvUserID)
    TextView tvUserID;
    @Bind(R.id.tvDB1)
    TextView tvDB1;
    @Bind(R.id.tvDB2)
    TextView tvDB2;
    @Bind(R.id.ivDB)
    ImageView ivDB;
    @Bind(R.id.llDB)
    LinearLayout llDB;
    @Bind(R.id.tvCK1)
    TextView tvCK1;
    @Bind(R.id.tvCK2)
    TextView tvCK2;
    @Bind(R.id.ivCK)
    ImageView ivCK;
    @Bind(R.id.llCK)
    LinearLayout llCK;
    @Bind(R.id.tvBM1)
    TextView tvBM1;
    @Bind(R.id.tvBM2)
    TextView tvBM2;
    @Bind(R.id.ivBM)
    ImageView ivBM;
    @Bind(R.id.llBM)
    LinearLayout llBM;
    @Bind(R.id.etTM)
    EditText etTM;
    @Bind(R.id.spGD)
    Spinner spGD;
    @Bind(R.id.tvKWTM)
    TextView tvKWTM;
    @Bind(R.id.tvCPTM)
    TextView tvCPTM;
    @Bind(R.id.tvPH)
    TextView tvPH;
    @Bind(R.id.tvPM)
    TextView tvPM;
    @Bind(R.id.tvLOTNO)
    TextView tvLOTNO;
    @Bind(R.id.etQTY)
    EditText etQTY;
    @Bind(R.id.tvDW)
    TextView tvDW;
    @Bind(R.id.tvGG)
    TextView tvGG;
    @Bind(R.id.btnSave)
    Button btnSave;
    @Bind(R.id.btnCommit)
    Button btnCommit;
    @Bind(R.id.ivBack)
    ImageView ivBack;
    @Bind(R.id.rlTopTitle)
    RelativeLayout rlTopTitle;
    @Bind(R.id.tvCPTM1)
    TextView tvCPTM1;

    List<String> data_list;

    ArrayAdapter<String> arr_adapter;

    private String CK1 = "", CK2 = "", BM1 = "", BM2 = "", DB2 = "", DB1 = "",DH="",DB="",GP,GD;

    private String FLAG1,ERROR1,SCSJ,DB3,DH3,XH,PH,PM,GG,QTY,DW,LOTNO,FLAG,ERROR;

    private ProgressDialog pd;

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
        tvUserID.setText(Constants.USERID);
    }

    @Override
    protected void showTM(String tmStr) {
        etTM.setText(tmStr);
        if (tmStr.contains("GP")) {
            try {
                GP = DecodeXml.decodeXml(tmStr, "GP");
                GD = DecodeXml.decodeXml(tmStr, "GD");
                data_list = new ArrayList<String>();
                data_list.add(GD);
                //适配器
                arr_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data_list);
                //设置样式
                arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spGD.setAdapter(arr_adapter);
                spGD.setSelection(0);
                etTM.getText().clear();
            } catch (Exception e) {
                Toast.makeText(this, "工票条码解析错误", Toast.LENGTH_SHORT).show();
            }

        }
        else if ((etTM.getText().toString().length() + "").equals(Constants.KWTMCD) & etTM.getText().toString().substring(0, 2).equals("KW")) {
            if (tvCK1.getText().toString().equals("")) {
                SoundManager.playSound(2, 1);
                Toast.makeText(this, "请先选择仓库！", Toast.LENGTH_SHORT).show();

            } else if (tvDB1.getText().toString().equals("")) {
                SoundManager.playSound(2, 1);
                Toast.makeText(this, "请先选择单别！", Toast.LENGTH_SHORT).show();

            } else {
                myHandler.sendEmptyMessage(3);
//                Thread mThread = new Thread(kwRunnable);
//                mThread.start();
            }
        } else if ((etTM.getText().toString().length() + "").equals(Constants.CPTMCD)) {
            tvCPTM1.setText("成品：");
            etQTY.setEnabled(false);
            if (tvKWTM.getText().toString().equals("")) {
                SoundManager.playSound(2, 1);
                Toast.makeText(this, "请先扫描库位条码！", Toast.LENGTH_SHORT).show();
                return;
            } else {
                tvCPTM.setText(etTM.getText());
                etTM.getText().clear();
                Thread mThread = new Thread(csRunnable);
                mThread.start();
            }
        } else if ((etTM.getText().toString().length() + "").equals(Constants.XTMCD) & etTM.getText().toString().substring(0, 2).equals("ZX")) {
            tvCPTM1.setText("纸箱：");
            etQTY.setEnabled(false);
            if (tvKWTM.getText().toString().equals("")) {
                SoundManager.playSound(2, 1);
                Toast.makeText(this, "请先扫描库位条码！", Toast.LENGTH_SHORT).show();
                return;
            } else {
                tvCPTM.setText(etTM.getText());
                etTM.getText().clear();
                save1();
            }
        } else if ((etTM.getText().toString().length() + "").equals(Constants.TPTMCD) & etTM.getText().toString().substring(0, 2).equals("ZB")) {
            tvCPTM1.setText("托盘：");
            etQTY.setEnabled(false);
            if (tvKWTM.getText().toString().equals("")) {
                SoundManager.playSound(2, 1);
                Toast.makeText(this, "请先扫描库位条码！", Toast.LENGTH_SHORT).show();
                return;
            } else {
                tvCPTM.setText(etTM.getText());
                etTM.getText().clear();
                save1();
            }
        } else if ((etTM.getText().toString().length() + "").equals(Constants.YCLTMCD) & etTM.getText().toString().substring(0, 1).equals("M")) {
            tvCPTM1.setText("原材料：");
            if (tvKWTM.getText().toString().equals("")) {
                SoundManager.playSound(2, 1);
                Toast.makeText(this, "请先扫描库位条码！", Toast.LENGTH_SHORT).show();
                return;
            } else {
                tvCPTM.setText(etTM.getText());
                etTM.getText().clear();
                Thread mThread = new Thread(csRunnable);
                mThread.start();
            }
        } else {
            etTM.requestFocus();
            SoundManager.playSound(2, 1);
            Toast.makeText(this, "请扫描或输入正确的条码！", Toast.LENGTH_SHORT).show();
        }


    }

    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    tvPH.setText(PH);
                    tvPM.setText(PM);
                    tvDW.setText(DW);
                    tvGG.setText(GG);
                    tvLOTNO.setText(LOTNO);
                    etQTY.setText(QTY);
                    save();
                    break;
                case 2:
                    tvPH.setText(PH);
                    tvPM.setText(PM);
                    tvDW.setText(DW);
                    tvGG.setText(GG);
                    tvLOTNO.setText(LOTNO);
                    etQTY.setText(QTY);
                    DB = tvDB1.getText().toString();
                    llDB.setVisibility(View.INVISIBLE);
                    break;
                case 3:
                    tvKWTM.setText(etTM.getText());
                    etTM.getText().clear();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @OnClick({R.id.ivDB, R.id.ivCK, R.id.ivBM, R.id.btnSave, R.id.btnCommit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivDB:
                Intent i = new Intent(SCRKActivity.this, DBActivity.class);
                i.putExtra("type", "58");
                startActivityForResult(i, 1);
                break;
            case R.id.ivCK:
                Intent i1 = new Intent(SCRKActivity.this, CKActivity.class);
                i1.putExtra("type", "PD");
                startActivityForResult(i1, 2);
                break;
            case R.id.ivBM:
                Intent i2 = new Intent(SCRKActivity.this, BMActivity.class);
                i2.putExtra("type", "PD");
                startActivityForResult(i2, 3);
                break;
            case R.id.btnSave:
                if ((tvCPTM.getText().length()+"").equals(Constants.CPTMCD)){
                    save();
                }else{
                    save1();
                }
                break;
            case R.id.btnCommit:
                pd = ProgressDialog.show(SCRKActivity.this, "标题", "提交中，请稍后……");
                Thread mThread2 = new Thread(rkRunnable);
                mThread2.start();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case 1:
                    DB2 = data.getStringExtra("MQ002");
                    DB1 = data.getStringExtra("MQ001");
                    tvDB1.setText(DB1);
                    tvDB2.setText(DB2);
                case 2:
                    CK1 = data.getStringExtra("MC001");
                    CK2 = data.getStringExtra("MC002");
                    tvCK1.setText(CK1);
                    tvCK2.setText(CK2);
                    break;
                case 3:
                    BM1 = data.getStringExtra("ME001");
                    BM2 = data.getStringExtra("ME002");
                    tvBM1.setText(BM1);
                    tvBM2.setText(BM2);
                    break;
            }
        }
    }
    private void  save(){
        if(Float.parseFloat(etQTY.getText().toString())>Float.parseFloat(QTY)){
            SoundManager.playSound(2, 1);
            Toast.makeText(SCRKActivity.this, "数量超出！", Toast.LENGTH_SHORT).show();
        }else if (tvCK1.getText().toString().equals("")) {
            SoundManager.playSound(2, 1);
            Toast.makeText(SCRKActivity.this, "请选择仓库！", Toast.LENGTH_SHORT).show();
        }else if(tvDB1.getText().toString().equals("")){
            SoundManager.playSound(2, 1);
            Toast.makeText(SCRKActivity.this, "请选择单别！", Toast.LENGTH_SHORT).show();
        }else if(tvKWTM.getText().toString().equals("")){
            SoundManager.playSound(2, 1);
            Toast.makeText(SCRKActivity.this, "请扫描库位！", Toast.LENGTH_SHORT).show();
        }
        else if(tvCPTM.getText().toString().equals("")){
            SoundManager.playSound(2, 1);
            Toast.makeText(SCRKActivity.this, "请扫描成品！", Toast.LENGTH_SHORT).show();
        }
        else if(tvPH.getText().toString().equals("")){
            SoundManager.playSound(2, 1);
            Toast.makeText(SCRKActivity.this, "请输入品号！", Toast.LENGTH_SHORT).show();
        }else if(etQTY.getText().toString().equals("")){
            SoundManager.playSound(2, 1);
            Toast.makeText(SCRKActivity.this, "请输入数量！", Toast.LENGTH_SHORT).show();
        }else  {
            pd = ProgressDialog.show(SCRKActivity.this, "标题", "保存中，请稍后……");
            Thread mThread1 = new Thread(saveRunnable1);
            mThread1.start();
        }
    }
    private void  save1(){
        if (tvCK1.getText().toString().equals("")) {
            SoundManager.playSound(2, 1);
            Toast.makeText(SCRKActivity.this, "请选择仓库！", Toast.LENGTH_SHORT).show();
        }else if(tvDB1.getText().toString().equals("")){
            SoundManager.playSound(2, 1);
            Toast.makeText(SCRKActivity.this, "请选择单别！", Toast.LENGTH_SHORT).show();
        }else if(tvKWTM.getText().toString().equals("")){
            SoundManager.playSound(2, 1);
            Toast.makeText(SCRKActivity.this, "请扫描库位！", Toast.LENGTH_SHORT).show();
        }else if(tvCPTM.getText().toString().equals("")){
            SoundManager.playSound(2, 1);
            Toast.makeText(SCRKActivity.this, "请扫描纸箱或托盘！", Toast.LENGTH_SHORT).show();
        }else  {
            pd = ProgressDialog.show(SCRKActivity.this, "标题", "保存中，请稍后……");
            Thread mThread1 = new Thread(saveRunnable1);
            mThread1.start();
        }
    }
    Runnable csRunnable = new Runnable() {

        @Override
        public void run() {
            Looper.prepare();
            Map<String, String> params = new HashMap<String, String>();
            String s_xlm_cs ="<?xml version=\"1.0\" encoding=\"GB2312\"?>" ;
            s_xlm_cs = s_xlm_cs + "<ROOT>";
            s_xlm_cs = s_xlm_cs + "<DETAIL>" ;
            s_xlm_cs = s_xlm_cs + "<USERID>" + Constants.USERID + "</USERID>" ;
            s_xlm_cs = s_xlm_cs + "<DATABASE>" + Constants.DATABASE + "</DATABASE>" ;
            s_xlm_cs = s_xlm_cs + "<SN>" + Constants.SN + "</SN>" ;
            s_xlm_cs = s_xlm_cs + "<BARCODE>" + tvCPTM.getText().toString() + "</BARCODE>" ;
            s_xlm_cs = s_xlm_cs + "<PFROM>" + type + "</PFROM>>" ;
            s_xlm_cs = s_xlm_cs + "<STORAGEID>" + CK1 + "</STORAGEID>";
            s_xlm_cs = s_xlm_cs + "<KW>" + tvKWTM.getText().toString() + "</KW>" ;
            s_xlm_cs = s_xlm_cs + "<MQ010>" + "1" + "</MQ010>" ;
            s_xlm_cs = s_xlm_cs + "</DETAIL>";
            s_xlm_cs = s_xlm_cs + "</ROOT>" ;
            Logs.d(TAG, s_xlm_cs);
            params.put("s_xml_cs", s_xlm_cs);
            try {
                String str = CallWebService.CallWebService(Constants.GetProductMethodName,Constants.Namespace ,params,Constants.getHttp());
                Logs.d(TAG,str );
                FLAG1 = DecodeXml.decodeXml(str,"FLAG");
                ERROR1 = DecodeXml.decodeXml(str,"ERROR");
                SCSJ = DecodeXml.decodeXml(str,"SCSJ");
                DB3 = DecodeXml.decodeXml(str,"DB");
                DH3 = DecodeXml.decodeXml(str,"DH");
                XH = DecodeXml.decodeXml(str,"XH");
                PH = DecodeXml.decodeXml(str,"PH");
                PM = DecodeXml.decodeXml(str,"PM");
                GG = DecodeXml.decodeXml(str,"GG");
                QTY = DecodeXml.decodeXml(str,"QTY");
                DW = DecodeXml.decodeXml(str,"DW");
                LOTNO = DecodeXml.decodeXml(str,"LOTNO");
                if (FLAG1.equals("S")) {
                    myHandler.sendEmptyMessage(1);
                }else {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(SCRKActivity.this, ERROR1, Toast.LENGTH_SHORT).show();
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
    Runnable rkRunnable = new Runnable() {

        @Override
        public void run() {
            Looper.prepare();
            Map<String, String> params = new HashMap<String, String>();
            String s_xlm_cs ="<?xml version=\"1.0\" encoding=\"GB2312\"?>" ;
            s_xlm_cs = s_xlm_cs + "<ROOT>";
            s_xlm_cs = s_xlm_cs + "<DETAIL>" ;
            s_xlm_cs = s_xlm_cs + "<USERID>" + Constants.USERID + "</USERID>" ;
            s_xlm_cs = s_xlm_cs + "<DATABASE>" + Constants.DATABASE + "</DATABASE>" ;
            s_xlm_cs = s_xlm_cs + "<DJTYPE>" + type + "</DJTYPE>" ;
            s_xlm_cs = s_xlm_cs + "<XHPFROM>" + "1" + "</XHPFROM>" ;
            s_xlm_cs = s_xlm_cs + "<SN>" + Constants.SN + "</SN>" ;
            s_xlm_cs = s_xlm_cs + "<DB>" + DB + "</DB>" ;
            s_xlm_cs = s_xlm_cs + "<DH>" + DH + "</DH>" ;
            s_xlm_cs = s_xlm_cs + "</DETAIL>";
            s_xlm_cs = s_xlm_cs + "</ROOT>" ;
            Logs.d("ddd", s_xlm_cs);
            params.put("s_xml_cs", s_xlm_cs);
            try {
                String str = CallWebService.CallWebService(Constants.Save_SCAN_CommitMethodName,Constants.Namespace ,params,Constants.getHttp());
                Logs.d(TAG,str );
                pd.dismiss();
                String  FLAG = DecodeXml.decodeXml(str,"FLAG");
                String ERROR = DecodeXml.decodeXml(str,"ERROR");
                if (FLAG.equals("S")) {
                    String  ERPNO = DecodeXml.decodeXml(str,"ERPNO");

                }else {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(SCRKActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                pd.dismiss();
                SoundManager.playSound(2, 1);
                Toast.makeText(SCRKActivity.this, "连接超时", Toast.LENGTH_SHORT).show();
            }
            Looper.loop();
        }
    };
    Runnable saveRunnable1 = new Runnable() {

        @Override
        public void run() {
            Looper.prepare();
            Map<String, String> params = new HashMap<String, String>();
            String s_xlm_cs = "<?xml version=\"1.0\" encoding=\"GB2312\"?>" + "<ROOT>";
            s_xlm_cs = s_xlm_cs + "<DETAIL>";
            s_xlm_cs = s_xlm_cs + "<DATABASE>" + Constants.DATABASE + "</DATABASE>";
            s_xlm_cs = s_xlm_cs + "<SN>" + Constants.SN + "</SN>" ;
            s_xlm_cs = s_xlm_cs + "<USERID>" + Constants.USERID + "</USERID>";
            s_xlm_cs = s_xlm_cs + "<DJTYPE>" + type + "</DJTYPE>" ;
            s_xlm_cs = s_xlm_cs + "<XHPFROM>" + 1 + "</XHPFROM>" ;
            s_xlm_cs = s_xlm_cs + "<MQ010>" + 1 + "</MQ010>" ;
            s_xlm_cs = s_xlm_cs + "<DHPFROM>" + DH + "</DHPFROM>" ;
            s_xlm_cs = s_xlm_cs + "<BM>" + BM1 + "</BM>" ;
            s_xlm_cs = s_xlm_cs + "<STORAGEID>" + tvCK1.getText().toString() + "</STORAGEID>";
            s_xlm_cs = s_xlm_cs + "<KW>" +tvKWTM.getText().toString() + "</KW>" ;
            s_xlm_cs = s_xlm_cs + "<BARCODE>" +tvCPTM.getText().toString() + "</BARCODE>" ;

            s_xlm_cs = s_xlm_cs + "<SCSJ>" +SCSJ+ "</SCSJ>" ;
            s_xlm_cs = s_xlm_cs + "<DB>" +tvDB1.getText().toString()+ "</DB>" ;
            s_xlm_cs = s_xlm_cs + "<DH>" +DH + "</DH>" ;
            s_xlm_cs = s_xlm_cs + "<XH>" +XH + "</XH>" ;
            s_xlm_cs = s_xlm_cs + "<PH>" +PH+ "</PH>" ;
            s_xlm_cs = s_xlm_cs + "<PM>" +PM + "</PM>" ;
            s_xlm_cs = s_xlm_cs + "<GG>" +GG + "</GG>" ;
            s_xlm_cs = s_xlm_cs + "<DW>" +DW + "</DW>" ;
            s_xlm_cs = s_xlm_cs + "<QTY>" +etQTY.getText().toString()+ "</QTY>" ;
            if ((tvCPTM.getText().length()+"").equals(Constants.CPTMCD)){
                s_xlm_cs = s_xlm_cs + "<BZXTYPE>" +"04"+ "</BZXTYPE>" ;
            }else if( (tvCPTM.getText().length()+"").equals(Constants.XTMCD)&tvCPTM.getText().toString().substring(0,2).equals("ZX")){
                s_xlm_cs = s_xlm_cs + "<BZXTYPE>" +"01"+ "</BZXTYPE>" ;
            }else if( (tvCPTM.getText().length()+"").equals(Constants.TPTMCD)&tvCPTM.getText().toString().substring(0,2).equals("ZB")){
                s_xlm_cs = s_xlm_cs + "<BZXTYPE>" +"02"+ "</BZXTYPE>" ;
            }
            s_xlm_cs = s_xlm_cs + "<LOTNO>" +LOTNO+ "</LOTNO>" ;
            s_xlm_cs = s_xlm_cs + "</DETAIL>";
            s_xlm_cs = s_xlm_cs + "</ROOT>" ;
            Logs.d(TAG, s_xlm_cs);
            params.put("s_xml_cs", s_xlm_cs);
            try {
                String str = CallWebService.CallWebService(Constants.Save_DJMethodName,Constants.Namespace ,params,Constants.getHttp());
                Logs.d(TAG,str );
                pd.dismiss();
                FLAG = DecodeXml.decodeXml(str,"FLAG");
                ERROR = DecodeXml.decodeXml(str,"ERROR");
                if (FLAG.equals("S")) {
                    Toast.makeText(SCRKActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                    DH = DecodeXml.decodeXml(str,"DH");

                    PH = DecodeXml.decodeXml(str,"PH");
                    PM = DecodeXml.decodeXml(str,"PM");
                    GG = DecodeXml.decodeXml(str,"GG");
                    QTY = DecodeXml.decodeXml(str,"QTY");
                    DW = DecodeXml.decodeXml(str,"DW");
                    LOTNO = DecodeXml.decodeXml(str,"LOTNO");
                    myHandler.sendEmptyMessage(2);

                }else {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(SCRKActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                pd.dismiss();
                SoundManager.playSound(2, 1);
                Toast.makeText(SCRKActivity.this, "连接超时", Toast.LENGTH_SHORT).show();
            }


            Looper.loop();
        }
    };
}
