package com.cpm.kelloggstacticalnew.upload;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.preference.PreferenceManager;
import android.util.Log;

import com.cpm.kelloggstacticalnew.constant.CommonString;
import com.cpm.kelloggstacticalnew.database.KelloggsTacticalDB;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

;

/**
 * Created by deepakp on 12/20/2017.
 */

public class UploadImageWithRetrofit {

    boolean isvalid;
    RequestBody body1;
    private Retrofit adapter;
    Context context;
    public static int uploadedFiles = 0;
    public int listSize = 0;
    int status = 0;
    KelloggsTacticalDB db;
    ProgressDialog pd;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String _UserId, date, app_ver;
    String[] jj;
    boolean statusUpdated = true;
    int from;

    public UploadImageWithRetrofit(Context context) {
        this.context = context;
    }

    public UploadImageWithRetrofit(Context context, KelloggsTacticalDB db, ProgressDialog pd, int from) {
        this.context = context;
        this.db = db;
        this.pd = pd;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
        this.from = from;
        _UserId = preferences.getString(CommonString.KEY_USERNAME, "");
        date = preferences.getString(CommonString.KEY_DATE, null);
        try {
            app_ver = String.valueOf(context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        db.open();
    }

/*
    public String UploadImage2(final String filename, String foldername, String folderPath) {
        try {
            status = 0;
            File originalFile = new File(folderPath + filename);
            final File finalFile = saveBitmapToFileSmaller(originalFile);
            isvalid = false;
            //RequestBody photo = RequestBody.create(MediaType.parse("application/octet-stream"), finalFile);
            RequestBody photo = RequestBody.create(MediaType.parse("multipart/form-data"), finalFile);
            MultipartBody.Part body =
                    MultipartBody.Part.createFormData(finalFile.getName(), finalFile.getName(), photo);
            RequestBody filename2 =
                    RequestBody.create(
                            MediaType.parse("multipart/form-data"), finalFile.getName());
            // add another part within the multipart request
            RequestBody foldername2 =
                    RequestBody.create(
                            MediaType.parse("multipart/form-data"), foldername);
*/
/*
            body1 = new MultipartBuilder()
                    .type(MultipartBuilder.FORM)
                    .addFormDataPart("file", finalFile.getName(), photo)
                    .addFormDataPart("FolderName", foldername)
                    .build();
*//*

           */
/* body1 = new MultipartBody.Builder().addPart(body)
                    .setType(MediaType.parse("multipart/form-data"))
                    .addFormDataPart("file", finalFile.getName(), photo)
                    .addFormDataPart("Foldername", foldername)
                    .build();*//*


            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            adapter = new Retrofit.Builder()
                    .baseUrl(CommonString.URL2)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
            PostApi api = adapter.create(PostApi.class);
            Call<String> call = api.getUploadImage(body, filename2, foldername2);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        finalFile.delete();
                        uploadedFiles++;
                        isvalid = true;
                        status = 1;
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    isvalid = true;
                    Toast.makeText(context, finalFile.getName() + " not uploaded", Toast.LENGTH_SHORT).show();
                    if (t instanceof SocketTimeoutException) {
                        status = 2;
                    } else if (t instanceof IOException) {
                        status = 3;
                    } else {
                        status = -1;
                    }

                }
            });

            while (isvalid == false) {
                synchronized (this) {
                    this.wait(25);
                }
            }
            if (isvalid) {
                synchronized (this) {
                    this.notify();
                }
            }
            if (status == 1) {
                return CommonString.KEY_SUCCESS;
            } else if (status == 2) {
                return CommonString.MESSAGE_SOCKETEXCEPTION;
            } else if (status == 3) {
                return CommonString.MESSAGE_SOCKETEXCEPTION;
            } else {
                return CommonString.KEY_FAILURE;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Error image", e.toString());
            return CommonString.KEY_FAILURE;
        }
    }
*/


    public String UploadImage2(String filename, String foldername, String path) {
        String image_valid = "";
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path + filename, o);

            // The new size we want to scale to
            final int REQUIRED_SIZE = 1024;

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
            Bitmap bitmap = BitmapFactory.decodeFile(path + filename, o2);

            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bao);
            byte[] ba = bao.toByteArray();
            String ba1 = Base64.encodeBytes(ba);

            SoapObject request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_NAME_IMAGE);
            //String[] split = path.split("/");
            request.addProperty("img", ba1);
            request.addProperty("name", filename);
            request.addProperty("FolderName", foldername);


            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE androidHttpTransport = new HttpTransportSE(
                    CommonString.URL);

            androidHttpTransport.call(CommonString.SOAP_ACTION + CommonString.METHOD_NAME_IMAGE, envelope);
            Object result = (Object) envelope.getResponse();
            System.out.println(result);

          /*  datacheck = result.toString();
            words = datacheck.split("\\;");
            validity = (words[0]);
*/
            if (result.toString().contains("Success")) {

                image_valid = "Success";
                new File(path + filename).delete();
            }
            if (result.toString().equals("Failure")) {

                image_valid = "Failure";

            }

        } catch (MalformedURLException e) {
            image_valid = "Failure";
            e.printStackTrace();
        } catch (IOException e) {
            image_valid = "Failure";
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println(e.getMessage());

        }
        return image_valid;
    }


    public File saveBitmapToFileSmaller(File file) {
        File file2 = file;
        try {
            int inWidth = 0;
            int inHeight = 0;

            InputStream in = new FileInputStream(file2);
            // decode image size (decode metadata only, not the whole image)
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, options);
            in.close();
            in = null;

            // save width and height
            inWidth = options.outWidth;
            inHeight = options.outHeight;

            // decode full image pre-resized
            in = new FileInputStream(file2);
            options = new BitmapFactory.Options();
            // calc rought re-size (this is no exact resize)
            options.inSampleSize = Math.max(inWidth / 800, inHeight / 500);
            // decode full image
            Bitmap roughBitmap = BitmapFactory.decodeStream(in, null, options);

            // calc exact destination size
            Matrix m = new Matrix();
            RectF inRect = new RectF(0, 0, roughBitmap.getWidth(), roughBitmap.getHeight());
            RectF outRect = new RectF(0, 0, 800, 500);
            m.setRectToRect(inRect, outRect, Matrix.ScaleToFit.CENTER);
            float[] values = new float[9];
            m.getValues(values);
            // resize bitmap
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(roughBitmap, (int) (roughBitmap.getWidth() * values[0]), (int) (roughBitmap.getHeight() * values[4]), true);
            // save image
            FileOutputStream out = new FileOutputStream(file2);
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);

        } catch (Exception e) {
            Log.e("Image", e.toString(), e);
            return file;
        }
        return file2;
    }


    //region uploadDataWithoutWait
