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
                if(stoppointDataID == 0 || batteryOnStoppointDataID == 0 || toWarehouseDataID == 0){
                    setItemStatus(TSValues.ACTIVITIES_STATUS_USERERROR);
                    break;
                }
                removeBattery(batteryOnStoppointDataID,toWarehouseDataID,util);
                break;
                
            case TSValues.ACTIVITIYCODE_DEVICE_SETUP:
                if(stoppointDataID == 0 || SelectedDeviceDataID == 0 || (setupBattery == 1 && selectedDeviceTwoDataID == 0)){
                    setItemStatus(TSValues.ACTIVITIES_STATUS_USERERROR);
                    break;
                }
                setupDeviceOnStoppoint(caseDataID,stoppointDataID,SelectedDeviceDataID,setupBattery,selectedDeviceTwoDataID,util);
                break;
                
            case TSValues.ACTIVITIYCODE_DEVICE_TAKEDOWN:
                if(stoppointDataID == 0 || deviceAtStoppointDataID == 0 || toWarehouseDataID == 0){
                    setItemStatus(TSValues.ACTIVITIES_STATUS_USERERROR);
                    break;
                }
                removeDeviceFromStoppoint(caseDataID,stoppointDataID,deviceAtStoppointDataID,toWarehouseDataID,setupModule, SelectedDeviceDataID,util);
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
                if(componentDataID == 0 || stoppointDataID == 0 || componentAmount < 1){
                    setItemStatus(TSValues.ACTIVITIES_STATUS_USERERROR);
                    break;
                }
                componentTakedown(componentDataID,stoppointDataID,componentAmount,util);
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
     * Activity for setting a battery up on a stoppoint. Will fail if the selected device is not of the type battery.
     * If the record has not yet been created it will set the device field to the given device on success.
     * @param batteryDataID int Value with DataID of the selected device
     * @param stoppointDataID int Value with DataID of the selected stoppoint
     * @param util Util class element
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
     * 
     * @param caseDataID 
     * @param stoppointDataID
     * @param SelectedDeviceDataID
     * @param SetupBattery
     * @param SelectedDeviceTwoDataID
     * @param util Util class 
     */
    private void setupDeviceOnStoppoint(int caseDataID, int stoppointDataID, int SelectedDeviceDataID, int SetupBattery, int SelectedDeviceTwoDataID, Util util){

        //Validation stage








        try{

            
            SolutionRecord spSR = util.getSolutionRecord(TSValues.STOPPOINT_ENTITY, stoppointDataID);
            SolutionRecord deviceCountdownSR = util.getSolutionRecord(TSValues.DEVICE_ENTITY, SelectedDeviceDataID);
            Device deviceCountdown = new Device(deviceCountdownSR);

            if(!deviceCountdown.isCountdownModule()){
                setRedirectErrorMsg("Selected Device is not a countdown module");
                setItemStatus(TSValues.ACTIVITIES_STATUS_USERERROR);
                return;
            }
            
            deviceCountdown.setStoppoint(spSR);
            
            setIntFieldValue(TSValues.ACTIVITY_DEVICE, SelectedDeviceDataID);
            
            if(SetupBattery == 1){
                SolutionRecord deviceBatterySR = util.getSolutionRecord(TSValues.DEVICE_ENTITY, SelectedDeviceTwoDataID);
                Device deviceBattery = new Device(deviceBatterySR);

                if(!deviceBattery.isBattery()){
                    setRedirectErrorMsg("Selected Device is not a Battery");
                    setItemStatus(TSValues.ACTIVITIES_STATUS_USERERROR);
                    return;
                }
                deviceBattery.setStoppoint(spSR);
                
                SolutionRecordNew srn = util.createSetupBatteryActivity(caseDataID, spSR, deviceBatterySR);
                setIntFieldValue(TSValues.ACTIVITY_SETUPBATTERY, 0);
                setStringFieldValue(TSValues.ACTIVITY_SELECTEDDEVICETWO, "");
                deviceBattery.persistChanges();
                srn.persistChanges(false);
            }
            deviceCountdown.persistChanges();
            setItemStatus(TSValues.ACTIVITIES_STATUS_APPROVED);
        }
        catch(IllegalArgumentException e){
            setItemStatus(TSValues.ACTIVITIES_STATUS_USERERROR);
        }
        catch(Exception e){
            setItemStatus(TSValues.ACTIVITIES_STATUS_SYSTEMERROR);
        }

    }
    
    /**
     * 
     * @param caseDataID
     * @param stoppointDataID
     * @param deviceAtStoppointDataID
     * @param toWarehouseDataID
     * @param setupModule
     * @param moduleDataID
     * @param util 
     */
    private void removeDeviceFromStoppoint(int caseDataID, int stoppointDataID, int deviceAtStoppointDataID, int toWarehouseDataID, int setupModule, int moduleDataID, Util util){
        try{
            SolutionRecord deviceSR = util.getSolutionRecord(TSValues.DEVICE_ENTITY, deviceAtStoppointDataID);
            SolutionRecord warehouseSR = util.getSolutionRecord(TSValues.WAREHOUSE_ENTITY, toWarehouseDataID);

            Device device = new Device(deviceSR);
            if(!device.isCountdownModule()){
                setItemStatus(TSValues.ACTIVITIES_STATUS_USERERROR);
                return;
            }
            device.setStorage(warehouseSR);
            if(setupModule == 1 && moduleDataID != 0){
                SolutionRecord setupModuleSR = util.getSolutionRecord(TSValues.DEVICE_ENTITY, moduleDataID);
                Device setupDevice = new Device(setupModuleSR);
                if(!setupDevice.isCountdownModule()){
                    setItemStatus(TSValues.ACTIVITIES_STATUS_USERERROR);
                    return;
                }
                
                setIntFieldValue(TSValues.ACTIVITY_SETUPMODULE, 0);
                setStringFieldValue(TSValues.ACTIVITY_SELECTEDDEVICE, "");
                
                SolutionRecord stoppointSR = util.getSolutionRecord(TSValues.STOPPOINT_ENTITY, stoppointDataID);

                setupDevice.setStoppoint(stoppointSR);
                
                SolutionRecordNew setupActivity = util.createSetupActivity(caseDataID,stoppointSR,setupModuleSR);
                
                setupActivity.persistChanges(false);
                setupDevice.persistChanges();
            }
            setIntFieldValue(TSValues.ACTIVITY_DEVICE, deviceAtStoppointDataID);
            
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
    
    private void componentTakedown(int componentDataID, int stoppointDataID,int componentAmount, Util util){
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
            
            //Set device storage
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
    

    
    private void removeBattery(int batteryOnStoppointDataID, int toWarehouseDataID, Util util){
        try{
            SolutionRecord batterySR = util.getSolutionRecord(TSValues.DEVICE_ENTITY, batteryOnStoppointDataID);
            SolutionRecord storageSR = util.getSolutionRecord(TSValues.WAREHOUSE_ENTITY, toWarehouseDataID);
            
            Device battery = new Device(batterySR);
            if(!battery.isBattery()){
                setItemStatus(TSValues.ACTIVITIES_STATUS_USERERROR);
                return;
            }
            setIntFieldValue(TSValues.ACTIVITY_DEVICE, batteryOnStoppointDataID);
            battery.setStorage(storageSR);
            battery.persistChanges();
            setItemStatus(TSValues.ACTIVITIES_STATUS_APPROVED);

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
