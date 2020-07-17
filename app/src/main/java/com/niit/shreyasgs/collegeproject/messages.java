package com.niit.shreyasgs.collegeproject;

public class messages {

    public String text;
    public String from;
    public String to;
    public long num;

    public messages(){

    }

    public messages(String text, String from, String to, long num) {
        this.text = text;
        this.from = from;
        this.to = to;
        this.num = num;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public long getNum() {
        return num;
    }

    public void setNum(long num) {
        this.num = num;
    }
}
