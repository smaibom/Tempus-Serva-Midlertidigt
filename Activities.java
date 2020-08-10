/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package activities;

import dk.p2e.blanket.codeunit.CodeunitFormevents;
import dk.p2e.blanket.form.handler.QueryPart;
import dk.p2e.util.Parser;
import dk.p2e.util.Systemout;
import dk.tempusserva.api.Session;
import dk.tempusserva.api.SessionFactory;
import dk.tempusserva.api.SolutionQuery;
import dk.tempusserva.api.SolutionQueryResultSet;
import dk.tempusserva.api.SolutionRecord;
import dk.tempusserva.api.SolutionRecordNew;
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
    

    private final String caseEntity = "workorder";
    
    private final String stoppointInvEntity = "stoppointinventory";
    private final String stoppointInvStoppoint = "STOPPOINT";
    private final String stoppointInvComponent = "COMPONENT";
    private final String stoppointInvAmount = "AMOUNT";
    
    private final String componentEntity = "components";
    
    
    private final String componentStorageEntity = "warehouseallocation";
    private final String componentStorageAmount = "AMOUNT";
    private final String componentStorageWarehouse = "WAREHOUSE";
    private final String componentStorageComponent = "COMPONENT";
    
    

    @Override
    public void beforeUpdateItem() throws Exception{
        String typeField = c.fields.getElementByFieldName(activityActivity).FieldValue;
        int SelectedDeviceDataID =  Parser.getInteger(c.fields.getElementByFieldName(activitySelectedDevice).FieldValue);
        int deviceAtStoppointDataID = Parser.getInteger(c.fields.getElementByFieldName(activityDeviceOnStoppoint).FieldValue);

        int stoppointDataID = Parser.getInteger(c.fields.getElementByFieldName(activityStoppoint).FieldValue);
        int toWarehouseDataID = Parser.getInteger(c.fields.getElementByFieldName(activityToInventory).FieldValue);
        int fromWarehouseDataID = Parser.getInteger(c.fields.getElementByFieldName(activityFromInventory).FieldValue);
        int caseDataID = Parser.getInteger(c.fields.getElementByFieldName(activityCase).FieldValue);
        int componentDataID = Parser.getInteger(c.fields.getElementByFieldName(activityComponentInstalled).FieldValue);
        int inventoryComponentRecordDataID = Parser.getInteger(c.fields.getElementByFieldName(activityInventoryComponent).FieldValue);
        int componentAmount = Parser.getInteger(c.fields.getElementByFieldName(activityComponentAmountInstalled).FieldValue);
        
        int spcDataID;
        
        

        Session ses = SessionFactory.getSession(this);

        
        switch(typeField){
            //Change device
            case "170":
                try{
                    SolutionRecord srInstalled = setDeviceStoppoint(SelectedDeviceDataID,stoppointDataID,ses);
                    SolutionRecord srUninstalled = setDeviceStorage(deviceAtStoppointDataID, toWarehouseDataID, ses);
                    SolutionRecordNew tempName = createSetupActivity(ses, caseDataID, stoppointDataID, SelectedDeviceDataID);
                    c.fields.getElementByFieldName(activityDevice).setFieldValue(deviceAtStoppointDataID);
                    c.fields.getElementByFieldName(activitySelectedDevice).setFieldValue("");
                    c.fields.getElementByFieldName(activityDeviceOnStoppoint).setFieldValue("");
                    c.fields.getElementByFieldName(activityActivity).setFieldValue(172);
                    srInstalled.persistChanges();
                    srUninstalled.persistChanges();
                    tempName.persistChanges(false);
                }
                catch(ValueException e){
                    c.fields.getElementByFieldName("DEFEKTOKSKALKONTROLLERES").setFieldValue("Dette");
                    setItemStatus(98);
                    //Invalid fields
                }
                catch(Exception e){
                    //Figure out a better exception
                }
                break;
                
                
            //Setup device at stoppoint
            case "171":
                try{
                    SolutionRecord sr = setDeviceStoppoint(SelectedDeviceDataID,stoppointDataID,ses);
                    c.fields.getElementByFieldName(activityDevice).setFieldValue(SelectedDeviceDataID);
                    c.fields.getElementByFieldName(activitySelectedDevice).setFieldValue("");
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
                    SolutionRecord sr = setDeviceStorage(deviceAtStoppointDataID,toWarehouseDataID,ses);
                    c.fields.getElementByFieldName(activityDevice).setFieldValue(deviceAtStoppointDataID);
                    c.fields.getElementByFieldName(activityDeviceOnStoppoint).setFieldValue("");
                    
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
                
                
            //Restart device
            case "174":
                try{
                    if(caseDataID == 0 || stoppointDataID == 0 || deviceAtStoppointDataID == 0){
                        setItemStatus(98);
                        break;
                    }
                    c.fields.getElementByFieldName(activityDevice).setFieldValue(deviceAtStoppointDataID);
                    c.fields.getElementByFieldName(activityDeviceOnStoppoint).setFieldValue("");
        
                }
                catch(Exception e){
                    
                }
                break;
                
                
            //Cablecheck
            case "175":
                try{
                    if(caseDataID == 0 || stoppointDataID == 0 || deviceAtStoppointDataID == 0){
                        setItemStatus(98);
                        break;
                    }
                    c.fields.getElementByFieldName(activityDevice).setFieldValue(deviceAtStoppointDataID);
                    c.fields.getElementByFieldName(activityDeviceOnStoppoint).setFieldValue("");
        
                }
                catch(Exception e){
                    
                }
                break;
                
                
            //Component setup
            case "176":
                if(componentDataID == 0 || stoppointDataID == 0 || componentAmount < 1){
                    setItemStatus(98);
                    break;
                }
                spcDataID = findStoppointComponentDataID(componentDataID,stoppointDataID,ses);
                if(spcDataID == 0){
                   try{
                        SolutionRecordNew srn = createStoppointInvComponentRecord(stoppointDataID, componentDataID, componentAmount, ses);
                        srn.persistChanges();
                   }
                   catch(ValueException e){
                        setItemStatus(98);

                   }
                   catch(Exception e){
                   }
                }
                else{
                    try{
                        SolutionRecord sr = addStoppointInvComponent(spcDataID, componentAmount, ses);
                        sr.persistChanges();
                    }
                    catch(ValueException e){
                        //Shouldnt realyl happen as theres a check before
                        setItemStatus(98);
                    }
                    catch(Exception e){
                    }
                }
                break;
                
                
            //Component takedown
            case "177":
                if(componentDataID == 0 || stoppointDataID == 0 || componentAmount < 1){
                    setItemStatus(98);
                    break;
                }
                spcDataID = findStoppointComponentDataID(componentDataID,stoppointDataID,ses);
                if(spcDataID == 0){
                    //Do nothing, we cant remove stuff that dont exist
                }
                else{
                    try{
                        SolutionRecord sr = removeStoppointInvComponent(spcDataID, componentAmount, ses);
                        sr.persistChanges();
                    }
                    catch(ValueException e){
                        //Shouldnt really happen as theres a check before
                        setItemStatus(98);
                    }
                    catch(Exception e){
                    }
                }
            
            //Move component
            case "178":
                //FromInv, InvComponent, ComponentAmount, ToInv
                
                if(toWarehouseDataID == 0 || inventoryComponentRecordDataID == 0 || fromWarehouseDataID == 0 || componentAmount < 1){
                    
                    setItemStatus(98);
                     c.fields.getElementByFieldName("DEBUGFIELD").setFieldValue("hej");
                    break;
                }
                
                try{
                    SolutionRecord srFrom = removeWarehouseInvComponent(inventoryComponentRecordDataID, componentAmount, ses);
                    int inventoryComponentDataID = srFrom.getValueInteger(componentStorageComponent);
                    int toWarehouseInventoryComponentDataID = findWarehouseComponentDataID(inventoryComponentDataID, toWarehouseDataID, ses);
                    

                    if(toWarehouseInventoryComponentDataID == 0){
                        
                        SolutionRecordNew srnTo = createWarehouseInvComponentRecord(toWarehouseDataID, inventoryComponentDataID, componentAmount, ses);

                        srFrom.persistChanges();
                        srnTo.persistChanges();

                    }
                    else{
                        //Update record
                        SolutionRecord srTo = addWarehouseComponentInvComponent(toWarehouseInventoryComponentDataID, componentAmount, ses);
                        srFrom.persistChanges();
                        srTo.persistChanges();
                    }    
                }
                catch(ValueException e){
                    //Shouldnt really happen as theres a check before
                    setItemStatus(98);
                }
                    catch(Exception e){
                }
                
                
                break;
                
            case "179":
                try{
                    SolutionRecord sr = setDeviceStorage(SelectedDeviceDataID,toWarehouseDataID,ses);
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
    
    
    
    
    
    //This should be a general function in the future, for now its there
    private SolutionRecordNew createSetupActivity(Session ses,int caseDataID, int stoppointDataID,int deviceDataID) throws Exception{
        int stoppointSolutionID = ses.getSolutionID(stoppointEntity);
        int deviceSolutionID = ses.getSolutionID(deviceEntity);
        int caseSolutionID = ses.getSolutionID(caseEntity);

        SolutionRecord stoppointSR = ses.getSolutionRecord(stoppointSolutionID, stoppointDataID);
        SolutionRecord deviceSR = ses.getSolutionRecord(deviceSolutionID, deviceDataID);
        SolutionRecord caseSR = ses.getSolutionRecord(caseSolutionID, caseDataID);

        if(stoppointSR.getInstanceID() != 0 && deviceSR.getInstanceID() != 0 && caseSR.getInstanceID() != 0){
            SolutionRecordNew srn = ses.getSolutionRecordNew(activityEntity);
            srn.setReference(activityCase, caseSR);
            srn.setReference(activityDevice, deviceSR);
            srn.setReference(activityStoppoint, stoppointSR);
            srn.setValueInteger(activityActivity, 171);

            return srn;
        }
        else{
            throw new ValueException("Invalid DataIDs");
        }
        
    }
    
    
    
    
    
    
    
    
    
    /**
     * Finds the DataID to the record of a given component type stored at a specific warehouse
     * @param componentDataID int value with DataID to the record of the component
     * @param warehouseDataID int value with DataID to the record of the stoppoint
     * @param ses Open Session object
     * @return int value higher than 0 if a record is found, 0 otherwise
     */
    private int findWarehouseComponentDataID(int componentDataID,int warehouseDataID, Session ses){
        SolutionQuery q = ses.getSolutionQuery(componentStorageEntity);
        q.addWhereCriterion(componentStorageWarehouse, QueryPart.EQUALS, String.valueOf(warehouseDataID));
        q.addWhereCriterion(componentStorageComponent, QueryPart.EQUALS, String.valueOf(componentDataID));
        SolutionQueryResultSet rs = q.executeQuery();
        if(rs.size() == 1){
            return rs.getRecord(0).getInstanceID();
        }
        return 0;
    }
    
        /**
     * Adds a given amount of components to a specific component storage  record, 
     * it assumed you use findWarehouseComponentDataID before calling this function.
     * Does not persist any data
     * @param warehouseComponentDataID int value with the DataID pointing to the specific record being updated     
     * @param componentAmount int value with the amount of components being added
     * @param ses Open Session object
     * @return A SolutionRecord with the updated amount
     * @throws ValueException on invalid DataID
     * @throws Exception on system error
     */
    private SolutionRecord addWarehouseComponentInvComponent(int warehouseComponentDataID, int componentAmount, Session ses) throws Exception{
        
        int componentStorageSolutionID = ses.getSolutionID(componentStorageEntity);
        
        SolutionRecord sr = ses.getSolutionRecord(componentStorageSolutionID, warehouseComponentDataID);
        if(sr.getInstanceID() == 0){
            throw new ValueException("Invalid DataIDs");
        }
        
        int newAmount = sr.getValueInteger(componentStorageAmount) + componentAmount;
        sr.setValueInteger(componentStorageAmount, newAmount);
        
        return sr;
    }
    
    
    /**
     * Removes a given amount of components to a specific warehouse inventory record, 
     * Does not persist any data
     * @param warehouseComponentDataID int value with the DataID pointing to the specific record being updated     
     * @param componentAmount int value with the amount of components being added
     * @param ses Open Session object
     * @return A SolutionRecord with the updated amount
     * @throws ValueException on invalid DataID
     * @throws Exception on system error
     */
    private SolutionRecord removeWarehouseInvComponent(int warehouseComponentDataID, int componentAmount, Session ses) throws Exception{
        
        int componentStorageSolutionID = ses.getSolutionID(componentStorageEntity);
        
        SolutionRecord sr = ses.getSolutionRecord(componentStorageSolutionID, warehouseComponentDataID);
        if(sr.getInstanceID() == 0){
            throw new ValueException("Invalid DataIDs");
        }
        
        int newAmount = sr.getValueInteger(componentStorageAmount) - componentAmount;
        if(newAmount >= 0){
            sr.setValueInteger(componentStorageAmount, newAmount);
        }
        return sr;

    }
    
        /**
     * Creates a new record in the stoppoint inventory entity with the given component and amount and a stoppoint.
     * it assumed you use findStoppointComponentDataID before calling this function to ensure you do not create duplicates
     * Does not persist data
     * @param warehouseDataID int value with the stoppoint DataID
     * @param componentDataIDint int value with the component DataID
     * @param componentAmount int value with the amount being added
     * @param ses Open Session Object
     * @return SolutionRecordNew Object with the fields set
     * @throws ValueException On invalid dataIDs
     * @throws Exception on system error
     */
    private SolutionRecordNew createWarehouseInvComponentRecord(int warehouseDataID, int componentDataID, int componentAmount, Session ses) throws Exception{
        
        int warehouseSolutionID = ses.getSolutionID(warehouseEntity);
        int componentSolutionID = ses.getSolutionID(componentEntity);
                              
        
        SolutionRecord warehouseSR = ses.getSolutionRecord(warehouseSolutionID, warehouseDataID);
        SolutionRecord compSR = ses.getSolutionRecord(componentSolutionID, componentDataID);
        if(warehouseSR.getInstanceID() == 0 || compSR.getInstanceID() == 0){
            throw new ValueException("Invalid DataIDs");
        }

        SolutionRecordNew srn = ses.getSolutionRecordNew(componentStorageEntity);
        srn.setReference(componentStorageWarehouse, warehouseSR);
        srn.setReference(componentStorageComponent, compSR);
        srn.setValueInteger(componentStorageAmount, componentAmount);
        return srn;
    }
  
    
    
    
    
    
    /**
     * Finds the DataID to the record of a given component type setup at a specific stoppoint
     * @param componentDataID int value with DataID to the record of the component
     * @param stoppointDataID int value with DataID to the record of the stoppoint
     * @param ses Open Session object
     * @return int value higher than 0 if a record is found, 0 otherwise
     */
    private int findStoppointComponentDataID(int componentDataID,int stoppointDataID, Session ses){
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
     * Adds a given amount of components to a specific stoppoint inventory record, 
     * it assumed you use findStoppointComponentDataID before calling this function.
     * Does not persist any data
     * @param stoppointInvDataID int value with the DataID pointing to the specific record being updated     
     * @param componentAmount int value with the amount of components being added
     * @param ses Open Session object
     * @return A SolutionRecord with the updated amount
     * @throws ValueException on invalid DataID
     * @throws Exception on system error
     */
    private SolutionRecord addStoppointInvComponent(int stoppointInvDataID, int componentAmount, Session ses) throws Exception{
        
        int stoppointInvSolutionID = ses.getSolutionID(stoppointInvEntity);
        
        SolutionRecord sr = ses.getSolutionRecord(stoppointInvSolutionID, stoppointInvDataID);
        if(sr.getInstanceID() == 0){
            throw new ValueException("Invalid DataIDs");
        }
        
        int newAmount = sr.getValueInteger(stoppointInvAmount) + componentAmount;
        sr.setValueInteger(stoppointInvAmount, newAmount);
        
        return sr;
    }
    
    
    /**
     * Removes a given amount of components to a specific stoppoint inventory record, if the amount is larger than installed nothing is removed,
     * it assumed you use findStoppointComponentDataID before calling this function.
     * Does not persist any data
     * @param stoppointInvDataID int value with the DataID pointing to the specific record being updated     
     * @param componentAmount int value with the amount of components being removed
     * @param ses Open Session object
     * @return A SolutionRecord with the updated amount
     * @throws ValueException on invalid DataID
     * @throws Exception on system error
     */
    private SolutionRecord removeStoppointInvComponent(int stoppointInvDataID, int componentAmount, Session ses) throws Exception{
        
        int stoppointInvSolutionID = ses.getSolutionID(stoppointInvEntity);
        
        SolutionRecord sr = ses.getSolutionRecord(stoppointInvSolutionID, stoppointInvDataID);
        if(sr.getInstanceID() == 0){
            throw new ValueException("Invalid DataIDs");
        }
        
        int newAmount = sr.getValueInteger(stoppointInvAmount) - componentAmount;
        if(newAmount >= 0){
            sr.setValueInteger(stoppointInvAmount, newAmount);
        }
        return sr;
    }
    
    /**
     * Creates a new record in the stoppoint inventory entity with the given component and amount and a stoppoint.
     * it assumed you use findStoppointComponentDataID before calling this function to ensure you do not create duplicates
     * Does not persist data
     * @param stoppointDataID int value with the stoppoint DataID
     * @param componentDataIDint int value with the component DataID
     * @param componentAmount int value with the amount being added
     * @param ses Open Session Object
     * @return SolutionRecordNew Object with the fields set
     * @throws ValueException On invalid dataIDs
     * @throws Exception on system error
     */
    private SolutionRecordNew createStoppointInvComponentRecord(int stoppointDataID, int componentDataID, int componentAmount, Session ses) throws Exception{
        
        int stoppointSolutionID = ses.getSolutionID(stoppointEntity);
        int componentSolutionID = ses.getSolutionID(componentEntity);
        
        
        SolutionRecord spSR = ses.getSolutionRecord(stoppointSolutionID, stoppointDataID);
        SolutionRecord compSR = ses.getSolutionRecord(componentSolutionID, componentDataID);
        if(spSR.getInstanceID() == 0 || compSR.getInstanceID() == 0){
            throw new ValueException("Invalid DataIDs");
        }

        SolutionRecordNew srn = ses.getSolutionRecordNew(stoppointInvEntity);
        srn.setReference(stoppointInvStoppoint, spSR);
        srn.setReference(stoppointInvComponent, compSR);
        srn.setValueInteger(stoppointInvAmount, componentAmount);
        return srn;
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
