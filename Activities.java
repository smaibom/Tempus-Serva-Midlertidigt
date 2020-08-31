/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package activities;

import dk.p2e.blanket.codeunit.CodeunitFormevents;
import dk.p2e.util.Parser;
import dk.tempusserva.api.Session;
import dk.tempusserva.api.SessionFactory;
import dk.tempusserva.api.SolutionRecord;
import dk.tempusserva.api.SolutionRecordNew;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;

/**
 *
 * CodeunitFormevent for the activities entity, handles all functionality of changing 
 * parent child relationship and removing field values that are not relevant to an activity
 * before submitting a record
 * @author XSMA
 */
public class Activities extends CodeunitFormevents{
    

    Util util;

    
    
    @Override
    public void beforeRenderList() throws Exception {

    }
    
    
    @Override
    public void beforeChangeItem() throws Exception {
       
    }
    

    @Override
    public void beforeUpdateItem() throws Exception{
        int statusID = getIntFieldValue(TSValues.STATUSID);

        if(!(statusID == TSValues.ACTIVITIES_STATUS_INIT || statusID == TSValues.ACTIVITIES_STATUS_USERERROR || statusID == TSValues.ACTIVITIES_STATUS_AWAITADMIN)){
            return;
            
        }
        
        String typeField = getStringFieldValue(TSValues.ACTIVITY_ACTIVITY);
        int SelectedDeviceDataID =  getIntFieldValue(TSValues.ACTIVITY_SELECTEDDEVICE);
        int deviceAtStoppointDataID = getIntFieldValue(TSValues.ACTIVITY_DEVICEONSTOPPOINT);
        int stoppointDataID = getIntFieldValue(TSValues.ACTIVITY_STOPPOINT);
        int toWarehouseDataID = getIntFieldValue(TSValues.ACTIVITY_TOINVENTORY);
        int fromWarehouseDataID = getIntFieldValue(TSValues.ACTIVITY_FROMINVENTORY);
        int caseDataID = getIntFieldValue(TSValues.ACTIVITY_CASE);
        int componentDataID = getIntFieldValue(TSValues.ACTIVITY_COMPONENTINSTALLED);
        int inventoryComponentRecordDataID = getIntFieldValue(TSValues.ACTIVITY_INVENTORYCOMPONENT);
        int componentAmount = getIntFieldValue(TSValues.ACTIVITY_COMPONENTAMOUNT);
        int batteryOnStoppointDataID = getIntFieldValue(TSValues.ACTIVITY_BATTERYONSTOPPOINT);
        int selectedDeviceTwoDataID = getIntFieldValue(TSValues.ACTIVITY_SELECTEDDEVICETWO);
        int setupBattery = getIntFieldValue(TSValues.ACTIVITY_SETUPBATTERY);
        int setupModule = getIntFieldValue(TSValues.ACTIVITY_SETUPMODULE);
         


        
 
        Session ses = SessionFactory.getSession(this);
        util = new Util(ses);
        
        switch(typeField){
            case TSValues.ACTIVITIYCODE_BATTERY_SETUP:
                setupBattery(caseDataID,selectedDeviceTwoDataID,stoppointDataID);
                break;
                
            case TSValues.ACTIVITIYCODE_BATTERY_TAKEDOWN:
                removeBattery(caseDataID,stoppointDataID,batteryOnStoppointDataID,toWarehouseDataID);
                break;
                
            case TSValues.ACTIVITIYCODE_DEVICE_SETUP:
                setupDeviceOnStoppoint(caseDataID,stoppointDataID,SelectedDeviceDataID,setupBattery,selectedDeviceTwoDataID);
                break;
                
            case TSValues.ACTIVITIYCODE_DEVICE_TAKEDOWN:
                removeDeviceFromStoppoint(caseDataID,stoppointDataID,deviceAtStoppointDataID,toWarehouseDataID,setupModule, SelectedDeviceDataID);
                break;
                
            case TSValues.ACTIVITIYCODE_DEVICE_RESTART:
                restartDevice(deviceAtStoppointDataID,stoppointDataID,caseDataID);
                break;
                
            case TSValues.ACTIVITIYCODE_DEVICE_CABLECHECK:
                cableCheck(deviceAtStoppointDataID,stoppointDataID,caseDataID);
                break;
                
            case TSValues.ACTIVITIYCODE_COMPONENT_SETUP:
                if(inventoryComponentRecordDataID == 0 || fromWarehouseDataID == 0 || stoppointDataID == 0 || componentAmount < 1){
                    setItemStatus(TSValues.ACTIVITIES_STATUS_USERERROR);
                    break;
                }
                //TODO: REMOVE MEEEEE
                int defaultStorageDataID = 161;
                componentSetup(caseDataID,defaultStorageDataID,inventoryComponentRecordDataID,componentDataID,stoppointDataID,componentAmount,util);
                break;
                
                
            case TSValues.ACTIVITIYCODE_COMPONENT_TAKEDOWN:
                componentTakedown(componentDataID,stoppointDataID,componentAmount);
                break;
            
            case TSValues.ACTIVITIYCODE_STORAGEMOVE_COMPONENT:
                if(toWarehouseDataID == 0 || inventoryComponentRecordDataID == 0 || componentAmount < 1){
                    setItemStatus(TSValues.ACTIVITIES_STATUS_USERERROR);
                    break;
                }
                moveComponent(inventoryComponentRecordDataID,toWarehouseDataID,componentAmount,util);
                break;
            
            case TSValues.ACTIVITIYCODE_STORAGEMOVE_DEVICE:
                if(SelectedDeviceDataID == 0 || toWarehouseDataID == 0){
                    setItemStatus(TSValues.ACTIVITIES_STATUS_USERERROR);
                    break;
                }
                moveDevice(SelectedDeviceDataID,toWarehouseDataID,util);
                break;
                
            default:
                break;
        } 
        
        ses.close();
        
    }
    
