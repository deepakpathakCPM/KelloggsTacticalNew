package com.cpm.kelloggstacticalnew.bean;

/**
 * Created by deepakp on 05-01-2018.
 */

public class TableBean {

    public static String getTable_DistributerList() {
        return table_DistributerList;
    }

    public static void setTable_DistributerList(String table_DistributerList) {
        TableBean.table_DistributerList = table_DistributerList;
    }

    public static String getTable_SearchList() {
        return table_SearchList;
    }

    public static void setTable_SearchList(String table_SearchList) {
        TableBean.table_SearchList = table_SearchList;
    }

    static String table_DistributerList;
    static String table_SearchList;
    static String table_NON_WORKING_REASON;

    public static String getTable_NON_DEPLOYMENT_REASON() {
        return table_NON_DEPLOYMENT_REASON;
    }

    public static void setTable_NON_DEPLOYMENT_REASON(String table_NON_DEPLOYMENT_REASON) {
        TableBean.table_NON_DEPLOYMENT_REASON = table_NON_DEPLOYMENT_REASON;
    }

    static String table_NON_DEPLOYMENT_REASON;

    public static String getTable_NON_WORKING_REASON() {
        return table_NON_WORKING_REASON;
    }

    public static void setTable_NON_WORKING_REASON(String table_NON_WORKING_REASON) {
        TableBean.table_NON_WORKING_REASON = table_NON_WORKING_REASON;
    }
}
