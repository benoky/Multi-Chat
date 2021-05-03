package com.example.multiplechat.message;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.multiplechat.R;

import java.util.ArrayList;

public class MessageAdapter extends BaseAdapter {
    ArrayList<Message> messages;
    LayoutInflater inflater;
    String userNickname;

    public MessageAdapter(ArrayList<Message> ms, LayoutInflater li, String nick){
        messages = ms;
        inflater = li;
        userNickname = nick;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int i) {
        return messages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Message message = messages.get(i);
        View itemView;
        if(message.getEmail().equals(userNickname)){   //닉네임 확인을 통한 자신 또는 타인의 메세지 출력 방법 결정
            itemView = inflater.inflate(R.layout.my_message, viewGroup, false);

            TextView tvNick = itemView.findViewById(R.id.tv_email1);
            TextView tvDate = itemView.findViewById(R.id.tv_date1);
            TextView tvTime = itemView.findViewById(R.id.tv_time1);
            TextView tvMsg = itemView.findViewById(R.id.tv_msgbox);
            tvNick.setText(message.getEmail());
            tvDate.setText(message.getDate());
            tvTime.setText(message.getTime());
            tvMsg.setText(message.getMsg());
        }else{
            itemView = inflater.inflate(R.layout.others_message, viewGroup, false);

            TextView tvNick = itemView.findViewById(R.id.tv_email2);
            TextView tvDate = itemView.findViewById(R.id.tv_date2);
            TextView tvTime = itemView.findViewById(R.id.tv_time2);
            TextView tvMsg = itemView.findViewById(R.id.tv_msgbox2);
            tvNick.setText(message.getEmail());
            tvDate.setText(message.getDate());
            tvTime.setText(message.getTime());
            tvMsg.setText(message.getMsg());
        }
        return itemView;
    }
}
