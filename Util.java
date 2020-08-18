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
import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;

/**
 * This class is mainly to perform operations that require the session object, it is very messy and needs to be cleaned up
 * @author XSMA
 */
public class Util {
    
    private final String stoppointInvEntity = "stoppointinventory";
    private final String stoppointInvStoppoint = "STOPPOINT";
    private final String stoppointInvComponent = "COMPONENT";
    private final String stoppointInvAmount = "AMOUNT";
    private final String caseEntity = "workorder";
    private final String componentStorageEntity = "warehouseallocation";
    private final String componentStorageAmount = "AMOUNT";
    private final String componentStorageWarehouse = "WAREHOUSE";
    private final String componentStorageComponent = "COMPONENT";
    private final String activityEntity = "activities";
    private final String activityCase = "WORKORDER";
    private final String activityStoppoint = "STOPPOINT";
    private final String activityDevice = "DEVICE";
    private final String activityActivity = "ACTIVITY";
    private final String activityComponentInstalled = "COMPONETTYPEINSTALLED";
    private final String activityComponentAmountInstalled = "COMPONETTYPEINSTALLEDAMOUNT";
    private final String activitySelectedDevice = "SELECTEDDEVICE";
    private final String activityDeviceOnStoppoint = "DEVICEONSTOPPOINT";
    private final String activityToInventory = "TOINVENTORY";
    private final String activityFromInventory = "FROMINVENTORY";
    private final String activityInventoryComponent = "INVENTORYCOMPONENT";
    private final String activityBatteryOnStoppoint = "BATTERYONSTOPPOINT";
    private final String activitySupplier = "SUPPLIER";
    private final String activitySelectedDeviceTwo = "SELECTEDDEVICE2";
    private final String activitySetupBattery = "SETUPBATTERY";
    
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
     * Creates a new record in the stoppoint inventory entity with the given component and amount and a stoppoint.
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

        SolutionRecordNew srn = ses.getSolutionRecordNew(stoppointInvEntity);
        srn.setReference(stoppointInvStoppoint, stoppointSR);
        srn.setReference(stoppointInvComponent, componentSR);
        srn.setValueInteger(stoppointInvAmount, componentAmount);
        return srn;
    }
    
    
    /**
     * Finds the DataID to the record of a given component type setup at a specific stoppoint
     * @param componentDataID int value with DataID to the record of the component
     * @param stoppointDataID int value with DataID to the record of the stoppoint
     * @return int value higher than 0 if a record is found, 0 otherwise
     */
    public int findStoppointComponentDataID(int componentDataID,int stoppointDataID){
        SolutionQuery q = ses.getSolutionQuery(stoppointInvEntity);
        q.addWhereCriterion(stoppointInvStoppoint, QueryPart.EQUALS, String.valueOf(stoppointDataID));
        q.addWhereCriterion(stoppointInvComponent, QueryPart.EQUALS, String.valueOf(componentDataID));
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

        SolutionRecordNew srn = ses.getSolutionRecordNew(componentStorageEntity);
        srn.setReference(componentStorageWarehouse, warehouseSR);
        srn.setReference(componentStorageComponent, componentSR);
        srn.setValueInteger(componentStorageAmount, componentAmount);
        return srn;
    }
    
    
        /**
     * Finds the DataID to the record of a given component type stored at a specific warehouse
     * @param componentDataID int value with DataID to the record of the component
     * @param warehouseDataID int value with DataID to the record of the stoppoint
     * @return int value higher than 0 if a record is found, 0 otherwise
     */
    public int findWarehouseComponentDataID(int componentDataID,int warehouseDataID){
        SolutionQuery q = ses.getSolutionQuery(componentStorageEntity);
        q.addWhereCriterion(componentStorageWarehouse, QueryPart.EQUALS, String.valueOf(warehouseDataID));
        q.addWhereCriterion(componentStorageComponent, QueryPart.EQUALS, String.valueOf(componentDataID));
        SolutionQueryResultSet rs = q.executeQuery();
        if(rs.size() == 1){
            return rs.getRecord(0).getInstanceID();
        }
        return 0;
    }
    
    
        
    
    public SolutionRecordNew createSetupBatteryActivity(int caseDataID, SolutionRecord stoppointSR,SolutionRecord deviceSR) throws Exception{
        

        if(stoppointSR.getInstanceID() != 0 && deviceSR.getInstanceID() != 0 ){
            SolutionRecordNew srn = ses.getSolutionRecordNew(activityEntity);
            if(caseDataID != 0){
                SolutionRecord caseSR = getSolutionRecord(caseEntity, caseDataID);
                srn.setReference(activityCase, caseSR);
            }
            srn.setReference(activityDevice, deviceSR);
            srn.setReference(activitySelectedDevice, deviceSR);

            srn.setReference(activityStoppoint, stoppointSR);
            srn.setValueInteger(activityActivity, 168);
            srn.setValueInteger("StatusID",100);
            return srn;
        }
        else{
            throw new IllegalArgumentException("Invalid DataIDs");
        }
    }

    
        
    //This should be a general function in the future, for now its there
    public SolutionRecordNew createSetupActivity(int caseDataID, SolutionRecord stoppointSR,SolutionRecord deviceSR) throws Exception{
        

        if(stoppointSR.getInstanceID() != 0 && deviceSR.getInstanceID() != 0 ){
            SolutionRecordNew srn = ses.getSolutionRecordNew(activityEntity);
            if(caseDataID != 0){
                SolutionRecord caseSR = getSolutionRecord(caseEntity, caseDataID);
                srn.setReference(activityCase, caseSR);
            }
            srn.setReference(activityDevice, deviceSR);
            srn.setReference(activitySelectedDevice, deviceSR);

            srn.setReference(activityStoppoint, stoppointSR);
            srn.setValueInteger(activityActivity, 171);
            srn.setValueInteger("StatusID",100);
            return srn;
        }
        else{
            throw new IllegalArgumentException("Invalid DataIDs");
        }
        
    }
    
    
}
