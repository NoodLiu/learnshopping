package com.neuedu.vo;

import java.io.Serializable;

public class ShippingVo implements Serializable {
    private String receiverName;

    private String receiverPhone;

    private String receiverProvince;

    private String recriverCity;

    private String revricerDistrict;

    private String receiverAddress;

    private String receiverZip;

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getReceiverProvince() {
        return receiverProvince;
    }

    public void setReceiverProvince(String receiverProvince) {
        this.receiverProvince = receiverProvince;
    }

    public String getRecriverCity() {
        return recriverCity;
    }

    public void setRecriverCity(String recriverCity) {
        this.recriverCity = recriverCity;
    }

    public String getRevricerDistrict() {
        return revricerDistrict;
    }

    public void setRevricerDistrict(String revricerDistrict) {
        this.revricerDistrict = revricerDistrict;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getReceiverZip() {
        return receiverZip;
    }

    public void setReceiverZip(String receiverZip) {
        this.receiverZip = receiverZip;
    }
}
