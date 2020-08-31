/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package activities;

import dk.p2e.blanket.form.handler.QueryPart;
import dk.tempusserva.api.Session;
import dk.tempusserva.api.SolutionQuery;
import dk.tempusserva.api.SolutionQueryResultSet;
import dk.tempusserva.api.SolutionRecord;
import dk.tempusserva.api.SolutionRecordNew;
import java.util.HashMap;
import java.util.Map;
import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;

/**
 * This class is mainly to perform operations that require the session object, it is very messy and needs to be cleaned up
 * @author XSMA
 */
public class Util {
    

    private Session ses;
    
    public Util(Session session){
        ses = session;
    }
    /**
     * Returns a SolutionRecord from a given entity name and a DataID. This is a wrapper function
     * as TS does not offer getSolutionRecord without searching for the solutionID first
     * @param entity String name of the entity 
     * @param DataID Int DataID of the record
     * @return SolutionRecord you must validate that it is valid(DataID != 0) as TS does not throw a catchable error on wrong dataID
     */
    public SolutionRecord getSolutionRecord(String entity, int DataID){
        int solutionID = ses.getSolutionID(entity);
        SolutionRecord sr = ses.getSolutionRecord(solutionID, DataID);
        return sr;
    }
    
    
    
    /**
     * Creates a restart device Solution Record that is set to approved, validation of 
     * records is assumed to be done before calling this function
     * @param deviceSR
     * @param stoppointSR
     * @return
     * @throws Exception 
     */
    public SolutionRecordNew restartDeviceRecord(SolutionRecord deviceSR, SolutionRecord stoppointSR, int caseDataID) throws Exception{
        SolutionRecordNew srn = ses.getSolutionRecordNew(TSValues.ACTIVITY_ENTITY);
        if(caseDataID != 0){
            SolutionRecord caseSR = getSolutionRecord(TSValues.CASE_ENTITY, caseDataID);
            srn.setReference(TSValues.ACTIVITY_CASE, caseSR);
        }
        srn.setReference(TSValues.ACTIVITY_DEVICE, deviceSR);
        srn.setReference(TSValues.ACTIVITY_DEVICEONSTOPPOINT, deviceSR);
        srn.setReference(TSValues.ACTIVITY_STOPPOINT, stoppointSR);
        srn.setValue(TSValues.ACTIVITY_ACTIVITY, TSValues.ACTIVITIYCODE_DEVICE_RESTART);
        srn.setValueInteger(TSValues.STATUSID, TSValues.ACTIVITIES_STATUS_APPROVED);
        return srn;
    }
    
    
    public SolutionRecordNew cableCheckRecord(SolutionRecord deviceSR, SolutionRecord stoppointSR, int caseDataID) throws Exception{
        SolutionRecordNew srn = ses.getSolutionRecordNew(TSValues.ACTIVITY_ENTITY);
        if(caseDataID != 0){
            SolutionRecord caseSR = getSolutionRecord(TSValues.CASE_ENTITY, caseDataID);
            srn.setReference(TSValues.ACTIVITY_CASE, caseSR);
        }
        srn.setReference(TSValues.ACTIVITY_DEVICE, deviceSR);
        srn.setReference(TSValues.ACTIVITY_DEVICEONSTOPPOINT, deviceSR);
        srn.setReference(TSValues.ACTIVITY_STOPPOINT, stoppointSR);
        srn.setValue(TSValues.ACTIVITY_ACTIVITY, TSValues.ACTIVITIYCODE_DEVICE_CABLECHECK);
        srn.setValueInteger(TSValues.STATUSID, TSValues.ACTIVITIES_STATUS_APPROVED);
        return srn;
    }
    
    public SolutionRecordNew batterySetupRecord(SolutionRecord deviceSR, SolutionRecord stoppointSR, int caseDataID) throws Exception{
        SolutionRecordNew srn = ses.getSolutionRecordNew(TSValues.ACTIVITY_ENTITY);
        if(caseDataID != 0){
            SolutionRecord caseSR = getSolutionRecord(TSValues.CASE_ENTITY, caseDataID);
            srn.setReference(TSValues.ACTIVITY_CASE, caseSR);
        }
        srn.setReference(TSValues.ACTIVITY_DEVICE, deviceSR);
        srn.setReference(TSValues.ACTIVITY_SELECTEDDEVICETWO, deviceSR);
        srn.setReference(TSValues.ACTIVITY_STOPPOINT, stoppointSR);
        srn.setValue(TSValues.ACTIVITY_ACTIVITY, TSValues.ACTIVITIYCODE_BATTERY_SETUP);
        srn.setValueInteger(TSValues.STATUSID, TSValues.ACTIVITIES_STATUS_APPROVED);
        return srn;
    }
    
    
        
