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
import java.util.Set;
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
    private final String warehouseEntity = "warehouse";
    private final String caseEntity = "workorder";
    private final String stoppointInvEntity = "stoppointinventory";
    private final String componentEntity = "components";
    
    private final String componentStorageEntity = "warehouseallocation";
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
    
    private final int statusIDinit = 83;
    private final int statusIDuserError = 98;
    

    
    
    @Override
    public void beforeRenderList() throws Exception {

    }
    

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
        int batteryOnStoppointDataID = Parser.getInteger(c.fields.getElementByFieldName(activityBatteryOnStoppoint).FieldValue);
        //int supplierDataID = Parser.getInteger(c.fields.getElementByFieldName(activitySupplier).FieldValue);
        int statusID = Parser.getInteger(c.fields.getElementByFieldName("StatusID").FieldValue);
        int selectedDeviceTwoDataID = Parser.getInteger(c.fields.getElementByFieldName(activitySelectedDeviceTwo).FieldValue);
        int SetupBattery = Parser.getInteger(c.fields.getElementByFieldName(activitySetupBattery).FieldValue);
        
         

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
                componentSetup(inventoryComponentRecordDataID,componentDataID,stoppointDataID,componentAmount,util);
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
            SolutionRecord devInstallSR = util.getSolutionRecord(deviceEntity, SelectedDeviceDataID);
            SolutionRecord devUninstallSR = util.getSolutionRecord(deviceEntity, deviceAtStoppointDataID);
            SolutionRecord spSR = util.getSolutionRecord(stoppointEntity, stoppointDataID);
            SolutionRecord warehouseSR = util.getSolutionRecord(warehouseEntity, toWarehouseDataID);
                    
            Device devInstall = new Device(devInstallSR);
            Device devUninstall = new Device(devUninstallSR);
            
            if(!(devInstall.isCountdownModule() && devUninstall.isCountdownModule())){
                setItemStatus(98);
                return;
            }
            devInstall.setStoppoint(spSR);
            devUninstall.setStorage(warehouseSR);
                    
            SolutionRecordNew setupActivityRecord = util.createSetupActivity(caseDataID, spSR, devInstallSR);
            
            c.fields.getElementByFieldName(activityDevice).setFieldValue(deviceAtStoppointDataID);
            c.fields.getElementByFieldName(activityActivity).setFieldValue(172);
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

            SolutionRecord spSR = util.getSolutionRecord(stoppointEntity, stoppointDataID);
            SolutionRecord deviceCountdownSR = util.getSolutionRecord(deviceEntity, SelectedDeviceDataID);
            Device deviceCountdown = new Device(deviceCountdownSR);

            if(!deviceCountdown.isCountdownModule()){
                setRedirectErrorMsg("Selected Device is not a countdown module");
                setItemStatus(98);
                return;
            }
            
            
            Device deviceBattery = null;
            if(SetupBattery == 1){
                SolutionRecord deviceBatterySR = util.getSolutionRecord(deviceEntity, SelectedDeviceTwoDataID);
                deviceBattery = new Device(deviceBatterySR);
                if(!deviceBattery.isBattery()){
                    setRedirectErrorMsg("Selected Device is not a Battery");
                    setItemStatus(98);
                    return;
                }
                deviceBattery.setStoppoint(spSR);
            }

            deviceCountdown.setStoppoint(spSR);

            c.fields.getElementByFieldName(activityDevice).setFieldValue(SelectedDeviceDataID);
            if(SetupBattery == 1){
                SolutionRecordNew srn = util.createSetupBatteryActivity(caseDataID , spSR, deviceCountdownSR);
                c.fields.getElementByFieldName(activitySetupBattery).setFieldValue(0);
                c.fields.getElementByFieldName(activitySelectedDeviceTwo).setFieldValue("");
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
            SolutionRecord deviceSR = util.getSolutionRecord(deviceEntity, deviceAtStoppointDataID);
            SolutionRecord warehouseSR = util.getSolutionRecord(warehouseEntity, toWarehouseDataID);

            Device device = new Device(deviceSR);
            if(!device.isCountdownModule()){
                setItemStatus(98);
                return;
            }
            device.setStorage(warehouseSR);
            
            c.fields.getElementByFieldName(activityDevice).setFieldValue(deviceAtStoppointDataID);
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
        c.fields.getElementByFieldName(activityDevice).setFieldValue(deviceAtStoppointDataID);
        setItemStatus(100);
    }
            
    private void cableCheck(int deviceAtStoppointDataID){
        c.fields.getElementByFieldName(activityDevice).setFieldValue(deviceAtStoppointDataID);
        setItemStatus(100);
    }
    
    private void componentSetup(int inventoryComponentRecordDataID, int componentDataID, int stoppointDataID,int componentAmount, Util util){
        try{
            StoppointComponent spcf = new StoppointComponent();

            SolutionRecord warehouseComponentSR = util.getSolutionRecord(componentStorageEntity, inventoryComponentRecordDataID);
            WarehouseComponent warehouseComponent = new WarehouseComponent(warehouseComponentSR);

            warehouseComponent.removeComponentsFromInventory(componentAmount);

            componentDataID = warehouseComponentSR.getValueInteger(componentStorageComponent);
            int spcDataID = util.findStoppointComponentDataID(componentDataID,stoppointDataID);

            if(spcDataID == 0){
                SolutionRecord stoppointSR = util.getSolutionRecord(stoppointEntity, stoppointDataID);
                SolutionRecord componentSR = util.getSolutionRecord(componentEntity, componentDataID);
                SolutionRecordNew srn = util.createStoppointInvComponentRecord(stoppointSR, componentSR, componentAmount);
                srn.persistChanges();
                warehouseComponent.persistChanges();
            }
            else{
                SolutionRecord sr = util.getSolutionRecord(stoppointInvEntity, spcDataID);
                spcf.addStoppointInvComponent(sr, componentAmount);
                sr.persistChanges();
                warehouseComponent.persistChanges();
            }
            setItemStatus(100);

        }
        catch(ValueException e){
            setRedirectErrorMsg("Not enough items in inventory");
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
        StoppointComponent spcf = new StoppointComponent();
        int spcDataID = util.findStoppointComponentDataID(componentDataID,stoppointDataID);
        if(spcDataID == 0){
            //Do nothing, we cant remove stuff that dont exist
            return;
        }
        try{
            SolutionRecord spcSR = util.getSolutionRecord(stoppointInvEntity, spcDataID);
            spcf.removeStoppointInvComponent(spcSR,componentAmount);
            spcSR.persistChanges();
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

            SolutionRecord srFrom = util.getSolutionRecord(componentStorageEntity, inventoryComponentRecordDataID);
            WarehouseComponent warehouseComponentFrom = new WarehouseComponent(srFrom);
            warehouseComponentFrom.removeComponentsFromInventory(componentAmount);
            
            int inventoryComponentDataID = srFrom.getValueInteger(componentStorageComponent);
            int toWarehouseInventoryComponentDataID = util.findWarehouseComponentDataID(inventoryComponentDataID, toWarehouseDataID);

            //Record dosent exist, Create it
            if(toWarehouseInventoryComponentDataID == 0){
                SolutionRecord warehouseToSR = util.getSolutionRecord(warehouseEntity, toWarehouseDataID);
                SolutionRecord componentSR = util.getSolutionRecord(componentEntity, inventoryComponentDataID);
                SolutionRecordNew srnTo = util.createWarehouseInvComponentRecord(warehouseToSR, componentSR, componentAmount);
                warehouseComponentFrom.persistChanges();
                srnTo.persistChanges();
            }
            else{
                //Update record
                SolutionRecord srTo = util.getSolutionRecord(componentStorageEntity, toWarehouseInventoryComponentDataID);
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
            SolutionRecord deviceSR = util.getSolutionRecord(deviceEntity, SelectedDeviceDataID);
            SolutionRecord warehouseSR = util.getSolutionRecord(warehouseEntity, toWarehouseDataID);
            Device device = new Device(deviceSR);
            if(device.isSetAtStoppoint()){
                setItemStatus(98);
                return;
            }
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
            SolutionRecord batterySR = util.getSolutionRecord(deviceEntity, SelectedDeviceDataID);
            SolutionRecord stoppointSR = util.getSolutionRecord(stoppointEntity, stoppointDataID);

            Device battery = new Device(batterySR);
            if(!battery.isBattery()){
                setItemStatus(98);
                return;
            }
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
            SolutionRecord batterySR = util.getSolutionRecord(deviceEntity, batteryOnStoppointDataID);
            SolutionRecord storageSR = util.getSolutionRecord(warehouseEntity, toWarehouseDataID);
            
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
