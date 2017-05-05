package com.jydy.pda.main;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.util.Xml;
import android.widget.RadioButton;
import android.widget.RadioGroup;


import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.jydy.pda.R;
import com.jydy.pda.bean.PowerListBean;
import com.jydy.pda.fragment.KFFragment;
import com.jydy.pda.fragment.SCFragment;
import com.jydy.pda.fragment.ZJFragment;
import com.jydy.pda.utils.ButtonAuthority;
import com.jydy.pda.https.CallWebService;
import com.jydy.pda.config.Constants;
import com.jydy.pda.https.DecodeXml;
import com.jydy.pda.utils.Logs;
import com.jydy.pda.utils.SoundManager;

import org.xmlpull.v1.XmlPullParser;

public class MainActivity extends FragmentActivity implements OnCheckedChangeListener{
    public String TAG = getClass().getSimpleName();

    //事务处理
    FragmentManager manager;

    FragmentTransaction ft;

    List<Fragment> fragments = new ArrayList<Fragment>();

    KFFragment kfFragment;

    SCFragment scFragment;

    ZJFragment zjFragment;

    RadioGroup rg;

    RadioButton rbKF,rbZJ,rbSC;

    private int currentTagIndex = 2;// 当前Fragment标识

    private final static int KF = 0;// 库房

    private final static int SC = 1;// 生产管理

    private final static int ZJ = 2;// 质量检测

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();//初始化RadioButton
        setUpMenu();//初始化fragments
        changeFragment(SC);//初始化fragment区域的内容
        Thread mThread = new Thread(mRunnable);
        mThread.start();

    }
    Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            Looper.prepare();
            Map<String, String> params = new HashMap<String, String>();
            String s_xlm_cs ="<?xml version=\"1.0\" encoding=\"GB2312\"?>" + "<ROOT>" ;
            s_xlm_cs = s_xlm_cs + "<DETAIL>" ;
            s_xlm_cs = s_xlm_cs + "<SN>" + Constants.SN + "</SN>" ;
            s_xlm_cs = s_xlm_cs + "<USERID>" + Constants.USERID + "</USERID>" ;
            s_xlm_cs = s_xlm_cs + "</DETAIL>" ;
            s_xlm_cs = s_xlm_cs + "</ROOT>" ;

            params.put("s_xml_cs", s_xlm_cs);
            Logs.d("URL", Constants.GetPowerListMethodName);
            try {
                String str = CallWebService.CallWebService(Constants.GetPowerListMethodName, Constants.Namespace ,params, Constants.getHttp());
                Logs.d(TAG,str );
                String flag = DecodeXml.decodeXml(str, "FLAG");
                String error = DecodeXml.decodeXml(str, "ERROR");
                if (flag.equals("S")) {
                    Constants.powerList =  decodeXmlPower(str);
                }else{
                    Toast.makeText(MainActivity.this,error,Toast.LENGTH_SHORT).show();
                }
                Boolean salaryRight =((ButtonAuthority.SAVE & 1540) == ButtonAuthority.SAVE);
                Logs.d(TAG,salaryRight+"");

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                SoundManager.playSound(2, 1);
                Toast.makeText(MainActivity.this,"网络异常！",Toast.LENGTH_SHORT).show();
            }

            Looper.loop();
        }
    };
    public ArrayList<PowerListBean> decodeXmlPower(String content) throws Exception {
        ArrayList<PowerListBean> mList  = null;
        PowerListBean detail = null;
        // 由android.util.Xml创建一个XmlPullParser实例
        XmlPullParser parser = Xml.newPullParser();
        // 设置输入流 并指明编码方式
        parser.setInput(new StringReader(content));
        // 产生第一个事件
        int eventType = parser.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT){
            switch (eventType) {
                // 判断当前事件是否为文档开始事件
                case XmlPullParser.START_DOCUMENT:
                    mList = new ArrayList<PowerListBean>();
                    break;
                // 判断当前事件是否为标签元素开始事件
                case XmlPullParser.START_TAG:
                    if (parser.getName().equals("DETAIL")) {
                        detail = new PowerListBean();
                    }
                    else if (parser.getName().equals("AuthorityID")) {
                        eventType = parser.next();
                        detail.setAuthorityID(parser.getText());
                    }  else if (parser.getName().equals("Authorities")) {
                        eventType = parser.next();
                        detail.setAuthorities(parser.getText());
                    }

                    break;

                // 判断当前事件是否为标签元素结束事件
                case XmlPullParser.END_TAG:
                    if (parser.getName().equals("DETAIL")) {
                        mList.add(detail);
                        detail = null;
                    }
                    break;
            }
            // 进入下一个元素并触发相应事件
            eventType = parser.next();
        }
        return mList;

    }
    private void initView() {
        rbKF = (RadioButton) findViewById(R.id.rbKF);
        rbZJ = (RadioButton) findViewById(R.id.rbZJ);
        rbSC = (RadioButton) findViewById(R.id.rbSC);
        Drawable drawable1 = getResources().getDrawable(R.drawable.home_rb_kc);
        drawable1.setBounds(0, 0, 50, 50);//第一0是距左边距离，第二0是距上边距离，40分别是长宽
        rbKF.setCompoundDrawables(null, drawable1, null, null);//只放左边
        Drawable drawable2 = getResources().getDrawable(R.drawable.home_rg_home);
        drawable2.setBounds(0, 0, 50, 50);//第一0是距左边距离，第二0是距上边距离，40分别是长宽
        rbSC.setCompoundDrawables(null, drawable2, null, null);//只放左边
        Drawable drawable3 = getResources().getDrawable(R.drawable.home_rg_quan);
        drawable3.setBounds(0, 0, 50, 50);//第一0是距左边距离，第二0是距上边距离，40分别是长宽
        rbZJ.setCompoundDrawables(null, drawable3, null, null);//只放左边


    }
    public void setUpMenu() {
        rg=(RadioGroup)findViewById(R.id.rg);
        rg.setOnCheckedChangeListener(this);
        rbKF.setEnabled(false);
        rbZJ.setEnabled(false);
        kfFragment=new KFFragment();
        scFragment=new SCFragment();
        zjFragment=new ZJFragment();


        fragments.add(kfFragment);
        fragments.add(scFragment);
        fragments.add(zjFragment);
    }
    public void changeFragment(int position){

        if(currentTagIndex!=position){
            manager=getFragmentManager();
            ft = manager.beginTransaction();

            ft.hide(fragments.get(currentTagIndex));

            if(!fragments.get(position).isAdded()){
                ft.add(R.id.fragment, fragments.get(position),null);
            }
            ft.show(fragments.get(position));
            ft.commitAllowingStateLoss();
        }
        currentTagIndex = position;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        // TODO Auto-generated method stub
        switch (checkedId) {
            case R.id.rbKF:
                changeFragment(KF);
                break;
            case R.id.rbSC:
                changeFragment(SC);
                break;
            case R.id.rbZJ:
                changeFragment(ZJ);
                break;

            default:
                break;
        }

    }

}


