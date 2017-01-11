package com.tryreminder.myandroid.newreminder;

/**
 * Created by Muzhou on 1/11/2017.
 */
public class Reminder {
    //*********************
    //实例变量和相关的get，set方法
    //*********************
    private int mId;
    private String mContent;
    private int mImportant;

    public Reminder(int important, String content, int id) {
        mImportant = important;
        mContent = content;
        mId = id;
    }

    public int getId() {
        return mId;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public String getContent() {
        return mContent;
    }

    public void setImportant(int important) {

        mImportant = important;
    }

    public int getImportant() {

        return mImportant;
    }

    public void setId(int id) {

        mId = id;
    }
}
