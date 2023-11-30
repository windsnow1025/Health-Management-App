package com.windsnow1025.health_management_app.utils;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.windsnow1025.health_management_app.R;
import com.windsnow1025.health_management_app.pojo.Alert;

import java.util.List;

public class AlertAdapter extends ArrayAdapter<Alert> {

    private List<Alert> alertList;
    //用于将上下文、listview 子项布局的 id 和数据都传递过来
    public AlertAdapter(@NonNull Context context, int resource, @NonNull List<Alert> list) {
        super(context, resource, list);
        alertList =list;
    }
    //增加一个方法添加动态数据
    public void add(Alert alert){
        alertList.add(alert);
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return alertList.size();
    }

    //每个子项被滚动到屏幕内的时候会被调用
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Alert alert = getItem(position);//得到当前项的 Alert 实例
        //为每一个子项加载设定的布局
        View view;
        if(convertView==null)
        {
            view = LayoutInflater.from(getContext()).inflate(R.layout.listview,parent,
                    false);
        }else {
            view = convertView;
        }

        //分别获取子布局textview 的实例
        TextView tv_title=view.findViewById(R.id.tv_title);
        TextView tv_time=view.findViewById(R.id.tv_time);
        TextView tv_date=view.findViewById(R.id.tv_date);
        // 设置要显示内容
        tv_title.setText(alert.getTitle());
//        tv_time.setText(alert.getTime());
//        tv_date.setText(alert.getDate());

        return view;
    }
}