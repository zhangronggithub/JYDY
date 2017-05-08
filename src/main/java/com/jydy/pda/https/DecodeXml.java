package com.jydy.pda.https;

import android.content.ContentValues;
import android.util.Xml;

import com.jydy.pda.utils.Logs;

import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.view.View.Y;


public class DecodeXml {

    String  TAG = getClass().getSimpleName();

    /**
     * 用来解析没有重复标签的xml字符串
     * @param content 待解析的xml字符串
     * @param key 标签
     * @return 标签的内容
     * @throws Exception
     */
    public static String decodeXml(String content,String key) throws Exception {

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
                             break;
                 // 判断当前事件是否为标签元素开始事件
                         case XmlPullParser.START_TAG:  
                             if (parser.getName().equals(key)) { // 判断开始标签元素是否是key
                            	 eventType = parser.next();//让解析器指向key属性的值
                            	 return parser.getText();
                             }
                             break;

                 // 判断当前事件是否为标签元素结束事件
                         case XmlPullParser.END_TAG:  
                             break;  
                         }
            // 进入下一个元素并触发相应事件
                         eventType = parser.next();
                     }
        return null;
        }

    /**
     * 解析有循环标签的xml字符串
     * @param content 待解析的xml字符串
     * @return 返回有循环标签对象的集合
     */
//    public ArrayList<ZTDetail> decodeXmlZT(String content) throws Exception {
//        ArrayList<ZTDetail> mList  = null;
//        ZTDetail detail = null;
//        // 由android.util.Xml创建一个XmlPullParser实例
//        XmlPullParser parser = Xml.newPullParser();
//        // 设置输入流 并指明编码方式
//        parser.setInput(new StringReader(content));
//        // 产生第一个事件
//        int eventType = parser.getEventType();
//
//        while (eventType != XmlPullParser.END_DOCUMENT){
//            switch (eventType) {
//                // 判断当前事件是否为文档开始事件
//                case XmlPullParser.START_DOCUMENT:
//                    mList = new ArrayList<ZTDetail>();
//                    break;
//                // 判断当前事件是否为标签元素开始事件
//                case XmlPullParser.START_TAG:
//                    if (parser.getName().equals("Type")) {
//                        eventType = parser.next();
//                        Type = parser.getText();
//                    }else if (parser.getName().equals("Message")) {
//                        eventType = parser.next();
//                        Message = parser.getText();
//                    }
//                    else if (parser.getName().equals("item")) {
//                        detail = new ZTDetail();
//                    } else if (parser.getName().equals("Bukrs")) {
//                        eventType = parser.next();
//                        detail.setMB001(parser.getText());
//                    } else if (parser.getName().equals("Butxt")) {
//                        eventType = parser.next();
//                        detail.setMB002(parser.getText());
//                    }
//                    break;
//
//                // 判断当前事件是否为标签元素结束事件
//                case XmlPullParser.END_TAG:
//                    if (parser.getName().equals("item")) {
//                        mList.add(detail);
//                        detail = null;
//                    }
//                    break;
//            }
//            // 进入下一个元素并触发相应事件
//            eventType = parser.next();
//        }
//        return mList;
//
//    }
    /**
     * 待测试
     * 解析dataset数据
     * @param str 待解析数据
     * @param flag 分割的标识
     * @return
     */
//    D/ContentValues: anyType{schema=anyType{element=anyType{complexType=anyType{choice=anyType{element=anyType{complexType=anyType{sequence=anyType{element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; }; }; }; }; }; }; };
// diffgram=anyType{NewDataSet=anyType{TAB_ZYLX=anyType{ZYLX001=50001; ZYLX002=作业类型01; ZYLX003=Y; ZYLX004=anyType{}; }; }; }; }
//    TAB_ZYLX=anyType{ZYLX001=50001; ZYLX002=作业类型01; ZYLX003=Y; ZYLX004=anyType{};
//    ZYLX001=50001; ZYLX002=作业类型01; ZYLX003=Y; ZYLX004=anyType{};
    //4
    public static ArrayList<Map<String, Object>> decodeDataset(String str, String flag){

        Map<String, Object> mapOne = new HashMap<String, Object>();

        Pattern pattern=Pattern.compile(flag+"=anyType\\{[^}]*\\};");

        Matcher m = pattern.matcher(str);//import java.util.regex.Matcher;
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
        while (m.find())
        {
            Logs.d("DecodeDataset",m.group());
            String tmp=m.group().replace(flag+"=anyType{","");
            Logs.d("DecodeDataset",tmp);
            String[] Strlen=tmp.split(";");

            if(Strlen.length>0)
            {
                Logs.d("DecodeDataset",Strlen.length);
                mapOne = new HashMap<String, Object>(); //只要新建1个new map，就能预防值被覆盖的情况
                for (int i = 0; i < Strlen.length; i++) {
                    if (Strlen[i].split("=").length>1) {
                        String key=Strlen[i].split("=")[0].trim();
                        String value=Strlen[i].split("=")[1].trim();
                        Logs.d("DecodeDataset", i);
                        Logs.d("DecodeDataset", key);
                        Logs.d("DecodeDataset", value);
                        mapOne.put(key,value);
                    }
                }

                list.add(mapOne);
            }
        }
        return list;
    }
}
