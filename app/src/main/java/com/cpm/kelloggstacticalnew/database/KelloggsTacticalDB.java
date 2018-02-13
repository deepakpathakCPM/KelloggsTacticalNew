package com.cpm.kelloggstacticalnew.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.cpm.kelloggstacticalnew.bean.TableBean;
import com.cpm.kelloggstacticalnew.constant.CommonString;
import com.cpm.kelloggstacticalnew.getterSetter.AnswerGetterSetter;
import com.cpm.kelloggstacticalnew.getterSetter.CoverageBean;
import com.cpm.kelloggstacticalnew.getterSetter.DeploymentStatusGetterSetter;
import com.cpm.kelloggstacticalnew.getterSetter.DistributerListGetterSetter;
import com.cpm.kelloggstacticalnew.getterSetter.JCPMasterGetterSetter;
import com.cpm.kelloggstacticalnew.getterSetter.NonDeploymentReasonGetterSetter;
import com.cpm.kelloggstacticalnew.getterSetter.NonWorkingReasonGetterSetter;
import com.cpm.kelloggstacticalnew.getterSetter.SearchListGetterSetter;

import java.util.ArrayList;

/**
 * Created by deepakp on 1/4/2018.
 */

public class KelloggsTacticalDB extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "KelloggsTacticalDB1";
    public static final int DATABASE_VERSION = 1;
    private SQLiteDatabase db;
    Context context;

    public KelloggsTacticalDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public void open() {
        try {
            db = this.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CommonString.CREATE_TABLE_COVERAGE_DATA);
            db.execSQL(CommonString.CREATE_DEPLOYMENT_STATUS_DATA);
            db.execSQL(TableBean.getTable_DistributerList());
            db.execSQL(TableBean.getTable_NON_WORKING_REASON());
            db.execSQL(TableBean.getTable_NON_DEPLOYMENT_REASON());
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void createTable(String table) {
        try {
            db.execSQL(table);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    public void deleteAllTables() {
        // DELETING TABLES
        db.delete(CommonString.TABLE_COVERAGE_DATA, null, null);
        db.delete(CommonString.TABLE_DEPLOYMENT_STATUS, null, null);

    }

    public CoverageBean getCoverageStatus(int storecd) {
        CoverageBean sb = new CoverageBean();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT " + CommonString.KEY_COVERAGE_STATUS + " from " + CommonString.TABLE_COVERAGE_DATA + " where "
                    + CommonString.KEY_STORE_CD + "='" + storecd + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    sb.setStatus((((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_COVERAGE_STATUS))))));
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return sb;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb;
    }

    public void deleteSpecificTables(String coverage_id) {
        // DELETING TABLES
        db.delete(CommonString.TABLE_COVERAGE_DATA, CommonString.KEY_ID + "= '" + coverage_id + "'", null);
    }


    public ArrayList<SearchListGetterSetter> getStoreData(String date, String userid) {
        ArrayList<SearchListGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;

        try {
            //dbcursor = db.rawQuery("SELECT  * from  STORE_LIST", null);
            dbcursor = db.rawQuery("SELECT  * from  STORE_LIST where " + CommonString.KEY_VISIT_DATE + "='" + date + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    SearchListGetterSetter sb = new SearchListGetterSetter();

                    sb.setSTORE_CD((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("STORE_CD"))));
                    sb.setKEYACCOUNT((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("KEYACCOUNT"))));
                    sb.setSTORENAME(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("STORENAME")));
                    sb.setADDRESS(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("ADDRESS")));
                    sb.setCITY((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("CITY"))));
                    sb.setSTORETYPE(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("STORETYPE")));
                    sb.setROUTE_NAME(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("ROUTE_NAME")));

                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception get JCP!", e.toString());
            return list;
        }


        return list;
    }

    public long insertCoverageData(CoverageBean data) {
        ContentValues values = new ContentValues();
        try {
            values.put(CommonString.KEY_STORE_CD, data.getStoreId());
            values.put(CommonString.KEY_USER_ID, data.getUserId());
            values.put(CommonString.KEY_IN_TIME, data.getInTime());
            values.put(CommonString.KEY_OUT_TIME, data.getOutTime());
            values.put(CommonString.KEY_VISIT_DATE, data.getVisitDate());
            values.put(CommonString.KEY_LATITUDE, data.getLatitude());
            values.put(CommonString.KEY_LONGITUDE, data.getLongitude());
            values.put(CommonString.KEY_COVERAGE_STATUS, data.getStatus());
            values.put(CommonString.KEY_IN_TIME_IMAGE, data.getIntime_Image());
            values.put(CommonString.KEY_OUT_TIME_IMAGE, data.getOuttime_Image());
            values.put(CommonString.KEY_REASON_ID, data.getReasonid());
            values.put(CommonString.KEY_REASON, data.getReason());

            return db.insert(CommonString.TABLE_COVERAGE_DATA, null, values);

        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }

    }

    public ArrayList<CoverageBean> getCoverageData(String visitdate) {
        ArrayList<CoverageBean> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * from " + CommonString.TABLE_COVERAGE_DATA + " where "
                    + CommonString.KEY_VISIT_DATE + "='" + visitdate + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CoverageBean sb = new CoverageBean();

                    sb.setStoreId(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_STORE_CD)));
                    sb.setUserId((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_USER_ID))));
                    sb.setInTime(((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IN_TIME)))));
                    sb.setOutTime(((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_OUT_TIME)))));
                    sb.setVisitDate((((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_VISIT_DATE))))));
                    sb.setLatitude(((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LATITUDE)))));
                    sb.setLongitude(((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LONGITUDE)))));
                    sb.setStatus((((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_COVERAGE_STATUS))))));
                    sb.setIntime_Image((((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IN_TIME_IMAGE))))));
                    sb.setOuttime_Image((((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_OUT_TIME_IMAGE))))));
                    sb.setReasonid((((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON_ID))))));
                    sb.setReason((((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON))))));

                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            Log.d("Excep fetch Coverage", e.toString());
        }
        return list;
    }

    public JCPMasterGetterSetter getJCPStatus(int storecd, String username) {
        JCPMasterGetterSetter sb = new JCPMasterGetterSetter();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT UPLOAD_STATUS from JOURNEY_PLAN where "
                    + CommonString.KEY_STORE_CD + "='" + storecd + "' and USERID = '" + username + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    sb.setUPLOAD_STATUS((((dbcursor.getString(dbcursor.getColumnIndexOrThrow("UPLOAD_STATUS"))))));
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return sb;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb;
    }

    public ArrayList<NonWorkingReasonGetterSetter> getNonWorkingData(boolean fromStore) {

        ArrayList<NonWorkingReasonGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;

        try {
            if (fromStore) {
              /*  dbcursor = db
                        .rawQuery(
                                "SELECT -1 AS REASON_CD,'Select' as REASON,'-1' as ENTRY_ALLOW,'-1' AS IMAGE_ALLOW,'-1' AS FOR_STORE,'-1' AS FOR_ATT union SELECT * FROM NON_WORKING_REASON_NEW WHERE FOR_STORE ='1'"
                                , null);*/

                dbcursor = db
                        .rawQuery(
                                "SELECT -1 AS REASON_CD,'Select' as REASON,'-1' as ENTRY_ALLOW, '-1' as IMAGE_ALLOW union SELECT * FROM NON_WORKING_REASON"
                                , null);

            } else {
                dbcursor = db
                        .rawQuery(
                                "SELECT -1 AS REASON_CD,'Select' as REASON,'-1' as ENTRY_ALLOW,'-1' AS FOR_STORE,'-1' AS FOR_ATT union SELECT * FROM NON_WORKING_REASON WHERE FOR_ATT ='1'"
                                , null);
            }

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    NonWorkingReasonGetterSetter sb = new NonWorkingReasonGetterSetter();

                    sb.setReason_cd(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("REASON_CD")));
                    sb.setReason(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("REASON")));
                    sb.setEntry_allow(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("ENTRY_ALLOW")));
                    sb.setIMAGE_ALLOW(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("IMAGE_ALLOW")));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return list;
        }
        return list;
    }

    public ArrayList<CoverageBean> getCoverageSpecificData(String visitdate, String storecd) {
        ArrayList<CoverageBean> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * from " + CommonString.TABLE_COVERAGE_DATA + " where "
                    + CommonString.KEY_VISIT_DATE + "='" + visitdate + "' and STORE_CD = '" + storecd + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CoverageBean sb = new CoverageBean();

                    sb.setStoreId(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_STORE_CD)));
                    sb.setUserId((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_USER_ID))));
                    sb.setInTime(((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IN_TIME)))));
                    sb.setOutTime(((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_OUT_TIME)))));
                    sb.setVisitDate((((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_VISIT_DATE))))));
                    sb.setLatitude(((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LATITUDE)))));
                    sb.setLongitude(((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LONGITUDE)))));
                    sb.setStatus((((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_COVERAGE_STATUS))))));
                    sb.setIntime_Image((((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IN_TIME_IMAGE))))));
                    sb.setOuttime_Image((((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_OUT_TIME_IMAGE))))));
                    sb.setReasonid((((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON_ID))))));
                    sb.setReason((((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON))))));

                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            Log.d("Excep fetch Coverage", e.toString());
        }
        return list;
    }

    public long updateCoverageData(CoverageBean coverageBean) {

        try {
            ContentValues values = new ContentValues();
            values.put(CommonString.KEY_OUT_TIME_IMAGE, "");
            values.put(CommonString.KEY_OUT_TIME, coverageBean.getOutTime());
            values.put(CommonString.KEY_COVERAGE_STATUS, coverageBean.getStatus());

            return db.update(CommonString.TABLE_COVERAGE_DATA, values,
                    CommonString.KEY_STORE_CD + "='" + coverageBean.getStoreId() + "'", null);

        } catch (Exception e) {
            return 0;
        }
    }

    public long updateCoverageStatus(String storeid, String status) {

        try {
            ContentValues values = new ContentValues();
            values.put(CommonString.KEY_COVERAGE_STATUS, status);
            return db.update(CommonString.TABLE_COVERAGE_DATA, values,
                    CommonString.KEY_STORE_CD + "='" + storeid + "'", null);

        } catch (Exception e) {
            return 0;
        }
    }

    public void insertDistributorListData(DistributerListGetterSetter data) {

        db.delete("DISTRIBUTOR_LIST", null, null);
        ContentValues values = new ContentValues();

        try {
            for (int i = 0; i < data.getKEYACCOUNT_CD().size(); i++) {
                values.put("KEYACCOUNT_CD", Integer.parseInt(data.getKEYACCOUNT_CD().get(i)));
                values.put("KEYACCOUNT", data.getKEYACCOUNT().get(i));
                values.put("ROUTE_NAME", (data.getROUTE_NAME().get(i)));

                db.insert("DISTRIBUTOR_LIST", null, values);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public ArrayList<DistributerListGetterSetter> getDistributorData() {

        ArrayList<DistributerListGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db
                    .rawQuery(
                            "SELECT -1 AS KEYACCOUNT_CD,'Select distributor' as KEYACCOUNT,'Select route' as ROUTE_NAME union SELECT * FROM DISTRIBUTOR_LIST"
                            , null);


            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    DistributerListGetterSetter sb = new DistributerListGetterSetter();

                    sb.setKEYACCOUNT_CD(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("KEYACCOUNT_CD")));
                    sb.setKEYACCOUNT(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("KEYACCOUNT")));
                    sb.setROUTE_NAME(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("ROUTE_NAME")));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            return list;
        }
        return list;
    }


    public ArrayList<AnswerGetterSetter> getAnswerData() {

        ArrayList<AnswerGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            list.clear();
            AnswerGetterSetter answerGetterSetter = new AnswerGetterSetter();
            answerGetterSetter.setAnswerCd(0);
            answerGetterSetter.setAnswer("Select");

            list.add(answerGetterSetter);

            AnswerGetterSetter answerGetterSetter2 = new AnswerGetterSetter();
            answerGetterSetter2.setAnswerCd(1);
            answerGetterSetter2.setAnswer("Yes");

            list.add(answerGetterSetter2);

            AnswerGetterSetter answerGetterSetter3 = new AnswerGetterSetter();
            answerGetterSetter3.setAnswerCd(0);
            answerGetterSetter3.setAnswer("No");

            list.add(answerGetterSetter3);


        } catch (Exception e) {
            return list;
        }
        return list;
    }

    public long insertDeploymentStatusData(DeploymentStatusGetterSetter data) {

        // db.delete(CommonString.TABLE_DEPLOYMENT_STATUS, null, null);
        ContentValues values = new ContentValues();

        try {

            values.put(CommonString.KEY_STORE_CD, Integer.parseInt(data.getStoreId()));
            values.put(CommonString.KEY_USER_ID, data.getUsername());
            values.put(CommonString.KEY_BILL_CUT_ID, (data.getBillCutid()));
            values.put(CommonString.KEY_RECEIPT_IMG, (data.getReceiptImg()));
            values.put(CommonString.KEY_HANGER_ID, (data.getHangerId()));
            values.put(CommonString.KEY_HANGER_IMG, (data.getHangerImg()));
            values.put(CommonString.KEY_REASON_ID, (data.getReasonCd()));

            return db.insert(CommonString.TABLE_DEPLOYMENT_STATUS, null, values);

        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }

    }

    //Insert JCP
    public long insertJCPData(SearchListGetterSetter data) {
        ContentValues values = new ContentValues();
        try {
            values.put(CommonString.KEY_STORE_CD, data.getSTORE_CD());
            values.put(CommonString.KEYACCOUNT, data.getKEYACCOUNT());
            values.put(CommonString.KEY_STORENAME, data.getSTORENAME());
            values.put(CommonString.KEY_ADDRESS, data.getADDRESS());
            values.put(CommonString.KEY_CITY, data.getCITY());
            values.put(CommonString.KEY_STORETYPE, data.getSTORETYPE());
            values.put(CommonString.KEY_ROUTE_NAME, data.getROUTE_NAME());
            values.put(CommonString.KEY_VISIT_DATE, data.getVISIT_DATE());

            return db.insert("STORE_LIST", null, values);

        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }

    }

    public void insertNonWorkingData(NonWorkingReasonGetterSetter data) {

        db.delete("NON_WORKING_REASON", null, null);
        ContentValues values = new ContentValues();

        try {

            for (int i = 0; i < data.getReason_cd().size(); i++) {

                values.put("REASON_CD", Integer.parseInt(data.getReason_cd().get(i)));
                values.put("REASON", data.getReason().get(i));
                values.put("ENTRY_ALLOW", data.getEntry_allow().get(i));
                values.put("IMAGE_ALLOW", data.getIMAGE_ALLOW().get(i));

                db.insert("NON_WORKING_REASON", null, values);

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public ArrayList<DeploymentStatusGetterSetter> getDeploymentData(String storeid) {
        ArrayList<DeploymentStatusGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT  * from  " + CommonString.TABLE_DEPLOYMENT_STATUS + " where " + CommonString.KEY_STORE_CD + " = '" + storeid + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    DeploymentStatusGetterSetter sb = new DeploymentStatusGetterSetter();

                    sb.setStoreId((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("STORE_CD"))));
                    sb.setBillCutid(dbcursor.getInt(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_BILL_CUT_ID)));
                    sb.setReceiptImg(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_RECEIPT_IMG)));
                    sb.setHangerId((dbcursor.getInt(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_HANGER_ID))));
                    sb.setHangerImg(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_HANGER_IMG)));
                    sb.setReasonCd(dbcursor.getInt(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_REASON_ID)));


                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception get JCP!", e.toString());
            return list;
        }


        return list;
    }

    public void insertNonDeploymentData(NonDeploymentReasonGetterSetter data) {

        db.delete("NON_DEPLOYMENT_REASON", null, null);
        ContentValues values = new ContentValues();

        try {

            for (int i = 0; i < data.getNDREASON_CD().size(); i++) {

                values.put("NDREASON_CD", Integer.parseInt(data.getNDREASON_CD().get(i)));
                values.put("NDREASON", data.getNDREASON().get(i));

                db.insert("NON_DEPLOYMENT_REASON", null, values);

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public ArrayList<NonDeploymentReasonGetterSetter> getNonDeploymentReasonData() {
        ArrayList<NonDeploymentReasonGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery(
                    "SELECT -1 AS NDREASON_CD,'Select' as NDREASON union SELECT * FROM NON_DEPLOYMENT_REASON"
                    , null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    NonDeploymentReasonGetterSetter sb = new NonDeploymentReasonGetterSetter();
                    sb.setNDREASON_CD(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("NDREASON_CD")));
                    sb.setNDREASON(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("NDREASON")));

                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return list;
        }
        return list;
    }


}
