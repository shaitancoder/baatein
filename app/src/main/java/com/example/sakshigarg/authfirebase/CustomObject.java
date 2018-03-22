package com.example.sakshigarg.authfirebase;

/**
 * Created by sakshi on 22-03-2017.
 */
public class CustomObject {

    private String name;
    private String msg;
    private int im;
    private String date;

    public CustomObject(String name, String msg, int im, String date)
    {
     this.name = name;
        this.msg=msg;
        this.im=im;
        this.date=date;
    }

    public String getName()
    {
        return name;
    }
    public String getMsg()
    {
        return msg;
    }
    public int getIm()
    {
        return im;
    }
    public String getDate(){return date;}
}