    /**
     * Creates a new record in the stoppoint inventory entity with the given componen t and amount and a stoppoint.
     * Does not persist data
     * @param stoppointSR SolutionRecord of a specific stoppoint
     * @param componentSR SolutionRecord of the component
     * @param componentAmount int value with the amount being added
     * @return SolutionRecordNew Object with the fields set
     * @throws IllegalArgumentException On invalid dataIDs
     * @throws Exception on system error
     */
    public SolutionRecordNew createStoppointInvComponentRecord(SolutionRecord stoppointSR, SolutionRecord componentSR, int componentAmount) throws Exception{
        
        
        if(stoppointSR.getInstanceID() == 0 || componentSR.getInstanceID() == 0){
            throw new IllegalArgumentException("Invalid DataIDs");
        }
        SolutionRecordNew srn = ses.getSolutionRecordNew(TSValues.STOPPOINTINV_ENTITY);
        srn.setReference(TSValues.STOPPOINTINV_STOPPOINT, stoppointSR);
        srn.setReference(TSValues.STOPPOINTINV_COMPONENT, componentSR);
        srn.setValueInteger(TSValues.STOPPOINTINV_AMOUNT, componentAmount);
        return srn;
    }
    
    
    /**
     * Finds the DataID to the record of a given component type setup at a specific stoppoint
     * @param componentDataID int value with DataID to the record of the component
     * @param stoppointDataID int value with DataID to the record of the stoppoint
     * @return int value higher than 0 if a record is found, 0 otherwise
     */
    public int findStoppointComponentDataID(int componentDataID,int stoppointDataID){
        SolutionQuery q = ses.getSolutionQuery(TSValues.STOPPOINTINV_ENTITY);
        q.addWhereCriterion(TSValues.STOPPOINTINV_STOPPOINT, QueryPart.EQUALS, String.valueOf(stoppointDataID));
        q.addWhereCriterion(TSValues.STOPPOINTINV_COMPONENT, QueryPart.EQUALS, String.valueOf(componentDataID));
        SolutionQueryResultSet rs = q.executeQuery();
        if(rs.size() == 1){
            return rs.getRecord(0).getInstanceID();
        }
        return 0;
    }
    
    
   
    /**
     * Creates a new record in the stoppoint inventory entity with the given component and amount and a stoppoint.
     * Does not persist data
     * @param warehouseSR int value with the stoppoint DataID
     * @param componentSR int value with the component DataID
     * @param componentAmount int value with the amount being added
     * @return SolutionRecordNew Object with the fields set
     * @throws IllegalArgumentException On invalid dataIDs
     * @throws Exception on system error
     */
    public SolutionRecordNew createWarehouseInvComponentRecord(SolutionRecord warehouseSR, SolutionRecord componentSR, int componentAmount) throws Exception{
        
        if(warehouseSR.getInstanceID() == 0 || componentSR.getInstanceID() == 0){
            throw new IllegalArgumentException("Invalid DataIDs");
        }

        SolutionRecordNew srn = ses.getSolutionRecordNew(TSValues.COMPONENTSTORAGE_ENTITY);
        srn.setReference(TSValues.COMPONENTSTORAGE_WAREHOUSE, warehouseSR);
        srn.setReference(TSValues.COMPONENTSTORAGE_COMPONENT, componentSR);
        srn.setValueInteger(TSValues.COMPONENTSTORAGE_AMOUNT, componentAmount);
        return srn;
    }
    
    
        /**
     * Finds the DataID to the record of a given component type stored at a specific warehouse
     * @param componentDataID int value with DataID to the record of the component
     * @param warehouseDataID int value with DataID to the record of the stoppoint
     * @return int value higher than 0 if a record is found, 0 otherwise
     */
    public int findWarehouseComponentDataID(int componentDataID,int warehouseDataID){
        SolutionQuery q = ses.getSolutionQuery(TSValues.COMPONENTSTORAGE_ENTITY);
        q.addWhereCriterion(TSValues.COMPONENTSTORAGE_WAREHOUSE, QueryPart.EQUALS, String.valueOf(warehouseDataID));
        q.addWhereCriterion(TSValues.COMPONENTSTORAGE_COMPONENT, QueryPart.EQUALS, String.valueOf(componentDataID));
        SolutionQueryResultSet rs = q.executeQuery();
        if(rs.size() == 1){
            return rs.getRecord(0).getInstanceID();
        }
        return 0;
    }
    
    
        
    
    public SolutionRecordNew createSetupBatteryActivity(int caseDataID, SolutionRecord stoppointSR,SolutionRecord deviceSR) throws Exception{
        
        
        if(stoppointSR.getInstanceID() != 0 && deviceSR.getInstanceID() != 0 ){
            SolutionRecordNew srn = ses.getSolutionRecordNew(TSValues.ACTIVITY_ENTITY);
            if(caseDataID != 0){
                SolutionRecord caseSR = getSolutionRecord(TSValues.CASE_ENTITY, caseDataID);
                srn.setReference(TSValues.ACTIVITY_CASE, caseSR);
            }
            srn.setReference(TSValues.ACTIVITY_DEVICE, deviceSR);
            srn.setReference(TSValues.ACTIVITY_SELECTEDDEVICE, deviceSR);
            srn.setReference(TSValues.ACTIVITY_STOPPOINT, stoppointSR);
            srn.setValue(TSValues.ACTIVITY_ACTIVITY, TSValues.ACTIVITIYCODE_BATTERY_SETUP);
            srn.setValueInteger(TSValues.STATUSID,TSValues.ACTIVITIES_STATUS_APPROVED);
            return srn;
        }
        else{
            throw new IllegalArgumentException("Invalid DataIDs");
        }
    }
   
    
        