    /**
     * Activity for setting a battery up on a stoppoint. Will not do any changes and set status of record to user error if
     * stoppoint and battery fields are not filled, or the battery selected is not of battery type.
     * @param caseDataID int Value with DataID of the case, can be 0(No selected case by user)
     * @param batteryDataID int Value with DataID of the selected battery deviceRemoved
     * @param stoppointDataID int Value with DataID of the selected stoppoint
     */
    private void setupBattery(int caseDataID, int batteryDataID, int stoppointDataID){
        //Validation stage
        if(stoppointDataID == 0 || batteryDataID == 0 ){
            setItemStatus(TSValues.ACTIVITIES_STATUS_USERERROR);
            return;
        }
        
        SolutionRecord batterySR = util.getSolutionRecord(TSValues.DEVICE_ENTITY, batteryDataID);        
        Device battery = new Device(batterySR);
         
        try {
            if(!battery.isBattery()){
                setItemStatus(TSValues.ACTIVITIES_STATUS_USERERROR);
                return;    
            }
        } catch (Exception ex) {
            setItemStatus(TSValues.ACTIVITIES_STATUS_SYSTEMERROR);
            return;
        }
        
        SolutionRecord stoppointSR = util.getSolutionRecord(TSValues.STOPPOINT_ENTITY, stoppointDataID);

        
        //Execution stage
        try{
            if(c.StatusID == TSValues.ACTIVITIES_STATUS_USERERROR){
                setItemStatus(TSValues.ACTIVITIES_STATUS_DELETE);
                SolutionRecordNew correctSrn = util.batterySetupRecord(batterySR, stoppointSR, caseDataID);
                correctSrn.persistChanges(false);
            }
            else{
                setIntFieldValue(TSValues.ACTIVITY_DEVICE, batteryDataID);
                setItemStatus(TSValues.ACTIVITIES_STATUS_APPROVED);
            }
            
            battery.setStoppoint(stoppointSR);
            battery.persistChanges();
            
        }
        catch(Exception e){
            setItemStatus(TSValues.ACTIVITIES_STATUS_SYSTEMERROR);
        }
    }
    
    
    /**
     * Activity for removing a battery from a stoppoint. Will not do any changes and set status of record to user error if
     * stoppoint, battery and towarehouse fields are not filled, or the battery selected is not of battery type.
     * @param caseDataID int Value with DataID of the case, can be 0(No selected case by user)
     * @param stoppointDataID int Value with DataID of the selected stoppoint
     * @param batteryDataID int Value with DataID of the selected deviceRemoved
     * @param toWarehouseDataID int Value with DataID of the selected warehouse
     */
    private void removeBattery(int caseDataID, int stoppointDataID, int batteryDataID, int toWarehouseDataID){
        //Validate for empty required fields
        if(stoppointDataID == 0 || batteryDataID == 0 || toWarehouseDataID == 0){
            setItemStatus(TSValues.ACTIVITIES_STATUS_USERERROR);
            return;
        }
        SolutionRecord batterySR = util.getSolutionRecord(TSValues.DEVICE_ENTITY, batteryDataID);
        SolutionRecord storageSR = util.getSolutionRecord(TSValues.WAREHOUSE_ENTITY, toWarehouseDataID);
        SolutionRecord stoppointSR = util.getSolutionRecord(TSValues.STOPPOINT_ENTITY, stoppointDataID);
        Device battery = new Device(batterySR);  
        
        try{
            if(!battery.isBattery()){
                setItemStatus(TSValues.ACTIVITIES_STATUS_USERERROR);
                return;
            }
        }
        catch(Exception e){
            setItemStatus(TSValues.ACTIVITIES_STATUS_SYSTEMERROR);
            return;
        }
            
        //Execution stage
        try {
            battery.setStorage(storageSR);
            if(c.StatusID == TSValues.ACTIVITIES_STATUS_USERERROR){
                setItemStatus(TSValues.ACTIVITIES_STATUS_DELETE);
                SolutionRecordNew srnCorrect = util.batteryTakedownRecord(batterySR, stoppointSR, toWarehouseDataID, caseDataID);
                srnCorrect.persistChanges(false);
            }
            else{
                setIntFieldValue(TSValues.ACTIVITY_DEVICE, batteryDataID);
                setItemStatus(TSValues.ACTIVITIES_STATUS_APPROVED);

            }
            battery.persistChanges();
        } catch (Exception ex) {
            setItemStatus(TSValues.ACTIVITIES_STATUS_SYSTEMERROR);
        }
    }
    
    
    /**
     * Activity for setting up a device on a stop point, offers optional setting up a battery at the same time. 
     * Will not do any changes and set status of record to user error if stoppoint or  deviceRemoved is empty
     * and if setupbattery is chosen battery must also be filled. Checks if deviceRemoved is countdown module and battery is a battery(If chosen)
     * If setup battery is chosen, the function also creates a setup battery record at the same time for logging purposes.
     * @param caseDataID int Value with DataID of the case, can be 0(No selected case by user)
     * @param stoppointDataID int Value with DataID of the selected stoppoint
     * @param deviceDataID int Value with DataID of the selected Countdown deviceRemoved
     * @param setupBattery Int value with 0/1 if setup battery is selected
     * @param batteryDataID int Value with DataID of the selected battery deviceRemoved
     */
    private void setupDeviceOnStoppoint(int caseDataID, int stoppointDataID, int deviceDataID, int setupBattery, int batteryDataID){

    //Validation stage
    if(stoppointDataID == 0 || deviceDataID == 0 || (setupBattery == 1 && batteryDataID == 0)){
        setItemStatus(TSValues.ACTIVITIES_STATUS_USERERROR);
        return;
    }

    SolutionRecord spSR = util.getSolutionRecord(TSValues.STOPPOINT_ENTITY, stoppointDataID);
    SolutionRecord deviceCountdownSR = util.getSolutionRecord(TSValues.DEVICE_ENTITY, deviceDataID);
    Device deviceCountdown = new Device(deviceCountdownSR);
    SolutionRecord batterySR = null;
    Device deviceBattery = null;

    try{
        if(!deviceCountdown.isCountdownModule()){
            setItemStatus(TSValues.ACTIVITIES_STATUS_USERERROR);
            return;
        }
            
        if(setupBattery == 1){
            batterySR = util.getSolutionRecord(TSValues.DEVICE_ENTITY, batteryDataID);
            deviceBattery = new Device(batterySR);
            if(!deviceBattery.isBattery()){
                setItemStatus(TSValues.ACTIVITIES_STATUS_USERERROR);
                return;
            }
        }
    }
    catch(Exception e){
        setItemStatus(TSValues.ACTIVITIES_STATUS_SYSTEMERROR);
        return;
    }   
        
        
    
   //Execute stage
    try {
        deviceCountdown.setStoppoint(spSR);
        if(setupBattery == 1){
            deviceBattery.setStoppoint(spSR);
        }
        
        if(c.StatusID == TSValues.ACTIVITIES_STATUS_USERERROR){
            setItemStatus(TSValues.ACTIVITIES_STATUS_DELETE);
            SolutionRecordNew setupDevSrn = util.deviceSetupRecord(deviceCountdownSR, spSR, caseDataID);
            setupDevSrn.persistChanges(false);
        }
        else{
            setIntFieldValue(TSValues.ACTIVITY_SETUPBATTERY, 0);
            setStringFieldValue(TSValues.ACTIVITY_SELECTEDDEVICETWO, "");
            setIntFieldValue(TSValues.ACTIVITY_DEVICE, deviceDataID);
            setItemStatus(TSValues.ACTIVITIES_STATUS_APPROVED);
        }
        
        //Persist stuff
        if(setupBattery == 1){
            SolutionRecordNew setupBatterySrn = util.batterySetupRecord(batterySR, spSR, caseDataID);
            setupBatterySrn.persistChanges(false);
            deviceBattery.persistChanges();
        }
        
        deviceCountdown.persistChanges();
        
    } catch (Exception ex) {
        setItemStatus(TSValues.ACTIVITIES_STATUS_SYSTEMERROR);
    }
            
            
            
                

                
                

            


    }
    