/*
    public void uploadDataWithoutWait(final ArrayList<String> keyList, final int keyIndex, final ArrayList<CoverageBean> coverageList, final int coverageIndex) {

        try {
            status = 0;
            isvalid = false;
            final String[] data_global = {""};
            String jsonString = "";
            int type = 0;
            JSONObject jsonObject;

            //region Creating json data
            switch (keyList.get(keyIndex)) {
                case "Deviation_JourneyPlan":
                    if (coverageList.get(coverageIndex).getFrom().equalsIgnoreCase(CommonString.TAG_FROM_PJP)) {
                        //region Deviation_journeyplan Data
                        JSONObject jsonObjectDeviation = new JSONObject();
                        jsonObjectDeviation.put("UserId", _UserId);
                        jsonObjectDeviation.put("StoreId", coverageList.get(coverageIndex).getStoreId());
                        jsonObjectDeviation.put("VisitDate", coverageList.get(coverageIndex).getVisitDate());

                        jsonObject = new JSONObject();
                        jsonObject.put("MID", "0");
                        jsonObject.put("Keys", "Deviation_JourneyPlan");
                        jsonObject.put("JsonData", jsonObjectDeviation.toString());
                        jsonObject.put("UserId", _UserId);

                        jsonString = jsonObject.toString();
                        type = CommonString.UPLOADJsonDetail;
                        //endregion
                    }
                    break;
                case "CoverageDetail_latest":
                    JourneyPlan journeyPlan;
                    if (coverageList.get(coverageIndex).getFrom().equalsIgnoreCase(CommonString.TAG_FROM_JCP)) {
                        journeyPlan = db.getSpecificStoreData(coverageList.get(coverageIndex).getVisitDate(),
                                coverageList.get(coverageIndex).getStoreId());
                    } else {
                        journeyPlan = db.getSpecificStoreDataDeviation(coverageList.get(coverageIndex).getVisitDate(),
                                coverageList.get(coverageIndex).getStoreId());
                    }

                    //region Coverage Data
                    jsonObject = new JSONObject();
                    jsonObject.put("StoreId", coverageList.get(coverageIndex).getStoreId());
                    jsonObject.put("VisitDate", coverageList.get(coverageIndex).getVisitDate());
                    jsonObject.put("Latitude", coverageList.get(coverageIndex).getLatitude());
                    jsonObject.put("Longitude", coverageList.get(coverageIndex).getLongitude());
                    jsonObject.put("ReasonId", coverageList.get(coverageIndex).getReasonid());
                    jsonObject.put("SubReasonId", coverageList.get(coverageIndex).getSub_reasonId());
                    jsonObject.put("Remark", coverageList.get(coverageIndex).getRemark());
                    jsonObject.put("ImageName", coverageList.get(coverageIndex).getImage());
                    jsonObject.put("AppVersion", app_ver);
                    jsonObject.put("UploadStatus", CommonString.KEY_P);
                    jsonObject.put("UserId", _UserId);
                    jsonObject.put("Trade_Area_Id", journeyPlan.getTradeAreaId());
                    jsonObject.put("City_Id", journeyPlan.getCityId());
                    jsonObject.put("Distributor_Id", journeyPlan.getDistributorId());
                    jsonObject.put("Store_Type_Id", journeyPlan.getStoreTypeId());
                    jsonObject.put("Store_Category_Id", journeyPlan.getStoreCategoryId());
                    jsonObject.put("Classification_Id", journeyPlan.getClassificationId());
                    jsonObject.put("TSE_Id", 0);

                    jsonString = jsonObject.toString();
                    type = CommonString.COVERAGE_DETAIL;
                    //endregion
                    break;
                case "Store_detail_New":
                    //region StoreDetail
                    ArrayList<JourneyPlan> storeDetailList = db.getStoreDetailData(Integer.valueOf(coverageList.get(coverageIndex).getStoreId()));
                    if (storeDetailList.size() > 0) {
                        JSONArray storeDetail = new JSONArray();
                        jsonObject = new JSONObject();
                        jsonObject.put("ProfilePic", storeDetailList.get(0).getProfilePic());
                        jsonObject.put("StoreID", coverageList.get(coverageIndex).getStoreId());
                        jsonObject.put("VisitDate", coverageList.get(coverageIndex).getVisitDate());
                        jsonObject.put("ContactPerson", storeDetailList.get(0).getContactPerson().replace("\"", ""));
                        jsonObject.put("ContactNo", storeDetailList.get(0).getContactNo());
                        jsonObject.put("Address1", storeDetailList.get(0).getAddress1().replace("\"", ""));
                        jsonObject.put("Address2", storeDetailList.get(0).getAddress2().replace("\"", ""));
                        jsonObject.put("Landmark", storeDetailList.get(0).getLandmark().replace("\"", ""));
                        jsonObject.put("Pincode", storeDetailList.get(0).getPincode());
                        jsonObject.put("StoreLayout", storeDetailList.get(0).getStoreLayout());
                        jsonObject.put("StoreSize", storeDetailList.get(0).getStoreSize());
                        jsonObject.put("KycId", storeDetailList.get(0).getKycId());
                        jsonObject.put("GstNo", storeDetailList.get(0).getGstNo());
                        jsonObject.put("GstImage", storeDetailList.get(0).getGstImage());
                        jsonObject.put("PanNo", storeDetailList.get(0).getPanNo());
                        jsonObject.put("PanImage", storeDetailList.get(0).getPanImage());
                        storeDetail.put(jsonObject);

                        jsonObject = new JSONObject();
                        jsonObject.put("MID", coverageList.get(coverageIndex).getMID());
                        jsonObject.put("Keys", "Store_detail_New");
                        jsonObject.put("JsonData", storeDetail.toString());
                        jsonObject.put("UserId", _UserId);

                        jsonString = jsonObject.toString();
                        type = CommonString.UPLOADJsonDetail;

                    }
                    //endregion
                    break;
                case "SS_Primary_Bay":
                    //region primary bay data
                    ArrayList<PrimaryBayGetterSetter> primary_Bay_size = db.getPrimaryBayData2(coverageList.get(coverageIndex).getStoreId());
                    ArrayList<PrimaryBayMaster> primary_Bay_Image = db.getPrimaryBayImagesByStoreIDData(coverageList.get(coverageIndex).getStoreId());
                    if (primary_Bay_Image.size() > 0) {
                        JSONArray primaryArray = new JSONArray();
                        JSONObject primaryfinalObj = new JSONObject();
                        for (int j = 0; j < primary_Bay_size.size(); j++) {
                            ArrayList<SkuStoreBean> skulist_primarybay = db.ViewInsertedPrimaryBaySkuStock2(coverageList.get(coverageIndex).getStoreId(), String.valueOf(primary_Bay_size.get(j).getCategoryId()), primary_Bay_size.get(j).getCommonId());
                            JSONArray skuArray = new JSONArray();
                            if (skulist_primarybay.size() > 0) {
                                for (int k = 0; k < skulist_primarybay.size(); k++) {
                                    JSONObject obj = new JSONObject();
                                    obj.put("SKU_CD", skulist_primarybay.get(k).getSkuId());
                                    obj.put("COMMON_ID", skulist_primarybay.get(k).getCommomid());
                                    obj.put("FACEUP", skulist_primarybay.get(k).getFaceup());
                                    obj.put("STOCK_QTY", skulist_primarybay.get(k).getStock());
                                    obj.put("LAST_SIX", skulist_primarybay.get(k).getDom1());
                                    obj.put("SIX_TO_TWELVE", skulist_primarybay.get(k).getDom2());
                                    obj.put("MORE_THEN_TWELVE", skulist_primarybay.get(k).getDom3());
                                    skuArray.put(obj);
                                }
                            }

                            ArrayList<BrandGroupMaster> primary_Bay_range_size = db.getPrimaryBayRangeDataatUpload(coverageList.get(coverageIndex).getStoreId(), String.valueOf(primary_Bay_size.get(j).getCategoryId()), primary_Bay_size.get(j).getCommonId());
                            JSONArray rangeArray = new JSONArray();
                            if (primary_Bay_range_size.size() > 0) {
                                for (int l = 0; l < primary_Bay_range_size.size(); l++) {
                                    JSONObject obj = new JSONObject();
                                    obj.put("COMMON_ID", primary_Bay_range_size.get(l).getCommonId());
                                    obj.put("CATEGORY_CD", primary_Bay_range_size.get(l).getCategoryId());
                                    obj.put("BRAND_GROUP_CD", primary_Bay_range_size.get(l).getBrandGroupId());
                                    obj.put("BRAND_GROUP", primary_Bay_range_size.get(l).getBrandGroup());
                                    obj.put("FACING", primary_Bay_range_size.get(l).getQuantity());
                                    rangeArray.put(obj);
                                }
                            }

                            JSONObject primaryObj = new JSONObject();
                            primaryObj.put("MID", coverageList.get(coverageIndex).getMID());
                            primaryObj.put("UserId", _UserId);
                            primaryObj.put("COMMON_ID", primary_Bay_size.get(j).getCommonId());
                            primaryObj.put("CATEGORY_ID", primary_Bay_size.get(j).getCategoryId());
                            primaryObj.put("CATEGORY", primary_Bay_size.get(j).getCategory());
                            primaryObj.put("GSK", primary_Bay_size.get(j).getGsk());
                            primaryObj.put("BRAND_STOCK", rangeArray);
                            primaryObj.put("SKU_STOCK", skuArray);

                            primaryArray.put(primaryObj);
                        }


                        JSONArray imageArray = new JSONArray();
                        if (primary_Bay_Image.size() > 0) {
                            for (int l = 0; l < primary_Bay_Image.size(); l++) {
                                JSONObject obj = new JSONObject();
                                obj.put("MID", coverageList.get(coverageIndex).getMID());
                                obj.put("UserId", _UserId);
                                obj.put("CATEGORY_ID", primary_Bay_Image.get(l).getCategoryId());
                                obj.put("BAY_ID", primary_Bay_Image.get(l).getBayId());
                                obj.put("BAY_IMAGE", primary_Bay_Image.get(l).getImage());
                                imageArray.put(obj);
                            }
                        }

                        primaryfinalObj.put("SS_Primary", primaryArray);
                        primaryfinalObj.put("SS_Primary_Bay_image", imageArray);

                        jsonObject = new JSONObject();
                        jsonObject.put("MID", coverageList.get(coverageIndex).getMID());
                        jsonObject.put("Keys", "SS_Primary_Bay");
                        jsonObject.put("JsonData", primaryfinalObj.toString());
                        jsonObject.put("UserId", _UserId);

                        jsonString = jsonObject.toString();
                        type = CommonString.UPLOADJsonDetail;
                    }
                    //endregion
                    break;
                case "SS_Secondary_Display":
                    //region secondary display data
                    ArrayList<SecondaryDisplayGetterSetter> secondary_display_size = db.getSecondarydisplayData2(coverageList.get(coverageIndex).getStoreId());
                    if (secondary_display_size.size() > 0) {
                        JSONArray secArray = new JSONArray();
                        for (int j = 0; j < secondary_display_size.size(); j++) {
                            JSONObject obj = new JSONObject();
                            obj.put("MID", coverageList.get(coverageIndex).getMID());
                            obj.put("UserId", _UserId);
                            obj.put("CATEGORY_ID", secondary_display_size.get(j).getCategoryID());
                            obj.put("BRAND_CD", secondary_display_size.get(j).getBrandCd());
                            obj.put("DISPLAY_CD", secondary_display_size.get(j).getDisplayCd());
                            obj.put("LOCATION_CD", secondary_display_size.get(j).getLocationCd());
                            obj.put("FACEUP", secondary_display_size.get(j).getQuantity());
                            obj.put("TOTALSTOCK", secondary_display_size.get(j).getTotalStock());
                            obj.put("IMAGE1", secondary_display_size.get(j).getImage1());
                            obj.put("IMAGE2", secondary_display_size.get(j).getImage2());
                            obj.put("IMAGE3", secondary_display_size.get(j).getImage3());
                            secArray.put(obj);
                        }

                        jsonObject = new JSONObject();
                        jsonObject.put("MID", coverageList.get(coverageIndex).getMID());
                        jsonObject.put("Keys", "SS_Secondary_Display");
                        jsonObject.put("JsonData", secArray.toString());
                        jsonObject.put("UserId", _UserId);

                        jsonString = jsonObject.toString();
                        type = CommonString.UPLOADJsonDetail;
                    }
                    //endregion
                    break;
                case "SS_Promotion":
                    //region Promotion data
                    ArrayList<PromotionGetterSetter> promotion_size = db.getPromotionData2(coverageList.get(coverageIndex).getStoreId());
                    if (promotion_size.size() > 0) {
                        JSONArray promoArray = new JSONArray();
                        for (int j = 0; j < promotion_size.size(); j++) {
                            JSONObject obj = new JSONObject();
                            obj.put("MID", coverageList.get(coverageIndex).getMID());
                            obj.put("UserId", _UserId);
                            obj.put("CATEGORY_ID", promotion_size.get(j).getCategoryId());
                            obj.put("BRAND_CD", promotion_size.get(j).getBrandCd());
                            obj.put("PROMOTYPE_CD", promotion_size.get(j).getPromotionCd());
                            obj.put("TOTALSTOCK", promotion_size.get(j).getTotalStock());
                            obj.put("DESCRIPTION", promotion_size.get(j).getDescription().replace("\"", ""));
                            obj.put("POSM_PRESENT", promotion_size.get(j).getPosm());
                            obj.put("RUNNING", promotion_size.get(j).getRunning());
                            obj.put("IMAGE1", promotion_size.get(j).getImage1());
                            promoArray.put(obj);
                        }
                        jsonObject = new JSONObject();
                        jsonObject.put("MID", coverageList.get(coverageIndex).getMID());
                        jsonObject.put("Keys", "SS_Promotion");
                        jsonObject.put("JsonData", promoArray.toString());
                        jsonObject.put("UserId", _UserId);

                        jsonString = jsonObject.toString();
                        type = CommonString.UPLOADJsonDetail;
                    }
                    //endregion
                    break;
                case "SS_Competition":
                    //region Competition data
                    ArrayList<CompetitionGetterSetter> competition_size = db.getComptetionData2(coverageList.get(coverageIndex).getStoreId());
                    if (competition_size.size() > 0) {
                        JSONArray compArray = new JSONArray();
                        for (int j = 0; j < competition_size.size(); j++) {
                            JSONObject obj = new JSONObject();
                            obj.put("MID", coverageList.get(coverageIndex).getMID());
                            obj.put("UserId", _UserId);
                            obj.put("CATEGORY_ID", competition_size.get(j).getCategoryId());
                            obj.put("BRAND_CD", competition_size.get(j).getBrandCd());
                            obj.put("PROMOTYPE_CD", competition_size.get(j).getPromotypeCd());
                            obj.put("DESCRIPTION", competition_size.get(j).getDescription().replace("\"", ""));
                            obj.put("IMAGE1", competition_size.get(j).getImage1());
                            compArray.put(obj);
                        }

                        jsonObject = new JSONObject();
                        jsonObject.put("MID", coverageList.get(coverageIndex).getMID());
                        jsonObject.put("Keys", "SS_Competition");
                        jsonObject.put("JsonData", compArray.toString());
                        jsonObject.put("UserId", _UserId);

                        jsonString = jsonObject.toString();
                        type = CommonString.UPLOADJsonDetail;
                    }
                    //endregion
                    break;
                case "SS_Touchpoint":
                    //region SS_POSM/Touchpoint
                    ArrayList<PosmGetterSetter> getPosmNewData_SS = db.getPosmDataUpload_SS(Integer.parseInt(coverageList.get(coverageIndex).getStoreId()));
                    if (getPosmNewData_SS.size() > 0) {
                        JSONArray posmArray = new JSONArray();
                        for (int j = 0; j < getPosmNewData_SS.size(); j++) {

                            if (getPosmNewData_SS.get(j).getPosm_qty() != null && !getPosmNewData_SS.get(j).getPosm_qty().equalsIgnoreCase("")) {

                                JSONObject obj = new JSONObject();
                                obj.put("MID", coverageList.get(coverageIndex).getMID());
                                obj.put("UserId", _UserId);
                                obj.put("TYPE", getPosmNewData_SS.get(j).getType());
                                obj.put("BRAND_ID", getPosmNewData_SS.get(j).getBrand_id());
                                obj.put("POSM_ID", getPosmNewData_SS.get(j).getPosm_id());
                                obj.put("POSM_QTY", getPosmNewData_SS.get(j).getPosm_qty());
                                obj.put("POSM_IMAGE", getPosmNewData_SS.get(j).getPosm_image());
                                posmArray.put(obj);
                            }

                        }

                        jsonObject = new JSONObject();
                        jsonObject.put("MID", coverageList.get(coverageIndex).getMID());
                        jsonObject.put("Keys", "SS_Touchpoint");
                        jsonObject.put("JsonData", posmArray.toString());
                        jsonObject.put("UserId", _UserId);

                        jsonString = jsonObject.toString();
                        type = CommonString.UPLOADJsonDetail;

                    }
                    //endregion
                    break;
                case "Secondary_backwall_image":
                    //region Secondary_backwall_image
                    ArrayList<BackwallGetterSetter> backwall_list = db.getBackwallData(Integer.parseInt(coverageList.get(coverageIndex).getStoreId()));
                    if (backwall_list.size() > 0) {
                        JSONArray topUpArray = new JSONArray();
                        for (int j = 0; j < backwall_list.size(); j++) {
                            JSONObject obj = new JSONObject();
                            obj.put("MID", coverageList.get(coverageIndex).getMID());
                            obj.put("UserId", _UserId);
                            obj.put("IMAGE", backwall_list.get(j).getImage());
                            // obj.put("CATEGORY_ID", backwall_list.get(j).getStore_id());
                            topUpArray.put(obj);
                        }

                        jsonObject = new JSONObject();
                        jsonObject.put("MID", coverageList.get(coverageIndex).getMID());
                        jsonObject.put("Keys", "Secondary_backwall_image");
                        jsonObject.put("JsonData", topUpArray.toString());
                        jsonObject.put("UserId", _UserId);

                        jsonString = jsonObject.toString();
                        type = CommonString.UPLOADJsonDetail;
                    }
                    //endregion
                    break;
                case "Primary_Window_image":
                    //region primary_image_Grid
                    ArrayList<PrimaryImage> categoryMasterlist = db.getPrimaryImageGridUploadData(Integer.parseInt(coverageList.get(coverageIndex).getStoreId()));
                    if (categoryMasterlist.size() > 0) {
                        JSONArray topUpArray = new JSONArray();
                        for (int j = 0; j < categoryMasterlist.size(); j++) {
                            JSONObject obj = new JSONObject();
                            obj.put("MID", coverageList.get(coverageIndex).getMID());
                            obj.put("UserId", _UserId);
                            obj.put("IMAGE", categoryMasterlist.get(j).getImage());
                            obj.put("CATEGORY_ID", categoryMasterlist.get(j).getCategory_id());
                            topUpArray.put(obj);
                        }

                        jsonObject = new JSONObject();
                        jsonObject.put("MID", coverageList.get(coverageIndex).getMID());
                        jsonObject.put("Keys", "Primary_Window_image");
                        jsonObject.put("JsonData", topUpArray.toString());
                        jsonObject.put("UserId", _UserId);

                        jsonString = jsonObject.toString();
                        type = CommonString.UPLOADJsonDetail;
                    }
                    //endregion
                    break;
                case "Primary_Stock":
                    //region Primary_Stock
                    ArrayList<SkuGetterSetter> getSkulist_primary = db.getSkuDefaultDataUpload(Integer.parseInt(coverageList.get(coverageIndex).getStoreId()));
                    // primaryImageData = db.getPrimaryImageData(Integer.parseInt(store_id));
                    if (getSkulist_primary.size() > 0) {
                        JSONArray topUpArray = new JSONArray();
                        for (int j = 0; j < getSkulist_primary.size(); j++) {
                            JSONObject obj = new JSONObject();
                            obj.put("MID", coverageList.get(coverageIndex).getMID());
                            obj.put("UserId", _UserId);
                            obj.put("SKU_ID", getSkulist_primary.get(j).getSku_id());
                            obj.put("STOCK_QTY", getSkulist_primary.get(j).getStock());
                            obj.put("FACEUP", getSkulist_primary.get(j).getFaceup());
                            obj.put("LAST_SIX", getSkulist_primary.get(j).getSku_start_date());
                            obj.put("SIX_TO_TWELVE", getSkulist_primary.get(j).getSku_end_date());
                            obj.put("MORE_THAN_TWELVE", getSkulist_primary.get(j).getSku_month());
                            topUpArray.put(obj);
                        }


                        jsonObject = new JSONObject();
                        jsonObject.put("MID", coverageList.get(coverageIndex).getMID());
                        jsonObject.put("Keys", "Primary_Stock");
                        jsonObject.put("JsonData", topUpArray.toString());
                        jsonObject.put("UserId", _UserId);

                        jsonString = jsonObject.toString();
                        type = CommonString.UPLOADJsonDetail;
                    }
                    //endregion
                    break;
                case "Touch_point":
                    //region Touch_point
                    ArrayList<PosmGetterSetter> getPosn_Data = db.getPosmDefaultDataUpload(Integer.parseInt(coverageList.get(coverageIndex).getStoreId()));
                    // primaryImageData = db.getPrimaryImageData(Integer.parseInt(store_id));
                    if (getPosn_Data.size() > 0) {
                        JSONArray topUpArray = new JSONArray();
                        for (int j = 0; j < getPosn_Data.size(); j++) {

                            if (!getPosn_Data.get(j).getPosm_qty().equals("")) {

                                JSONObject obj = new JSONObject();
                                obj.put("MID", coverageList.get(coverageIndex).getMID());
                                obj.put("UserId", _UserId);
                                obj.put("TYPE", getPosn_Data.get(j).getType());
                                obj.put("BRAND_ID", getPosn_Data.get(j).getBrand_id());
                                obj.put("POSM_ID", getPosn_Data.get(j).getPosm_id());
                                obj.put("POSM_QTY", getPosn_Data.get(j).getPosm_qty());
                                obj.put("POSM_IMAGE", getPosn_Data.get(j).getPosm_image());
                                topUpArray.put(obj);
                            }

                        }

                        jsonObject = new JSONObject();
                        jsonObject.put("MID", coverageList.get(coverageIndex).getMID());
                        jsonObject.put("Keys", "Touch_point");
                        jsonObject.put("JsonData", topUpArray.toString());
                        jsonObject.put("UserId", _UserId);

                        jsonString = jsonObject.toString();
                        type = CommonString.UPLOADJsonDetail;

                    }
                    //endregion
                    break;
                case "Secondary_Window":
                    //region window data
                    ArrayList<WindowGetterSetter> getWindowlist_Secondary = db.getWindowDataUpload(Integer.parseInt(coverageList.get(coverageIndex).getStoreId()));
                    if (getWindowlist_Secondary.size() > 0) {
                        JSONArray primaryArray = new JSONArray();
                        JSONObject primaryfinalObj = new JSONObject();
                        for (int j = 0; j < getWindowlist_Secondary.size(); j++) {
                            ArrayList<ChecklistGetterSetter> getChecklist_Secondary = db.getCheckListDataUpload(Integer.parseInt(coverageList.get(coverageIndex).getStoreId()), getWindowlist_Secondary.get(j).getKey_id());
                            JSONArray skuArray = new JSONArray();
                            if (getChecklist_Secondary.size() > 0) {
                                for (int k = 0; k < getChecklist_Secondary.size(); k++) {
                                    JSONObject obj = new JSONObject();
                                    obj.put("COMMON_ID", getWindowlist_Secondary.get(j).getKey_id());
                                    obj.put("CHECKLIST_ID", getChecklist_Secondary.get(k).getChecklist_id());
                                    obj.put("STATUS", getChecklist_Secondary.get(k).isExit());
                                    skuArray.put(obj);

                                }

                            }

                            ArrayList<WindowBrandGetterSetter> getStock_Secondary = db.getSecondaryStockDataUpload(Integer.parseInt(coverageList.get(coverageIndex).getStoreId()), getWindowlist_Secondary.get(j).getKey_id());
                            JSONArray stockArray = new JSONArray();
                            if (getStock_Secondary.size() > 0) {
                                for (int k = 0; k < getStock_Secondary.size(); k++) {
                                    if (!getStock_Secondary.get(k).getStock().equals("")) {
                                        JSONObject obj = new JSONObject();
                                        obj.put("COMMON_ID", getStock_Secondary.get(k).getCommon_id());
                                        obj.put("BRAND_ID", getStock_Secondary.get(k).getBrand_id());
                                        obj.put("STOCK", getStock_Secondary.get(k).getStock());
                                        stockArray.put(obj);
                                    }
                                }

                            }

                            JSONObject json = new JSONObject();
                            json.put("MID", coverageList.get(coverageIndex).getMID());
                            json.put("USER_ID", _UserId);
                            json.put("BRAND_GROUP_ID", getWindowlist_Secondary.get(j).getBrand_group_id());
                            json.put("COMMON_ID", getWindowlist_Secondary.get(j).getKey_id());
                            json.put("WINDOW_ID", getWindowlist_Secondary.get(j).getWindow_id());
                            json.put("WINDOW_IMAGE", getWindowlist_Secondary.get(j).getImage());
                            json.put("WINDOW_EXIST", getWindowlist_Secondary.get(j).isExist());
                            json.put("LOCATION_ID", getWindowlist_Secondary.get(j).getLocation_id());
                            json.put("CHECKLIST", skuArray);
                            json.put("SECONDARY_STOCK", stockArray);
                            primaryArray.put(json);

                        }
                        primaryfinalObj.put("SECONDARY_WINDOW", primaryArray);
                        jsonObject = new JSONObject();
                        jsonObject.put("MID", coverageList.get(coverageIndex).getMID());
                        jsonObject.put("Keys", "Secondary_Window");
                        jsonObject.put("JsonData", primaryfinalObj.toString());
                        jsonObject.put("UserId", _UserId);

                        jsonString = jsonObject.toString();
                        type = CommonString.UPLOADJsonDetail;

                    }
                    //endregion
                    break;
                case "GeoTag":
                    //region GeoTag
                    ArrayList<GeotaggingBeans> geotaglist = db.getinsertGeotaggingData(coverageList.get(coverageIndex).getStoreId(), "N");
                    if (geotaglist.size() > 0) {
                        JSONArray topUpArray = new JSONArray();
                        for (int j = 0; j < geotaglist.size(); j++) {
                            JSONObject obj = new JSONObject();
                            obj.put(CommonString.KEY_STORE_ID, geotaglist.get(j).getStoreid());
                            obj.put(CommonString.KEY_VISIT_DATE, date);
                            obj.put(CommonString.KEY_LATITUDE, geotaglist.get(j).getLatitude());
                            obj.put(CommonString.KEY_LONGITUDE, geotaglist.get(j).getLongitude());
                            obj.put("FRONT_IMAGE", geotaglist.get(j).getImage());
                            topUpArray.put(obj);
                        }

                        jsonObject = new JSONObject();
                        jsonObject.put("MID", coverageList.get(coverageIndex).getMID());
                        jsonObject.put("Keys", "GeoTag");
                        jsonObject.put("JsonData", topUpArray.toString());
                        jsonObject.put("UserId", _UserId);

                        jsonString = jsonObject.toString();
                        type = CommonString.UPLOADJsonDetail;
                    }
                    //endregion
                    break;
                case "Top_Up":
                    //region Top_up description
                    ArrayList<TopupGetterSetter> topupn_store_list;
                    ArrayList<TopupGetterSetter> topupn_posm_list;
                    topupn_store_list = db.getTopUpStoreForUpload();
                    if (topupn_store_list.size() > 0) {
                        JSONArray topUpArray = new JSONArray();
                        for (int j = 0; j < topupn_store_list.size(); j++) {

                            JSONObject topUpobj = new JSONObject();

                            topupn_posm_list = db.getTopUpData(topupn_store_list.get(j).getKey_topup_id());
                            JSONArray posmArray = new JSONArray();
                            for (int k = 0; k < topupn_posm_list.size(); k++) {
                                JSONObject posmobj = new JSONObject();
                                posmobj.put("TOPUP_ID", topupn_posm_list.get(j).getKey_topup_id());
                                posmobj.put("TYPE", topupn_posm_list.get(j).getTypeId());
                                posmobj.put("BRAND_CD", topupn_posm_list.get(j).getBrand_cd());
                                posmobj.put("POSM_CD", topupn_posm_list.get(j).getPosm_id());
                                posmobj.put("QTY", topupn_posm_list.get(j).getQuantity());
                                posmobj.put("IMAGE_URL", topupn_posm_list.get(j).getImage());
                                posmArray.put(posmobj);
                            }

                            topUpobj.put("TOPUP_ID", topupn_store_list.get(j).getKey_topup_id());
                            topUpobj.put("VISIT_DATE", date);
                            topUpobj.put("STORE_NAME", topupn_store_list.get(j).getStorename().replace("\"", ""));
                            topUpobj.put("STORE_ADDRESS", topupn_store_list.get(j).getStoreaddr().replace("\"", ""));
                            topUpobj.put("DISPLAY_TYPE_CD", topupn_store_list.get(j).getDiplaytype());
                            topUpobj.put("CITY_CD", topupn_store_list.get(j).getCity_cd());
                            topUpobj.put("CLASSIFICATION_CD", topupn_store_list.get(j).getClassificationId());
                            topUpobj.put("STORE_TYPE_CD", topupn_store_list.get(j).getTypeId());
                            topUpobj.put("DISTRIBUTOR_CD", topupn_store_list.get(j).getDistributorId());
                            topUpobj.put("DEALER_BOARD_IMAGE", topupn_store_list.get(j).getDealer_image());
                            topUpobj.put("BACKWALL_IMG", topupn_store_list.get(j).getBackwall());
                            topUpobj.put("SHELF_ONE_IMG", topupn_store_list.get(j).getShelf1());
                            topUpobj.put("SHELF_TWO_IMG", topupn_store_list.get(j).getShelf2());
                            topUpobj.put("TOPUP_POSM", posmArray);
                            topUpArray.put(topUpobj);
                        }

                        jsonObject = new JSONObject();
                        jsonObject.put("MID", "0");
                        jsonObject.put("Keys", "Top_Up");
                        jsonObject.put("JsonData", topUpArray.toString());
                        jsonObject.put("UserId", _UserId);

                        jsonString = jsonObject.toString();
                        type = CommonString.UPLOADJsonDetail;
                    }
                    //endregion
                    break;
            }
            //endregion

            final int[] finalJsonIndex = {keyIndex};
            final String finalKeyName = keyList.get(keyIndex);

            if (jsonString != null && !jsonString.equalsIgnoreCase("")) {

                pd.setMessage("Uploading (" + keyIndex + "/" + keyList.size() + ") \n" + keyList.get(keyIndex) + "\n Store uploading " + (coverageIndex + 1) + "/" + coverageList.size());
                RequestBody jsonData = RequestBody.create(MediaType.parse("application/json"), jsonString);
                adapter = new Retrofit.Builder().baseUrl(CommonString.URL2).addConverterFactory(GsonConverterFactory.create()).build();
                PostApi api = adapter.create(PostApi.class);
                Call<ResponseBody> call = null;

                if (type == CommonString.COVERAGE_DETAIL) {
                    call = api.getCoverageDetail(jsonData);
                } else if (type == CommonString.UPLOADJsonDetail) {
                    call = api.getUploadJsonDetail(jsonData);
                }


                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        ResponseBody responseBody = response.body();
                        String data = null;
                        if (responseBody != null && response.isSuccessful()) {
                            try {
                                data = response.body().string();
                                if (data.equalsIgnoreCase("")) {
                                    data_global[0] = "";
                                    AlertandMessages.showAlert((Activity) context, "Invalid Data : problem occured at " + keyList.get(keyIndex), true);
                                } else {
                                    data = data.substring(1, data.length() - 1).replace("\\", "");
                                    data_global[0] = data;

                                    if (finalKeyName.equalsIgnoreCase("CoverageDetail_latest")) {
                                        try {
                                            coverageList.get(coverageIndex).setMID(Integer.parseInt(data_global[0]));
                                        } catch (NumberFormatException ex) {
                                            AlertandMessages.showAlert((Activity) context, "Error in Uploading Data at " + finalKeyName, true);
                                        }
                                    } else if (data_global[0].contains(CommonString.KEY_SUCCESS)) {

                                        if (finalKeyName.equalsIgnoreCase("GeoTag")) {
                                            db.updateInsertedGeoTagStatus(coverageList.get(coverageIndex).getStoreId(), CommonString.KEY_Y);
                                            db.updateStatus(coverageList.get(coverageIndex).getStoreId(), CommonString.KEY_Y);
                                            db.updateGeoTagStatusForPjpDevaition(coverageList.get(coverageIndex).getStoreId(), CommonString.KEY_Y);
                                        } else if (finalKeyName.equalsIgnoreCase("Top_Up")) {
                                            db.updateStoreStatusTopUpAll();
                                        }
                                    } else {
                                        AlertandMessages.showAlert((Activity) context, "Error in Uploading Data at " + finalKeyName + " : " + data_global[0], true);
                                    }


                                    finalJsonIndex[0]++;
                                    if (finalJsonIndex[0] != keyList.size()) {
                                        uploadDataWithoutWait(keyList, finalJsonIndex[0], coverageList, coverageIndex);
                                    } else {
                                        pd.setMessage("updating status :" + coverageIndex);
                                        //uploading status D for current store from coverageList


                                        updateStatus(coverageList, coverageIndex, CommonString.KEY_D);

                                    }
                                }

                            } catch (Exception e) {
                                pd.dismiss();
                                AlertandMessages.showAlert((Activity) context, "Error in Uploading Data at " + finalKeyName, true);
                            }
                        } else {
                            pd.dismiss();
                            AlertandMessages.showAlert((Activity) context, "Error in Uploading Data at " + finalKeyName, true);

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        isvalid = true;
                        pd.dismiss();
                        if (t instanceof SocketTimeoutException) {
                            AlertandMessages.showAlert((Activity) context, CommonString.MESSAGE_INTERNET_NOT_AVALABLE, true);
                        } else if (t instanceof IOException) {
                            AlertandMessages.showAlert((Activity) context, CommonString.MESSAGE_INTERNET_NOT_AVALABLE, true);
                        } else if (t instanceof SocketException) {
                            AlertandMessages.showAlert((Activity) context, CommonString.MESSAGE_INTERNET_NOT_AVALABLE, true);
                        } else {
                            AlertandMessages.showAlert((Activity) context, CommonString.MESSAGE_INTERNET_NOT_AVALABLE, true);
                        }

                    }
                });

            } else {
                finalJsonIndex[0]++;
                if (finalJsonIndex[0] != keyList.size()) {
                    uploadDataWithoutWait(keyList, finalJsonIndex[0], coverageList, coverageIndex);
                } else {
                    pd.setMessage("updating status :" + coverageIndex);
                    //uploading status D for current store from coverageList
                    updateStatus(coverageList, coverageIndex, CommonString.KEY_D);

                }
            }
        } catch (Exception ex) {

        }

    }
*/
    //endregion

    //region updateStatus
