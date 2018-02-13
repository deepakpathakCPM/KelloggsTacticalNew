package com.cpm.kelloggstacticalnew.dailyEntry;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.cpm.kelloggstacticalnew.R;
import com.cpm.kelloggstacticalnew.constant.AlertandMessages;
import com.cpm.kelloggstacticalnew.constant.CommonFunctions;
import com.cpm.kelloggstacticalnew.constant.CommonString;
import com.cpm.kelloggstacticalnew.database.KelloggsTacticalDB;
import com.cpm.kelloggstacticalnew.getterSetter.CoverageBean;
import com.cpm.kelloggstacticalnew.getterSetter.JCPMasterGetterSetter;
import com.cpm.kelloggstacticalnew.getterSetter.NonWorkingReasonGetterSetter;
import com.cpm.kelloggstacticalnew.getterSetter.SearchListGetterSetter;
import com.cpm.kelloggstacticalnew.xmlHandler.UploadCoverageClass;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class NonWorkingActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    ArrayList<NonWorkingReasonGetterSetter> reasondata = new ArrayList<NonWorkingReasonGetterSetter>();

    private Spinner reasonspinner, merNotAllowedSpinner;
    private KelloggsTacticalDB database;
    String reasonname, reasonid, subreasonname, subreasonid = "0", entry_allow, image_allow, image, entry, reason_reamrk, intime;
    Button save;
    private ArrayAdapter<CharSequence> reason_adapter;
    protected String _path, str;
    protected String _pathforcheck = "";
    private String image1 = "";
    private ArrayList<CoverageBean> cdata = new ArrayList<CoverageBean>();
    protected boolean _taken;
    protected static final String PHOTO_TAKEN = "photo_taken";
    private SharedPreferences preferences;
    String _UserId, visit_date, store_id;
    protected boolean status = true;
    EditText text, informTo;
    AlertDialog alert;
    ImageButton camera;
    RelativeLayout reason_lay, rel_cam;
    boolean leave_flag = false;
    Context context;
    ArrayList<JCPMasterGetterSetter> jcp;
    SearchListGetterSetter searchGetSetOfIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_non_working);
        declaration();
        //jcp = database.getStoreData(visit_date, _UserId);
        reasondata = database.getNonWorkingData(true);
        /*cdata = database.getCoverageData(visit_date, null);
        storedata = database.getStoreData(visit_date);*/
        intime = CommonFunctions.getCurrentTimeHHMMSSWithColon();

        reason_adapter = new ArrayAdapter<CharSequence>(this,
                android.R.layout.simple_spinner_item);
        for (int i = 0; i < reasondata.size(); i++) {
            reason_adapter.add(reasondata.get(i).getReason().get(0));
        }
        reasonspinner.setAdapter(reason_adapter);
        reason_adapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reasonspinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        finish();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
                               long arg3) {
        // TODO Auto-generated method stub

        switch (arg0.getId()) {
            case R.id.spinner2:
                if (position != 0) {
                    reasonname = reasondata.get(position).getReason().get(0);
                    reasonid = reasondata.get(position).getReason_cd().get(0);
                    entry_allow = reasondata.get(position).getEntry_allow().get(0);
                    image_allow = reasondata.get(position).getIMAGE_ALLOW().get(0);
                    //entry_allow = "1";
                    //image = reasondata.get(position - 1).getImage();
                    //	entry = reasondata.get(position - 1).getEntry();
                    //reason_reamrk = reasondata.get(position - 1).getREASON_REMARK();

                    if (image_allow.equalsIgnoreCase("1")) {
                        rel_cam.setVisibility(View.VISIBLE);
                        image = "true";
                    } else {
                        rel_cam.setVisibility(View.GONE);
                        image = "false";
                    }

                } else {
                    reasonname = "";
                    reasonid = "0";
                    entry_allow = "1";
                    //image = "";
                    //entry = "";
                    //reason_reamrk = "";
                }
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }

    protected void startCameraActivity() {

        try {
            Log.i("MakeMachine", "startCameraActivity()");
            File file = new File(_path);
            Uri outputFileUri = Uri.fromFile(file);

            Intent intent = new Intent(
                    MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

            startActivityForResult(intent, 0);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("MakeMachine", "resultCode: " + resultCode);
        switch (resultCode) {
            case 0:
                Log.i("MakeMachine", "User cancelled");
                break;

            case -1:

                if (_pathforcheck != null && !_pathforcheck.equals("")) {
                    if (new File(str + _pathforcheck).exists()) {

                        camera.setBackground(getResources().getDrawable(R.drawable.camera_green));
                        //camera.setBackgroundResource(R.drawable.camera_list_tick);
                        image1 = _pathforcheck;

                    }
                }

                break;
        }

    }

    public boolean imageAllowed() {
        boolean result = true;

        if (image.equalsIgnoreCase("true")) {

            if (image1.equalsIgnoreCase("")) {

                result = false;
            }
        }

        return result;

    }

    public boolean textAllowed() {
        boolean result = true;

        //if (reason_reamrk.equalsIgnoreCase("true")) {

      /*  if (text.getText().toString().trim().equals("")) {

            result = false;

        }*/
        //}

        return result;
    }

/*
    public boolean isDataUploaded() {

        jcp = database.getNewStoreListFromJCP(visit_date);
        boolean flag = true;

        if (entry_allow.equals("0")) {

            if (jcp.size() > 0) {
                for (int i = 0; i < jcp.size(); i++) {
                    if (jcp.get(i).getUpload_Status().equalsIgnoreCase(CommonString.KEY_U)) {
                        flag = false;
                        break;
                    }
                }
            }
        }

        return flag;
    }
*/


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v.getId() == R.id.fab) {

            if (validatedata()) {

                if (imageAllowed()) {
                    // if (true) {

                    if (textAllowed()) {

                        //if (isDataUploaded()) {
                        if (true) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage("Do you want to save the data ")
                                    .setCancelable(false)
                                    .setPositiveButton("OK",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int id) {

                                                    alert.getButton(
                                                            AlertDialog.BUTTON_POSITIVE)
                                                            .setEnabled(false);


                                                    CoverageBean cdata = new CoverageBean();
                                                    cdata.setStoreId(store_id);
                                                    cdata.setUserId(_UserId);
                                                    cdata.setInTime(intime);
                                                    cdata.setOutTime(CommonFunctions.getCurrentTimeHHMMSSWithColon());
                                                    cdata.setVisitDate(visit_date);
                                                    cdata.setLatitude("0.0");
                                                    cdata.setLongitude("0.0");
                                                    cdata.setIntime_Image(image1);
                                                    cdata.setOuttime_Image("");
                                                    cdata.setStatus(CommonString.KEY_L);
                                                    cdata.setReason(reasonname);
                                                    cdata.setReasonid(reasonid);

                                                    if (database.insertCoverageData(cdata) > 0) {

                                                        if (database.insertJCPData(searchGetSetOfIntent) > 0) {
                                                            //startActivity(new Intent(context, DeploymentStatusActivity.class).putExtra(CommonString.TAG_OBJECT, searchGetSetOfIntent));
                                                            AlertandMessages.showToastMsg(context, "Data saved successfully");
                                                            ((Activity) context).finish();
                                                        } else {
                                                            AlertandMessages.showToastMsg(context, "Store not saved");
                                                        }

                                                    } else {
                                                        AlertandMessages.showToastMsg(context, "Coverage not saved");
                                                    }


                                                 /*   if (entry_allow.equals("0")) {

                                                        database.deleteAllTables();

                                                        for (int i = 0; i < jcp.size(); i++) {

                                                            String stoteid = String.valueOf(jcp.get(i).getSTORE_CD().get(0));

                                                            CoverageBean cdata = new CoverageBean();
                                                            cdata.setStoreId(stoteid);
                                                            cdata.setUserId(_UserId);
                                                            cdata.setInTime(intime);
                                                            cdata.setOutTime(CommonFunctions.getCurrentTimeHHMMSSWithColon());
                                                            cdata.setVisitDate(visit_date);
                                                            cdata.setLatitude("0.0");
                                                            cdata.setLongitude("0.0");
                                                            cdata.setIntime_Image("");
                                                            cdata.setOuttime_Image("");
                                                            cdata.setStatus(CommonString.KEY_U);
                                                            cdata.setReason(reasonname);
                                                            cdata.setReasonid(reasonid);

                                                            //new UploadCoverageClass(context, "Non working Data", cdata).execute();

                                                           *//* SharedPreferences.Editor editor = preferences
                                                                    .edit();
                                                            editor.putString(CommonString.KEY_STOREVISITED_STATUS + stoteid, "No");
                                                            editor.putString(
                                                                    CommonString.KEY_STOREVISITED_STATUS,
                                                                    "");
                                                            editor.putString(
                                                                    CommonString.KEY_STORE_IN_TIME,
                                                                    "");
                                                            editor.putString(
                                                                    CommonString.KEY_LATITUDE,
                                                                    "");
                                                            editor.putString(
                                                                    CommonString.KEY_LONGITUDE,
                                                                    "");
                                                            editor.commit();*//*
                                                        }

                                                    } else {

                                                        CoverageBean cdata = new CoverageBean();
                                                        cdata.setStoreId(store_id);
                                                        cdata.setUserId(_UserId);
                                                        cdata.setInTime(intime);
                                                        cdata.setOutTime(CommonFunctions.getCurrentTimeHHMMSSWithColon());
                                                        cdata.setVisitDate(visit_date);
                                                        cdata.setLatitude("0.0");
                                                        cdata.setLongitude("0.0");
                                                        cdata.setIntime_Image("");
                                                        cdata.setOuttime_Image("");
                                                        cdata.setStatus(CommonString.KEY_U);
                                                        cdata.setReason(reasonname);
                                                        cdata.setReasonid(reasonid);

                                                        if (database.insertCoverageData(cdata) > 0) {

                                                            if (database.insertJCPData(searchGetSetOfIntent) > 0) {
                                                                startActivity(new Intent(context, DeploymentStatusActivity.class).putExtra(CommonString.TAG_OBJECT, searchGetSetOfIntent));
                                                                ((Activity) context).finish();
                                                            }
                                                            else {
                                                                AlertandMessages.showToastMsg(context, "Store not saved");
                                                            }

                                                        } else {
                                                            AlertandMessages.showToastMsg(context, "Coverage not saved");
                                                        }

                                                        //new UploadCoverageClass(context, "Non working Data", cdata).execute();

                                                      *//*  SharedPreferences.Editor editor = preferences
                                                                .edit();

                                                        editor.putString(CommonString.KEY_STOREVISITED_STATUS + store_id, "No");
                                                        editor.putString(
                                                                CommonString.KEY_STOREVISITED_STATUS,
                                                                "");
                                                        editor.putString(
                                                                CommonString.KEY_STORE_IN_TIME,
                                                                "");
                                                        editor.putString(
                                                                CommonString.KEY_LATITUDE,
                                                                "");
                                                        editor.putString(
                                                                CommonString.KEY_LONGITUDE,
                                                                "");
                                                        editor.commit();*//*

                                                    }*/
                                                }
                                            })
                                    .setNegativeButton("Cancel",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int id) {
                                                    dialog.cancel();
                                                }
                                            });

                            alert = builder.create();
                            alert.show();
                        } else {
                            Toast.makeText(context, "Data has been uploaded for some" +
                                    " stores, please select another reason", Toast.LENGTH_LONG).show();
                        }

                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Please enter required remark reason",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please Capture Image", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(),
                        "Please Select a Reason", Toast.LENGTH_SHORT).show();

            }
        } else if (v.getId() == R.id.imgcam) {
            _pathforcheck = store_id + "_NONWORKING_" + visit_date.replace("/", "") +
                    "_" + getCurrentTime().replace(":", "") + ".jpg";
            _path = CommonString.FILE_PATH + _pathforcheck;
            startCameraActivity();
        }

    }

    public boolean validatedata() {
        boolean result = false;
        if (reasonid != null && !reasonid.equalsIgnoreCase("") && !reasonid.equalsIgnoreCase("0")) {
            result = true;
        }
        return result;
    }

    public String getCurrentTime() {

        Calendar m_cal = Calendar.getInstance();

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String cdate = formatter.format(m_cal.getTime());

       /* String intime = m_cal.get(Calendar.HOUR_OF_DAY) + ":"
                + m_cal.get(Calendar.MINUTE) + ":" + m_cal.get(Calendar.SECOND);*/

        return cdate;

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
        reasonspinner = (Spinner) findViewById(R.id.spinner2);
        searchGetSetOfIntent = (SearchListGetterSetter) getIntent().getSerializableExtra(CommonString.TAG_OBJECT);
        camera = (ImageButton) findViewById(R.id.imgcam);
        rel_cam = (RelativeLayout) findViewById(R.id.relimgcam);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        _UserId = preferences.getString(CommonString.KEY_USERNAME, "");
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        store_id = searchGetSetOfIntent.getSTORE_CD();

        database = new KelloggsTacticalDB(this);
        database.open();
        str = CommonString.FILE_PATH;
        camera.setOnClickListener(this);
        fab.setOnClickListener(this);
    }
}
