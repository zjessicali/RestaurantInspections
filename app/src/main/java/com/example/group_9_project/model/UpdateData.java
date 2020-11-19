package com.example.group_9_project.model;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

public class UpdateData {
    private String lastUpdated;
    private Boolean needUpdate;

    private static UpdateData instance;

    private UpdateData() {
        this.lastUpdated = "";
        this.needUpdate = null;
    }
    public static UpdateData getInstance(){
        if(instance == null){
            instance = new UpdateData();
        }
        return instance;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Boolean getNeedUpdate() {
        return needUpdate;
    }

    public void setNeedUpdate(Boolean needUpdate) {
        this.needUpdate = needUpdate;
    }


}
