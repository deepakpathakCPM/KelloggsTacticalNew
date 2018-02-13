package com.cpm.kelloggstacticalnew.upload;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cpm.kelloggstacticalnew.MainActivity;
import com.cpm.kelloggstacticalnew.R;
import com.cpm.kelloggstacticalnew.constant.AlertandMessages;
import com.cpm.kelloggstacticalnew.constant.CommonFunctions;
import com.cpm.kelloggstacticalnew.constant.CommonString;
import com.cpm.kelloggstacticalnew.database.KelloggsTacticalDB;
import com.cpm.kelloggstacticalnew.getterSetter.CoverageBean;
import com.cpm.kelloggstacticalnew.getterSetter.DeploymentStatusGetterSetter;
import com.cpm.kelloggstacticalnew.getterSetter.FailureGetterSetter;
import com.cpm.kelloggstacticalnew.xmlHandler.FailureXMLHandler;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class UploadDataActivity extends Activity {

    private Dialog dialog;
    private ProgressBar pb;
    private TextView percentage, message;
    String app_ver;
    private String visit_date, username;
    private SharedPreferences preferences;
    private KelloggsTacticalDB database;
    private String reasonid, faceup, stock, length;
    private int factor, k;
    String datacheck = "";
    String[] words;
    String validity, storename;
    int mid;
    String sod = "";
    String total_sku = "";
    String sku = "";
    String sos_data = "";
    String category_data = "";
    Data data;
    ArrayList<DeploymentStatusGetterSetter> store_detail = new ArrayList<>();
    private ArrayList<CoverageBean> coverageBeanlist = new ArrayList<CoverageBean>();
    int timeout = 20000;
    private FailureGetterSetter failureGetterSetter = null;
    static int counter = 1;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_data);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        context = this;
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        app_ver = preferences.getString(CommonString.KEY_VERSION, "");
        database = new KelloggsTacticalDB(this);
        database.open();

        new UploadTask(context).execute();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        database.close();
    }

    private class UploadTask extends AsyncTask<Void, Data, String> {
        private Context context;

        UploadTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            dialog = new Dialog(context);
            dialog.setContentView(R.layout.custom2);
            dialog.setTitle("Uploading Data");
            dialog.setCancelable(false);
            dialog.show();
            pb = (ProgressBar) dialog.findViewById(R.id.progressBar1);
            percentage = (TextView) dialog.findViewById(R.id.percentage);
            message = (TextView) dialog.findViewById(R.id.message);
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            dialog.dismiss();

            if (result.equals(CommonString.KEY_SUCCESS)) {

                AlertandMessages.showAlert((Activity) context, "Data Uploaded Successfully", true);
            } else if (!result.equals("")) {
                AlertandMessages.showAlert((Activity) context, "Error in uploading: " + result, true);
            }

        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub
            data = new Data();
            try {
                SoapObject request;
                HttpTransportSE androidHttpTransport;
                SoapSerializationEnvelope envelope;
                database.open();
                coverageBeanlist = database.getCoverageData(visit_date);

                // uploading coverage

                SAXParserFactory saxPF = SAXParserFactory.newInstance();
                SAXParser saxP = saxPF.newSAXParser();
                XMLReader xmlR = saxP.getXMLReader();

                for (int i = 0; i < coverageBeanlist.size(); i++) {
                    String remark, devId;
                    boolean uploadflag = false;
//					storestatus = database.getStoreStatus(coverageBeanlist.get(
//							i).getStoreId());

                    if (coverageBeanlist.get(i).getStatus()
                            .equalsIgnoreCase("C") || coverageBeanlist.get(i).getStatus()
                            .equalsIgnoreCase("P") || coverageBeanlist.get(i).getStatus()
                            .equalsIgnoreCase("L")) {

                        String in_time = coverageBeanlist.get(i).getInTime();

                        if (in_time == null) {
                            in_time = "00:00:00";
                        }

                        String out_time = coverageBeanlist.get(i).getOutTime();
                        if (out_time == null) {
                            out_time = in_time;
                        } else if (out_time.equals("null")) {
                            out_time = in_time;
                        }

                        String onXML = "[DATA][USER_DATA][STORE_CD]"
                                + coverageBeanlist.get(i).getStoreId()
                                + "[/STORE_CD]" + "[VISIT_DATE]"
                                + coverageBeanlist.get(i).getVisitDate()
                                + "[/VISIT_DATE][LATITUDE]"
                                + coverageBeanlist.get(i).getLatitude()
                                + "[/LATITUDE][APP_VERSION]"
                                + app_ver
                                + "[/APP_VERSION][LONGITUDE]"
                                + coverageBeanlist.get(i).getLongitude()
                                + "[/LONGITUDE][IN_TIME]"
                                + in_time
                                + "[/IN_TIME][OUT_TIME]"
                                + out_time
                                + "[/OUT_TIME][UPLOAD_STATUS]"
                                + "N"
                                + "[/UPLOAD_STATUS][USER_ID]" + username
                                + "[/USER_ID]"
                                +
                                "[IMAGE_URL]" + coverageBeanlist.get(i).getIntime_Image()
                                + "[/IMAGE_URL]"
                                +
                                "[IMAGE_URL1]" + ""
                                + "[/IMAGE_URL1]"
                                +
                                "[REASON_ID]"
                                + coverageBeanlist.get(i).getReasonid()
                                + "[/REASON_ID][REASON_REMARK]"
                                + ""
                                + "[/REASON_REMARK][/USER_DATA][/DATA]";


                        request = new SoapObject(
                                CommonString.NAMESPACE,
                                CommonString.METHOD_UPLOAD_DR_STORE_COVERAGE);
                        request.addProperty("onXML", onXML);

                        envelope = new SoapSerializationEnvelope(
                                SoapEnvelope.VER11);
                        envelope.dotNet = true;
                        envelope.setOutputSoapObject(request);

                        androidHttpTransport = new HttpTransportSE(
                                CommonString.URL,timeout);

                        androidHttpTransport.call(
                                CommonString.SOAP_ACTION + CommonString.METHOD_UPLOAD_DR_STORE_COVERAGE,
                                envelope);

                        Object result = (Object) envelope.getResponse();


                        datacheck = result.toString();
                        words = datacheck.split("\\;");
                        validity = (words[0]);

                        if (validity
                                .equalsIgnoreCase(CommonString.KEY_SUCCESS)) {

                            uploadflag = true;
                            database.open();
                            database.updateCoverageStatus(coverageBeanlist.get(i).getStoreId(), CommonString.KEY_P);
//
//								database.updateStoreStatusOnLeave(
//										coverageBeanlist.get(i).getStoreId(),
//										visit_date, CommonString.KEY_P);

                            data.value = 10;
                            data.name = "Coverage Data Uploading";
                        } else {

                            return CommonString.METHOD_UPLOAD_DR_STORE_COVERAGE;

                        }
                        mid = Integer.parseInt((words[1]));
                        publishProgress(data);


                        // store data is upload
                        String storedata = "";
                        database.open();
                        store_detail = database.getDeploymentData(coverageBeanlist.get(i).getStoreId());
                        if (store_detail.size() > 0) {

                            for (int j = 0; j < store_detail.size(); j++) {

                                onXML = "[USER_DATA][STORE_ID]"
                                        + coverageBeanlist.get(i)
                                        .getStoreId()
                                        + "[/STORE_ID]"
                                        + "[CREATED_BY]"
                                        + username
                                        + "[/CREATED_BY]"
                                        + "[REASON_CD]"
                                        + store_detail.get(j).getReasonCd()
                                        + "[/REASON_CD]"
                                        + "[BILL_CUT]"
                                        + store_detail.get(j).getBillCutid()
                                        + "[/BILL_CUT]"
                                        + "[BILL_CUT_IMAGE]"
                                        + store_detail.get(j).getReceiptImg()
                                        + "[/BILL_CUT_IMAGE][HANGAR_ALLOWED]"
                                        + store_detail.get(0).getHangerId()
                                        + "[/HANGAR_ALLOWED]"
                                        + "[HANGAR_IMAGE]"
                                        + store_detail.get(0).getHangerImg()
                                        + "[/HANGAR_IMAGE][MID]" + mid
                                        + "[/MID][STATUS]" + "U" + "[/STATUS]"
                                        + "[/USER_DATA]";
                                storedata = storedata + onXML;
                            }


                            String totalStoreData = "[DATA]" + storedata + "[/DATA]";
                            request = new SoapObject(
                                    CommonString.NAMESPACE,
                                    CommonString.METHOD_GENERIC_UPLOAD);

                            request.addProperty("MID", mid);
                            request.addProperty("KEYS", "DEPLOYMENT_STATUS_NEW");
                            request.addProperty("XMLDATA", totalStoreData);
                            request.addProperty("USERNAME", username);


                            envelope = new SoapSerializationEnvelope(
                                    SoapEnvelope.VER11);
                            envelope.dotNet = true;
                            envelope.setOutputSoapObject(request);

                            androidHttpTransport = new HttpTransportSE(
                                    CommonString.URL, timeout);

                            androidHttpTransport
                                    .call(CommonString.SOAP_ACTION_METHOD_GENERIC_UPLOAD,
                                            envelope);
                            result = (Object) envelope.getResponse();


                            if (result.toString().equalsIgnoreCase(
                                    CommonString.KEY_SUCCESS)) {

                                uploadflag = true;
                                data.value = 20;
                                data.name = "Deployment Data Uploading";

                            } else {
                                return CommonString.METHOD_GENERIC_UPLOAD;
                            }
                            publishProgress(data);
                        }
                    }

                    if (coverageBeanlist.get(i).getIntime_Image() != null && !coverageBeanlist.get(i).getIntime_Image().equals("")) {

                        if (new File(CommonString.FILE_PATH + coverageBeanlist.get(i).getIntime_Image()).exists()) {
                            Object result = UploadImage(coverageBeanlist.get(i).getIntime_Image(), "StoreImages");

                            if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                return "StoreImages";
                            } else if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                                return "StoreImages";
                            } else if (result.toString().equalsIgnoreCase(CommonString.KEY_FAILURE)) {

                                return "StoreImages"
                                        + "," + "Failure";
                            }
                            runOnUiThread(new Runnable() {

                                public void run() {

                                    message.setText("store Image Uploaded");
                                }
                            });

                        }
                    }

                    for (int j = 0; j < store_detail.size(); j++) {
                        //region bill cut image
                        if (store_detail.get(j).getReceiptImg() != null && !store_detail.get(j).getReceiptImg().equals("")) {

                            if (new File(CommonString.FILE_PATH + store_detail.get(j).getReceiptImg()).exists()) {
                                Object result = UploadImage(store_detail.get(j).getReceiptImg(), "billcutImages");

                                if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                    return "Receipt images";
                                } else if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                                    return "Receipt images";
                                } else if (result.toString().equalsIgnoreCase(CommonString.KEY_FAILURE)) {

                                    return "Receipt images"
                                            + "," + "Failure";
                                }
                                runOnUiThread(new Runnable() {

                                    public void run() {

                                        message.setText("Billcut Image Uploaded");
                                    }
                                });

                            }
                        }
                        //endregion

                        //region hangar image
                        if (store_detail.get(j).getHangerImg() != null && !store_detail.get(j).getHangerImg().equals("")) {

                            if (new File(CommonString.FILE_PATH + store_detail.get(j).getHangerImg()).exists()) {
                                Object result = UploadImage(store_detail.get(j).getHangerImg(), "hangerImages");

                                if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                    return "Hangar images";
                                } else if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                                    return "Hangar images";
                                } else if (result.toString().equalsIgnoreCase(CommonString.KEY_FAILURE)) {

                                    return "Hangar images"
                                            + "," + "Failure";
                                }
                                runOnUiThread(new Runnable() {

                                    public void run() {

                                        message.setText("Hangar Image Uploaded");
                                    }
                                });

                            }
                        }                    //endregion
                    }

                    if (uploadflag) {
                        database.open();
                        database.updateCoverageStatus(coverageBeanlist.get(i).getStoreId(), CommonString.KEY_U);
                    }

                }

                data.value = 100;
                data.name = "Finishing";
                publishProgress(data);

                return CommonString.KEY_SUCCESS;

            } catch (SocketTimeoutException ex) {
                ex.printStackTrace();
                return CommonString.MESSAGE_INTERNET_NOT_AVAILABLE;
            } catch (SocketException ex) {
                ex.printStackTrace();
                return CommonString.MESSAGE_INTERNET_NOT_AVAILABLE;
            } catch (IOException ex) {
                ex.printStackTrace();
                return CommonString.MESSAGE_INTERNET_NOT_AVAILABLE;
            } catch (Exception ex) {

                ex.printStackTrace();
                return CommonString.MESSAGE_EXCEPTION;
            }

        }

        @Override
        protected void onProgressUpdate(Data... values) {
            // TODO Auto-generated method stub

            pb.setProgress(values[0].value);
            percentage.setText(values[0].value + "%");
            message.setText(values[0].name);

        }
    }

    class Data {
        int value;
        String name;
    }

    public String UploadImage(String path, String folder_name) throws Exception {

        String errormsg = "";
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(CommonString.FILE_PATH + path, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 1639;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;

        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        Bitmap bitmap = BitmapFactory.decodeFile(
                CommonString.FILE_PATH + path, o2);

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        String ba1 = Base64.encodeBytes(ba);

        SoapObject request = new SoapObject(CommonString.NAMESPACE,
                CommonString.METHOD_UPLOAD_IMAGE);

        String[] split = path.split("/");
        String path1 = split[split.length - 1];

        request.addProperty("img", ba1);
        request.addProperty("name", path1);
        request.addProperty("FolderName", folder_name);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                CommonString.URL, 20000);

        androidHttpTransport
                .call(CommonString.SOAP_ACTION_UPLOAD_IMAGE,
                        envelope);
        Object result = (Object) envelope.getResponse();

        if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {

            if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                return CommonString.KEY_FALSE;
            }

            SAXParserFactory saxPF = SAXParserFactory.newInstance();
            SAXParser saxP = saxPF.newSAXParser();
            XMLReader xmlR = saxP.getXMLReader();

            // for failure
            FailureXMLHandler failureXMLHandler = new FailureXMLHandler();
            xmlR.setContentHandler(failureXMLHandler);

            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(result.toString()));
            xmlR.parse(is);

            failureGetterSetter = failureXMLHandler
                    .getFailureGetterSetter();

            if (failureGetterSetter.getStatus().equalsIgnoreCase(
                    CommonString.KEY_FAILURE)) {
                errormsg = failureGetterSetter.getErrorMsg();
                return CommonString.KEY_FAILURE;
            }
        } else {
            new File(CommonString.FILE_PATH + path).delete();

        }

        return result.toString();
    }
}