    //This should be a general function in the future, for now its there
    public SolutionRecordNew createSetupActivity(int caseDataID, SolutionRecord stoppointSR,SolutionRecord deviceSR) throws Exception{
        

        if(stoppointSR.getInstanceID() != 0 && deviceSR.getInstanceID() != 0 ){
            SolutionRecordNew srn = ses.getSolutionRecordNew(TSValues.ACTIVITY_ENTITY);
            if(caseDataID != 0){
                SolutionRecord caseSR = getSolutionRecord(TSValues.CASE_ENTITY, caseDataID);
                srn.setReference(TSValues.ACTIVITY_CASE, caseSR);
            }
            srn.setReference(TSValues.ACTIVITY_DEVICE, deviceSR);
            srn.setReference(TSValues.ACTIVITY_SELECTEDDEVICE, deviceSR);

            srn.setReference(TSValues.ACTIVITY_STOPPOINT, stoppointSR);
            srn.setValue(TSValues.ACTIVITY_ACTIVITY, TSValues.ACTIVITIYCODE_DEVICE_SETUP);
            srn.setValueInteger(TSValues.STATUSID,TSValues.ACTIVITIES_STATUS_APPROVED);
            return srn;
        }
        else{
            throw new IllegalArgumentException("Invalid DataIDs");
        }
        
    }
    

    public SolutionRecordNew createComponentSetupActivity(int caseDataID, SolutionRecord stoppointSR, int fromWarehouseDataID, int warehouseComponentDataID, int amount) throws Exception{
        SolutionRecordNew srn = ses.getSolutionRecordNew(TSValues.ACTIVITY_ENTITY);
        if(caseDataID != 0){
            SolutionRecord caseSR = getSolutionRecord(TSValues.CASE_ENTITY, caseDataID);
            srn.setReference(TSValues.ACTIVITY_CASE, caseSR);
        }
        srn.setReference(TSValues.ACTIVITY_STOPPOINT, stoppointSR);
        srn.setValueInteger(TSValues.ACTIVITY_FROMINVENTORY, fromWarehouseDataID);
        srn.setValueInteger(TSValues.ACTIVITY_INVENTORYCOMPONENT, warehouseComponentDataID);
        srn.setValueInteger(TSValues.ACTIVITY_COMPONENTAMOUNT, amount);
        srn.setValue(TSValues.ACTIVITY_ACTIVITY, TSValues.ACTIVITIYCODE_COMPONENT_SETUP);
        srn.setValueInteger(TSValues.STATUSID,TSValues.ACTIVITIES_STATUS_INIT);
        return srn;

        
    }
    
}
