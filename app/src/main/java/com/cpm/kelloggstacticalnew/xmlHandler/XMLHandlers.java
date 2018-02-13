package com.cpm.kelloggstacticalnew.xmlHandler;

import com.cpm.kelloggstacticalnew.getterSetter.DistributerListGetterSetter;
import com.cpm.kelloggstacticalnew.getterSetter.FailureGetterSetter;
import com.cpm.kelloggstacticalnew.getterSetter.LoginGetterSetter;
import com.cpm.kelloggstacticalnew.getterSetter.NonDeploymentReasonGetterSetter;
import com.cpm.kelloggstacticalnew.getterSetter.NonWorkingReasonGetterSetter;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by deepakp on 1/4/2018.
 */

public class XMLHandlers {

    public static FailureGetterSetter failureXMLHandler(XmlPullParser xpp, int eventType) {
        FailureGetterSetter failureGetterSetter = new FailureGetterSetter();
        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("STATUS")) {
                        failureGetterSetter.setStatus(xpp.nextText());
                    }
                    if (xpp.getName().equals("ERRORMSG")) {
                        failureGetterSetter.setErrorMsg(xpp.nextText());
                    }

                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return failureGetterSetter;
    }

    public static LoginGetterSetter loginXMLHandler(XmlPullParser xpp, int eventType) {
        LoginGetterSetter lgs = new LoginGetterSetter();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {

                    if (xpp.getName().equals("SUCCESS")) {
                        lgs.setResult(xpp.nextText());
                    }
                    if (xpp.getName().equals("APP_VERSION")) {
                        lgs.setVERSION(xpp.nextText());
                    }
                    if (xpp.getName().equals("APP_PATH")) {
                        lgs.setPATH(xpp.nextText());
                    }
                    if (xpp.getName().equals("CURRENTDATE")) {
                        lgs.setDATE(xpp.nextText());
                    }

                    if (xpp.getName().equals("RIGHTNAME")) {
                        lgs.setRIGHTNAME(xpp.nextText());
                    }
                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return lgs;
    }

    public static DistributerListGetterSetter DistributerListXMLHandler(XmlPullParser xpp, int eventType) {
        DistributerListGetterSetter lgs = new DistributerListGetterSetter();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {

                    if (xpp.getName().equals("META_DATA")) {
                        lgs.setTable_DistributerList(xpp.nextText());
                    }

                    if (xpp.getName().equals("KEYACCOUNT_CD")) {
                        lgs.setKEYACCOUNT_CD(xpp.nextText());
                    }
                    if (xpp.getName().equals("KEYACCOUNT")) {
                        lgs.setKEYACCOUNT(xpp.nextText());
                    }
                    if (xpp.getName().equals("ROUTE_NAME")) {
                        lgs.setROUTE_NAME(xpp.nextText());
                    }
                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return lgs;
    }


    public static DistributerListGetterSetter SearchListXMLHandler(XmlPullParser xpp, int eventType) {
        DistributerListGetterSetter lgs = new DistributerListGetterSetter();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {

                    if (xpp.getName().equals("META_DATA")) {
                        lgs.setTable_DistributerList(xpp.nextText());
                    }
                    if (xpp.getName().equals("STORE_CD")) {
                        lgs.setSTORE_CD(xpp.nextText());
                    }
                    if (xpp.getName().equals("KEYACCOUNT")) {
                        lgs.setKEYACCOUNT(xpp.nextText());
                    }
                    if (xpp.getName().equals("STORENAME")) {
                        lgs.setSTORENAME(xpp.nextText());
                    }
                    if (xpp.getName().equals("ADDRESS")) {
                        lgs.setADDRESS(xpp.nextText());
                    }
                    if (xpp.getName().equals("CITY")) {
                        lgs.setCITY(xpp.nextText());
                    }
                    if (xpp.getName().equals("STORETYPE")) {
                        lgs.setSTORETYPE(xpp.nextText());
                    }
                    if (xpp.getName().equals("ROUTE_NAME")) {
                        lgs.setROUTE_NAME(xpp.nextText());
                    }
                    if (xpp.getName().equals("VISIT_DATE")) {
                        lgs.setVISIT_DATE(xpp.nextText());
                    }
                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return lgs;
    }

    public static NonWorkingReasonGetterSetter nonWorkinReasonXML(XmlPullParser xpp,
                                                                  int eventType) {
        NonWorkingReasonGetterSetter nonworking = new NonWorkingReasonGetterSetter();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {

                    if (xpp.getName().equals("META_DATA")) {
                        nonworking.setNonworking_table(xpp.nextText());
                    }
                    if (xpp.getName().equals("REASON_CD")) {
                        nonworking.setReason_cd(xpp.nextText());
                    }
                    if (xpp.getName().equals("REASON")) {
                        nonworking.setReason(xpp.nextText());
                    }
                    if (xpp.getName().equals("ENTRY_ALLOW")) {
                        nonworking.setEntry_allow(xpp.nextText());
                    }
                    if (xpp.getName().equals("IMAGE_ALLOW")) {
                        nonworking.setIMAGE_ALLOW(xpp.nextText());
                    }
                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return nonworking;
    }


    public static NonDeploymentReasonGetterSetter nonDeploymentReasonXML(XmlPullParser xpp,
                                                                         int eventType) {
        NonDeploymentReasonGetterSetter nonworking = new NonDeploymentReasonGetterSetter();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {

                    if (xpp.getName().equals("META_DATA")) {
                        nonworking.setTable_NON_DEPLOYMENT_REASON(xpp.nextText());
                    }
                    if (xpp.getName().equals("NDREASON_CD")) {
                        nonworking.setNDREASON_CD(xpp.nextText());
                    }
                    if (xpp.getName().equals("NDREASON")) {
                        nonworking.setNDREASON(xpp.nextText());
                    }
                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return nonworking;
    }

}
