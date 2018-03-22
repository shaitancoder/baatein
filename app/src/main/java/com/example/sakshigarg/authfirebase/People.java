package com.example.sakshigarg.authfirebase;

/**
 * Created by sakshi on 22-03-2017.
 */
public class People {

    private String name;
    private String message;
    private int im;
    private String date;

    public People(){}   ///IMPORTANT
    public People(String name, String message, int im, String date){
        this.name=name;
        this.message=message;
        this.im=im;
        this.date=date;
    }
    public String getName()
    {return name;}
    public String getMessage()
    {return message;}
    public int getIm()
    {return im;}
    public String getDate()
    {
     return date;
    }
}
//json file
//dependies