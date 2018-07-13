package com.example.muhammadfahad.jslocation.bean;

/**
 * Created by Fahad on 01/03/2018.
 */

public class DataBean {
    private int catId;
    private int recId;
    private String attribute;
    private String value;
    private String recordDate;
    private String mobileIMEI;
    private InfoBean infoBean;

    public DataBean(int catId, int recId, String attribute, String value, String recordDate,String mobileIMEI, InfoBean infoBean) {
        this.catId = catId;
        this.recId = recId;
        this.attribute = attribute;
        this.value = value;
        this.recordDate = recordDate;
        this.mobileIMEI = mobileIMEI;
        this.infoBean = infoBean;

    }

    public int getCatId() {
        return catId;
    }

    public void setCatId(int catId) {
        this.catId = catId;
    }

    public int getRecId() {
        return recId;
    }

    public void setRecId(int recId) {
        this.recId = recId;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }

    public InfoBean getInfoBean() {
        return infoBean;
    }

    public void setInfoBean(InfoBean infoBean) {
        this.infoBean = infoBean;
    }

    public String getMobileIMEI() {
        return mobileIMEI;
    }

    public void setMobileIMEI(String mobileIMEI) {
        this.mobileIMEI = mobileIMEI;
    }

    @Override
    public String toString() {
        return "DataBean{" +
                "catId=" + catId +
                ", recId=" + recId +
                ", attribute='" + attribute + '\'' +
                ", value='" + value + '\'' +
                ", recordDate='" + recordDate + '\'' +
                ", mobileIMEI='" + mobileIMEI + '\'' +
                ", infoBean=" + infoBean +
                '}';
    }
}
