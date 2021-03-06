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

    AgArrayAdapter(Activity context, List<MessageAgree> values) {
        super(context, R.layout.row_mess, values);
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
            rowView = inflater.inflate(R.layout.row_mess, null, true);
            holder = new ViewHolder();
            holder.textView = (TextView) rowView.findViewById(R.id.textView);
            holder.imageView = (ImageView) rowView.findViewById(R.id.icon);
            rowView.setTag(holder);
        }else {
            holder = (ViewHolder) rowView.getTag();
        }
        holder.textView.setText(values.get(position).getSubject());
        holder.imageView.setImageResource(R.drawable.ic_stop);

        if (values.get(position).getAgrStat().equals("+")){
            holder.imageView.setImageResource(R.drawable.ic_ok);
        }else if (values.get(position).getAgrStat().equals("*")){
            holder.imageView.setImageResource(R.drawable.ic_wait);
        }else if (values.get(position).getAgrStat().equals("/")){
            holder.imageView.setImageResource(R.drawable.ic_ab);
        }else {
            holder.imageView.setImageResource(R.drawable.ic_stop);
        }

        return rowView;
    }

    public void onClickListViev(View view){

    }


}
