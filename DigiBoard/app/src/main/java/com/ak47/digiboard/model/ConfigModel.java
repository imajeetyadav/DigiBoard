package com.ak47.digiboard.model;

public class ConfigModel {
    String message;
    Boolean service;
    int version;

    public ConfigModel(String message, Boolean service, int version) {
        this.message = message;
        this.service = service;
        this.version = version;
    }

    public ConfigModel() {
    }

    public int getVersion() {
        return version;
    }

    public String getMessage() {
        return message;
    }

    public Boolean getService() {
        return service;
    }
}