    /**
     * Activity for removing a device at a stop point, offers optional setting up another device at the same time. 
     * Validates stoppoint, removed device and towarehouse are not empty values. If setup module is true setup device is also validated to not be empty.
     * Removed device and setup device(if chosen) is validated if they are countdown modules. If validation fails sets the record to fail user error status
     * and performs no changes.
     * If successful validation and the setupModule is true, function creates an additional setupactivity record for logging purposes.
     * @param caseDataID int Value with DataID of the case, can be 0(No selected case by user)
     * @param stoppointDataID int Value with DataID of the selected stoppoint
     * @param deviceRemovedDataID int Value with DataID of the device that is being removed
     * @param toWarehouseDataID int value with DataID of the warehouse the device is being moved to
     * @param setupModule int value with 0 or 1 if a setupmodule option is chosen. 1 is true
     * @param deviceSetupDataID int value with DataID of the device being setup on stoppoint. Only relevant if setupModule is chosen
     */
    private void removeDeviceFromStoppoint(int caseDataID, int stoppointDataID, int deviceRemovedDataID, int toWarehouseDataID, int setupModule, int deviceSetupDataID){
        if(stoppointDataID == 0 || deviceRemovedDataID == 0 || toWarehouseDataID == 0 || (setupModule == 1 && deviceSetupDataID == 0) || deviceRemovedDataID == deviceSetupDataID){
            setItemStatus(TSValues.ACTIVITIES_STATUS_USERERROR);
            return;
        }
        
        SolutionRecord deviceRemovedSR = util.getSolutionRecord(TSValues.DEVICE_ENTITY, deviceRemovedDataID);
        SolutionRecord warehouseSR = util.getSolutionRecord(TSValues.WAREHOUSE_ENTITY, toWarehouseDataID);
        SolutionRecord stoppointSR = util.getSolutionRecord(TSValues.STOPPOINT_ENTITY, stoppointDataID);
        Device deviceRemoved = new Device(deviceRemovedSR);
        
        SolutionRecord deviceSetupSR = null;
        Device deviceSetup = null;
        
        try{
            if(!deviceRemoved.isCountdownModule()){
                setItemStatus(TSValues.ACTIVITIES_STATUS_USERERROR);
                return;
            }
            if(setupModule == 1){
                deviceSetupSR = util.getSolutionRecord(TSValues.DEVICE_ENTITY, deviceSetupDataID);
                deviceSetup = new Device(deviceSetupSR);
                if(!deviceSetup.isCountdownModule()){
                    setItemStatus(TSValues.ACTIVITIES_STATUS_USERERROR);
                    return;
                }
            }
        }
        catch(Exception e){
            setItemStatus(TSValues.ACTIVITIES_STATUS_SYSTEMERROR);
            return;
        }   
            

        
        try{
            deviceRemoved.setStorage(warehouseSR);
            if(setupModule == 1){
                deviceSetup.setStoppoint(stoppointSR);
            }
            if(c.StatusID == TSValues.ACTIVITIES_STATUS_USERERROR){
                setItemStatus(TSValues.ACTIVITIES_STATUS_DELETE);
                SolutionRecordNew takedownActivity = util.deviceTakedownRecord(deviceRemovedSR, stoppointSR, toWarehouseDataID, caseDataID);
                takedownActivity.persistChanges(false);
            }
            else{
                setIntFieldValue(TSValues.ACTIVITY_SETUPMODULE, 0);
                setStringFieldValue(TSValues.ACTIVITY_SELECTEDDEVICE, "");
                setIntFieldValue(TSValues.ACTIVITY_DEVICE, deviceRemovedDataID);
                setItemStatus(TSValues.ACTIVITIES_STATUS_APPROVED);
            }
            if(setupModule == 1){
                SolutionRecordNew setupActivity = util.deviceSetupRecord(deviceSetupSR, stoppointSR, caseDataID);
                setupActivity.persistChanges(false);
                deviceSetup.persistChanges();
            }
            deviceRemoved.persistChanges();
        }
        catch(Exception e){
            setItemStatus(TSValues.ACTIVITIES_STATUS_SYSTEMERROR);
        }       

    }
    
