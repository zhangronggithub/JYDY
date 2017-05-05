package com.jydy.pda.main;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.Toast;

import com.imscs.barcodemanager.BarcodeManager;
import com.jydy.pda.utils.Logs;
import com.jydy.pda.utils.SoundManager;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import butterknife.ButterKnife;

/**
 * 项目基类
 * Created by 23923 on 2017/4/12.
 * 调用M80扫描仪，定义showTM()方法处理条码
 */

public abstract class BaseActivity extends Activity implements  BarcodeManager.OnEngineStatus{
    //调用扫描仪
    private final int SCANKEY_LEFT = 301;

    private final int SCANKEY_RIGHT = 300;

    private final int SCANTIMEOUT = 3000;

    long mScanAccount = 0;

    private boolean mbKeyDown = true;

    private Handler mDoDecodeHandler;

    private BarcodeManager mBarcodeManager = null;

    private DoDecodeThread mDoDecodeThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 取消标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getContentLayout() != 0) {
            setContentView(getContentLayout());
        }
        //初始化ButterKnife，必须放在setcontentview之后使用
        ButterKnife.bind(this);
        //初始化扫描仪
        mDoDecodeThread = new DoDecodeThread();
        mDoDecodeThread.start();
        //初始化声音
        SoundManager.getInstance();
        SoundManager.initSounds(getBaseContext());
        SoundManager.loadSounds();
        //初始化
        initView();
        initAction();
        initData();
    }
    /**
     * 设置布局文件
     */
    protected abstract int getContentLayout();

    /**
     * 初始化UI
     */
    protected abstract void initView();
    /**
     * 初始化事件
     */
    protected abstract void initAction();
    /**
     * 初始化数据
     */
    protected abstract void initData();
    /**
     * 让子类处理扫描得到的条码
     */
    protected abstract void showTM(String tmStr);

    //以下是调用pda相关代码
    class DoDecodeThread extends Thread {
        public void run() {
            Looper.prepare();

            mDoDecodeHandler = new Handler();

            Looper.loop();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_ENTER:
            case SCANKEY_LEFT:
            case SCANKEY_RIGHT:
            case KeyEvent.KEYCODE_SLASH: // hal key
                try {
                    if (mbKeyDown) {
                        DoScan();
                        mbKeyDown = false;
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return true;
            case KeyEvent.KEYCODE_BACK:
                this.finish();
                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_ENTER:
            case SCANKEY_LEFT:
            case SCANKEY_RIGHT:
            case KeyEvent.KEYCODE_SLASH: // hal key
                try {
                    mbKeyDown = true;
                    cancleScan();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return true;
            case KeyEvent.KEYCODE_BACK:
                this.finish();
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    private void DoScan() throws Exception {
        doScanInBackground();
    }

    private void cancleScan() throws Exception {
        if (mBarcodeManager != null) {
            mBarcodeManager.exitScan();
        }
    }

    private void doScanInBackground() {
        mDoDecodeHandler.post(new Runnable() {

            @Override
            public void run() {
                if (mBarcodeManager != null) {
                    // TODO Auto-generated method stub
                    try {
                        synchronized (mBarcodeManager) {
                            mBarcodeManager.executeScan(SCANTIMEOUT);
                        }

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (mBarcodeManager == null) {
                // initialize decodemanager
                mBarcodeManager = new BarcodeManager(this, this);
            }
            SoundManager.getInstance();
            SoundManager.initSounds(getBaseContext());
            SoundManager.loadSounds();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(BaseActivity.this, "此设备不具备M80扫描仪!", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mBarcodeManager != null) {
            try {
                mBarcodeManager.release();
                mBarcodeManager = null;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBarcodeManager != null) {
            try {

                mBarcodeManager.release();
                mBarcodeManager = null;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onEngineReady() {
        ScanResultHandler.sendEmptyMessage(com.imscs.barcodemanager.Constants.DecoderReturnCode.RESULT_DECODER_READY);

    }

    @Override
    public int scanResult(boolean arg0, BarcodeManager.ScanResult arg1) {
        // TODO Auto-generated method stub
        Message m = new Message();
        m.obj = arg1;
        if (arg0) {
            // docode successfully
            m.what = com.imscs.barcodemanager.Constants.DecoderReturnCode.RESULT_SCAN_SUCCESS;
        } else {
            m.what = com.imscs.barcodemanager.Constants.DecoderReturnCode.RESULT_SCAN_FAIL;

        }
        ScanResultHandler.sendMessage(m);
        return 0;

    }

    private Handler ScanResultHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case com.imscs.barcodemanager.Constants.DecoderReturnCode.RESULT_SCAN_SUCCESS:
                    mScanAccount++;
                    String strDecodeResult = "";
                    BarcodeManager.ScanResult decodeResult = (BarcodeManager.ScanResult) msg.obj;
                    SoundManager.playSound(1, 1);
                    int codeid = decodeResult.codeid;
                    int aimid = decodeResult.aimid;
                    int iLength = decodeResult.len;
                    try {
                        byte[] bytereuslt = null;
                        if (mBarcodeManager != null) {
                            bytereuslt = mBarcodeManager.getScanResultData();
                        }

                        if (bytereuslt != null) {
                            try {
                                strDecodeResult = new String(bytereuslt, "SJIS");
                                Logs.d(strDecodeResult);
                                showTM(strDecodeResult);
                            }
                            catch (UnsupportedEncodingException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
//                    strDecodeResult = decodeResult.result;
//                    try{
//                        byte[] a = strDecodeResult.getBytes("UTF-8");
//                        String b = new String(a,"SJIS");
//                        Logs.d(b);
//                        showTM(b);
//                    }catch(Exception e){
//
//                    }
                    break;

                case com.imscs.barcodemanager.Constants.DecoderReturnCode.RESULT_SCAN_FAIL: {
                    SoundManager.playSound(2, 1);
                    Toast.makeText(BaseActivity.this,"扫描失败！",Toast.LENGTH_SHORT).show();
                }
                break;
                case com.imscs.barcodemanager.Constants.DecoderReturnCode.RESULT_DECODER_READY: {
                    // Enable all sysbology if needed
                    // try {
                    // mDecodeManager.enableSymbology(SymbologyID.SYM_ALL);   //enable all Sym
                    // } catch (RemoteException e) {
                    // // TODO Auto-generated catch block
                    // e.printStackTrace();
                    // }
                }
                break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };
}
