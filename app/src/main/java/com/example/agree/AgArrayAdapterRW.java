package com.example.agree;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AgArrayAdapterRW extends RecyclerView.Adapter<AgArrayAdapterRW.AgViewHolder>{

    private int numberItem;
    private final List<MessageAgree> values;
    Context context;

    public AgArrayAdapterRW(List<MessageAgree> values){
        this.values = values;
        this.numberItem = values.size();
    }

    @NonNull
    @Override
    public AgViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        int layoutIdRowMessRw = R.layout.row_mess_rw;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdRowMessRw, parent, false);
        AgViewHolder viewHolder = new AgViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AgViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return numberItem;
    }

    class AgViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView textViewRW;
        ImageView iconRW;
        public AgViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewRW =  itemView.findViewById(R.id.textViewRw);
            iconRW =  itemView.findViewById(R.id.iconRw);
            textViewRW.setOnClickListener(this);
            iconRW.setOnClickListener(this);
        }

        void bind(int listIndex){
            StringBuilder strMess = new StringBuilder();
            if (MainActivity.isShowOldMess() && !ServiceTasks.removeTime(values.get(listIndex).getDateAgr()).equals(ServiceTasks.removeTime(new Date()))) {
                SimpleDateFormat formatD = new SimpleDateFormat("dd.MM.yyyy");
                strMess.append(formatD.format(values.get(listIndex).getDateAgr()));
                strMess.append(" - ");
            }
            strMess.append(values.get(listIndex).getSubject());
            textViewRW.setText(strMess.toString());
            iconRW.setImageResource(R.drawable.ic_stop);
            if (values.get(listIndex).getAgrStat().equals("+")) {
                iconRW.setImageResource(R.drawable.ic_ok);
            } else if (values.get(listIndex).getAgrStat().equals("*")) {
                iconRW.setImageResource(R.drawable.ic_wait);
            } else if (values.get(listIndex).getAgrStat().equals("/")) {
                iconRW.setImageResource(R.drawable.ic_ab);
            } else {
                iconRW.setImageResource(R.drawable.ic_stop);
            }

        }

        @Override
        public void onClick(View v){
            int pos = getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION){
                switch (v.getId()) {
                    case R.id.iconRw:
                        if(ServiceTasks.removeTime(values.get(pos).getDateAgr()).equals(ServiceTasks.removeTime(new Date()))) {
                            iconClick(pos, v);
                        }else {
                            ((MainActivity)itemView.getContext()).snackPopup("Это согласование неактуально");
                        }
                        break;
                    case R.id.textViewRw:
                        textClick(pos, v);
                }
            }
        }

        private void iconClick(final int pos, View v){
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            final View popupView = layoutInflater.inflate(R.layout.popup_choose_window, null);
            ImageView stop = popupView.findViewById(R.id.stop);
            ImageView ok =  popupView.findViewById(R.id.ok);
            ImageView wait = popupView.findViewById(R.id.wait);
            ImageView ab =  popupView.findViewById(R.id.ab);
            int width = LinearLayout.LayoutParams.WRAP_CONTENT;
            int height = LinearLayout.LayoutParams.WRAP_CONTENT;
            boolean focusable = true;
            final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
            int x = (int)v.getX();
            int y = (int)v.getY();
            popupWindow.showAsDropDown(v);
            stop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MessageAgree messageAgree = values.get(pos);
                    messageAgree.setAgrStat(messageAgree.getAgrNumInMail());
                    popupWindow.dismiss();
                    ((MainActivity)itemView.getContext()).displayAllMessages();
                }
            });
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setStatIcon(popupWindow, pos, "+");
                }
            });
            wait.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setStatIcon(popupWindow, pos, "*");
                }
            });
            ab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setStatIcon(popupWindow, pos, "/");
                }
            });
        }

        private void textClick(int pos, View v){
            MessageAgree messageAgree = values.get(pos);
            ((MainActivity)itemView.getContext()).openFileChoose(messageAgree);
        }

        private void setStatIcon(PopupWindow popupWindow, int pos, String s){
            MessageAgree messageAgree = values.get(pos);
            messageAgree.setAgrStat(s);
            popupWindow.dismiss();
            ((MainActivity)itemView.getContext()).displayAllMessages();
        }
    }
}