    /**
     * Activity for restarting a device, this changes nothing in the system, exist 
     * for logging purposes
     * @param deviceDataID int value with the DataID of the device being restarted
     * @param stoppointDataID int value with the stoppoint chosen
     * @param caseDataID int Value with DataID of the case, can be 0(No selected case by user)
     */
    private void restartDevice(int deviceDataID, int stoppointDataID, int caseDataID){
        //Validation
        if(deviceDataID == 0 || stoppointDataID == 0){
            setItemStatus(TSValues.ACTIVITIES_STATUS_USERERROR);
            return;
        }
        

       
        if(c.StatusID == TSValues.ACTIVITIES_STATUS_USERERROR){
            setItemStatus(TSValues.ACTIVITIES_STATUS_DELETE);
            SolutionRecord srDev = util.getSolutionRecord(TSValues.DEVICE_ENTITY, deviceDataID);
            SolutionRecord srSP = util.getSolutionRecord(TSValues.STOPPOINT_ENTITY, stoppointDataID);
            try {
                SolutionRecordNew srnCorrect = util.restartDeviceRecord(srDev,srSP,caseDataID);
                srnCorrect.persistChanges(false);
            } catch (Exception ex) {
            }
        }
        else{
            c.fields.getElementByFieldName(TSValues.ACTIVITY_DEVICE).setFieldValue(deviceDataID);
            setItemStatus(TSValues.ACTIVITIES_STATUS_APPROVED);
        }
        

                    

    }
    
        
    /**
     * Activity for performing cablecheck on a device, this changes nothing in the system, exist 
     * for logging purposes
     * @param deviceDataID int value with the DataID of the device that has had cablecheck performed on
     * @param stoppointDataID int value with the stoppoint chosen
     * @param caseDataID int Value with DataID of the case, can be 0(No selected case by user)
     */
    private void cableCheck(int deviceDataID, int stoppointDataID, int caseDataID){
        //Validation
        if(deviceDataID == 0 || stoppointDataID == 0){
            setItemStatus(TSValues.ACTIVITIES_STATUS_USERERROR);
            return;
        }
        
        if(c.StatusID == TSValues.ACTIVITIES_STATUS_USERERROR){
            setItemStatus(TSValues.ACTIVITIES_STATUS_DELETE);
            SolutionRecord srDev = util.getSolutionRecord(TSValues.DEVICE_ENTITY, deviceDataID);
            SolutionRecord srSP = util.getSolutionRecord(TSValues.STOPPOINT_ENTITY, stoppointDataID);
            try {
                SolutionRecordNew srnCorrect = util.cableCheckRecord(srDev,srSP,caseDataID);
                srnCorrect.persistChanges(false);
            } catch (Exception ex) {
            }
        }
        else{
            c.fields.getElementByFieldName(TSValues.ACTIVITY_DEVICE).setFieldValue(deviceDataID);
            setItemStatus(TSValues.ACTIVITIES_STATUS_APPROVED);
        }
    }
    
