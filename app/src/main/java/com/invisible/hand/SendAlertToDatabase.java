package com.invisible.hand;

public class SendAlertToDatabase {
    private double longitude;
    private double latitude;
    private String userName;
    private String mobileNumber;
    private String helpType;
    public SendAlertToDatabase(String userName,String mobileNumber,String helpType,double longitude,double latitude){
        this.userName=userName;
        this.mobileNumber=mobileNumber;
        this.helpType=helpType;
        this.latitude=latitude;
        this.longitude=longitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getHelpType() {
        return helpType;
    }

    public void setHelpType(String helpType) {
        this.helpType = helpType;
    }
}