/*
    void updateStatus(final ArrayList<CoverageBean> coverageList, final int coverageIndex, String status) {
        if (coverageList.get(coverageIndex) != null) {
            try {

                final int[] tempcoverageIndex = {coverageIndex};
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("StoreId", coverageList.get(coverageIndex).getStoreId());
                jsonObject.put("VisitDate", coverageList.get(coverageIndex).getVisitDate());
                jsonObject.put("UserId", _UserId);
                jsonObject.put("Status", status);

                RequestBody jsonData = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                adapter = new Retrofit.Builder().baseUrl(CommonString.URL).addConverterFactory(GsonConverterFactory.create()).build();
                PostApi api = adapter.create(PostApi.class);
                Call<ResponseBody> call = null;

                call = api.getCoverageStatusDetail(jsonData);

                pd.setMessage("Uploading store status " + (coverageIndex + 1) + "/" + coverageList.size());
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        ResponseBody responseBody = response.body();
                        String data = null;
                        if (responseBody != null && response.isSuccessful()) {
                            try {
                                data = response.body().string();
                                if (data.equalsIgnoreCase("")) {
                                    pd.dismiss();
                                    AlertandMessages.showAlert((Activity) context, "Error in Uploading status at coverage :" + coverageIndex, true);
                                } else {
                                    data = data.substring(1, data.length() - 1).replace("\\", "");
                                    if (data.contains("1")) {
                                        if (coverageList.get(tempcoverageIndex[0]).getFrom().equalsIgnoreCase(CommonString.TAG_FROM_JCP)) {
                                            db.updateCheckoutStatus(coverageList.get(tempcoverageIndex[0]).getStoreId(), CommonString.KEY_D, CommonString.TABLE_Journey_Plan);
                                        } else {
                                            db.updateCheckoutStatus(coverageList.get(tempcoverageIndex[0]).getStoreId(), CommonString.KEY_D, CommonString.TABLE_Deviation_Journey_Plan);
                                        }
                                        tempcoverageIndex[0]++;
                                        if (tempcoverageIndex[0] != coverageList.size()) {

                                            //updateStatus(coverageList, tempcoverageIndex[0], CommonString.KEY_D);
                                            uploadDataUsingCoverageRecursive(coverageList, tempcoverageIndex[0]);
                                        } else {
                                            pd.setMessage("updoading images");
                                            String coverageDate = null;
                                            if (coverageList.size() > 0) {
                                                coverageDate = coverageList.get(0).getVisitDate();
                                            } else {
                                                coverageDate = date;
                                            }
                                            uploadImage(coverageDate);
                                        }

                                    } else {
                                        pd.dismiss();
                                        AlertandMessages.showAlert((Activity) context, "Error in Uploading status at coverage :" + coverageIndex, true);
                                    }

                                }
                                // jsonStringList.remove(finalJsonIndex);
                                // KeyNames.remove(finalJsonIndex);


                            } catch (Exception e) {
                                pd.dismiss();
                                AlertandMessages.showAlert((Activity) context, "Error in Uploading status at coverage :" + coverageIndex, true);
                            }
                        } else {
                            pd.dismiss();
                            AlertandMessages.showAlert((Activity) context, "Error in Uploading status at coverage :" + coverageIndex, true);

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        isvalid = true;
                        pd.dismiss();
                        if (t instanceof SocketTimeoutException) {
                            AlertandMessages.showAlert((Activity) context, CommonString.MESSAGE_INTERNET_NOT_AVALABLE, true);
                        } else if (t instanceof IOException) {
                            AlertandMessages.showAlert((Activity) context, CommonString.MESSAGE_INTERNET_NOT_AVALABLE, true);
                        } else if (t instanceof SocketException) {
                            AlertandMessages.showAlert((Activity) context, CommonString.MESSAGE_INTERNET_NOT_AVALABLE, true);
                        } else {
                            AlertandMessages.showAlert((Activity) context, CommonString.MESSAGE_INTERNET_NOT_AVALABLE, true);
                        }

                    }
                });


            } catch (JSONException ex) {

            }
        }

    }
*/
    //endregion

    //region uploadDataUsingCoverageRecursive