    private void componentSetup(int caseDataID, int defaultStorage, int inventoryComponentRecordDataID, int componentDataID,int stoppointDataID,int componentAmount, Util util){
        try{
            
            //Get record of inventory for specific warehouse
            SolutionRecord warehouseComponentSR = util.getSolutionRecord(TSValues.COMPONENTSTORAGE_ENTITY, inventoryComponentRecordDataID);
            WarehouseComponent warehouseComponent = new WarehouseComponent(warehouseComponentSR);
            SolutionRecordNew defaultWarehouseActivity = null;
            componentDataID = warehouseComponent.getComponentDataID();
            int availableComponents = warehouseComponent.getInventoryAmount();
            int amountRemoved = 0;
            //Check if there is enough items from the storage being taken from. 
            if(availableComponents > componentAmount){
                warehouseComponent.removeComponentsFromInventory(componentAmount);
                amountRemoved = componentAmount;
            }
            else{
                if(defaultStorage == warehouseComponent.getWarehouseDataID()){
                    setItemStatus(TSValues.ACTIVITIES_STATUS_AWAITADMIN);
                    return;
                }
                
                
                int defaultComponentStorageDataID = util.findWarehouseComponentDataID(componentDataID, defaultStorage);
                //Well if a record dosent exist in default storage theres not much to do.........
                if(defaultComponentStorageDataID == 0){
                    setRedirectErrorMsg("Home storage component record does not exist");
                    setItemStatus(TSValues.ACTIVITIES_STATUS_USERERROR);
                    return;
                }
                
                SolutionRecord defaultComponentStorageSR = util.getSolutionRecord(TSValues.COMPONENTSTORAGE_ENTITY, defaultComponentStorageDataID);
                
                //If a user attempts to take from a storage with 0 in inventory we do not create a record, just change the existing one 
                //to be an record from the defaul storage instead
                if(availableComponents == 0){
                    warehouseComponent = new WarehouseComponent(defaultComponentStorageSR);
                    setIntFieldValue(TSValues.ACTIVITY_FROMINVENTORY, defaultStorage);
                    setIntFieldValue(TSValues.ACTIVITY_INVENTORYCOMPONENT, defaultComponentStorageDataID);
                    try{
                        warehouseComponent.removeComponentsFromInventory(componentAmount);
                        amountRemoved = componentAmount;
                    }
                    catch(ValueException e){
                        setItemStatus(TSValues.ACTIVITIES_STATUS_AWAITADMIN);
                        return;
                    }

                }
                //Substract from current selected and the main warehouse
                else{
                    int defaultWarehouseComponentAmount = componentAmount - availableComponents;
                    setIntFieldValue(TSValues.ACTIVITY_COMPONENTAMOUNT, availableComponents);
                    SolutionRecord spSR = util.getSolutionRecord(TSValues.STOPPOINT_ENTITY, stoppointDataID);
                    defaultWarehouseActivity = util.createComponentSetupActivity(caseDataID, spSR, defaultStorage, defaultComponentStorageDataID, defaultWarehouseComponentAmount);
                    amountRemoved = availableComponents;
                    warehouseComponent.removeComponentsFromInventory(availableComponents);
                }
                
            }
            
            
            
            
            int spcDataID = util.findStoppointComponentDataID(componentDataID,stoppointDataID);
            if(spcDataID == 0){
                SolutionRecord stoppointSR = util.getSolutionRecord(TSValues.STOPPOINT_ENTITY, stoppointDataID);
                SolutionRecord componentSR = util.getSolutionRecord(TSValues.COMPONENT_ENTITY, componentDataID);
                SolutionRecordNew srn = util.createStoppointInvComponentRecord(stoppointSR, componentSR, amountRemoved);
                srn.persistChanges();
            }
            else{
                SolutionRecord stoppointComponentSR = util.getSolutionRecord(TSValues.STOPPOINTINV_ENTITY, spcDataID);
                StoppointComponent stoppointComponent = new StoppointComponent(stoppointComponentSR);
                stoppointComponent.addStoppointInvComponent(amountRemoved);
                stoppointComponent.persistChanges();
            }
            warehouseComponent.persistChanges();
            if(defaultWarehouseActivity != null){
                defaultWarehouseActivity.persistChanges(true);
            }
                      
 
            setItemStatus(TSValues.ACTIVITIES_STATUS_APPROVED);

        }
        catch(ValueException e){
            //setRedirectErrorMsg("Not enough items in inventory");
            setItemStatus(TSValues.ACTIVITIES_STATUS_USERERROR);
        }
        catch(IllegalArgumentException e){
            setRedirectErrorMsg("Invalid DataIDs");
            setItemStatus(TSValues.ACTIVITIES_STATUS_USERERROR);
        }
        catch(Exception e){
            setItemStatus(TSValues.ACTIVITIES_STATUS_SYSTEMERROR);
        }
    }
    
