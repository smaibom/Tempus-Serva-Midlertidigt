/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package activities;

import dk.p2e.blanket.codeunit.CodeunitPagecontent;
import dk.p2e.blanket.form.Command;
import dk.p2e.blanket.form.Security;
import dk.p2e.blanket.form.handler.QueryPart;
import dk.p2e.util.Systemout;
import dk.tempusserva.api.Session;
import dk.tempusserva.api.SessionFactory;
import dk.tempusserva.api.SolutionQuery;
import dk.tempusserva.api.SolutionQueryResultSet;
import dk.tempusserva.api.SolutionRecord;
import dk.tempusserva.api.SolutionRecordNew;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Class for calling and parsing APIs from Axentia and in the future MultiQ
 * Currently it is a CodeunitPagecontent for testing purposes, in the future change to a
 * codeunit that runs on a set timer on Tempus Serva platform. 
 * It is required that you use the compile flag -encoding UTF-8 to prevent encoding related errors
 * @author XSMA
 */
public class AxentiaAPI extends CodeunitPagecontent {
    
    private final int initStatusID = 95;


    @Override
    public String execute(Command cmnd, Security scrt, Hashtable hshtbl) {
        String url = "https://ibusapi.axentia.se/api/Display/all/status/warning?api-version=1.0";
        String apikey = "";
        Session ses = SessionFactory.getSession(this);
        try {
            //String res = callApi(url,apikey);
            //parseAxentia(res);
            SolutionRecordNew sr = createNewWorkorder("Fejl","Batteri - Lavt","Testing",16097,0, ses);
            if(sr != null){
                sr.persistChanges();
            }
        } catch (Exception ex) {
            Logger.getLogger(AxentiaAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
        ses.close();
        return "";

    }

    
    
    /**
     * Performs a get call on a given url and api key. Meant to be used on axentia's api. Made generic enough for the future adding of MultiQ
     * Axentias api is documented on https://ibusapi.axentia.se/swagger/v1/swagger.json 
     * @param url String value with the url
     * @param apikey String value with the apikey 
     * @return String value with the response. 
     * @throws Exception On failed 
     */
    private String callApi(String url, String apikey) throws Exception{
        StringBuilder res = new StringBuilder();
        
        try{
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpGet httpget = new HttpGet(url);
            httpget.addHeader("apikey", apikey );
            
            HttpResponse httpresponse = httpclient.execute(httpget);

            if(httpresponse.getStatusLine().getStatusCode() == 200){
                Scanner sc = new Scanner(httpresponse.getEntity().getContent(),"UTF-8");
                while(sc.hasNext()) {
                    res.append(sc.nextLine());
                }
            }
            else{
                httpclient.close();
                throw new Exception("callApi did not get statuscode 200");
            }
            httpclient.close();

        }
        catch(Exception e){
            Systemout.println(e.getMessage());
            Systemout.println("CallAPI function needs logging");
        }
        return res.toString();
    }
    
    
    /**
     * Parses the axentia json that is returned from the API, the documentation for the 
     * fields can be found on https://ibusapi.axentia.se/swagger/v1/swagger.json 
     * @param json String with the json containing information from all modules
     */
    private void parseAxentia(String json){
                            
        try{
            JSONArray jsonList = (JSONArray) new JSONParser().parse(json);
            for(int i = 0; i < jsonList.size(); i++){
                JSONObject jsonObj = (JSONObject) jsonList.get(i);
                createAxentiaRecords(jsonObj);
            }
        }
        catch(ParseException e){
            Systemout.println("In parseAxentia");
            Systemout.println(e.toString());
        }
    }
    
    /**
     * Creates the TempusServa records from the json api string. Wrires to the 
     * @param jsonObj JSONObject containing the information from 1 module
     */
    private void createAxentiaRecords(JSONObject jsonObj){
        HashMap<String,String> stringValues = new HashMap<>();
        long id = (long) jsonObj.get("ibusId");
        long batteryStatus = (long) jsonObj.get("batteryStatus");
        long uptime = (long) jsonObj.get("upTime");
        long errorCode = (long) jsonObj.get("errorCode");
        String displayMode = convertDisplayModeCode((long) jsonObj.get("displayMode"));
        
        //String values is simply to handle empty or null return values in the string fields.
        stringValues.put("DEVICECOMMENTS", (String) jsonObj.get("displayComments"));
        stringValues.put("FIRMWAREVERSION", (String) jsonObj.get("firmware"));
        stringValues.put("BATTERYVOLTAGE", (String) jsonObj.get("batteryVoltage"));
        stringValues.put("STOPPOINTID", (String) jsonObj.get("stopPointId"));
        stringValues.put("STOPPOINTNAME", (String) jsonObj.get("stopPointName"));
        stringValues.put("DESCRIPTION", (String) jsonObj.get("statusDescription"));
        
        String lastDataPackage = parseAxentiaDate((String) jsonObj.get("lastDataPacket"));
        String timestamp = parseAxentiaDate((String) jsonObj.get("timeStamp"));

        JSONObject position = (JSONObject) jsonObj.get("displayPosition");
        Boolean isSet = (Boolean) position.get("isSet");
        Double latitude = (Double) position.get("latitude");
        Double longitude = (Double) position.get("longitude");
        

        
        stringValues.values().removeIf(Objects::isNull);

        Session session = SessionFactory.getSession(this);
        try{
            SolutionRecord deviceSR = lookupDevice((int) id, session);
            if(deviceSR != null){

                SolutionRecordNew record = session.getSolutionRecordNew("statuslog");
                stringValues.entrySet().forEach((entry) -> {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    record.setValue(key, value);
                });
                if(isSet){
                    String pos = String.valueOf(latitude) + " " + String.valueOf(longitude);
                    record.setValue("DEVICEPOSITION", pos);
                }

                record.setValue("TIMESTAMP", timestamp);
                record.setValue("LASTUPDATE", lastDataPackage);
                record.setValueInteger("BATTERYSTATUS", Math.toIntExact(batteryStatus));
                record.setValue("ERRORCODE", String.valueOf(errorCode));
                record.setValueInteger("UPTIME", Math.toIntExact(uptime));
                record.setReference("DEVICEID", deviceSR);
                record.setValue("DISPLAYMODE", displayMode);
                record.setValueInteger("StatusID", initStatusID);
                record.persistChanges();     

            }
        }
        catch (Exception e){
            Systemout.println("createAxentiaRecords in CallAPI class: Temp error handling fix this");
            Systemout.println(e.toString());
        }
        session.close();
    }
    

    /**
     * Parses an axentia date format into a Date object
     * @param dateString String date to be converted to a date object
     * @return A Date object with the value according to the string value, null if the string could not be parsed
     */
    private String parseAxentiaDate(String dateString){
        List<SimpleDateFormat> dateFormats = new ArrayList<>();
        
        dateFormats.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"));
        dateFormats.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        for(SimpleDateFormat pattern : dateFormats){
            try {
                Date dt = pattern.parse(dateString);
                String res = dateFormat.format(dt);
                return res;
            } 
            catch (java.text.ParseException ex) {
                //Do nothing next loop
            }
        }
        
        
        
        return "";
    }
    
    
    /**
     * Convert a status code from axentia to its meaning
     * @param statusCode Int value representing the status code
     * @return A String that represents the corrosponding statuscode, if unknown statuscode empty string is returned
     */
    private String convertDisplayModeCode(long statusCode){
        int code = Math.toIntExact(statusCode);
        HashMap<Integer,String> convertionTable = new HashMap<>();
        convertionTable.put(0,"Not mounted [disabled / off]");
        convertionTable.put(1,"In Operation");
        convertionTable.put(2,"In Operation [not mounted on site]");
        convertionTable.put(3,"In Operation [demo / mobile]");
        convertionTable.put(4,"Service / Repair");
        convertionTable.put(5,"Mounted [disabled / off]");
        
        if(convertionTable.containsKey(code)){
            return convertionTable.get(code);
        }
        return "";
    }
    
    
    private SolutionRecord lookupDevice(int serialNr, Session ses){
       SolutionQuery q = ses.getSolutionQuery(TSValues.DEVICE_ENTITY);
       q.addWhereCriterion(TSValues.DEVICE_SERIALNR, QueryPart.EQUALS, String.valueOf(serialNr));
       SolutionQueryResultSet rs = q.executeQuery();
       if(rs.size() == 1){
           return rs.getRecord(0);
       }
       return null;   
    }
    
    private SolutionRecord lookupStoppoint(int stoppointID, Session ses){
       SolutionQuery q = ses.getSolutionQuery(TSValues.STOPPOINT_ENTITY);
       q.addWhereCriterion(TSValues.STOPPOINT_ID, QueryPart.EQUALS, String.valueOf(stoppointID));
       SolutionQueryResultSet rs = q.executeQuery();
       if(rs.size() == 1){
           return rs.getRecord(0);
       }
       return null;   
    }
    
    private SolutionRecordNew createNewWorkorder(String workorderType, String errorType, String systemErrorText,int deviceDataID, int stoppointDataID, Session ses) throws Exception{
           if(doesWorkorderExist(workorderType, errorType, deviceDataID, ses)){
            return null;

        }    
                SolutionRecordNew sr = ses.getSolutionRecordNew(TSValues.CASE_ENTITY);

        int a = 185;
        sr.setValueInteger("SOURCE", a);
        sr.setValue("TYPE", workorderType);
        sr.setValue("ERRORSERVICE", errorType);

                    sr.setValue("ERRORWARNING", "Jeg eksistere sku");

        sr.setValueInteger("DEVICE", deviceDataID);
        return sr;   
    }
    
    private Boolean doesWorkorderExist(String workorderType, String errorType, int deviceDataID, Session ses){
        SolutionQuery q = ses.getSolutionQuery("workorder");
        
        q.addWhereCriterion("DEVICE", QueryPart.EQUALS,  String.valueOf(deviceDataID));
        q.addWhereCriterion("TYPE", QueryPart.EQUALS, workorderType);
        q.addWhereCriterion("ERRORSERVICE", QueryPart.EQUALS,errorType);
        q.addWhereCriterion("StatusID", QueryPart.NOT_EQUAL, "80");
        SolutionQueryResultSet rs = q.executeQuery();
        
        return (rs.size() > 0);
    }
    
}

                    
                    