/*
    public void uploadDataUsingCoverageRecursive(ArrayList<CoverageBean> coverageList, int coverageIndex) {
        try {
            ArrayList<String> keyList = new ArrayList<>();
            keyList.clear();
            String store_id = coverageList.get(coverageIndex).getStoreId();
            String status = null;
            pd.setMessage("Uploading store " + (coverageIndex + 1) + "/" + coverageList.size());

            if (coverageList.get(coverageIndex).getFrom().equalsIgnoreCase(CommonString.TAG_FROM_JCP)) {
                status = db.getSpecificStoreData(coverageList.get(coverageIndex).getVisitDate(), store_id).getUploadStatus();
            } else {
                status = db.getSpecificStoreDataDeviation(coverageList.get(coverageIndex).getVisitDate(), store_id).getUploadStatus();
            }
         */
/*   if (from == CommonString.TAG_FROM_CURRENT) {

            } else if (from == CommonString.TAG_FROM_PREVIOUS) {
                if (coverageList.get(coverageIndex).getFrom().equalsIgnoreCase(CommonString.TAG_FROM_JCP)) {
                    status = db.getSpecificStoreDataPrevious(coverageList.get(coverageIndex).getVisitDate(), store_id).getUploadStatus();
                } else {
                    status = db.getSpecificStoreDataDeviationPrevious(coverageList.get(coverageIndex).getVisitDate(), store_id).getUploadStatus();
                }
            }*//*



            if (status != null && !status.equalsIgnoreCase(CommonString.KEY_D)) {

                keyList.add("Deviation_JourneyPlan");
                keyList.add("CoverageDetail_latest");
                keyList.add("Store_detail_New");
                keyList.add("SS_Primary_Bay");
                keyList.add("SS_Secondary_Display");
                keyList.add("SS_Promotion");
                keyList.add("SS_Competition");
                keyList.add("SS_Touchpoint");
                keyList.add("Secondary_backwall_image");
                keyList.add("Primary_Window_image");
                keyList.add("Primary_Stock");
                keyList.add("Touch_point");
                keyList.add("Secondary_Window");
                keyList.add("GeoTag");
                keyList.add("Top_Up");

            }

            if (keyList.size() > 0) {

                UploadImageWithRetrofit upload = new UploadImageWithRetrofit(context, db, pd, from);
                upload.uploadDataWithoutWait(keyList, 0, coverageList, coverageIndex);
            } else {

                if (++coverageIndex != coverageList.size()) {
                    uploadDataUsingCoverageRecursive(coverageList, coverageIndex);
                } else {
                    String coverageDate = null;
                    if (coverageList.size() > 0) {
                        coverageDate = coverageList.get(0).getVisitDate();
                    } else {
                        coverageDate = date;
                    }
                    uploadImage(coverageDate);
                }

            }
            //endregion

        } catch (Exception e) {
            e.printStackTrace();
            pd.dismiss();
            AlertandMessages.showAlert((Activity) context, CommonString.MESSAGE_SOCKETEXCEPTION, true);
        }

    }
*/
    //endregion


    //region uploadImage