    private void componentTakedown(int componentDataID, int stoppointDataID,int componentAmount){
        if(componentDataID == 0 || stoppointDataID == 0 || componentAmount < 1){
            setItemStatus(TSValues.ACTIVITIES_STATUS_USERERROR);
            return;
        }
        
        
        int spcDataID = util.findStoppointComponentDataID(componentDataID,stoppointDataID);
        
        if(spcDataID == 0){
            //Do nothing, we cant remove stuff that dont exist
            return;
        }
        
        
        try{
            SolutionRecord spcSR = util.getSolutionRecord(TSValues.STOPPOINTINV_ENTITY, spcDataID);
            StoppointComponent stoppointComponent = new StoppointComponent(spcSR);

            stoppointComponent.removeStoppointInvComponent(componentAmount);
            stoppointComponent.persistChanges();
            setItemStatus(TSValues.ACTIVITIES_STATUS_APPROVED);
        }
        catch(IllegalArgumentException e){
            setItemStatus(TSValues.ACTIVITIES_STATUS_USERERROR);
        }
        catch(ValueException e){
            setItemStatus(TSValues.ACTIVITIES_STATUS_USERERROR);
        }
        catch(Exception e){
            setItemStatus(TSValues.ACTIVITIES_STATUS_SYSTEMERROR);
        }
    }
    
