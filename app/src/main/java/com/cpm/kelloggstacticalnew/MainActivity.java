package com.cpm.kelloggstacticalnew;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.cpm.kelloggstacticalnew.constant.AlertandMessages;
import com.cpm.kelloggstacticalnew.constant.CommonFunctions;
import com.cpm.kelloggstacticalnew.constant.CommonString;
import com.cpm.kelloggstacticalnew.dailyEntry.StoreListActivity;
import com.cpm.kelloggstacticalnew.database.KelloggsTacticalDB;
import com.cpm.kelloggstacticalnew.download.DownloadActivity;
import com.cpm.kelloggstacticalnew.getterSetter.CoverageBean;
import com.cpm.kelloggstacticalnew.upload.UploadDataActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Context context;
    KelloggsTacticalDB db;
    SharedPreferences preferences;
    String visit_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        db = new KelloggsTacticalDB(context);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        visit_date = preferences.getString(CommonString.KEY_DATE, "");
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        db.open();
        if (id == R.id.nav_dailyentry) {

            if (db.getDistributorData().size() > 0) {
                startActivity(new Intent(context, StoreListActivity.class));
                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
            } else {
                AlertandMessages.showSnackbarMsg(context, "Please Download Data");
            }

        } else if (id == R.id.nav_download) {
            if (CommonFunctions.checkNetIsAvailable(context)) {
                Intent startDownload = new Intent(context, DownloadActivity.class);
                startActivity(startDownload);
            } else {
                AlertandMessages.showSnackbarMsg(context, CommonString.MESSAGE_INTERNET_NOT_AVAILABLE);
            }
        } else if (id == R.id.nav_upload) {

            if (db.getDistributorData().size() > 0) {
                ArrayList<CoverageBean> coverage = db.getCoverageData(visit_date);
                if (coverage.size() > 0) {
                    boolean flag_checkin = false;
                    for (int i = 0; i < coverage.size(); i++) {
                        if (coverage.get(i).getStatus().equalsIgnoreCase(CommonString.KEY_CHECK_IN)) {
                            flag_checkin = true;
                            break;
                        }
                    }

                    if (flag_checkin) {
                        AlertandMessages.showSnackbarMsg(context, "Please checkout from current store");
                    } else {

                        boolean flag_valid = false;
                        for (int i = 0; i < coverage.size(); i++) {
                            if (coverage.get(i).getStatus().equalsIgnoreCase(CommonString.KEY_C)
                                    || coverage.get(i).getStatus().equalsIgnoreCase(CommonString.KEY_L)
                                    || coverage.get(i).getStatus().equalsIgnoreCase(CommonString.KEY_P)) {
                                flag_valid = true;
                                break;
                            }
                        }

                        if (flag_valid) {
                            Intent startDownload = new Intent(context, UploadDataActivity.class);
                            startActivity(startDownload);
                        } else {
                            AlertandMessages.showSnackbarMsg(context, "No data for upload");
                        }

                    }

                } else {
                    AlertandMessages.showSnackbarMsg(context, "No Data for Upload");
                }

            } else {
                AlertandMessages.showSnackbarMsg(context, "Please Download Data");
            }

        } else if (id == R.id.nav_exit) {
            Intent exitIntent = new Intent(context, LoginActivity.class);
            exitIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(exitIntent);
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            finish();
        } else if (id == R.id.nav_export) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
            builder1.setMessage("Are you sure you want to take the backup of your data")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @SuppressWarnings("resource")
                        public void onClick(DialogInterface dialog, int id) {
                            try {
                                File file = new File(Environment.getExternalStorageDirectory(), "kelloggsTactical_backup");
                                if (!file.isDirectory()) {
                                    file.mkdir();
                                }

                                File sd = Environment.getExternalStorageDirectory();
                                File data = Environment.getDataDirectory();

                                if (sd.canWrite()) {
                                    long date = System.currentTimeMillis();

                                    SimpleDateFormat sdf = new SimpleDateFormat("MMM/dd/yy");
                                    String dateString = sdf.format(date);

                                    String currentDBPath = "//data//com.cpm.kelloggstacticalnew//databases//" + KelloggsTacticalDB.DATABASE_NAME;
                                    String backupDBPath = "kelloggsTactical_backup" + dateString.replace('/', '-');

                                    String path = Environment.getExternalStorageDirectory().getPath();

                                    File currentDB = new File(data, currentDBPath);
                                    File backupDB = new File(path, backupDBPath);

                                    AlertandMessages.showSnackbarMsg(context, "Database Exported Successfully");

                                    if (currentDB.exists()) {
                                        @SuppressWarnings("resource")
                                        FileChannel src = new FileInputStream(currentDB).getChannel();
                                        FileChannel dst = new FileOutputStream(backupDB).getChannel();
                                        dst.transferFrom(src, 0, src.size());
                                        src.close();
                                        dst.close();
                                    }
                                }
                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                            }
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert1 = builder1.create();
            alert1.show();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
