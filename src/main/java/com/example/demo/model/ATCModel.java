package com.example.demo.model;


public class ATCModel {

    private String id;
    private String status__c;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus__c() {
        return status__c;
    }

    public void setStatus__c(String status__c) {
        this.status__c = status__c;
    }

    @Override
    public String toString() {
        return "ATCModel{" +
                "id='" + id + '\'' +
                ", status__c='" + status__c + '\'' +
                '}';
    }
}
