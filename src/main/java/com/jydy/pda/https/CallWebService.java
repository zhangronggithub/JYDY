package com.jydy.pda.https;

import android.util.Log;


import com.jydy.pda.utils.Logs;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class CallWebService {
	public static String CallWebService(String MethodName,String Namespace, Map<String, String> Params,String str) {
        // 1、指定webservice的命名空间和调用的方法名
        
        SoapObject request = new SoapObject(Namespace, MethodName);
        // 2、设置调用方法的参数值，如果没有参数，可以省略，
        if (Params != null) {
            Iterator iter = Params.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                request.addProperty((String) entry.getKey(),
                        (String) entry.getValue());
            }
        }
        // 3、生成调用Webservice方法的SOAP请求信息。该信息由SoapSerializationEnvelope对象描述
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER12);
        envelope.bodyOut = request;
        // c#写的应用程序必须加上这句
        envelope.dotNet = true;
        HttpTransportSE ht = new HttpTransportSE(str);
        // 使用call方法调用WebService方法
        try {
            ht.call(null, envelope);
        } catch (HttpResponseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        try {
//            final SoapPrimitive object = (SoapPrimitive) envelope.getResponse();
        	final Object object = (Object) envelope.getResponse();
            if (object != null) {
                Logs.d("----返回的数据----", object.toString());
                return object.toString();
            }

        } catch (SoapFault e) {
            Log.e("----返回的数据---", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

}


