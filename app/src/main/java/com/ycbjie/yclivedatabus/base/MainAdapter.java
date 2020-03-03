package com.ycbjie.yclivedatabus.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ycbjie.yclivedatabus.R;

import java.util.List;


public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyViewHolder> {

    private final List<String> list;
    private final Context context;
    public MainAdapter(List<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.item_main, parent, false);
        MyViewHolder holder = new MyViewHolder(inflate);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        if(list!=null && list.size()>0){
            holder.tv_item.setText(list.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return list==null ? 0 : list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder  {

        private TextView tv_item;

        public MyViewHolder(View view) {
            super(view);
            tv_item = view.findViewById(R.id.tv_item);
        }
    }

}
