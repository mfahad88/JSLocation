package com.example.muhammadfahad.jslocation.bean;

public class InfoBean {
    private String mobileNo;
    private String cnicNo;
    private String channelId;

    public InfoBean(String mobileNo, String cnicNo, String channelId) {
        this.mobileNo = mobileNo;
        this.cnicNo = cnicNo;
        this.channelId = channelId;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getCnicNo() {
        return cnicNo;
    }

    public void setCnicNo(String cnicNo) {
        this.cnicNo = cnicNo;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    @Override
    public String toString() {
        return "InfoBean{" +
                "channelId='" + channelId + '\'' +
                ", cnicNo='" + cnicNo + '\'' +
                ", mobileNo='" + mobileNo + '\'' +
                '}';
    }
}