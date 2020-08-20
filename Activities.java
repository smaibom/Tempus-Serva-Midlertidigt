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
import java.util.HashMap;
import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;

/**
 *
 * CodeunitFormevent for the activities entity, handles all functionality of changing 
 * parent child relationship and removing field values that are not relevant to an activity
 * before submitting a record
 * @author XSMA
 */
public class Activities extends CodeunitFormevents{
    
       
    private final int statusIDinit = 83;
    private final int statusIDuserError = 98;
    

    
    
    @Override
    public void beforeRenderList() throws Exception {

    }
    

    @Override
    public void beforeUpdateItem() throws Exception{
        String typeField = c.fields.getElementByFieldName(TSValues.ACTIVITY_ACTIVITY).FieldValue;
        int SelectedDeviceDataID =  Parser.getInteger(c.fields.getElementByFieldName(TSValues.ACTIVITY_SELECTEDDEVICE).FieldValue);
        int deviceAtStoppointDataID = Parser.getInteger(c.fields.getElementByFieldName(TSValues.ACTIVITY_DEVICEONSTOPPOINT).FieldValue);

        int stoppointDataID = Parser.getInteger(c.fields.getElementByFieldName(TSValues.ACTIVITY_STOPPOINT).FieldValue);
        int toWarehouseDataID = Parser.getInteger(c.fields.getElementByFieldName(TSValues.ACTIVITY_TOINVENTORY).FieldValue);
        int fromWarehouseDataID = Parser.getInteger(c.fields.getElementByFieldName(TSValues.ACTIVITY_FROMINVENTORY).FieldValue);
        int caseDataID = Parser.getInteger(c.fields.getElementByFieldName(TSValues.ACTIVITY_CASE).FieldValue);
        int componentDataID = Parser.getInteger(c.fields.getElementByFieldName(TSValues.ACTIVITY_COMPONENTINSTALLED).FieldValue);
        int inventoryComponentRecordDataID = Parser.getInteger(c.fields.getElementByFieldName(TSValues.ACTIVITY_INVENTORYCOMPONENT).FieldValue);
        int componentAmount = Parser.getInteger(c.fields.getElementByFieldName(TSValues.ACTIVITY_COMPONENTAMOUNTINSTALLED).FieldValue);
        int batteryOnStoppointDataID = Parser.getInteger(c.fields.getElementByFieldName(TSValues.ACTIVITY_BATTERYONSTOPPOINT).FieldValue);
        //int supplierDataID = Parser.getInteger(c.fields.getElementByFieldName(activitySupplier).FieldValue);
        int statusID = Parser.getInteger(c.fields.getElementByFieldName("StatusID").FieldValue);
        int selectedDeviceTwoDataID = Parser.getInteger(c.fields.getElementByFieldName(TSValues.ACTIVITY_SELECTEDDEVICETWO).FieldValue);
        int SetupBattery = Parser.getInteger(c.fields.getElementByFieldName(TSValues.ACTIVITY_SETUPBATTERY).FieldValue);
        
         

        if(!(statusID == statusIDinit || statusID == statusIDuserError)){
            return;
        }
        
        
        Session ses = SessionFactory.getSession(this);
        Util util = new Util(ses);
        
        switch(typeField){
            //Setup battery
            case "168":
                if(stoppointDataID == 0 || SelectedDeviceDataID == 0 ){
                        setItemStatus(98);
                        break;
                }
                setupBattery(SelectedDeviceDataID,stoppointDataID,util);
                break;
                
            //Remove Battery from stoppoint
            case "169":
                if(stoppointDataID == 0 || batteryOnStoppointDataID == 0 || toWarehouseDataID == 0){
                    
                    setItemStatus(98);
                    break;
                }
                removeBattery(batteryOnStoppointDataID,toWarehouseDataID,util);
                break;
                
            //Change deviceCountdown
            case "170":
                if(SelectedDeviceDataID == 0 || deviceAtStoppointDataID == 0 || stoppointDataID == 0 || toWarehouseDataID == 0 || SelectedDeviceDataID == deviceAtStoppointDataID){
                    setItemStatus(98);
                    break;
                }
                changeDevice(SelectedDeviceDataID,deviceAtStoppointDataID,stoppointDataID,toWarehouseDataID,caseDataID,util);
                break;
                
                
            //Setup deviceCountdown at stoppoint
            case "171":
                if(stoppointDataID == 0 || SelectedDeviceDataID == 0 || (SetupBattery == 1 && selectedDeviceTwoDataID == 0)){
                    setRedirectErrorMsg("Missing required fields");
                    setItemStatus(98);
                    break;
                }
                setupDeviceOnStoppoint(caseDataID,stoppointDataID,SelectedDeviceDataID,SetupBattery,selectedDeviceTwoDataID,util);
                break;
                
                
            //Remove deviceCountdown from stoppoint
            case "172":
                if(deviceAtStoppointDataID == 0 || toWarehouseDataID == 0){
                    setItemStatus(98);
                    break;
                }
                removeDeviceFromStoppoint(deviceAtStoppointDataID,toWarehouseDataID,util);
                break;
                
                
            //Restart deviceCountdown
            case "174":
                if(stoppointDataID == 0 || deviceAtStoppointDataID == 0){
                    setItemStatus(98);
                    break;
                }
                restartDevice(deviceAtStoppointDataID);
                break;
                
                
            //Cablecheck
            case "175":
                if(stoppointDataID == 0 || deviceAtStoppointDataID == 0){
                    setItemStatus(98);
                        break;
                }
                cableCheck(deviceAtStoppointDataID);
                break;
                
                
            //Component setup
            case "176":
                if(inventoryComponentRecordDataID == 0 || fromWarehouseDataID == 0 || stoppointDataID == 0 || componentAmount < 1){
                    setRedirectErrorMsg("Missing required fields");
                    setItemStatus(98);
                    break;
                }
                //TODO: REMOVE MEEEEE
                int defaultStorageDataID = 161;
                componentSetup(defaultStorageDataID,inventoryComponentRecordDataID,componentDataID,stoppointDataID,componentAmount,util);
                break;
                
                
            //Component takedown
            case "177":
                if(componentDataID == 0 || stoppointDataID == 0 || componentAmount < 1){
                    setItemStatus(98);
                    break;
                }
                componentTakedown(componentDataID,stoppointDataID,componentAmount,util);
                break;
            
            //Move component
            case "178":
                if(toWarehouseDataID == 0 || inventoryComponentRecordDataID == 0 || componentAmount < 1){
                    setItemStatus(98);
                    break;
                }
                moveComponent(inventoryComponentRecordDataID,toWarehouseDataID,componentAmount,util);
                break;
            
            //Move deviceCountdown
            case "179":
                if(SelectedDeviceDataID == 0 || toWarehouseDataID == 0){
                    setItemStatus(98);
                    break;
                }
                moveDevice(SelectedDeviceDataID,toWarehouseDataID,util);
                break;
                
            default:
                break;
        } 
        
        ses.close();
        
        
    }
    
    
    
