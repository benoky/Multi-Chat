package com.example.multiplechat.userList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.multiplechat.R;

import java.util.ArrayList;

public class UserListAdapter extends BaseAdapter {
    private ArrayList<UserList> userLists;
    private LayoutInflater inflater;

    public UserListAdapter(ArrayList<UserList> userLists, LayoutInflater inflater){
        this.userLists = userLists;
        this.inflater = inflater;
    }

    @Override
    public int getCount() {
        return userLists.size();
    }

    @Override
    public Object getItem(int i) {
        return userLists.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        UserList userList = userLists.get(i);
        View itemView;

        itemView = inflater.inflate(R.layout.userlist, viewGroup, false);

        TextView userNameTv = itemView.findViewById(R.id.tv_user);

        userNameTv.setText(userList.getUserName());

        return itemView;
    }
}
