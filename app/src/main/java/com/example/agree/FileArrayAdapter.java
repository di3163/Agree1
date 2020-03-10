package com.example.agree;

import android.app.Activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class FileArrayAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final List<String> values;

    public FileArrayAdapter(@NonNull Activity context, List<String> values) {
        super(context, R.layout.row_service_file, values);
        this.context = context;
        this.values = values;
    }

    static class ViewHolder{
        ImageView imageView;
        TextView textView;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder holder;
        View rowView = convertView;

        if (rowView == null){
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.row_service_file, null, true);
            holder = new FileArrayAdapter.ViewHolder();
            holder.textView = (TextView) rowView.findViewById(R.id.textFile);
            holder.imageView = (ImageView) rowView.findViewById(R.id.iconF);
            rowView.setTag(holder);
        }else {
            holder = (FileArrayAdapter.ViewHolder) rowView.getTag();
        }

        holder.textView.setText(values.get(position));
        holder.imageView.setImageResource(R.drawable.ic_unknown);
        if (values.get(position).contains("log.dat")){
            holder.imageView.setImageResource(R.drawable.ic_log);
        }else if (values.get(position).contains(".pdf"))
            holder.imageView.setImageResource(R.drawable.ic_pdf);
        else {
            holder.imageView.setImageResource(R.drawable.ic_unknown);
        }

        return rowView;
    }
}
