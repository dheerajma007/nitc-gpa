package com.kkroo.dheeraj.nitcgpa;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Dheeraj on 28-12-2014.
 */
public class CustomList extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] name;
    private final Integer[] imgId;

    public CustomList(Context context, int resource) {
        super(context, resource);
        this.context = null;
        name = new String[0];
        imgId = new Integer[0];
    }

    public CustomList(Activity context, String name[], Integer imgId[])
    {
        super(context, R.layout.list_layout, name);

        this.context = context;
        this.name = name;
        this.imgId = imgId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_layout, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.textViewItem);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.imageViewItem);
        txtTitle.setText(name[position]);
        imageView.setImageResource(imgId[position]);
        return rowView;
    }
}