    private void changeDevice(int SelectedDeviceDataID,int deviceAtStoppointDataID,int stoppointDataID, int toWarehouseDataID, int caseDataID, Util util){
        try{
            
            SolutionRecord devInstallSR = util.getSolutionRecord(TSValues.DEVICE_ENTITY, SelectedDeviceDataID);
            SolutionRecord devUninstallSR = util.getSolutionRecord(TSValues.DEVICE_ENTITY, deviceAtStoppointDataID);
            SolutionRecord spSR = util.getSolutionRecord(TSValues.STOPPOINT_ENTITY, stoppointDataID);
            SolutionRecord warehouseSR = util.getSolutionRecord(TSValues.WAREHOUSE_ENTITY, toWarehouseDataID);
                    
            Device devInstall = new Device(devInstallSR);
            Device devUninstall = new Device(devUninstallSR);
            
            if(!(devInstall.isCountdownModule() && devUninstall.isCountdownModule())){
                setItemStatus(98);
                return;
            }
            devInstall.setStoppoint(spSR);
            devUninstall.setStorage(warehouseSR);
                    
            SolutionRecordNew setupActivityRecord = util.createSetupActivity(caseDataID, spSR, devInstallSR);
            
            c.fields.getElementByFieldName(TSValues.ACTIVITY_DEVICE).setFieldValue(deviceAtStoppointDataID);
            c.fields.getElementByFieldName(TSValues.ACTIVITY_ACTIVITY).setFieldValue(172);
            devInstall.persistChanges();
            devUninstall.persistChanges();
            setupActivityRecord.persistChanges(false);
            setItemStatus(100);
        }
        catch(IllegalArgumentException e){
            setItemStatus(98);
        }
        catch(Exception e){
            setItemStatus(101);
        }
    }
    
