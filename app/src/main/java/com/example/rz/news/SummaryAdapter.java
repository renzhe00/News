package com.example.rz.news;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

public class SummaryAdapter extends ArrayAdapter<Summary> {

    private int resourceId;

    public SummaryAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Summary> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Summary summary = getItem(position);
        ViewHolder viewHolder;
        if (convertView != null) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        Glide.with(getContext()).load(summary.getImageUrl()).into(viewHolder.newsPhoto);
        viewHolder.newsViews.get(0).setText(summary.getTitle());
        viewHolder.newsViews.get(1).setText(summary.getDescription());
        viewHolder.newsViews.get(2).setText(summary.getTime());

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.news_photo)
        ImageView newsPhoto;
        @BindViews({R.id.news_title, R.id.news_description, R.id.news_time})
        List<TextView> newsViews;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
