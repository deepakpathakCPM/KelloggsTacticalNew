package com.cpm.kelloggstacticalnew.getterSetter;

import java.util.ArrayList;

/**
 * Created by deepakp on 11-01-2018.
 */

public class NonDeploymentReasonGetterSetter {
    public String getTable_NON_DEPLOYMENT_REASON() {
        return table_NON_DEPLOYMENT_REASON;
    }

    public void setTable_NON_DEPLOYMENT_REASON(String table_NON_DEPLOYMENT_REASON) {
        this.table_NON_DEPLOYMENT_REASON = table_NON_DEPLOYMENT_REASON;
    }

    public ArrayList<String> getNDREASON_CD() {
        return NDREASON_CD;
    }

    public void setNDREASON_CD(String NDREASON_CD) {
        this.NDREASON_CD.add(NDREASON_CD);
    }

    public ArrayList<String> getNDREASON() {
        return NDREASON;
    }

    public void setNDREASON(String NDREASON) {
        this.NDREASON.add(NDREASON);
    }

    String table_NON_DEPLOYMENT_REASON;
    ArrayList<String> NDREASON_CD = new ArrayList();
    ArrayList<String> NDREASON = new ArrayList();
}
