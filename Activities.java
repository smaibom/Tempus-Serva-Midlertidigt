/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package activities;

import dk.p2e.blanket.codeunit.CodeunitFormevents;
import dk.p2e.util.Parser;
import dk.p2e.util.Systemout;
import dk.tempusserva.api.Session;
import dk.tempusserva.api.SessionFactory;
import dk.tempusserva.api.SolutionQuery;
import dk.tempusserva.api.SolutionRecord;
import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;

/**
 *
 * CodeunitFormevent for the activities entity, handles all functionality of changing 
 * parent child relationship and removing field values that are not relevant to an activity
 * before submitting a record
 * @author XSMA
 */
public class Activities extends CodeunitFormevents{
    
    //TempusServa string names on system, change here if system value is changed
    private final String stoppointEntity = "stoppoint";
    
    private final String deviceEntity = "devices";
    private final String deviceStopPoint = "INSTALLEDATSTOPPOINT";
    private final String deviceStorageLoc = "STORAGELOCATION";
    
    private final String warehouseEntity = "warehouse";
    
    @Override
    public void beforeUpdateItem(){
        Session ses = SessionFactory.getSession(this);
        String typeField = c.fields.getElementByFieldName("ACTIVITY").FieldValue;
        int deviceInstalledDataID = Parser.getInteger(c.fields.getElementByFieldName("DEVICEINSTALLED").FieldValue);
        int deviceUninstalledDataID = Parser.getInteger(c.fields.getElementByFieldName("DEVICEUNINSTALLED").FieldValue);

        int stoppointDataID = Parser.getInteger(c.fields.getElementByFieldName("STOPPOINT").FieldValue);
        int warehouseDataID = Parser.getInteger(c.fields.getElementByFieldName("TOINVENTORY").FieldValue);
        
        switch(typeField){
            //Change device with new one
            case "170":
                //if(removeDevice(deviceUninstalledDataID, warehouseDataID)){
                //    setDeviceStoppoint(deviceInstalledDataID, stoppointDataID, ses);
                //}
                break;
            //Setup device
            case "171":
                try{
                    SolutionRecord sr = setDeviceStoppoint(deviceInstalledDataID,stoppointDataID,ses);
                    sr.persistChanges();
                }
                catch(ValueException e){
                    setItemStatus(98);
                    //Invalid fields
                }
                catch(Exception e){
                    //Figure out a better exception
                }
                break;
            //Remove device from stoppoint
            case "172":
                try{
                    SolutionRecord sr = setDeviceStorage(deviceInstalledDataID,warehouseDataID,ses);
                    sr.persistChanges();
                }
                catch(ValueException e){
                    setItemStatus(98);
                    //Invalid fields
                }
                catch(Exception e){
                    //Figure out a better exception
                }
                break;

            default:
                break;
        } 
        
        ses.close();
        
        
    }
    
    
    /**
     * Sets the storage location reference to nothing and the stoppoint reference to the given DataID
     * Does not persist any changes
     * @param deviceDataID int value with DataID of the device
     * @param stoppointDataID int value with DataID of the stoppoint
     * @param ses Open Session object
     * @return A SolutionRecord th
     * @throws ValueException If either of the DataIDs are not valid records
     * @throw Exception General systems error 
     */
    private SolutionRecord setDeviceStoppoint(int deviceDataID,int stoppointDataID,Session ses) throws Exception{
        int stoppointSolutionID = ses.getSolutionID(stoppointEntity);
        int deviceSolutionID = ses.getSolutionID(deviceEntity);

        SolutionRecord stoppointSR = ses.getSolutionRecord(stoppointSolutionID, stoppointDataID);
        SolutionRecord deviceSR = ses.getSolutionRecord(deviceSolutionID, deviceDataID);

        if(stoppointSR.getInstanceID() != 0 && deviceSR.getInstanceID() != 0){
            deviceSR.setValue(deviceStorageLoc, "");
            deviceSR.setReference(deviceStopPoint, stoppointSR);
        }
        else{
            throw new ValueException("Invalid DataIDs");
        }

        return deviceSR;

    }
    
    
    /**
     * Sets the storage location reference to the given warehouse DataID and the stoppoint reference to nothing
     * Does not persist any changes
     * @param deviceDataID int value with DataID of the device
     * @param warehouseDataID int value with DataID of the warehouse
     * @param ses Open Session object
     * @return A SolutionRecord th
     * @throws ValueException If either of the DataIDs are not valid records
     * @throw Exception General systems error 
     */
    private SolutionRecord setDeviceStorage(int deviceDataID,int warehouseDataID,Session ses) throws Exception{
        int warehouseSolutionID = ses.getSolutionID(warehouseEntity);
        int deviceSolutionID = ses.getSolutionID(deviceEntity);

        SolutionRecord warehouseSR = ses.getSolutionRecord(warehouseSolutionID, warehouseDataID);
        SolutionRecord deviceSR = ses.getSolutionRecord(deviceSolutionID, deviceDataID);
        if(warehouseSR.getInstanceID() != 0 && deviceSR.getInstanceID() != 0){
            deviceSR.setValue(deviceStopPoint, "");
            deviceSR.setReference(deviceStorageLoc, warehouseSR);
        }
        else{
            throw new ValueException("Invalid DataIDs");
        }
        return deviceSR;
        
    }
    
    
    
}
