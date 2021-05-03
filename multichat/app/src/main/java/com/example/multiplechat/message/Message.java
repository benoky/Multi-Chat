package com.example.multiplechat.message;

public class Message {
    String email;
    String msg;
    String date;
    String time;

    public Message(String email, String msg, String date, String time){
        this.email = email;
        this.msg = msg;
        this.date = date;
        this.time = time;
    }

    public String getEmail(){
        return email;
    }
    public String getMsg(){
        return msg;
    }
    public String getDate(){
        return date;
    }
    public String getTime(){
        return time;
    }
}
