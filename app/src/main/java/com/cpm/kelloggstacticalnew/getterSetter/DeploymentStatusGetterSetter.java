package com.cpm.kelloggstacticalnew.getterSetter;

/**
 * Created by deepakp on 05-01-2018.
 */

public class DeploymentStatusGetterSetter {

    String storeId;
    String username;
    int billCutid;
    String receiptImg;
    int hangerId;
    String hangerImg;
    int reasonCd;

    public int getReasonCd() {
        return reasonCd;
    }

    public void setReasonCd(int reasonCd) {
        this.reasonCd = reasonCd;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getBillCutid() {
        return billCutid;
    }

    public void setBillCutid(int billCutid) {
        this.billCutid = billCutid;
    }

    public String getReceiptImg() {
        return receiptImg;
    }

    public void setReceiptImg(String receiptImg) {
        this.receiptImg = receiptImg;
    }

    public int getHangerId() {
        return hangerId;
    }

    public void setHangerId(int hangerId) {
        this.hangerId = hangerId;
    }

    public String getHangerImg() {
        return hangerImg;
    }

    public void setHangerImg(String hangerImg) {
        this.hangerImg = hangerImg;
    }
}
