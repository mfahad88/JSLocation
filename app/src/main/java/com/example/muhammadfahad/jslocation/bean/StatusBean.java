package com.example.muhammadfahad.jslocation.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StatusBean {

@SerializedName("status")
@Expose
private String status;

public String getStatus() {
return status;
}

public void setStatus(String status) {
this.status = status;
}

}