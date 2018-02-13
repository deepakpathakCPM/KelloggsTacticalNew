package com.cpm.kelloggstacticalnew.dailyEntry;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.cpm.kelloggstacticalnew.R;
import com.cpm.kelloggstacticalnew.bean.TableBean;
import com.cpm.kelloggstacticalnew.constant.AlertandMessages;
import com.cpm.kelloggstacticalnew.constant.CommonString;
import com.cpm.kelloggstacticalnew.database.KelloggsTacticalDB;
import com.cpm.kelloggstacticalnew.download.DownloadActivity;
import com.cpm.kelloggstacticalnew.getterSetter.CoverageBean;
import com.cpm.kelloggstacticalnew.getterSetter.DistributerListGetterSetter;
import com.cpm.kelloggstacticalnew.getterSetter.JCPMasterGetterSetter;
import com.cpm.kelloggstacticalnew.getterSetter.SearchListGetterSetter;
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
import java.util.ArrayList;


public class StoreListActivity extends AppCompatActivity {
    Context context;
    RecyclerView recyclerView;
    Intent storeImageIntent;
    KelloggsTacticalDB db;
    private String date, username, distributerId, routename;
    SharedPreferences preferences;
    ArrayList<SearchListGetterSetter> storelist;
    ArrayList<CoverageBean> coverage;
    StoreListAdapter adapter;
    SearchListAdapter adapter2;
    LinearLayout storelist_ll, no_data_ll;
    FloatingActionButton fab;
    Spinner routeSpn;
    ArrayList<DistributerListGetterSetter> list;
    Button searchBtn;
    DistributerListGetterSetter distributerListGetterSetter;
    ArrayList<SearchListGetterSetter> searchList;
    SearchListGetterSetter searchGetSetOfIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_list);
        declaration();
        list = db.getDistributorData();
        //------------for state Master List---------------
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item);
        for (int i = 0; i < list.size(); i++) {
            arrayAdapter.add(list.get(i).getROUTE_NAME().get(0));
        }
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        routeSpn.setAdapter(arrayAdapter);
        //------------------------------------------------

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (routeSpn.getSelectedItemPosition() != 0) {
                    //if (true) {
                    new BackgroundTask().execute();
                } else {
                    AlertandMessages.showSnackbarMsg(v, "Please Select Route");
                }

            }
        });
        routeSpn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    distributerId = list.get(position).getKEYACCOUNT_CD().get(0);
                    routename = list.get(position).getROUTE_NAME().get(0);
                } else {
                    distributerId = "0";
                    routename = "";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        prepareList();
    }

    void prepareList() {
        storelist = db.getStoreData(date, username);
        coverage = db.getCoverageData(date);

        boolean flag_checkin = false;
        for (int i = 0; i < coverage.size(); i++) {
            if (coverage.get(i).getStatus().equalsIgnoreCase(CommonString.KEY_CHECK_IN)) {
                flag_checkin = true;
                break;
            }
        }
        routeSpn.setSelection(0);
        if (flag_checkin) {
            routeSpn.setEnabled(false);
            searchBtn.setEnabled(false);
        } else {
            routeSpn.setEnabled(true);
            searchBtn.setEnabled(true);
        }
        if (storelist.size() > 0) {
            adapter = new StoreListAdapter(context, storelist);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            storelist_ll.setVisibility(View.VISIBLE);
            no_data_ll.setVisibility(View.GONE);
            fab.setVisibility(View.GONE);

        } else {
            no_data_ll.setVisibility(View.GONE);
            fab.setVisibility(View.GONE);
            storelist_ll.setVisibility(View.GONE);
        }
    }

    class StoreListAdapter extends RecyclerView.Adapter<MyViewHolder> {

        LayoutInflater inflater;
        ArrayList<SearchListGetterSetter> list;

        StoreListAdapter(Context context, ArrayList<SearchListGetterSetter> list) {
            this.list = list;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.item_add_store_list_item_view, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            final SearchListGetterSetter jcpMasterGetterSetter = list.get(position);
         /*   int storeCd = 0;
            if (coverage.size() > 0) {
                for (int i = 0; i < coverage.size(); i++) {
                    if (Integer.parseInt(jcpMasterGetterSetter.getSTORE_CD()) == Integer.parseInt(coverage.get(i).getStoreId())) {
                        storeCd = Integer.parseInt(coverage.get(i).getStoreId());
                        break;
                    }
                }
            }*/

            ArrayList<CoverageBean> coverage = db.getCoverageSpecificData(date, jcpMasterGetterSetter.getSTORE_CD());
            if (coverage.size() > 0) {
                if (coverage.get(0).getStatus().equalsIgnoreCase("U")) {
                    holder.img_storeImage.setImageDrawable(getResources().getDrawable(R.drawable.ticku));
                    holder.img_storeImage.setVisibility(View.VISIBLE);
                } else if (coverage.get(0).getStatus().equalsIgnoreCase(CommonString.KEY_C)) {
                    holder.img_storeImage.setImageDrawable(getResources().getDrawable(R.drawable.tick_c));
                    holder.img_storeImage.setVisibility(View.VISIBLE);
                } else if (coverage.get(0).getStatus().equalsIgnoreCase(CommonString.KEY_L)) {
                    holder.img_storeImage.setImageDrawable(getResources().getDrawable(R.drawable.tickl));
                    holder.img_storeImage.setVisibility(View.VISIBLE);
                } else if (coverage.get(0).getStatus().equalsIgnoreCase("I")) {
                    holder.img_storeImage.setImageDrawable(getResources().getDrawable(R.drawable.checkin_ico));
                    holder.img_storeImage.setVisibility(View.VISIBLE);
                } else {
                    holder.img_storeImage.setVisibility(View.INVISIBLE);
                }
            } else {
                holder.img_storeImage.setVisibility(View.INVISIBLE);
            }

           /* if (db.getCoverageStatus(storeCd).getStatus().equalsIgnoreCase(CommonString.KEY_CHECK_IN)) {
                holder.checkoutbtn.setVisibility(View.VISIBLE);
                // holder.img_storeImage.setVisibility(View.INVISIBLE);
                holder.img_storeImage.setImageDrawable(getResources().getDrawable(R.drawable.checkin_ico));
                holder.img_storeImage.setVisibility(View.VISIBLE);
            } else {
                holder.checkoutbtn.setVisibility(View.GONE);
            }*/


            //holder.textView.setText(jcpMasterGetterSetter.getSTORENAME());
            holder.textView.setText(jcpMasterGetterSetter.getSTORE_CD() + "\n" + jcpMasterGetterSetter.getSTORENAME() + "\n" + jcpMasterGetterSetter.getADDRESS());

           /* holder.checkoutbtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    try {
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage("Are you sure you want to Checkout")
                                    .setCancelable(false)
                                    .setPositiveButton("OK",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface dialog, int id) {
                                                    //Intent i = new Intent(context, StoreImageActivity.class);
                                                    //i.putExtra(CommonString.TAG_FOR, CommonString.KEY_OUT_TIME);
                                                    //i.putExtra(CommonString.TAG_OBJECT, jcpMasterGetterSetter);
                                                    //startActivity(i);

                                                }
                                            })
                                    .setNegativeButton("Cancel",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            });
                            AlertDialog alert = builder.create();
                            alert.show();

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("error", e.toString());
                    }

                }
            });*/


            holder.storelist_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int store_id = Integer.parseInt(jcpMasterGetterSetter.getSTORE_CD());
                    //ArrayList<CoverageBean> coverage = db.getCoverageSpecificData(date, jcpMasterGetterSetter.getSTORE_CD());
                    //String username = (jcpMasterGetterSetter.getUSERNAME().get(0));
                    String status = db.getCoverageStatus(store_id).getStatus();

                    //JCPMasterGetterSetter jcpgetset = db.getJCPStatus(store_id, username);

                    if (status != null && status.equalsIgnoreCase(CommonString.KEY_U)) {
                        AlertandMessages.showSnackbarMsg(context, getResources().getString(R.string.title_store_list_activity_store_already_done));
                    } else if (status.equalsIgnoreCase(CommonString.KEY_C)) {
                        AlertandMessages.showSnackbarMsg(context, getResources().getString(R.string.title_store_list_activity_store_already_checkout));
                    } else if (status.equalsIgnoreCase(CommonString.KEY_P)) {
                        AlertandMessages.showSnackbarMsg(context, getResources().getString(R.string.title_store_list_activity_store_again_uploaded));
                    } else if (status.equalsIgnoreCase(CommonString.KEY_L)) {
                       /* boolean isVisitlater = false;
                        for (int i = 0; i < coverage.size(); i++) {
                            if (store_id == Integer.parseInt(coverage.get(i).getStoreId())) {
                                if (coverage.get(i).getReasonid().equalsIgnoreCase("11")
                                        || coverage.get(i).getReason().equalsIgnoreCase("Visit Later")) {
                                    isVisitlater = true;
                                    break;
                                }
                            }
                        }
                        if (isVisitlater) {

                            boolean entry_flag = true;
                            for (int j = 0; j < storelist.size(); j++) {
                                if (status.equalsIgnoreCase(CommonString.KEY_CHECK_IN)) {

                                    if (store_id != Integer.parseInt(storelist.get(j).getSTORE_CD().get(0))) {
                                        entry_flag = false;
                                        break;

                                    } else {
                                        break;
                                    }
                                }
                            }

                            if (entry_flag) {
                                // showMyDialog(jcpMasterGetterSetter);
                            } else {
                                AlertandMessages.showSnackbarMsg(context, getResources().getString(R.string.title_store_list_checkout_current));
                            }
                        } else {*/
                        AlertandMessages.showSnackbarMsg(context, getResources().getString(R.string.title_store_list_activity_already_store_closed));

                        //}

                    } else if (status.equalsIgnoreCase(CommonString.KEY_CHECK_IN)) {
                        Intent storeIntent = new Intent(context, DeploymentStatusActivity.class);
                        startActivity(storeIntent.putExtra(CommonString.TAG_FOR, CommonString.KEY_IN_TIME).putExtra(CommonString.TAG_OBJECT, jcpMasterGetterSetter));
                        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                        //showMyDialog(jcpMasterGetterSetter);

                    /*    boolean entry_flag = true;
                        for (int j = 0; j < storelist.size(); j++) {
                            String status2 = db.getCoverageStatus(Integer.parseInt(storelist.get(j).getSTORE_CD().get(0))).getStatus();
                            if (status2.equalsIgnoreCase(CommonString.KEY_CHECK_IN)) {
                                if (store_id != Integer.parseInt(storelist.get(j).getSTORE_CD().get(0))) {
                                    entry_flag = false;
                                    break;

                                } else {
                                    break;
                                }
                            }
                        }

                        if (entry_flag) {
                            showMyDialog(jcpMasterGetterSetter);
                        } else {
                            AlertandMessages.showSnackbarMsg(context, getResources().getString(R.string.title_store_list_checkout_current));

                        }*/
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    void showMyDialog(final SearchListGetterSetter as) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogbox);
        RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.radiogrpvisit);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if (checkedId == R.id.yes) {
                    dialog.cancel();
                    startActivity(storeImageIntent.putExtra(CommonString.TAG_FOR, CommonString.KEY_IN_TIME).putExtra(CommonString.TAG_OBJECT, as));
                    overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

                } else if (checkedId == R.id.no) {
                    dialog.cancel();
                    int coverage_id = 0;
                    String checkout_status = "";
                    if (coverage.size() > 0) {
                        for (int i = 0; i < coverage.size(); i++) {

                            if (as.getSTORE_CD().equals(coverage.get(i).getStoreId())) {
                                checkout_status = coverage.get(i).getStatus();
                                break;
                            }
                        }
                    }
                    final int finalCoverage_id = coverage_id;
                    if (checkout_status.equals(CommonString.KEY_INVALID) || checkout_status.equals(CommonString.KEY_VALID)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(StoreListActivity.this);
                        builder.setMessage(CommonString.DATA_DELETE_ALERT_MESSAGE)
                                .setCancelable(false)
                                .setPositiveButton("Yes",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int id) {
                                                db.deleteSpecificTables(String.valueOf(finalCoverage_id));
                                                Intent in = new Intent(StoreListActivity.this, NonWorkingActivity.class)
                                                        .putExtra(CommonString.TAG_OBJECT, as);
                                                startActivity(in);
                                            }
                                        })
                                .setNegativeButton("No",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int id) {

                                                dialog.cancel();
                                            }
                                        });
                        AlertDialog alert = builder.create();

                        alert.show();
                    } else {
                        db.open();
                        db.deleteSpecificTables(String.valueOf(finalCoverage_id));
                        Intent in = new Intent(StoreListActivity.this, NonWorkingActivity.class)
                                .putExtra(CommonString.TAG_OBJECT, as);
                        startActivity(in);
                        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                    }
                    //finish();
                }
            }

        });

        dialog.show();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        LinearLayout storelist_ll;
        Button checkoutbtn;
        ImageView img_storeImage;


        MyViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.txt_storeName);
            checkoutbtn = view.findViewById(R.id.checkoutbtn);
            storelist_ll = view.findViewById(R.id.storelist_ll);
            img_storeImage = view.findViewById(R.id.img_storeImage);
        }
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

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }

    void declaration() {
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        routeSpn = (Spinner) findViewById(R.id.routeSpn);
        searchBtn = (Button) findViewById(R.id.searchBtn);
        storelist_ll = (LinearLayout) findViewById(R.id.storelist);
        no_data_ll = (LinearLayout) findViewById(R.id.no_data_lay);
        storeImageIntent = new Intent(context, StoreImageActivity.class);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        date = preferences.getString(CommonString.KEY_DATE, null);
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        db = new KelloggsTacticalDB(context);
        db.open();

        searchGetSetOfIntent = (SearchListGetterSetter) getIntent().getSerializableExtra(CommonString.TAG_OBJECT);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(getApplicationContext(), DownloadActivity.class);
                startActivity(in);
                finish();
            }
        });
    }

    private class BackgroundTask extends AsyncTask<Void, Data, String> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pd = new ProgressDialog(context);
            pd.setCancelable(false);
            pd.setMessage("Fetching stores");
            pd.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            String resultHttp = "";
            try {
                // data
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();
                SoapObject request;
                SoapSerializationEnvelope envelope;
                HttpTransportSE androidHttpTransport;

                // JOURNEY_PLAN data
                request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);
                String userid = username + ":" + routename;
                request.addProperty("UserName", userid);
                request.addProperty("Type", "STORE_SEARCH");

                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                androidHttpTransport = new HttpTransportSE(CommonString.URL);
                androidHttpTransport.call(CommonString.SOAP_ACTION_UNIVERSAL, envelope);

                Object resultJcp = (Object) envelope.getResponse();

                if (resultJcp.toString() != null) {
                    xpp.setInput(new StringReader(resultJcp.toString()));
                    xpp.next();
                    int eventType = xpp.getEventType();

                    distributerListGetterSetter = XMLHandlers.SearchListXMLHandler(xpp, eventType);

                    String searchListMasterTable = distributerListGetterSetter.getTable_DistributerList();
                    TableBean.setTable_SearchList(searchListMasterTable);

                    if (distributerListGetterSetter.getSTORE_CD().size() > 0) {
                        resultHttp = CommonString.KEY_SUCCESS;
                        searchList = new ArrayList<>();
                        searchList.clear();
                        for (int i = 0; i < distributerListGetterSetter.getSTORE_CD().size(); i++) {

                            SearchListGetterSetter searchGetSet = new SearchListGetterSetter();
                            searchGetSet.setSTORE_CD(distributerListGetterSetter.getSTORE_CD().get(i));
                            searchGetSet.setSTORENAME(distributerListGetterSetter.getSTORENAME().get(i));
                            searchGetSet.setADDRESS(distributerListGetterSetter.getADDRESS().get(i));
                            searchGetSet.setCITY(distributerListGetterSetter.getCITY().get(i));
                            searchGetSet.setSTORETYPE(distributerListGetterSetter.getSTORETYPE().get(i));
                            searchGetSet.setROUTE_NAME(distributerListGetterSetter.getROUTE_NAME().get(i));
                            searchGetSet.setVISIT_DATE(distributerListGetterSetter.getVISIT_DATE().get(i));
                            searchList.add(searchGetSet);
                        }

                    } else {
                        return "Search List";
                    }

                }
                db = new KelloggsTacticalDB(context);
                db.open();
                if (TableBean.getTable_SearchList() != null) {
                    db.createTable(TableBean.getTable_SearchList());
                }

                /*if (distributerListGetterSetter.getKEYACCOUNT_CD().size() > 0) {
                    db.insertDistributorListData(distributerListGetterSetter);
                }*/

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
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();
            if (result.equals(CommonString.KEY_SUCCESS)) {
                //  AlertandMessages.showAlert((Activity) context, CommonString.MESSAGE_DOWNLOAD, true);
                if (searchList.size() > 0) {
                    Dialog dialog = new Dialog(context);
                    dialog.setCancelable(true);
                    dialog.setContentView(R.layout.layout_search_store);
                    dialog.setTitle("Route - " + routename);
                    RecyclerView searchRecyclerView = dialog.findViewById(R.id.searchRecyclerView);
                    adapter2 = new SearchListAdapter(context, searchList);
                    searchRecyclerView.setAdapter(adapter2);
                    searchRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                    dialog.show();
                } else {

                }

            } else {
                AlertandMessages.showAlert((Activity) context, CommonString.MESSAGE_JCP_FALSE + " " + result, false);
            }
        }
    }

    class Data {
        int value;
        String name;
    }


    class SearchListAdapter extends RecyclerView.Adapter<MyViewHolder> {

        LayoutInflater inflater;
        ArrayList<SearchListGetterSetter> list;

        SearchListAdapter(Context context, ArrayList<SearchListGetterSetter> list) {
            this.list = list;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.item_add_store_list_item_view2, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            final SearchListGetterSetter distributorGetterSetter = list.get(position);
         /*   int storeCd = 0;
            if (coverage.size() > 0) {
                for (int i = 0; i < coverage.size(); i++) {
                    if (Integer.parseInt(distributorGetterSetter.getSTORE_CD()) == Integer.parseInt(coverage.get(i).getStoreId())) {
                        storeCd = Integer.parseInt(coverage.get(i).getStoreId());
                        break;
                    }
                }
            }*/

            holder.textView.setText(distributorGetterSetter.getSTORE_CD() + "\n" + distributorGetterSetter.getSTORENAME() + "\n" + distributorGetterSetter.getADDRESS());

            holder.storelist_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int store_id = Integer.parseInt(distributorGetterSetter.getSTORE_CD());
                    // String username = (distributorGetterSetter.getUSERNAME().get(0));
                    String status = db.getCoverageStatus(store_id).getStatus();

                    // JCPMasterGetterSetter jcpgetset = db.getJCPStatus(store_id, username);

                    /*if (jcpgetset != null && jcpgetset.getUPLOAD_STATUS().get(0).equalsIgnoreCase(CommonString.KEY_U)) {
                        AlertandMessages.showSnackbarMsg(context, getResources().getString(R.string.title_store_list_activity_store_already_done));
                    } else if (status.equalsIgnoreCase(CommonString.KEY_C)) {
                        AlertandMessages.showSnackbarMsg(context, getResources().getString(R.string.title_store_list_activity_store_already_checkout));
                    } else if (status.equalsIgnoreCase(CommonString.KEY_P)) {
                        AlertandMessages.showSnackbarMsg(context, getResources().getString(R.string.title_store_list_activity_store_again_uploaded));
                    }
                    else
                    if (status.equalsIgnoreCase(CommonString.KEY_L)) {
                        boolean isVisitlater = false;
                        for (int i = 0; i < coverage.size(); i++) {
                            if (store_id == Integer.parseInt(coverage.get(i).getStoreId())) {
                                if (coverage.get(i).getReasonid().equalsIgnoreCase("11")
                                        || coverage.get(i).getReason().equalsIgnoreCase("Visit Later")) {
                                    isVisitlater = true;
                                    break;
                                }
                            }
                        }
                        if (isVisitlater) {

                            boolean entry_flag = true;
                            for (int j = 0; j < storelist.size(); j++) {
                                if (status.equalsIgnoreCase(CommonString.KEY_CHECK_IN)) {

                                    if (store_id != Integer.parseInt(storelist.get(j).getSTORE_CD())) {
                                        entry_flag = false;
                                        break;

                                    } else {
                                        break;
                                    }
                                }
                            }

                            if (entry_flag) {
                                //showMyDialog(distributorGetterSetter);
                            } else {
                                AlertandMessages.showSnackbarMsg(context, getResources().getString(R.string.title_store_list_checkout_current));
                            }
                        } else {
                            AlertandMessages.showSnackbarMsg(context, getResources().getString(R.string.title_store_list_activity_already_store_closed));

                        }

                    } else {

                        boolean entry_flag = true;
                        for (int j = 0; j < storelist.size(); j++) {
                            String status2 = db.getCoverageStatus(Integer.parseInt(storelist.get(j).getSTORE_CD())).getStatus();
                            if (status2.equalsIgnoreCase(CommonString.KEY_CHECK_IN)) {
                                if (store_id != Integer.parseInt(storelist.get(j).getSTORE_CD())) {
                                    entry_flag = false;
                                    break;

                                } else {
                                    break;
                                }
                            }
                        }

                        // if (entry_flag) {

                    }*/

                    ArrayList<SearchListGetterSetter> list = db.getStoreData(date, username);
                    boolean isvalid = true;
                    if (list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).getSTORE_CD().equalsIgnoreCase(distributorGetterSetter.getSTORE_CD())) {
                                isvalid = false;
                                break;
                            }
                        }
                    }

                    if (isvalid) {
                        showMyDialog(distributorGetterSetter);
                    } else {
                        AlertandMessages.showSnackbarMsg(context, "Store Already done");

                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }


}