/*
    void uploadImage(String coverageDate) {

        File f = new File(CommonString.FILE_PATH);
        File file[] = f.listFiles();

        if (file.length > 0) {
            UploadImageWithRetrofitOne.uploadedFiles = 0;
            UploadImageWithRetrofitOne.totalFiles = file.length;
            UploadImageWithRetrofitOne uploadImg = new UploadImageWithRetrofitOne(date, _UserId, context);
            uploadImg.UploadImageRecursive(context);
        } else {
            UploadImageWithRetrofitOne.totalFiles = file.length;
            new StatusUpload(coverageDate).execute();
        }
    }
*/
    //endregion

    //region class StatusUpload
/*
    class StatusUpload extends AsyncTask<String, String, String> {
        String coverageDate;

        StatusUpload(String coverageDate) {
            this.coverageDate = coverageDate;
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                db = new GSKGTMerDB(context);
                db.open();
                ArrayList<JourneyPlan> storeList = db.getStoreData(coverageDate);
                for (int i = 0; i < storeList.size(); i++) {
                    if (storeList.get(i).getUploadStatus().equalsIgnoreCase(CommonString.KEY_D)) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("StoreId", storeList.get(i).getStoreId());
                        jsonObject.put("VisitDate", coverageDate);
                        jsonObject.put("UserId", _UserId);
                        jsonObject.put("Status", CommonString.KEY_U);

                        UploadImageWithRetrofit upload = new UploadImageWithRetrofit(context);
                        String jsonString2 = jsonObject.toString();
                        String result = upload.downloadDataUniversal(jsonString2, CommonString.COVERAGEStatusDetail);

                        if (result.equalsIgnoreCase(CommonString.MESSAGE_NO_RESPONSE_SERVER)) {
                            statusUpdated = false;
                            throw new SocketTimeoutException();
                        } else if (result.equalsIgnoreCase(CommonString.MESSAGE_SOCKETEXCEPTION)) {
                            statusUpdated = false;
                            throw new IOException();
                        } else if (result.equalsIgnoreCase(CommonString.MESSAGE_INVALID_JSON)) {
                            statusUpdated = false;
                            throw new JsonSyntaxException("Coverage Status Detail");
                        } else if (result.equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                            statusUpdated = false;
                            throw new Exception();
                        } else {
                            statusUpdated = true;
                            if (db.updateCheckoutStatus(String.valueOf(storeList.get(i).getStoreId()), CommonString.KEY_U, CommonString.TABLE_Journey_Plan) > 0) {
                                db.deleteTableWithStoreID(String.valueOf(storeList.get(i).getStoreId()));
                                //AlertandMessages.show
                                // Alert((Activity) context, "All Image Uploaded Successfully", false);
                            } else {
                                //AlertandMessages.showAlert((Activity) context, "Store status not updated", false);
                            }
                        }
                    }
                }


                ArrayList<JourneyPlan> storeList_deviation = db.getPJPDeviationList(coverageDate);
                for (int i = 0; i < storeList_deviation.size(); i++) {
                    if (storeList_deviation.get(i).getUploadStatus().equalsIgnoreCase(CommonString.KEY_D)) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("StoreId", storeList_deviation.get(i).getStoreId());
                        jsonObject.put("VisitDate", coverageDate);
                        jsonObject.put("UserId", _UserId);
                        jsonObject.put("Status", CommonString.KEY_U);

                        UploadImageWithRetrofit upload = new UploadImageWithRetrofit(context);
                        String jsonString2 = jsonObject.toString();
                        String result = upload.downloadDataUniversal(jsonString2, CommonString.COVERAGEStatusDetail);

                        if (result.equalsIgnoreCase(CommonString.MESSAGE_NO_RESPONSE_SERVER)) {
                            statusUpdated = false;
                            throw new SocketTimeoutException();
                        } else if (result.equalsIgnoreCase(CommonString.MESSAGE_SOCKETEXCEPTION)) {
                            statusUpdated = false;
                            throw new IOException();
                        } else if (result.equalsIgnoreCase(CommonString.MESSAGE_INVALID_JSON)) {
                            statusUpdated = false;
                            throw new JsonSyntaxException("Coverage Status Detail");
                        } else if (result.equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                            statusUpdated = false;
                            throw new Exception();
                        } else {
                            statusUpdated = true;
                            if (db.updateCheckoutStatus(String.valueOf(storeList_deviation.get(i).getStoreId()), CommonString.KEY_U, CommonString.TABLE_Deviation_Journey_Plan) > 0) {
                                db.deleteTableWithStoreID(String.valueOf(storeList_deviation.get(i).getStoreId()));
                                //AlertandMessages.show
                                // Alert((Activity) context, "All Image Uploaded Successfully", false);
                            } else {
                                //AlertandMessages.showAlert((Activity) context, "Store status not updated", false);
                            }
                        }

                    }
                }


            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                AlertandMessages.showAlert((Activity) context, CommonString.MESSAGE_SOCKETEXCEPTION, true);
            } catch (IOException e) {
                e.printStackTrace();
                AlertandMessages.showAlert((Activity) context, CommonString.MESSAGE_SOCKETEXCEPTION, true);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                AlertandMessages.showAlert((Activity) context, CommonString.MESSAGE_INVALID_JSON, true);
            } catch (Exception e) {
                e.printStackTrace();

            }
            if (statusUpdated) {
                return CommonString.KEY_SUCCESS;
            } else {
                return CommonString.KEY_FAILURE;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            if (s.equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                if (UploadImageWithRetrofitOne.totalFiles == uploadedFiles && statusUpdated) {
                    AlertandMessages.showAlert((Activity) context, "All images uploaded Successfully", true);
                } else if (UploadImageWithRetrofitOne.totalFiles == uploadedFiles && !statusUpdated) {
                    AlertandMessages.showAlert((Activity) context, "All images uploaded Successfully, but status not updated", true);
                } else {
                    AlertandMessages.showAlert((Activity) context, "Some images not uploaded", true);
                }
            }
        }
    }
*/
    //endregion

    //region class DownloadImageTask
