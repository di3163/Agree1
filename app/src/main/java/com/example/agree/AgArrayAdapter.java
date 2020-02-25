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

public class AgArrayAdapter extends ArrayAdapter<MessageAgree> {

    private final Activity context;
    private final List<MessageAgree> values;

    public AgArrayAdapter(Activity context, List<MessageAgree> values) {
        super(context, R.layout.row_mess, values);
        this.context = context;
        this.values = values;
    }

    static class ViewHolder{
        public ImageView imageView;
        public TextView textView;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder holder;

        View rowView = convertView;

        if (rowView == null){
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.row_mess, null, true);
            holder = new ViewHolder();
            holder.textView = (TextView) rowView.findViewById(R.id.textView);
            holder.imageView = (ImageView) rowView.findViewById(R.id.icon);
            rowView.setTag(holder);
        }else {
            holder = (ViewHolder) rowView.getTag();
        }
        holder.textView.setText(values.get(position).subject);
        holder.imageView.setImageResource(R.drawable.ic_stop);
        // Изменение иконки
//        String s = values.get(position);
//        if (s.startsWith("Windows7") || s.startsWith("iPhone")
//                || s.startsWith("Solaris")) {
//
//            holder.imageView.setImageResource(R.drawable.no);
//        } else {
//            holder.imageView.setImageResource(R.drawable.ok);
//        }

        return rowView;
    }
}
