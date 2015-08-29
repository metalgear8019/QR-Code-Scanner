package com.citu.qrcodescanner.model;

import java.util.Date;

/**
 * Created by metalgear8019 on 8/21/2015.
 */
public class Entry {

    private int _id;
    private String data;
    private long dateScanned;

    public Entry() {}

    public Entry(int id, String data)
    {
        this._id = id;
        this.data = data;
    }

    public Entry(String data)
    {
        this.data = data;
    }

    public String getData()
    {
        return this.data;
    }

    public int getID()
    {
        return this._id;
    }

    public void setData(String data)
    {
        this.data = data;
    }

    public void setID(int id)
    {
        this._id = id;
    }

    public void setDate (long date) {
        this.dateScanned = date;
    }

    public long getDate() {
        return this.dateScanned;
    }
}