    private void setupDeviceOnStoppoint(int caseDataID, int stoppointDataID, int SelectedDeviceDataID, int SetupBattery, int SelectedDeviceTwoDataID, Util util){
        try{
            
            SolutionRecord spSR = util.getSolutionRecord(TSValues.STOPPOINT_ENTITY, stoppointDataID);
            SolutionRecord deviceCountdownSR = util.getSolutionRecord(TSValues.DEVICE_ENTITY, SelectedDeviceDataID);
            Device deviceCountdown = new Device(deviceCountdownSR);

            if(!deviceCountdown.isCountdownModule()){
                setRedirectErrorMsg("Selected Device is not a countdown module");
                setItemStatus(98);
                return;
            }
            
            deviceCountdown.setStoppoint(spSR);
            
            c.fields.getElementByFieldName(TSValues.ACTIVITY_DEVICE).setFieldValue(SelectedDeviceDataID);
            if(SetupBattery == 1){
                SolutionRecord deviceBatterySR = util.getSolutionRecord(TSValues.DEVICE_ENTITY, SelectedDeviceTwoDataID);
                Device deviceBattery = new Device(deviceBatterySR);

                if(!deviceBattery.isBattery()){
                    setRedirectErrorMsg("Selected Device is not a Battery");
                    setItemStatus(98);
                    return;
                }
                deviceBattery.setStoppoint(spSR);
                HashMap<String,SolutionRecord> records = new HashMap();
                records.put(TSValues.ACTIVITY_DEVICE, deviceBatterySR);
                records.put(TSValues.ACTIVITY_SELECTEDDEVICE, deviceBatterySR);
                records.put(TSValues.ACTIVITY_STOPPOINT, spSR);
                SolutionRecordNew srn = util.createActivityRecord(caseDataID, records, 168, 100);
                c.fields.getElementByFieldName(TSValues.ACTIVITY_SETUPBATTERY).setFieldValue(0);
                c.fields.getElementByFieldName(TSValues.ACTIVITY_SELECTEDDEVICETWO).setFieldValue("");
                deviceBattery.persistChanges();
                srn.persistChanges(false);
            }
            deviceCountdown.persistChanges();
            setItemStatus(100);
        }
        catch(IllegalArgumentException e){
            setItemStatus(98);
        }
        catch(Exception e){
            setItemStatus(101);
        }

    }
    
    private void removeDeviceFromStoppoint(int deviceAtStoppointDataID, int toWarehouseDataID, Util util){
        try{
            SolutionRecord deviceSR = util.getSolutionRecord(TSValues.DEVICE_ENTITY, deviceAtStoppointDataID);
            SolutionRecord warehouseSR = util.getSolutionRecord(TSValues.WAREHOUSE_ENTITY, toWarehouseDataID);

            Device device = new Device(deviceSR);
            if(!device.isCountdownModule()){
                setItemStatus(98);
                return;
            }
            device.setStorage(warehouseSR);
            
            c.fields.getElementByFieldName(TSValues.ACTIVITY_DEVICE).setFieldValue(deviceAtStoppointDataID);
            device.persistChanges();
            setItemStatus(100);

        }
        catch(IllegalArgumentException e){
            setItemStatus(98);
        }
        catch(Exception e){
            setItemStatus(101);
        }
    }
    
    private void restartDevice(int deviceAtStoppointDataID){
        c.fields.getElementByFieldName(TSValues.ACTIVITY_DEVICE).setFieldValue(deviceAtStoppointDataID);
        setItemStatus(100);
    }
            
    private void cableCheck(int deviceAtStoppointDataID){
        c.fields.getElementByFieldName(TSValues.ACTIVITY_DEVICE).setFieldValue(deviceAtStoppointDataID);
        setItemStatus(100);
    }
    
