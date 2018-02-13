package com.cpm.kelloggstacticalnew.dailyEntry;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.cpm.kelloggstacticalnew.R;
import com.cpm.kelloggstacticalnew.constant.AlertandMessages;
import com.cpm.kelloggstacticalnew.constant.CommonFunctions;
import com.cpm.kelloggstacticalnew.constant.CommonString;
import com.cpm.kelloggstacticalnew.database.KelloggsTacticalDB;
import com.cpm.kelloggstacticalnew.getterSetter.AnswerGetterSetter;
import com.cpm.kelloggstacticalnew.getterSetter.CoverageBean;
import com.cpm.kelloggstacticalnew.getterSetter.DeploymentStatusGetterSetter;
import com.cpm.kelloggstacticalnew.getterSetter.NonDeploymentReasonGetterSetter;
import com.cpm.kelloggstacticalnew.getterSetter.SearchListGetterSetter;

import java.io.File;
import java.util.ArrayList;

public class DeploymentStatusActivity extends AppCompatActivity implements View.OnClickListener {

    Context context;
    Spinner receiptSpn, hangerSpn;
    ImageView receiptCam, hangerCam;
    KelloggsTacticalDB db;
    ArrayList<AnswerGetterSetter> receiptAnswer, hangerAnswer;
    ArrayList<NonDeploymentReasonGetterSetter> reasonGetSet;
    SearchListGetterSetter searchGetSetOfIntent;
    String img_str_receipt = "", img_str_hanger = "", _pathforcheck, store_id, visit_date, username, visit_date_formatted, _path, msg, reasonId = "0";
    SharedPreferences preferences;
    ImageView globalcam;
    int billcutid, hangerId;
    LinearLayout hangerCam_ll, reason_ll;
    Spinner reasonSpn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deployment_status);
        declaration();
        prepareList();

        receiptSpn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (receiptSpn.getSelectedItem().toString().equalsIgnoreCase("Yes")) {
                    billcutid = 1;
                    receiptCam.setImageResource(R.drawable.camera_pink);
                    receiptCam.setEnabled(true);
                    hangerSpn.setEnabled(true);
                } else if (receiptSpn.getSelectedItem().toString().equalsIgnoreCase("No")) {
                    billcutid = 0;
                    receiptCam.setImageResource(R.drawable.camera_grey);
                    receiptCam.setEnabled(false);
                    hangerSpn.setEnabled(false);
                    img_str_receipt = "";
                } else {
                    billcutid = 0;
                    receiptCam.setImageResource(R.drawable.camera_grey);
                    receiptCam.setEnabled(false);
                    hangerSpn.setEnabled(false);
                    img_str_receipt = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        hangerSpn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (hangerSpn.getSelectedItem().toString().equalsIgnoreCase("Yes")) {
                    hangerId = 1;
                    hangerCam.setImageResource(R.drawable.camera_pink);
                    hangerCam.setEnabled(true);
                    hangerCam_ll.setVisibility(View.VISIBLE);
                    reason_ll.setVisibility(View.GONE);
                } else if (hangerSpn.getSelectedItem().toString().equalsIgnoreCase("No")) {
                    hangerId = 0;
                    hangerCam.setImageResource(R.drawable.camera_grey);
                    hangerCam.setEnabled(false);
                    img_str_hanger = "";
                    hangerCam_ll.setVisibility(View.GONE);
                    reason_ll.setVisibility(View.VISIBLE);
                } else {
                    hangerId = 0;
                    hangerCam.setImageResource(R.drawable.camera_grey);
                    hangerCam.setEnabled(false);
                    img_str_hanger = "";
                    hangerCam_ll.setVisibility(View.GONE);
                    reason_ll.setVisibility(View.GONE);
                    reasonSpn.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        reasonSpn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    reasonId = reasonGetSet.get(position).getNDREASON_CD().get(0);
                } else {
                    reasonId = "0";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    void prepareList() {
        receiptAnswer = db.getAnswerData();

        //------------for Answer Master List---------------
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item);
        for (int i = 0; i < receiptAnswer.size(); i++) {
            arrayAdapter.add(receiptAnswer.get(i).getAnswer());
        }
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        receiptSpn.setAdapter(arrayAdapter);
        //------------------------------------------------

        hangerAnswer = db.getAnswerData();

        //------------for Answer Master List---------------
        ArrayAdapter arrayAdapter2 = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item);
        for (int i = 0; i < hangerAnswer.size(); i++) {
            arrayAdapter2.add(hangerAnswer.get(i).getAnswer());
        }
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hangerSpn.setAdapter(arrayAdapter2);
        //------------------------------------------------

        reasonGetSet = db.getNonDeploymentReasonData();
        //------------for Reason Master List---------------
        ArrayAdapter arrayAdapter3 = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item);
        for (int i = 0; i < reasonGetSet.size(); i++) {
            arrayAdapter3.add(reasonGetSet.get(i).getNDREASON().get(0));
        }
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reasonSpn.setAdapter(arrayAdapter3);
        //------------------------------------------------


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // NavUtils.navigateUpFromSameTask(this);
            finish();
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        }

        return super.onOptionsItemSelected(item);
    }

    void declaration() {
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        receiptSpn = (Spinner) findViewById(R.id.receiptSpn);
        hangerSpn = (Spinner) findViewById(R.id.hangerSpn);
        receiptCam = (ImageView) findViewById(R.id.receiptCam);
        hangerCam = (ImageView) findViewById(R.id.hangerCam);
        hangerCam_ll = (LinearLayout) findViewById(R.id.hangerCam_ll);
        reason_ll = (LinearLayout) findViewById(R.id.reason_ll);
        reasonSpn = (Spinner) findViewById(R.id.reasonSpn);

        db = new KelloggsTacticalDB(context);
        db.open();
        if (getIntent() != null) {
            searchGetSetOfIntent = (SearchListGetterSetter) getIntent().getSerializableExtra(CommonString.TAG_OBJECT);
            store_id = searchGetSetOfIntent.getSTORE_CD();
        } else {
            store_id = "0";
        }

        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        visit_date = preferences.getString(CommonString.KEY_DATE, "");
        username = preferences.getString(CommonString.KEY_USERNAME, "");
        visit_date_formatted = preferences.getString(CommonString.KEY_YYYYMMDD_DATE, "");

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    DeploymentStatusGetterSetter dsGetset = new DeploymentStatusGetterSetter();
                    dsGetset.setStoreId(store_id);
                    dsGetset.setUsername(username);
                    dsGetset.setBillCutid(billcutid);
                    dsGetset.setReceiptImg(img_str_receipt);
                    dsGetset.setHangerId(hangerId);
                    dsGetset.setHangerImg(img_str_hanger);
                    dsGetset.setReasonCd(Integer.parseInt(reasonId));

                    if (db.insertDeploymentStatusData(dsGetset) > 0) {

                        CoverageBean cdata = new CoverageBean();
                        cdata.setStoreId(store_id);
                        cdata.setUserId(username);
                        cdata.setOutTime(CommonFunctions.getCurrentTimeHHMMSSWithColon());
                        cdata.setVisitDate(visit_date);
                        cdata.setStatus(CommonString.KEY_C);
                        db.updateCoverageData(cdata);

                        AlertandMessages.showToastMsg(context, "Data saved successfully");
                        ((Activity) context).finish();
                    } else {
                        AlertandMessages.showToastMsg(context, "Data not saved");
                    }

                } else {
                    AlertandMessages.showToastMsg(context, msg);
                }
            }
        });

        receiptCam.setOnClickListener(this);
        hangerCam.setOnClickListener(this);
    }

    boolean validate() {
        boolean isvalid = true;

        if (receiptSpn.getSelectedItemPosition() == 0) {
            isvalid = false;
            msg = "Please Select Bill cut.";
        } else {
            if (receiptSpn.getSelectedItem().toString().equalsIgnoreCase("yes")) {
                if (img_str_receipt != null && !img_str_receipt.equalsIgnoreCase("")) {

                    if (hangerSpn.getSelectedItemPosition() == 0) {
                        isvalid = false;
                        msg = "Please Select Hanger.";
                    } else {
                        if (hangerSpn.getSelectedItem().toString().equalsIgnoreCase("yes")) {
                            if (img_str_hanger != null && !img_str_hanger.equalsIgnoreCase("")) {
                                isvalid = true;
                            } else {
                                isvalid = false;
                                msg = "Please click Hanger photo";
                            }
                        } else {
                            if (reasonSpn.getSelectedItemPosition() != 0 && reasonId != null && !reasonId.equalsIgnoreCase("0")) {
                                isvalid = true;
                            } else {
                                isvalid = false;
                                msg = "Please Select Reason";
                            }
                        }
                    }

                } else {
                    isvalid = false;
                    msg = "Please click Receipt photo";
                }
            } else {
                isvalid = true;
            }

        }


        return isvalid;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.receiptCam:
                img_str_receipt = "";
                globalcam = receiptCam;
                _pathforcheck = store_id + "_" + username.replace(".", "") + "_ReceiptImg-" + visit_date_formatted + "-" + CommonFunctions.getCurrentTimeHHMMSS() + ".jpg";
                _path = CommonString.FILE_PATH + _pathforcheck;
                CommonFunctions.startCameraActivity((Activity) context, _path);
                break;
            case R.id.hangerCam:
                img_str_hanger = "";
                globalcam = hangerCam;
                _pathforcheck = store_id + "_" + username.replace(".", "") + "_HangerImg-" + visit_date_formatted + "-" + CommonFunctions.getCurrentTimeHHMMSS() + ".jpg";
                _path = CommonString.FILE_PATH + _pathforcheck;
                CommonFunctions.startCameraActivity((Activity) context, _path);
                break;

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case 0:
                Log.i("MakeMachine", "User cancelled");
                break;
            case -1:
                if (_pathforcheck != null && !_pathforcheck.equals("")) {
                    if (new File(_path).exists()) {
                        globalcam.setImageResource(R.drawable.camera_green);
                        if (globalcam == receiptCam) {
                            img_str_receipt = _pathforcheck;
                        } else if (globalcam == hangerCam) {
                            img_str_hanger = _pathforcheck;
                        }
                        _pathforcheck = "";
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        finish();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }


}
