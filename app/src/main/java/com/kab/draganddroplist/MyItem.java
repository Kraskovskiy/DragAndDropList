package com.kab.draganddroplist;

/**
 * Created by Kraskovskiy on 12.07.2016.
 */
public class MyItem {
    private long id;
    private String text;
    private String date;
    private String next;
    private String prev;

    public MyItem(long id, String text) {
        this.id = id;
        this.text = text;
        this.date = Utils.getCurrentDate();
        this.next=String.valueOf(id+1);
        this.prev=String.valueOf(id-1);
    }

    public MyItem(long id, String text, String date, String next, String prev) {
        this.id = id;
        this.text = text;
        this.date = date;
        this.next = String.valueOf(next);
        this.prev = String.valueOf(prev);
    }

    public MyItem(long id, String text, long next, long prev) {
        this.id = id;
        this.text = text;
        this.date = Utils.getCurrentDate();
        this.next = String.valueOf(next);
        this.prev = String.valueOf(prev);
    }

    public long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getDate() {
        return date;
    }

    public String getNext() {
        return next;
    }

    public String getPrev() {
        return prev;
    }

    @Override
    public String toString() {
        return "MyItem{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", date='" + date + '\'' +
                ", next='" + next + '\'' +
                ", prev='" + prev + '\'' +
                '}';
    }
}