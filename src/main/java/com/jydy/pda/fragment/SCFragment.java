package com.jydy.pda.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jydy.pda.R;
import com.jydy.pda.main.FormScanActivity;
import com.jydy.pda.ui.sc.FZKSActivity;
import com.jydy.pda.ui.sc.JDCXActivity;
import com.jydy.pda.ui.sc.JXKSActivity;
import com.jydy.pda.ui.sc.YDZKSActivity;
import com.jydy.pda.ui.sc.scrk.SCRKActivity;
import com.jydy.pda.adapter.GvBaseAdapter;
import com.jydy.pda.view.CustomDialog;
import com.jydy.pda.view.LineGridView;

/**
 * Created by 23923 on 2016/11/3.
 */

public class SCFragment extends Fragment  implements AdapterView.OnItemClickListener{

    public final String TAG = getClass().getSimpleName();

    private LineGridView gv;

    private BaseAdapter gvBaseAdapter;

    private TextView tvTitle;

    private CustomDialog builder;

    // 图片封装为一个数组
    private int[] icon = { R.drawable.caigou, R.drawable.caigou, R.drawable.caigou, R.drawable.caigou, R.drawable.caigou};
    private String[] iconName = { "生产工艺", "生产入库" ,"进度查询","接线完工","工票结束"};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_kf, null);
        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvTitle.setText("生产管理");
        gv = (LineGridView) view.findViewById(R.id.gv);
        gvBaseAdapter = new GvBaseAdapter(icon,iconName);
        gv.setAdapter(gvBaseAdapter);
        gv.setOnItemClickListener(this);
        return  view;
    }

    public void ShowDialog(String str){

        builder = new CustomDialog(getActivity(),
                R.style.customdialog);
        if (str=="0"){
            builder.settv1("开始生产", new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    builder.dismiss();
                    Intent i = new Intent(getActivity(),FormScanActivity.class);
                    i.putExtra("type", "00");
                    startActivity(i);

                }
            });
            builder.settv4("结束生产", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder.dismiss();
                    Intent i = new Intent(getActivity(),FormScanActivity.class);
                    i.putExtra("type", "01");
                    startActivity(i);
                }
            });
           }else if (str=="1"){
            builder.settv1("开始生产", new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    builder.dismiss();
                    Intent i = new Intent(getActivity(),FormScanActivity.class);
                    i.putExtra("type", "02");
                    startActivity(i);

                }
            });
            builder.settv4("结束生产", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder.dismiss();
                    Intent i = new Intent(getActivity(),FormScanActivity.class);
                    i.putExtra("type", "03");
                    startActivity(i);
                }
            });
        }

        builder.setCancelable(true);
        builder.show();

    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case 0:
                Intent i = new Intent(getActivity(),FormScanActivity.class);
                i.putExtra("type", "0");
                startActivity(i);
                break;
            case 1:
                Intent i1 = new Intent(getActivity(), SCRKActivity.class);
                startActivity(i1);
//                ShowDialog("1");
                break;
            case 2:
                Intent i2= new Intent(getActivity(),  JDCXActivity.class);
                startActivity(i2);
                break;
            case 3:
                Intent i3 = new Intent(getActivity(),FormScanActivity.class);
                i3.putExtra("type", "3");
                startActivity(i3);
                break;
            case 4:
                Intent i4 = new Intent(getActivity(),FormScanActivity.class);
                i4.putExtra("type", "4");
                startActivity(i4);
                break;
            default:
                break;
        }
    }


}