    private void moveComponent(int inventoryComponentRecordDataID, int toWarehouseDataID, int componentAmount, Util util){
        
        try{

            SolutionRecord srFrom = util.getSolutionRecord(TSValues.COMPONENTSTORAGE_ENTITY, inventoryComponentRecordDataID);
            WarehouseComponent warehouseComponentFrom = new WarehouseComponent(srFrom);
            warehouseComponentFrom.removeComponentsFromInventory(componentAmount);
            
            int inventoryComponentDataID = warehouseComponentFrom.getComponentDataID();
            int toWarehouseInventoryComponentDataID = util.findWarehouseComponentDataID(inventoryComponentDataID, toWarehouseDataID);

            //Record dosent exist, Create it
            if(toWarehouseInventoryComponentDataID == 0){
                SolutionRecord warehouseToSR = util.getSolutionRecord(TSValues.WAREHOUSE_ENTITY, toWarehouseDataID);
                SolutionRecord componentSR = util.getSolutionRecord(TSValues.COMPONENT_ENTITY, inventoryComponentDataID);
                SolutionRecordNew srnTo = util.createWarehouseInvComponentRecord(warehouseToSR, componentSR, componentAmount);
                warehouseComponentFrom.persistChanges();
                srnTo.persistChanges();
            }
            else{
                //Update record
                SolutionRecord srTo = util.getSolutionRecord(TSValues.COMPONENTSTORAGE_ENTITY, toWarehouseInventoryComponentDataID);
                WarehouseComponent warehouseComponentTo = new WarehouseComponent(srTo);
                warehouseComponentTo.addInventoryComponent(componentAmount);
                warehouseComponentFrom.persistChanges();
                warehouseComponentTo.persistChanges();
            }
            setItemStatus(TSValues.ACTIVITIES_STATUS_APPROVED);
        }
        catch(IllegalArgumentException e){
            //Shouldnt really happen as theres a check before
            setItemStatus(TSValues.ACTIVITIES_STATUS_USERERROR);
        }
        catch(ValueException e){
            //If you remove more than there is present
            setItemStatus(TSValues.ACTIVITIES_STATUS_USERERROR);
        }
            catch(Exception e){
            setItemStatus(TSValues.ACTIVITIES_STATUS_SYSTEMERROR);
        }
    }