    private void componentSetup(int defaultStorage, int inventoryComponentRecordDataID, int componentDataID,int stoppointDataID,int componentAmount, Util util){
        try{
            
            //Get record of inventory for specific warehouse
            SolutionRecord warehouseComponentSR = util.getSolutionRecord(TSValues.COMPONENTSTORAGE_ENTITY, inventoryComponentRecordDataID);
            WarehouseComponent warehouseComponent = new WarehouseComponent(warehouseComponentSR);
            WarehouseComponent defaultWarehouseComponent = null;
            componentDataID = warehouseComponent.getComponentDataID();
            int availableComponents = warehouseComponent.getInventoryAmount();
            
            //Check if there is enough items from the storage being taken from. 
            if(availableComponents > componentAmount){
                warehouseComponent.removeComponentsFromInventory(componentAmount);
            }
            else{
                int defaultComponentStorageDataID = util.findWarehouseComponentDataID(componentDataID, defaultStorage);
                //Well if a record dosent exist in default storage theres not much to do.........
                if(defaultComponentStorageDataID == 0){
                    setRedirectErrorMsg("Home storage component record does not exist");
                    setItemStatus(98);
                    return;
                }
                
                SolutionRecord defaultComponentStorageSR = util.getSolutionRecord(TSValues.COMPONENTSTORAGE_ENTITY, defaultComponentStorageDataID);
                
                //If a user attempts to take from a storage with 0 in inventory we do not create a record, just change the existing one 
                //to be an record from the defaul storage instead
                if(availableComponents == 0){
                    setRedirectErrorMsg("Did i get to this point");
                    warehouseComponent = new WarehouseComponent(defaultComponentStorageSR);
                    warehouseComponent.removeComponentsFromInventory(componentAmount);
                    
                    c.fields.getElementByFieldName(TSValues.ACTIVITY_FROMINVENTORY).setFieldValue(defaultStorage);
                    c.fields.getElementByFieldName(TSValues.ACTIVITY_INVENTORYCOMPONENT).setFieldValue(defaultComponentStorageDataID);
                }
                else{
                    
                }
                
            }
            
            
            
            
            int spcDataID = util.findStoppointComponentDataID(componentDataID,stoppointDataID);
            if(spcDataID == 0){
                SolutionRecord stoppointSR = util.getSolutionRecord(TSValues.STOPPOINT_ENTITY, stoppointDataID);
                SolutionRecord componentSR = util.getSolutionRecord(TSValues.COMPONENT_ENTITY, componentDataID);
                SolutionRecordNew srn = util.createStoppointInvComponentRecord(stoppointSR, componentSR, componentAmount);
                srn.persistChanges();
            }
            else{
                SolutionRecord stoppointComponentSR = util.getSolutionRecord(TSValues.STOPPOINTINV_ENTITY, spcDataID);
                StoppointComponent stoppointComponent = new StoppointComponent(stoppointComponentSR);
                stoppointComponent.addStoppointInvComponent(componentAmount);
                stoppointComponent.persistChanges();
            }
            warehouseComponent.persistChanges();
            if(true){
                
            }
                      
 
            setItemStatus(100);

        }
        catch(ValueException e){
            //setRedirectErrorMsg("Not enough items in inventory");
            setItemStatus(98);
        }
        catch(IllegalArgumentException e){
            setRedirectErrorMsg("Invalid DataIDs");
            setItemStatus(98);
        }
        catch(Exception e){
            setItemStatus(101);
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
            setItemStatus(100);
        }
        catch(IllegalArgumentException e){
            setItemStatus(98);
        }
        catch(ValueException e){
            setItemStatus(98);
        }
        catch(Exception e){
            setItemStatus(101);
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
            setItemStatus(100);
        }
        catch(IllegalArgumentException e){
            //Shouldnt really happen as theres a check before
            setItemStatus(98);
        }
        catch(ValueException e){
            //If you remove more than there is present
            setItemStatus(98);
        }
            catch(Exception e){
            setItemStatus(101);
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
                setItemStatus(98);
                return;
            }
            
            //Set device storage
            device.setStorage(warehouseSR);
            device.persistChanges();
            setItemStatus(100);
        }
        catch(IllegalArgumentException e){
            setItemStatus(98);
        }
        catch(Exception e){
            setItemStatus(101);
        }
    }
    
    private void setupBattery(int SelectedDeviceDataID, int stoppointDataID, Util util){
        try{
            //Get records
            SolutionRecord batterySR = util.getSolutionRecord(TSValues.DEVICE_ENTITY, SelectedDeviceDataID);
            SolutionRecord stoppointSR = util.getSolutionRecord(TSValues.STOPPOINT_ENTITY, stoppointDataID);
            Device battery = new Device(batterySR);
            
            //Validation checks
            if(!battery.isBattery()){
                setItemStatus(98);
                return;
            }
            
            //Set device stoppoint
            battery.setStoppoint(stoppointSR);
            battery.persistChanges();
            setItemStatus(100);
        }
        catch(Exception e){
            setItemStatus(101);
        }
    }
    
    private void removeBattery(int batteryOnStoppointDataID, int toWarehouseDataID, Util util){
        try{
            SolutionRecord batterySR = util.getSolutionRecord(TSValues.DEVICE_ENTITY, batteryOnStoppointDataID);
            SolutionRecord storageSR = util.getSolutionRecord(TSValues.WAREHOUSE_ENTITY, toWarehouseDataID);
            
            Device battery = new Device(batterySR);
            if(!battery.isBattery()){
                setItemStatus(98);
                return;
            }
            battery.setStorage(storageSR);
            battery.persistChanges();
            setItemStatus(100);

        }
        catch(Exception e){
            setItemStatus(101);
        }
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
