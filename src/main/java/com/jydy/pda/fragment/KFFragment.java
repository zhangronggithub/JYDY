package com.jydy.pda.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.jydy.pda.R;
import com.jydy.pda.adapter.GvBaseAdapter;
import com.jydy.pda.view.LineGridView;

/**
 * Created by 23923 on 2016/11/3.
 */

public class KFFragment extends Fragment  {

    public final String TAG = getClass().getSimpleName();

    private LineGridView gv;

    private BaseAdapter gvBaseAdapter;

    // 图片封装为一个数组
    private int[] icon = { R.drawable.caigou, R.drawable.caigou,
            R.drawable.caigou, R.drawable.caigou, R.drawable.caigou,
            R.drawable.caigou,  R.drawable.caigou, R.drawable.caigou,
            R.drawable.caigou,R.drawable.caigou,R.drawable.caigou,R.drawable.caigou };
    private String[] iconName = { "采购", "领料", "销售", "调拨", "库存交易", "不合格品", "库存查询",
            "库存盘点", "库位转移", "修改密码","纸箱拆分","托盘拆分" };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_kf, null);
        gv = (LineGridView) view.findViewById(R.id.gv);
        gvBaseAdapter = new GvBaseAdapter(icon,iconName);
        gv.setAdapter(gvBaseAdapter);
        return  view;
    }



    }
