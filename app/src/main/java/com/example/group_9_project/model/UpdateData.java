package com.example.group_9_project.model;

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