    private void moveDevice(int SelectedDeviceDataID, int toWarehouseDataID, Util util){
        try{
            
            //Get records
            SolutionRecord deviceSR = util.getSolutionRecord(TSValues.DEVICE_ENTITY, SelectedDeviceDataID);
            SolutionRecord warehouseSR = util.getSolutionRecord(TSValues.WAREHOUSE_ENTITY, toWarehouseDataID);
            Device device = new Device(deviceSR);
            
            //Validation checks
            if(device.isSetAtStoppoint()){
                setItemStatus(TSValues.ACTIVITIES_STATUS_USERERROR);
                return;
            }
            
            //Set deviceRemoved storage
            device.setStorage(warehouseSR);
            device.persistChanges();
            setItemStatus(TSValues.ACTIVITIES_STATUS_APPROVED);
        }
        catch(IllegalArgumentException e){
            setItemStatus(TSValues.ACTIVITIES_STATUS_USERERROR);
        }
        catch(Exception e){
            setItemStatus(TSValues.ACTIVITIES_STATUS_SYSTEMERROR);
        }
    }
    


    

    
    

    private void setStringFieldValue(String fieldName, String value){
        c.fields.getElementByFieldName(fieldName).setFieldValue(value);
    }
    
    private void setIntFieldValue(String fieldName, int value){
        c.fields.getElementByFieldName(fieldName).setFieldValue(value);
    }
    
    private String getStringFieldValue(String fieldName){
        return c.fields.getElementByFieldName(fieldName).FieldValue;
    }

    private int getIntFieldValue(String fieldName){
        return Parser.getInteger(c.fields.getElementByFieldName(fieldName).FieldValue);
    }
  
    

    
    
    private void setDebugMessage(String s){
        //c.fields.getElementByFieldName("DEBUGFIELD").setFieldValue(s);
    }
    
    private void setRedirectErrorMsg(String errorMsg){
        StringBuilder sb = new StringBuilder();
        sb.append("main?SagID=");
        sb.append(String.valueOf(c.SagID));
        sb.append("&DataID=");
        sb.append(String.valueOf(c.DataID));
        sb.append("&command=edit");
        sb.append("&SubformSession=1");
        sb.append("&errormsg=");
        sb.append(errorMsg);
        this.setRedirect(sb.toString());
    }
    

    

    
    
    
    
}
