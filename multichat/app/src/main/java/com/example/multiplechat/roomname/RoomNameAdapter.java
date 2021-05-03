package com.example.multiplechat.roomname;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.multiplechat.R;

import java.util.ArrayList;

public class RoomNameAdapter extends BaseAdapter {
    private ArrayList<RoomName> roomNames;
    private LayoutInflater inflater;

    public RoomNameAdapter(ArrayList<RoomName> roomNames, LayoutInflater inflater){
        this.roomNames = roomNames;
        this.inflater = inflater;
    }

    @Override
    public int getCount() {
        return roomNames.size();
    }

    @Override
    public Object getItem(int i) {
        return roomNames.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        RoomName roomName = roomNames.get(i); //roomNames에 저장된 순서대로 roomName에 불러와 저장한다.
        View itemView;

        itemView = inflater.inflate(R.layout.chatlist, viewGroup, false);

        TextView roomNameTv = itemView.findViewById(R.id.tv_roomname);

        roomNameTv.setText(roomName.getRoomName()); //roomNames로부터 불러온 대화방 이름을 화면에 표시힌다.

        return itemView;
    }
}
