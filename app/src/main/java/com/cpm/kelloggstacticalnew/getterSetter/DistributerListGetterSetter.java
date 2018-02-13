package com.cpm.kelloggstacticalnew.getterSetter;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by deepakp on 05-01-2018.
 */

public class DistributerListGetterSetter implements Serializable {

    public String getTable_DistributerList() {
        return table_DistributerList;
    }

    public void setTable_DistributerList(String table_DistributerList) {
        this.table_DistributerList = table_DistributerList;
    }

    public ArrayList<String> getKEYACCOUNT_CD() {
        return KEYACCOUNT_CD;
    }

    public void setKEYACCOUNT_CD(String KEYACCOUNT_CD) {
        this.KEYACCOUNT_CD.add(KEYACCOUNT_CD);
    }

    public ArrayList<String> getKEYACCOUNT() {
        return KEYACCOUNT;
    }

    public void setKEYACCOUNT(String KEYACCOUNT) {
        this.KEYACCOUNT.add(KEYACCOUNT);
    }

    public ArrayList<String> getROUTE_NAME() {
        return ROUTE_NAME;
    }

    public void setROUTE_NAME(String ROUTE_NAME) {
        this.ROUTE_NAME.add(ROUTE_NAME);
    }

    String table_DistributerList;
    ArrayList<String> KEYACCOUNT_CD = new ArrayList<>();
    ArrayList<String> KEYACCOUNT = new ArrayList<>();
    ArrayList<String> ROUTE_NAME = new ArrayList<>();


    public ArrayList<String> getSTORE_CD() {
        return STORE_CD;
    }

    public void setSTORE_CD(String STORE_CD) {
        this.STORE_CD.add(STORE_CD);
    }

    public ArrayList<String> getSTORENAME() {
        return STORENAME;
    }

    public void setSTORENAME(String STORENAME) {
        this.STORENAME.add(STORENAME);
    }

    public ArrayList<String> getADDRESS() {
        return ADDRESS;
    }

    public void setADDRESS(String ADDRESS) {
        this.ADDRESS.add(ADDRESS);
    }

    public ArrayList<String> getCITY() {
        return CITY;
    }

    public void setCITY(String CITY) {
        this.CITY.add(CITY);
    }

    public ArrayList<String> getSTORETYPE() {
        return STORETYPE;
    }

    public void setSTORETYPE(String STORETYPE) {
        this.STORETYPE.add(STORETYPE);
    }

    public ArrayList<String> getVISIT_DATE() {
        return VISIT_DATE;
    }

    public void setVISIT_DATE(String VISIT_DATE) {
        this.VISIT_DATE.add(VISIT_DATE);
    }

    ArrayList<String> VISIT_DATE = new ArrayList<>();
    ArrayList<String> STORE_CD = new ArrayList<>();
    ArrayList<String> STORENAME = new ArrayList<>();
    ArrayList<String> ADDRESS = new ArrayList<>();
    ArrayList<String> CITY = new ArrayList<>();
    ArrayList<String> STORETYPE = new ArrayList<>();


}
