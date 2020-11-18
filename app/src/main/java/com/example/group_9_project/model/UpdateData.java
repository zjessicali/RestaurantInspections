package com.example.group_9_project.model;

public class UpdateData {
    private String lastModified;
    private String lastUpdated;
    private Boolean needUpdate;
    private boolean wantUpdate;

    private static UpdateData instance;

    private UpdateData() {
        this.lastModified = "";
        this.lastUpdated = "";
        this.needUpdate = null;
        this.wantUpdate = false;
    }
    public static UpdateData getInstance(){
        if(instance == null){
            instance = new UpdateData();
        }
        return instance;
    }

    public boolean isWantUpdate() {
        return wantUpdate;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public void setWantUpdate(boolean bool) {
        this.wantUpdate = bool;
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
