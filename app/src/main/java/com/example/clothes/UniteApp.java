package com.example.clothes;

import android.app.Application;

import org.xutils.x;

public class UniteApp extends Application {

    @Override
    public void onCreate(){
        super.onCreate();
        x.Ext.init(this);

    }
}
