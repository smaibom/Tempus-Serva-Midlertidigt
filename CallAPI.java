/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moveitems;

import dk.p2e.blanket.codeunit.CodeunitPagecontent;
import dk.p2e.blanket.form.Command;
import dk.p2e.blanket.form.Security;
import dk.p2e.blanket.form.handler.QueryPart;
import dk.p2e.util.Systemout;
import dk.tempusserva.api.Session;
import dk.tempusserva.api.SessionFactory;
import dk.tempusserva.api.SolutionQuery;
import dk.tempusserva.api.SolutionQueryResultSet;
import dk.tempusserva.api.SolutionRecordNew;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
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

/**
 * Class for calling and parsing APIs from Axentia and in the future MultiQ
 * Currently it is a CodeunitPagecontent for testing purposes, in the future change to a
 * codeunit that runs on a set timer on Tempus Serva platform. 
 * It is required that you use the compile flag -encoding UTF-8 to prevent encoding related errors
 * @author XSMA
 */
public class CallAPI extends CodeunitPagecontent {

    @Override
    public String execute(Command cmnd, Security scrt, Hashtable hshtbl) {
        String url = "https://ibusapi.axentia.se/api/Display/all/status/warning?api-version=1.0";
        String apikey = "mB3cBZenw2rf2WWIXZtFjRFLv";
        try {
            parseAxentia(callApi(url,apikey));
        } catch (Exception ex) {
            Systemout.println("Test");
            Logger.getLogger(CallAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(url);
        httpget.addHeader("apikey", apikey );
        StringBuilder res = new StringBuilder();
        try{
            HttpResponse httpresponse = httpclient.execute(httpget);


            if(httpresponse.getStatusLine().getStatusCode() == 200){
                Scanner sc = new Scanner(httpresponse.getEntity().getContent());
                while(sc.hasNext()) {
                    res.append(sc.nextLine());
                }
            }
            else{
                httpclient.close();
                throw new Exception("callApi did not get statuscode 200");
            }
        }
        catch(Exception e){
            httpclient.close();
            throw new Exception(e.getMessage());
        }
        httpclient.close();
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
        catch(Exception e){
            Systemout.println(e.toString());
        }
    }
    
    /**
     * Creates the TempusServa records from the api json. Currently only writing to a warning 
     * log, if more logging is required add in here
     * @param jsonObj JSONObject containing the information from 1 module
     */
    private void createAxentiaRecords(JSONObject jsonObj){
        HashMap<String,String> stringValues = new HashMap<String,String>();
        long id = (long) jsonObj.get("ibusId");
        //longValues.put("BATTERYSTATUS", (Long) jsonObj.get("batteryStatus"));
        long batteryStatus = (long) jsonObj.get("batteryStatus");
        long uptime = (long) jsonObj.get("upTime");
        long errorCode = (long) jsonObj.get("errorCode");
        long displayMode = (long) jsonObj.get("displayMode");
        
        //String values is simply to handle empty or null return values in the string fields.
        stringValues.put("DISPLAYCOMMENTS", (String) jsonObj.get("displayComments"));
        stringValues.put("FIRMWARE", (String) jsonObj.get("firmware"));
        stringValues.put("BATTERYVOLTAGE", (String) jsonObj.get("batteryVoltage"));
        stringValues.put("STOPPOINTID", (String) jsonObj.get("stopPointId"));
        stringValues.put("STOPPOINTNAMES", (String) jsonObj.get("stopPointName"));
        stringValues.put("WARNINGS", (String) jsonObj.get("statusDescription"));
        stringValues.put("LASTDATAPACKAGE", (String) jsonObj.get("lastDataPacket"));
        stringValues.put("TIMESTAMP", (String) jsonObj.get("timeStamp"));
        
        JSONObject position = (JSONObject) jsonObj.get("displayPosition");
        Boolean isSet = (Boolean) position.get("isSet");
        Double latitude = (Double) position.get("latitude");
        Double longitude = (Double) position.get("longitude");

        
        stringValues.values().removeIf(Objects::isNull);

        Session session = SessionFactory.getSession(this);
        try{
            if(displayMode   == 1){

                SolutionQuery query = session.getSolutionQuery("komponentmedserienr");
                query.addWhereCriterion("ID",QueryPart.EQUALS, String.valueOf(id));
                SolutionQueryResultSet executeQuery = query.executeQuery();
                if(executeQuery.size() > 0){
                    
                    SolutionRecordNew record = session.getSolutionRecordNew("apilog");
                    for (Map.Entry<String, String> entry : stringValues.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        record.setValue(key, value);
                    }
                    
                    record.setValueInteger("BATTERYSTATUS", Math.toIntExact(batteryStatus));
                    record.setValueInteger("ERRORS", Math.toIntExact(errorCode));
                    record.setValueInteger("UPTIME", Math.toIntExact(uptime));
                    record.setReference("ID", executeQuery.getRecord(0));
                    record.persistChanges();     
                }
            }
        }
        catch (Exception e){
            Systemout.println("createAxentiaRecords in CallAPI class: Temp error handling fix this");
            Systemout.println(e.toString());
        }
        session.close();
    }
    

    
}

                    
                    