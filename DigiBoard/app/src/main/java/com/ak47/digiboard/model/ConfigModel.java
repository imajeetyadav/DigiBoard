package com.ak47.digiboard.model;

public class ConfigModel {
    String message;
    Boolean service;

    public ConfigModel(String message, Boolean service) {
        this.message = message;
        this.service = service;
    }

    public ConfigModel() {
    }

    public String getMessage() {
        return message;
    }

    public Boolean getService() {
        return service;
    }
}