/*
    class DownloadImageTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {

            try {
                downloadImages();
                return CommonString.KEY_SUCCESS;
            } catch (FileNotFoundException ex) {
                return CommonString.KEY_FAILURE;
            } catch (IOException ex) {
                return CommonString.KEY_FAILURE;
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                pd.dismiss();
                AlertandMessages.showAlert((Activity) context, "All data downloaded Successfully", true);
            } else {
                pd.dismiss();
                AlertandMessages.showAlert((Activity) context, "Error in downloading", true);
            }

        }

    }
*/
    //endregion

    //region downloadImages
/*
    void downloadImages() throws IOException, FileNotFoundException {
        //region JCP Image Download
        if (jcpObject != null) {

            for (int i = 0; i < jcpObject.getJourneyPlan().size(); i++) {

                String image_name = jcpObject.getJourneyPlan().get(i).getImageName();
                if (image_name != null && !image_name.equalsIgnoreCase("NA")
                        && !image_name.equalsIgnoreCase("")) {
                    URL url = new URL(jcpObject.getJourneyPlan().get(i).getImagePath() + image_name);
                    HttpURLConnection c = (HttpURLConnection) url.openConnection();
                    c.setRequestMethod("GET");
                    c.getResponseCode();
                    c.connect();

                    if (c.getResponseCode() == 200) {
                        int length = c.getContentLength();
                        String size = new DecimalFormat("##.##")
                                .format((double) ((double) length / 1024))
                                + " KB";

                               */
/* String PATH = Environment
                                        .getExternalStorageDirectory()
                                        + "/GT_GSK_Images/";*//*

                        File file = new File(CommonString.FILE_PATH_Downloaded);
                        file.mkdirs();

                        if (!new File(CommonString.FILE_PATH_Downloaded
                                + image_name).exists()
                                && !size.equalsIgnoreCase("0 KB")) {

                            jj = image_name.split("\\/");
                            image_name = jj[jj.length - 1];

                            File outputFile = new File(file,
                                    image_name);
                            FileOutputStream fos = null;

                            fos = new FileOutputStream(outputFile);
                            InputStream is1 = (InputStream) c.getInputStream();
                            int bytes = 0;
                            byte[] buffer = new byte[1024];
                            int len1 = 0;

                            while ((len1 = is1.read(buffer)) != -1) {

                                bytes = (bytes + len1);

                                // data.value = (int) ((double) (((double)
                                // bytes) / length) * 100);

                                fos.write(buffer, 0, len1);

                            }

                            fos.close();
                            is1.close();

                        }
                    }
                }
            }

        }
        //endregion

        //region Category Images
        if (categoryObject != null) {

            for (int i = 0; i < categoryObject.getCategoryMaster().size(); i++) {

                String image_name = categoryObject.getCategoryMaster().get(i).getIcon();
                if (image_name != null && !image_name.equalsIgnoreCase("NA")
                        && !image_name.equalsIgnoreCase("")) {
                    URL url = new URL(categoryObject.getCategoryMaster().get(i).getImagePath() + image_name);
                    HttpURLConnection c = (HttpURLConnection) url.openConnection();
                    c.setRequestMethod("GET");
                    c.getResponseCode();
                    c.connect();

                    if (c.getResponseCode() == 200) {

                        int length = c.getContentLength();

                        String size = new DecimalFormat("##.##")
                                .format((double) ((double) length / 1024))
                                + " KB";

                               */
/* String PATH = Environment
                                        .getExternalStorageDirectory()
                                        + "/GT_GSK_Images/";*//*

                        File file = new File(CommonString.FILE_PATH_Downloaded);
                        file.mkdirs();

                        if (!new File(CommonString.FILE_PATH_Downloaded
                                + image_name).exists()
                                && !size.equalsIgnoreCase("0 KB")) {

                            jj = image_name.split("\\/");
                            image_name = jj[jj.length - 1];

                            File outputFile = new File(file,
                                    image_name);
                            FileOutputStream fos = new FileOutputStream(
                                    outputFile);
                            InputStream is1 = (InputStream) c
                                    .getInputStream();

                            int bytes = 0;
                            byte[] buffer = new byte[1024];
                            int len1 = 0;

                            while ((len1 = is1.read(buffer)) != -1) {

                                bytes = (bytes + len1);

                                // data.value = (int) ((double) (((double)
                                // bytes) / length) * 100);

                                fos.write(buffer, 0, len1);

                            }

                            fos.close();
                            is1.close();

                        }
                    }
                }


                String image_name2 = categoryObject.getCategoryMaster().get(i).getIconDone();
                if (image_name2 != null && !image_name2.equalsIgnoreCase("NA")
                        && !image_name2.equalsIgnoreCase("")) {
                    URL url = new URL(categoryObject.getCategoryMaster().get(i).getImagePath() + image_name2);
                    HttpURLConnection c = (HttpURLConnection) url.openConnection();
                    c.setRequestMethod("GET");
                    c.getResponseCode();
                    c.connect();

                    if (c.getResponseCode() == 200) {

                        int length = c.getContentLength();

                        String size = new DecimalFormat("##.##")
                                .format((double) ((double) length / 1024))
                                + " KB";

                                */
/*String PATH = Environment
                                        .getExternalStorageDirectory()
                                        + "/GT_GSK_Images/";*//*

                        File file = new File(CommonString.FILE_PATH_Downloaded);
                        file.mkdirs();

                        if (!new File(CommonString.FILE_PATH_Downloaded
                                + image_name2).exists()
                                && !size.equalsIgnoreCase("0 KB")) {

                            jj = image_name2.split("\\/");
                            image_name2 = jj[jj.length - 1];

                            File outputFile = new File(file,
                                    image_name2);
                            FileOutputStream fos = new FileOutputStream(
                                    outputFile);
                            InputStream is1 = (InputStream) c
                                    .getInputStream();

                            int bytes = 0;
                            byte[] buffer = new byte[1024];
                            int len1 = 0;

                            while ((len1 = is1.read(buffer)) != -1) {

                                bytes = (bytes + len1);

                                // data.value = (int) ((double) (((double)
                                // bytes) / length) * 100);

                                fos.write(buffer, 0, len1);
                            }
                            fos.close();
                            is1.close();
                        }
                    }
                }
            }

        }
        //endregion

        //region mapping window Images
        if (mappingWObject != null) {

            for (int i = 0; i < mappingWObject.getMappingWindow().size(); i++) {

                String image_name = mappingWObject.getMappingWindow().get(i).getPlanogramImage();
                if (image_name != null && !image_name.equalsIgnoreCase("NA")
                        && !image_name.equalsIgnoreCase("")) {
                    URL url = new URL(mappingWObject.getMappingWindow().get(i).getImagePath() + image_name);
                    HttpURLConnection c = (HttpURLConnection) url.openConnection();
                    c.setRequestMethod("GET");
                    c.getResponseCode();
                    c.connect();

                    if (c.getResponseCode() == 200) {
                        int length = c.getContentLength();
                        String size = new DecimalFormat("##.##")
                                .format((double) ((double) length / 1024))
                                + " KB";

                        String PATH = Environment
                                .getExternalStorageDirectory()
                                + "/GT_GSK_Images/";
                        File file = new File(CommonString.FILE_PATH_Downloaded);
                        file.mkdirs();

                        if (!new File(CommonString.FILE_PATH_Downloaded
                                + image_name).exists()
                                && !size.equalsIgnoreCase("0 KB")) {

                            jj = image_name.split("\\/");
                            image_name = jj[jj.length - 1];

                            File outputFile = new File(file,
                                    image_name);
                            FileOutputStream fos = new FileOutputStream(
                                    outputFile);
                            InputStream is1 = (InputStream) c
                                    .getInputStream();

                            int bytes = 0;
                            byte[] buffer = new byte[1024];
                            int len1 = 0;

                            while ((len1 = is1.read(buffer)) != -1) {

                                bytes = (bytes + len1);

                                // data.value = (int) ((double) (((double)
                                // bytes) / length) * 100);

                                fos.write(buffer, 0, len1);

                            }

                            fos.close();
                            is1.close();

                        }
                    }
                }
            }

        }
        //endregion

    }
*/
    //endregion

    //region createTable
/*
    String createTable(TableStructureGetterSetter tableGetSet) {
        List<TableQuery> tableList = tableGetSet.getResult();
        for (int i = 0; i < tableList.size(); i++) {
            String table = tableList.get(i).getSqlText();
            if (db.createtable(table) == 0) {
                return table;
            }
        }
        return CommonString.KEY_SUCCESS;
    }
*/
    //endregion

    //region downloadDataUniversalWithoutWait
