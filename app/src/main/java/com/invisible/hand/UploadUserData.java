package com.invisible.hand;

public class UploadUserData {
    private String userName;
    private String userEmail;
    private String userMobileNumber;

    public UploadUserData(){

    }

    public UploadUserData(String userName,String userEmail,String userMobileNumber){
        this.userName=userName;
        this.userEmail=userEmail;
        this.userMobileNumber=userMobileNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserMobileNumber() {
        return userMobileNumber;
    }

    public void setUserMobileNumber(String userMobileNumber) {
        this.userMobileNumber = userMobileNumber;
    }
}
