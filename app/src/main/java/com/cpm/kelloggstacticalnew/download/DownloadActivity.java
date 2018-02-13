package com.cpm.kelloggstacticalnew.download;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cpm.kelloggstacticalnew.R;
import com.cpm.kelloggstacticalnew.bean.TableBean;
import com.cpm.kelloggstacticalnew.constant.AlertandMessages;
import com.cpm.kelloggstacticalnew.constant.CommonString;
import com.cpm.kelloggstacticalnew.database.KelloggsTacticalDB;
import com.cpm.kelloggstacticalnew.getterSetter.DistributerListGetterSetter;
import com.cpm.kelloggstacticalnew.getterSetter.JCPMasterGetterSetter;
import com.cpm.kelloggstacticalnew.getterSetter.NonDeploymentReasonGetterSetter;
import com.cpm.kelloggstacticalnew.getterSetter.NonWorkingReasonGetterSetter;
import com.cpm.kelloggstacticalnew.xmlHandler.XMLHandlers;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;


public class DownloadActivity extends AppCompatActivity {

    KelloggsTacticalDB db;
    SharedPreferences.Editor editor;
    SharedPreferences preferences;
    Context context;
    int downloadindex = 0;
    String userId, date;
    private Dialog dialog;
    private ProgressBar pb;
    private Data data;
    int eventType;
    int timeout = 20000;
    private TextView percentage, message;
    DistributerListGetterSetter distributerListGetterSetter;
    NonWorkingReasonGetterSetter nonWorkingReasonGetterSetter;
    NonDeploymentReasonGetterSetter nonDeploymentReasonGetterSetter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        declaration();
        new BackgroundTask().execute();
    }


    private class BackgroundTask extends AsyncTask<Void, Data, String> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom);
            //dialog.setTitle("Download Files");
            dialog.setCancelable(false);
            dialog.show();

            pb = (ProgressBar) dialog.findViewById(R.id.progressBar1);
            percentage = (TextView) dialog.findViewById(R.id.percentage);
            message = (TextView) dialog.findViewById(R.id.message);
        }

        @Override
        protected String doInBackground(Void... params) {
            String resultHttp = "";
            try {
                data = new Data();

                // data
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();
                SoapObject request;
                SoapSerializationEnvelope envelope;
                HttpTransportSE androidHttpTransport;

                // JOURNEY_PLAN data
                request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);
                request.addProperty("UserName", userId);
                request.addProperty("Type", "DISTRIBUTOR_LIST");

                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                androidHttpTransport = new HttpTransportSE(CommonString.URL, timeout);
                androidHttpTransport.call(CommonString.SOAP_ACTION_UNIVERSAL, envelope);

                Object resultJcp = (Object) envelope.getResponse();

                if (resultJcp.toString() != null) {
                    xpp.setInput(new StringReader(resultJcp.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();

                    distributerListGetterSetter = XMLHandlers.DistributerListXMLHandler(xpp, eventType);

                    String distributerListMasterTable = distributerListGetterSetter.getTable_DistributerList();
                    TableBean.setTable_DistributerList(distributerListMasterTable);

                    if (distributerListGetterSetter.getKEYACCOUNT_CD().size() > 0) {
                        resultHttp = CommonString.KEY_SUCCESS;
                    } else {
                        return "Distributor List";
                    }
                    data.value = 5;
                    data.name = "Distributor List Downloading";
                }
                publishProgress(data);


                //region Description
                request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);
                request.addProperty("UserName", userId);
                request.addProperty("Type", "NON_WORKING_REASON");

                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                androidHttpTransport = new HttpTransportSE(CommonString.URL, timeout);
                androidHttpTransport.call(CommonString.SOAP_ACTION_UNIVERSAL, envelope);

                Object result = (Object) envelope.getResponse();

                if (result.toString() != null) {
                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();

                    nonWorkingReasonGetterSetter = XMLHandlers.nonWorkinReasonXML(xpp, eventType);

                    if (nonWorkingReasonGetterSetter.getReason_cd().size() > 0) {
                        resultHttp = CommonString.KEY_SUCCESS;
                        String visitorLoginTable = nonWorkingReasonGetterSetter.getNonworking_table();
                        TableBean.setTable_NON_WORKING_REASON(visitorLoginTable);
                    } else {
                        return "NON_WORKING_REASON Data";
                    }

                    data.value = 20;
                    data.name = "NON WORKING REASON Data";
                }
                publishProgress(data);
                //endregion

                //region Description
                request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);
                request.addProperty("UserName", userId);
                request.addProperty("Type", "NON_DEPLOYMENT_REASON");

                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                androidHttpTransport = new HttpTransportSE(CommonString.URL, timeout);
                androidHttpTransport.call(CommonString.SOAP_ACTION_UNIVERSAL, envelope);

                Object resultnonDeploymentReason = (Object) envelope.getResponse();

                if (resultnonDeploymentReason.toString() != null) {
                    xpp.setInput(new StringReader(resultnonDeploymentReason.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();

                    nonDeploymentReasonGetterSetter = XMLHandlers.nonDeploymentReasonXML(xpp, eventType);

                    if (nonDeploymentReasonGetterSetter.getNDREASON_CD().size() > 0) {
                        resultHttp = CommonString.KEY_SUCCESS;
                        String nondeploymentTable = nonDeploymentReasonGetterSetter.getTable_NON_DEPLOYMENT_REASON();
                        TableBean.setTable_NON_DEPLOYMENT_REASON(nondeploymentTable);
                    } else {
                        return "NON_DEPLOYMENT_REASON Data";
                    }
                    data.value = 20;
                    data.name = "NON_DEPLOYMENT_REASON REASON Data";
                }
                publishProgress(data);
                //endregion


                db = new KelloggsTacticalDB(context);
                db.open();

                if (distributerListGetterSetter.getKEYACCOUNT_CD().size() > 0) {
                    db.insertDistributorListData(distributerListGetterSetter);
                }
                if (nonWorkingReasonGetterSetter.getReason_cd().size() > 0) {
                    db.insertNonWorkingData(nonWorkingReasonGetterSetter);
                }
                if (nonDeploymentReasonGetterSetter.getNDREASON_CD().size() > 0) {
                    db.insertNonDeploymentData(nonDeploymentReasonGetterSetter);
                }


                editor = preferences.edit();
                editor.putBoolean(CommonString.KEY_ISDATADOWNLOADED, true);
                editor.commit();

                data.value = 100;
                data.name = "Finishing";
                publishProgress(data);

                return resultHttp;
            } catch (MalformedURLException e) {
                return CommonString.MESSAGE_EXCEPTION;
            } catch (IOException e) {
                return CommonString.MESSAGE_SOCKETEXCEPTION;
            } catch (Exception e) {
                return CommonString.MESSAGE_EXCEPTION;
            }
        }

        @Override
        protected void onProgressUpdate(Data... values) {
            pb.setProgress(values[0].value);
            percentage.setText(values[0].value + "%");
            message.setText(values[0].name);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if (result.equals(CommonString.KEY_SUCCESS)) {
                AlertandMessages.showAlert((Activity) context, CommonString.MESSAGE_DOWNLOAD, true);
            } else {
                AlertandMessages.showAlert((Activity) context, CommonString.MESSAGE_JCP_FALSE + " " + result, true);
            }
        }
    }

    class Data {
        int value;
        String name;
    }

    void declaration() {
        context = this;
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        userId = preferences.getString(CommonString.KEY_USERNAME, null);
        date = preferences.getString(CommonString.KEY_DATE, "");
        downloadindex = preferences.getInt(CommonString.KEY_DOWNLOAD_INDEX, 0);
        getSupportActionBar().setTitle("Download - " + date);

    }

}