/*
    public void downloadDataUniversalWithoutWait(final ArrayList<String> jsonStringList, final ArrayList<String> KeyNames, int downloadindex, int type) {
        status = 0;
        isvalid = false;
        final String[] data_global = {""};
        String jsonString = "", KeyName = "";
        int jsonIndex = 0;

        if (jsonStringList.size() > 0) {

            jsonString = jsonStringList.get(downloadindex);
            KeyName = KeyNames.get(downloadindex);
            jsonIndex = downloadindex;

            pd.setMessage("Downloading (" + downloadindex + "/" + listSize + ") \n" + KeyName + "");
            RequestBody jsonData = RequestBody.create(MediaType.parse("application/json"), jsonString);
            adapter = new Retrofit.Builder().baseUrl(CommonString.URL2).addConverterFactory(GsonConverterFactory.create()).build();
            PostApi api = adapter.create(PostApi.class);
            Call<ResponseBody> call = null;

            if (type == CommonString.LOGIN_SERVICE) {
                call = api.getLogindetail(jsonData);
            } else if (type == CommonString.DOWNLOAD_ALL_SERVICE) {
                call = api.getDownloadAll(jsonData);
            } else if (type == CommonString.COVERAGE_DETAIL) {
                call = api.getCoverageDetail(jsonData);
            } else if (type == CommonString.UPLOADJCPDetail) {
                call = api.getUploadJCPDetail(jsonData);
            } else if (type == CommonString.UPLOADJsonDetail) {
                call = api.getUploadJsonDetail(jsonData);
            } else if (type == CommonString.COVERAGEStatusDetail) {
                call = api.getCoverageStatusDetail(jsonData);
            } else if (type == CommonString.CHECKOUTDetail) {
                call = api.getCheckout(jsonData);
            } else if (type == CommonString.DELETE_COVERAGE) {
                call = api.deleteCoverageData(jsonData);
            }

            final int[] finalJsonIndex = {jsonIndex};
            final String finalKeyName = KeyName;
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    ResponseBody responseBody = response.body();
                    String data = null;
                    if (responseBody != null && response.isSuccessful()) {
                        try {
                            data = response.body().string();
                            if (data.equalsIgnoreCase("")) {
                                data_global[0] = "";

                            } else {
                                data = data.substring(1, data.length() - 1).replace("\\", "");
                                data_global[0] = data;
                                if (finalKeyName.equalsIgnoreCase("Table_Structure")) {

                                    editor.putInt(CommonString.KEY_DOWNLOAD_INDEX, finalJsonIndex[0]);
                                    editor.apply();
                                    tableStructureObj = new Gson().fromJson(data, TableStructureGetterSetter.class);
                                    String isAllTableCreated = createTable(tableStructureObj);
                                    if (isAllTableCreated != CommonString.KEY_SUCCESS) {
                                        pd.dismiss();
                                        AlertandMessages.showAlert((Activity) context, isAllTableCreated + " not created", true);
                                    }
                                } else {
                                    editor.putInt(CommonString.KEY_DOWNLOAD_INDEX, finalJsonIndex[0]);
                                    editor.apply();
                                    switch (finalKeyName) {
                                        case "Journey_Plan":
                                            jcpObject = new Gson().fromJson(data, JCPGetterSetter.class);
                                            if (jcpObject != null && !db.insertJCPData(jcpObject)) {
                                                pd.dismiss();
                                                AlertandMessages.showSnackbarMsg(context, "JCP data not saved");
                                            }
                                            break;
                                        case "Non_Working_Reason":
                                            nonWorkingObj = new Gson().fromJson(data, NonWorkingReasonGetterSetter.class);
                                            if (nonWorkingObj != null && !db.insertNonWorkingData(nonWorkingObj)) {
                                                pd.dismiss();
                                                AlertandMessages.showSnackbarMsg(context, "Non Working Reason not saved");
                                            }
                                            break;
                                        case "non_working_sub_reason":
                                            nonWorkingSubObj = new Gson().fromJson(data, NonWorkingSubReasonGetterSetter.class);
                                            if (nonWorkingSubObj != null && !db.insertNonWorkingSubReasonData(nonWorkingSubObj)) {
                                                pd.dismiss();
                                                AlertandMessages.showSnackbarMsg(context, "Non Working Sub Reason not saved");
                                            }
                                            break;
                                        case "Sku_Master":
                                            skuObject = new Gson().fromJson(data, SkuMasterGetterSetter.class);
                                            if (skuObject != null && !db.InsertSkuMaster(skuObject)) {
                                                pd.dismiss();
                                                AlertandMessages.showSnackbarMsg(context, "Sku Master Reason not saved");
                                            }
                                            break;
                                        case "Category_Master":
                                            categoryObject = new Gson().fromJson(data, CategoryMasterGetterSetter.class);
                                            if (categoryObject != null && !db.InsertCategoryMaster(categoryObject)) {
                                                pd.dismiss();
                                                AlertandMessages.showSnackbarMsg(context, "Category Master data not saved");
                                            }
                                            break;
                                        case "Brand_Group_Master":
                                            brandGObject = new Gson().fromJson(data, BrandGroupMasterGetterSetter.class);
                                            if (brandGObject != null && !db.InsertBrandGroupMaster(brandGObject)) {
                                                pd.dismiss();
                                                AlertandMessages.showSnackbarMsg(context, "Brand Group data not saved");
                                            }
                                            break;
                                        case "Brand_Master":
                                            brandMObject = new Gson().fromJson(data, BrandMasterGetterSetter.class);
                                            if (brandMObject != null && !db.InsertBrandMaster(brandMObject)) {
                                                pd.dismiss();
                                                AlertandMessages.showSnackbarMsg(context, "Brand Master not saved");
                                            }
                                            break;
                                        case "Window_Master":
                                            windowMObject = new Gson().fromJson(data, WindowMasterGetterSetter.class);
                                            if (windowMObject != null && !db.insertWindowMaster(windowMObject)) {
                                                pd.dismiss();
                                                AlertandMessages.showSnackbarMsg(context, "Window Master not saved");
                                            }
                                            break;
                                        case "Window_Checklist":
                                            windowCObject = new Gson().fromJson(data, WindowChecklistGetterSetter.class);
                                            if (windowCObject != null && !db.insertWindowChecklist(windowCObject)) {
                                                pd.dismiss();
                                                AlertandMessages.showSnackbarMsg(context, "Window Checklist not saved");
                                            }
                                            break;
                                        case "Posm_Master":
                                            posmMObject = new Gson().fromJson(data, PosmMasterGetterSetter.class);
                                            if (posmMObject != null && !db.insertPosmMaster(posmMObject)) {
                                                pd.dismiss();
                                                AlertandMessages.showSnackbarMsg(context, "Posm Master not saved");
                                            }
                                            break;
                                        case "Window_Location":
                                            windowLObject = new Gson().fromJson(data, WindowLocationGetterSetter.class);
                                            if (windowLObject != null && !db.insertWindowLocation(windowLObject)) {
                                                pd.dismiss();
                                                AlertandMessages.showSnackbarMsg(context, "Window Location not saved");
                                            }
                                            break;
                                        case "Mapping_Window":
                                            mappingWObject = new Gson().fromJson(data, MappingWindowGetterSetter.class);
                                            if (mappingWObject != null && !db.insertMappingWindow(mappingWObject)) {
                                                pd.dismiss();
                                                AlertandMessages.showSnackbarMsg(context, "Mapping Window not saved");
                                            }
                                            break;
                                        case "Mapping_Stock":
                                            mappingSObject = new Gson().fromJson(data, MappingStockGetterSetter.class);
                                            if (mappingSObject != null && !db.insertMappingStock(mappingSObject)) {
                                                pd.dismiss();
                                                AlertandMessages.showSnackbarMsg(context, "Mapping Stock not saved");
                                            }
                                            break;
                                        case "Mapping_Selfservice_Brand_Group":
                                            mappingBrandGroupObject = new Gson().fromJson(data, MappingBrandGroupGetterSetter.class);
                                            if (mappingBrandGroupObject != null && !db.insertMappingBrandGroup(mappingBrandGroupObject)) {
                                                pd.dismiss();
                                                AlertandMessages.showSnackbarMsg(context, "Mapping Brand Group not saved");
                                            }
                                            break;
                                        case "Primary_Bay_Master":
                                            primaryBayMasterObject = new Gson().fromJson(data, PrimaryBayMasterGetterSetter.class);
                                            if (primaryBayMasterObject != null && !db.insertPrimaryBayMaster(primaryBayMasterObject)) {
                                                pd.dismiss();
                                                AlertandMessages.showSnackbarMsg(context, "Primary Bay Master not saved");
                                            }
                                            break;
                                        case "Mapping_Selfservice_Category":
                                            mappingSSCategoryObject = new Gson().fromJson(data, MappingSelfServiceCategoryGetterSetter.class);
                                            if (mappingSSCategoryObject != null && !db.insertMappingSelfserviceCategory(mappingSSCategoryObject)) {
                                                pd.dismiss();
                                                AlertandMessages.showSnackbarMsg(context, "Mapping Self Service Category not saved");
                                            }
                                            break;
                                        case "Display_Master":
                                            displayMasterObject = new Gson().fromJson(data, DisplayMasterGetterSetter.class);
                                            if (displayMasterObject != null && !db.insertDisplayMaster(displayMasterObject)) {
                                                pd.dismiss();
                                                AlertandMessages.showSnackbarMsg(context, "Display Master not saved");
                                            }
                                            break;
                                        case "Display_Type_Master":
                                            displayTypeMasterObject = new Gson().fromJson(data, DisplayTypeMasterGetterSetter.class);
                                            if (displayTypeMasterObject != null && !db.insertDisplayTypeMaster(displayTypeMasterObject)) {
                                                pd.dismiss();
                                                AlertandMessages.showSnackbarMsg(context, "Display Master not saved");
                                            }
                                            break;
                                        case "Display_Term_Master":
                                            displayTermMasterObject = new Gson().fromJson(data, DisplayTermMasterGetterSetter.class);
                                            if (displayTermMasterObject != null && !db.insertDisplayTermMaster(displayTermMasterObject)) {
                                                AlertandMessages.showSnackbarMsg(context, "Display Master not saved");
                                            }
                                            break;
                                        case "Mapping_Selfservice_Category_Display":
                                            mappingCDObject = new Gson().fromJson(data, MappingSelfserviceCategoryDisplayGetterSetter.class);
                                            if (mappingCDObject != null && !db.insertMappingCategoryDisplayMaster(mappingCDObject)) {
                                                pd.dismiss();
                                                AlertandMessages.showSnackbarMsg(context, "Display Master not saved");
                                            }
                                            break;
                                        case "Promo_Type_Master":
                                            promoObject = new Gson().fromJson(data, PromoTypeMasterGetterSetter.class);
                                            if (promoObject != null && !db.insertPromoTypeMaster(promoObject)) {
                                                pd.dismiss();
                                                AlertandMessages.showSnackbarMsg(context, "Promo Master not saved");
                                            }
                                            break;
                                        case "Deviation_Journey_Plan":
                                            deviationObject = new Gson().fromJson(data, DeviationJourneyPlanGetterSetter.class);
                                            if (deviationObject != null && !db.insertDeviationJourneyPlan(deviationObject)) {
                                                pd.dismiss();
                                                AlertandMessages.showSnackbarMsg(context, "Deviation Journey Plan not saved");
                                            }
                                            break;
                                        case "Topup_City":
                                            topUpObject = new Gson().fromJson(data, TopupCityGetterSetter.class);
                                            if (topUpObject != null && !db.insertTopUpCity(topUpObject)) {
                                                pd.dismiss();
                                                AlertandMessages.showSnackbarMsg(context, "TopUp City Plan not saved");
                                            }
                                            break;
                                        case "Mydb_Distributor":
                                            distributerObject = new Gson().fromJson(data, MydbDistributorGetterSetter.class);
                                            if (distributerObject != null && !db.insertMyDbDistributor(distributerObject)) {
                                                pd.dismiss();
                                                AlertandMessages.showSnackbarMsg(context, "MyDb Distributer Plan not saved");
                                            }
                                            break;
                                        case "Mapping_Posm_MonthlyPriority":
                                            mapPMPObject = new Gson().fromJson(data, MappingPosmMonthlyPriorityGetterSetter.class);
                                            if (mapPMPObject != null && !db.insertMappingPosmMonthlyPriority(mapPMPObject)) {
                                                pd.dismiss();
                                                AlertandMessages.showSnackbarMsg(context, "MyDb Posm Monthly Priority not saved");
                                            }
                                            break;
                                        case "Mapping_Posm_MustHave":
                                            mapPMHObject = new Gson().fromJson(data, MappingMappingPosmMustHaveGetterSetter.class);
                                            if (mapPMHObject != null && !db.insertMappingPosmMustHave(mapPMHObject)) {
                                                pd.dismiss();
                                                AlertandMessages.showSnackbarMsg(context, "MyDb Posm Must Have not saved");
                                            }

                                            break;
                                        case "Mapping_Posm_Activity":
                                            mapPAObject = new Gson().fromJson(data, MappingMappingPosmActivityGetterSetter.class);
                                            if (mapPAObject != null && !db.insertMappingPosmActivity(mapPAObject)) {
                                                pd.dismiss();
                                                AlertandMessages.showSnackbarMsg(context, "MyDb Posm Activity not saved");
                                            }

                                            break;
                                        case "Mapping_Posm":
                                            mappingPObject = new Gson().fromJson(data, MappingPosmGetterSetter.class);
                                            if (mappingPObject != null && !db.insertMappingPosm(mappingPObject)) {
                                                pd.dismiss();
                                                AlertandMessages.showSnackbarMsg(context, "Mapping Posm not saved");
                                            }
                                            break;
                                        case "My_performance_Mer":
                                            myPerformanceObject = new Gson().fromJson(data, MyPerformanceGetterSetter.class);
                                            if (myPerformanceObject != null && !db.insertMyPerformanceMer(myPerformanceObject)) {
                                                pd.dismiss();
                                                AlertandMessages.showSnackbarMsg(context, "MyDb My performance Mer not saved");
                                            }
                                            break;
                                        case "My_performance_Routewise_Mer":
                                            myPRObject = new Gson().fromJson(data, MyPerformanceRoutewiseGetterSetter.class);
                                            if (myPRObject != null && !db.insertMyperformanceRoutewiseMer(myPRObject)) {
                                                pd.dismiss();
                                                AlertandMessages.showSnackbarMsg(context, "MyDb My performance Routewise Mer not saved");
                                            }
                                            break;
                                        case "Store_Layout_Master":
                                            storeLayoutObject = new Gson().fromJson(data, StoreLayoutMasterGetterSetter.class);
                                            if (storeLayoutObject != null && !db.insertStoreLayout(storeLayoutObject)) {
                                                pd.dismiss();
                                                AlertandMessages.showSnackbarMsg(context, "Store Layout not saved");
                                            }
                                            break;
                                        case "Store_Size_Master":
                                            storeSizeObject = new Gson().fromJson(data, StoreSizeMasterGetterSetter.class);
                                            if (storeSizeObject != null && !db.insertStoreSize(storeSizeObject)) {
                                                pd.dismiss();
                                                AlertandMessages.showSnackbarMsg(context, "Store Size not saved");
                                            }
                                            break;
                                        case "Store_Classification_Master":
                                            storeClassMasterObject = new Gson().fromJson(data, StoreClassificationMasterGetterSetter.class);
                                            if (storeClassMasterObject != null && !db.insertStoreClassificationMaster(storeClassMasterObject)) {
                                                pd.dismiss();
                                                AlertandMessages.showSnackbarMsg(context, "Store Classification Master not saved");
                                            }
                                            break;
                                        case "Store_Type_Master":
                                            storeTypeMasterObject = new Gson().fromJson(data, StoreTypeMasterGetterSetter.class);
                                            if (storeTypeMasterObject != null && !db.insertStoreTypeMaster(storeTypeMasterObject)) {
                                                pd.dismiss();
                                                AlertandMessages.showSnackbarMsg(context, "Store Type Master not saved");
                                            }
                                            break;
                                        case "Topup_Distributor":
                                            topupDistributorObject = new Gson().fromJson(data, TopupDistributorGetterSetter.class);
                                            if (topupDistributorObject != null && !db.insertTopupDistributor(topupDistributorObject)) {
                                                pd.dismiss();
                                                AlertandMessages.showSnackbarMsg(context, "Top Up Distributer not saved");
                                            }
                                            break;
                                        case "Mapping_Primary_Category_Images":
                                            mapPCIObject = new Gson().fromJson(data, MappingPrimaryCategoryImagesGetterSetter.class);
                                            if (mapPCIObject != null && !db.insertMappingMyDbPCI(mapPCIObject)) {
                                                pd.dismiss();
                                                AlertandMessages.showSnackbarMsg(context, "MyDb Posm Plan not saved");
                                            }
                                            break;
                                        case "Store_Category_Master":
                                            storeCategoryMasterObject = new Gson().fromJson(data, StoreCategoryMasterGetterSetter.class);
                                            if (storeCategoryMasterObject != null && !db.insertStoreCategoryMaster(storeCategoryMasterObject)) {
                                                pd.dismiss();
                                                AlertandMessages.showSnackbarMsg(context, "Store_Category_Master not saved");
                                            }
                                            break;
                                        case "Mapping_Mydb_Posm_MustHave":
                                            mappingMydbPosmMustHaveGetterSetter = new Gson().fromJson(data, MappingMydbPosmMustHaveGetterSetter.class);
                                            if (mappingMydbPosmMustHaveGetterSetter != null && !db.insertMappingMydbPosmMustHave(mappingMydbPosmMustHaveGetterSetter)) {
                                                pd.dismiss();
                                                AlertandMessages.showSnackbarMsg(context, "Mapping Mydb Posm MustHave not saved");
                                            }
                                            break;
                                        case "Mapping_Mydb_Posm_MonthlyPriority":
                                            mappingMydbPosmMonthlyPriorityGetterSetter = new Gson().fromJson(data, MappingMydbPosmMonthlyPriorityGetterSetter.class);
                                            if (mappingMydbPosmMonthlyPriorityGetterSetter != null && !db.insertMappingMydbPosmMonthlyPriority(mappingMydbPosmMonthlyPriorityGetterSetter)) {
                                                pd.dismiss();
                                                AlertandMessages.showSnackbarMsg(context, "Mapping Mydb Posm Monthly Priority not saved");
                                            }
                                            break;
                                        case "Mapping_Mydb_Posm_Activity":
                                            mappingMydbPosmActivityGetterSetter = new Gson().fromJson(data, MappingMydbPosmActivityGetterSetter.class);
                                            if (mappingMydbPosmActivityGetterSetter != null && !db.insertMappingMydbPosmActivity(mappingMydbPosmActivityGetterSetter)) {
                                                pd.dismiss();
                                                AlertandMessages.showSnackbarMsg(context, "Mapping Mydb PosmActivity not saved");
                                            }
                                            break;
                                        case "Mapping_Mydb_Posm_Others":
                                            mappingMydbPosmOthersGetterSetter = new Gson().fromJson(data, MappingMydbPosmOthersGetterSetter.class);
                                            if (mappingMydbPosmOthersGetterSetter != null && !db.insertMappingMydbPosmOthers(mappingMydbPosmOthersGetterSetter)) {
                                                pd.dismiss();
                                                AlertandMessages.showSnackbarMsg(context, "Mapping Mydb Posm Others not saved");
                                            }
                                            break;
                                        case "Kyc_Master":
                                            kycMasterGetterSetter = new Gson().fromJson(data, KycMasterGetterSetter.class);
                                            if (kycMasterGetterSetter != null && !db.insertkycMasterData(kycMasterGetterSetter)) {
                                                pd.dismiss();
                                                AlertandMessages.showSnackbarMsg(context, "KYC Master Data not saved");
                                            }
                                            break;
                                        case "Documents_Data":
                                            //documentsDataGetterSetter =  new Gson().fromJson(data, DocumentsDataGetterSetter.class);
                                            //if (documentsDataGetterSetter != null && !db.insertDocumentData(documentsDataGetterSetter)) {
                                            //pd.dismiss();
                                            //    AlertandMessages.showSnackbarMsg(context, "Document Data not saved");
                                            //}
                                            break;
                                    }
                                }
                            }
                            // jsonStringList.remove(finalJsonIndex);
                            // KeyNames.remove(finalJsonIndex);
                            finalJsonIndex[0]++;
                            if (finalJsonIndex[0] != KeyNames.size()) {
                                editor.putInt(CommonString.KEY_DOWNLOAD_INDEX, finalJsonIndex[0]);
                                editor.apply();
                                downloadDataUniversalWithoutWait(jsonStringList, KeyNames, finalJsonIndex[0], CommonString.DOWNLOAD_ALL_SERVICE);
                            } else {
                                editor.putInt(CommonString.KEY_DOWNLOAD_INDEX, 0);
                                editor.apply();
                                //pd.dismiss();
                                //AlertandMessages.showAlert((Activity) context, "All data downloaded Successfully", true);
                                //downloadImages();
                                pd.setMessage("Downloading Images");
                                new DownloadImageTask().execute();
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                            editor.putInt(CommonString.KEY_DOWNLOAD_INDEX, finalJsonIndex[0]);
                            editor.apply();
                            pd.dismiss();
                            AlertandMessages.showAlert((Activity) context, "Error in downloading Data at " + finalKeyName, true);
                        }
                    } else {
                        editor.putInt(CommonString.KEY_DOWNLOAD_INDEX, finalJsonIndex[0]);
                        editor.apply();
                        pd.dismiss();
                        AlertandMessages.showAlert((Activity) context, "Error in downloading Data at " + finalKeyName, true);

                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    isvalid = true;
                    pd.dismiss();
                    if (t instanceof SocketTimeoutException) {
                        AlertandMessages.showAlert((Activity) context, CommonString.MESSAGE_INTERNET_NOT_AVALABLE, true);
                    } else if (t instanceof IOException) {
                        AlertandMessages.showAlert((Activity) context, CommonString.MESSAGE_INTERNET_NOT_AVALABLE, true);
                    } else if (t instanceof SocketException) {
                        AlertandMessages.showAlert((Activity) context, CommonString.MESSAGE_INTERNET_NOT_AVALABLE, true);
                    } else {
                        AlertandMessages.showAlert((Activity) context, CommonString.MESSAGE_INTERNET_NOT_AVALABLE, true);
                    }

                }
            });

        } else {
            editor.putInt(CommonString.KEY_DOWNLOAD_INDEX, 0);
            editor.apply();
            // pd.dismiss();
            // AlertandMessages.showAlert((Activity) context, "All data downloaded Successfully", true);
            pd.setMessage("Downloading Images");
            new DownloadImageTask().execute();


        }

    }
*/
    //endregion

    //region StringConverterFactory
/*
    class StringConverterFactory extends retrofit2.Converter.Factory implements retrofit.Converter.Factory {
        private StringConverterFactory() {
        }

        @Override
        public retrofit.Converter<String> get(Type type) {
            Class<?> cls = (Class<?>) type;
            if (String.class.isAssignableFrom(cls)) {
                return new StringConverter();
            }
            return null;
        }
    }
*/
    //endregion

    //region StringConverter
/*
    private static class StringConverter implements retrofit.Converter<String> {
        private static final MediaType PLAIN_TEXT = MediaType.parse("text/plain; charset=UTF-8");

        @Override
        public String fromBody(com.squareup.okhttp.ResponseBody body) throws IOException {
            return new String(body.bytes());
        }

        @Override
        public com.squareup.okhttp.RequestBody toBody(String value) {
            // return RequestBody.create(PLAIN_TEXT, convertToBytes(value));
            return com.squareup.okhttp.RequestBody.create(com.squareup.okhttp.MediaType.parse("multipart/form-data"), convertToBytes(value));
        }

        private static byte[] convertToBytes(String string) {
            try {
                return string.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
    }
*/
    //endregion


}
