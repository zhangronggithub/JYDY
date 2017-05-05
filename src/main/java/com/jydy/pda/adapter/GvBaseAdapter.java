package com.jydy.pda.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jydy.pda.R;

/**
 * Created by 23923 on 2017/2/7.
 */

public class GvBaseAdapter extends BaseAdapter {

    private int[] icon;

    private String[] iconName;

    public GvBaseAdapter(int[] icon,String[] iconName){
        this.icon = icon;
        this.iconName = iconName;
    }
    @Override
    public int getCount() {
        return iconName.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item, parent, false);
            viewHolder.tvIcon= (TextView) convertView.findViewById(R.id.text);
            viewHolder.ivIconName= (ImageView) convertView.findViewById(R.id.image);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvIcon.setText(iconName[position]);
        viewHolder.ivIconName.setImageDrawable(parent.getContext().getResources().getDrawable(icon[position]));

        return convertView;
    }
    private class ViewHolder{
        private TextView tvIcon;
        private ImageView ivIconName;
    }

}
