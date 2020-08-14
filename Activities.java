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
        
        
        

        Session ses = SessionFactory.getSession(this);

        
        switch(typeField){
            //Setup battery
            case "168":
                if(stoppointDataID == 0 || SelectedDeviceDataID ==0){
                        setItemStatus(98);
                        break;
                }
                setupBattery(SelectedDeviceDataID,stoppointDataID,ses);
                break;
                
            //Remove Battery from stoppoint
            case "169":
                if(stoppointDataID == 0 || batteryOnStoppointDataID == 0 || toWarehouseDataID == 0){
                        setItemStatus(98);
                        break;
                }
                removeBattery(batteryOnStoppointDataID,toWarehouseDataID,ses);
                break;
                
            //Change device
            case "170":
                if(SelectedDeviceDataID == 0 || deviceAtStoppointDataID == 0 || stoppointDataID == 0 || toWarehouseDataID == 0 || caseDataID == 0){
                    setItemStatus(98);
                    break;
                }
                changeDevice(SelectedDeviceDataID,deviceAtStoppointDataID,stoppointDataID,toWarehouseDataID,caseDataID,ses);
                break;
                
                
            //Setup device at stoppoint
            case "171":
                if(stoppointDataID == 0 || SelectedDeviceDataID == 0){
                    setItemStatus(98);
                    break;
                }
                setupDeviceOnStoppoint(stoppointDataID,SelectedDeviceDataID,ses);
                break;
                
                
            //Remove device from stoppoint
            case "172":
                if(deviceAtStoppointDataID == 0 || toWarehouseDataID == 0){
                    setItemStatus(98);
                    break;
                }
                removeDeviceFromStoppoint(deviceAtStoppointDataID,toWarehouseDataID,ses);
                break;
                
                
            //Restart device
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
                if(inventoryComponentRecordDataID == 0 || componentDataID == 0 || stoppointDataID == 0 || componentAmount < 1){
                    setItemStatus(98);
                    break;
                }
                componentSetup(inventoryComponentRecordDataID,componentDataID,stoppointDataID,componentAmount,ses);
                break;
                
                
            //Component takedown
            case "177":
                if(componentDataID == 0 || stoppointDataID == 0 || componentAmount < 1){
                    setItemStatus(98);
                    break;
                }
                componentTakedown(componentDataID,stoppointDataID,componentAmount,ses);
                break;
            
            //Move component
            case "178":
                if(toWarehouseDataID == 0 || inventoryComponentRecordDataID == 0 || componentAmount < 1){
                    setItemStatus(98);
                    break;
                }
                moveComponent(inventoryComponentRecordDataID,toWarehouseDataID,componentAmount,ses);
                break;
            
            //Move device
            case "179":
                if(SelectedDeviceDataID == 0 || toWarehouseDataID == 0){
                    setItemStatus(98);
                    break;
                }
                moveDevice(SelectedDeviceDataID,toWarehouseDataID,ses);
                break;
                
            default:
                break;
        } 
        
        ses.close();
        
        
    }
    
    
    
    private void changeDevice(int SelectedDeviceDataID,int deviceAtStoppointDataID,int stoppointDataID, int toWarehouseDataID, int caseDataID, Session ses){
        try{
            SolutionRecord devInstallSR = Util.getSolutionRecord(deviceEntity, SelectedDeviceDataID, ses);
            SolutionRecord devUninstallSR = Util.getSolutionRecord(deviceEntity, deviceAtStoppointDataID, ses);
            SolutionRecord spSR = Util.getSolutionRecord(stoppointEntity, stoppointDataID, ses);
            SolutionRecord warehouseSR = Util.getSolutionRecord(warehouseEntity, toWarehouseDataID, ses);
                    
            DeviceFunctions dev = new DeviceFunctions();
            dev.setStoppoint(devInstallSR, spSR);
            dev.setStorage(devUninstallSR, warehouseSR);
                    
            SolutionRecordNew setupActivityRecord = createSetupActivity(ses, caseDataID, spSR, devInstallSR);
            
            c.fields.getElementByFieldName(activityDevice).setFieldValue(deviceAtStoppointDataID);
            c.fields.getElementByFieldName(activityActivity).setFieldValue(172);
            devInstallSR.persistChanges();
            devUninstallSR.persistChanges();
            setupActivityRecord.persistChanges(false);
        }
        catch(IllegalArgumentException e){
            setItemStatus(98);
        }
        catch(Exception e){
            //Figure out a better exception
        }
    }
    
    private void setupDeviceOnStoppoint(int stoppointDataID, int SelectedDeviceDataID, Session ses){
        try{
            SolutionRecord spSR = Util.getSolutionRecord(stoppointEntity, stoppointDataID, ses);
            SolutionRecord deviceSR = Util.getSolutionRecord(deviceEntity, SelectedDeviceDataID, ses);
            DeviceFunctions dev = new DeviceFunctions();
            dev.setStoppoint(deviceSR, spSR);
            c.fields.getElementByFieldName(activityDevice).setFieldValue(SelectedDeviceDataID);
            deviceSR.persistChanges();
        }
        catch(IllegalArgumentException e){
            setItemStatus(98);
        }
        catch(Exception e){
        }

    }
    
    private void removeDeviceFromStoppoint(int deviceAtStoppointDataID, int toWarehouseDataID, Session ses){
        try{
            SolutionRecord deviceSR = Util.getSolutionRecord(deviceEntity, deviceAtStoppointDataID, ses);
            SolutionRecord warehouseSR = Util.getSolutionRecord(warehouseEntity, toWarehouseDataID, ses);
            
            DeviceFunctions dev = new DeviceFunctions();
            dev.setStorage(deviceSR, warehouseSR);
            
            c.fields.getElementByFieldName(activityDevice).setFieldValue(deviceAtStoppointDataID);
            deviceSR.persistChanges();

        }
        catch(IllegalArgumentException e){
            setItemStatus(98);
        }
        catch(Exception e){
        }
    }
    
    private void restartDevice(int deviceAtStoppointDataID){
        c.fields.getElementByFieldName(activityDevice).setFieldValue(deviceAtStoppointDataID);
    }
            
    private void cableCheck(int deviceAtStoppointDataID){
        c.fields.getElementByFieldName(activityDevice).setFieldValue(deviceAtStoppointDataID);
    }
    
    private void componentSetup(int inventoryComponentRecordDataID, int componentDataID, int stoppointDataID,int componentAmount, Session ses){
        try{
            WarehouseComponentFunctions whf = new WarehouseComponentFunctions();
            StoppointComponentFunctions spcf = new StoppointComponentFunctions();

            SolutionRecord warehouseStorageInvSR = Util.getSolutionRecord(componentStorageEntity, inventoryComponentRecordDataID, ses);
            whf.removeComponentsFromInventory(warehouseStorageInvSR, componentAmount);

            componentDataID = warehouseStorageInvSR.getValueInteger(componentStorageComponent);
            int spcDataID = spcf.findStoppointComponentDataID(componentDataID,stoppointDataID,ses);

            if(spcDataID == 0){
                SolutionRecord stoppointSR = Util.getSolutionRecord(stoppointEntity, stoppointDataID, ses);
                SolutionRecord componentSR = Util.getSolutionRecord(componentEntity, componentDataID, ses);
                SolutionRecordNew srn = spcf.createStoppointInvComponentRecord(stoppointSR, componentSR, componentAmount, ses);
                srn.persistChanges();
                warehouseStorageInvSR.persistChanges();
            }
            else{
                SolutionRecord sr = Util.getSolutionRecord(stoppointInvEntity, spcDataID, ses);
                spcf.addStoppointInvComponent(sr, componentAmount);
                sr.persistChanges();
                warehouseStorageInvSR.persistChanges();
            }

        }
        catch(ValueException e){
            setItemStatus(98);
        }
        catch(IllegalArgumentException e){
            setItemStatus(98);
        }
        catch(Exception e){
        }
    }
    
    private void componentTakedown(int componentDataID, int stoppointDataID,int componentAmount, Session ses){
        StoppointComponentFunctions spcf = new StoppointComponentFunctions();
        int spcDataID = spcf.findStoppointComponentDataID(componentDataID,stoppointDataID,ses);
        if(spcDataID == 0){
            //Do nothing, we cant remove stuff that dont exist
            return;
        }
        try{
            SolutionRecord spcSR = Util.getSolutionRecord(stoppointInvEntity, spcDataID, ses);
            spcf.removeStoppointInvComponent(spcSR,componentAmount);
            spcSR.persistChanges();
        }
        catch(IllegalArgumentException e){
            setItemStatus(98);
        }
        catch(ValueException e){
            setItemStatus(98);
        }
        catch(Exception e){
        }
    }
    
    private void moveComponent(int inventoryComponentRecordDataID, int toWarehouseDataID, int componentAmount, Session ses){
        
        try{
            WarehouseComponentFunctions whf = new WarehouseComponentFunctions();

            SolutionRecord srFrom = Util.getSolutionRecord(componentStorageEntity, inventoryComponentRecordDataID, ses);
            whf.removeComponentsFromInventory(srFrom,componentAmount);
            
            int inventoryComponentDataID = srFrom.getValueInteger(componentStorageComponent);
            int toWarehouseInventoryComponentDataID = whf.findWarehouseComponentDataID(inventoryComponentDataID, toWarehouseDataID, ses);

            //Record dosent exist, Create it
            if(toWarehouseInventoryComponentDataID == 0){
                SolutionRecord warehouseToSR = Util.getSolutionRecord(warehouseEntity, toWarehouseDataID, ses);
                SolutionRecord componentSR = Util.getSolutionRecord(componentEntity, inventoryComponentDataID, ses);
                SolutionRecordNew srnTo = whf.createWarehouseInvComponentRecord(warehouseToSR, componentSR, componentAmount, ses);
                srFrom.persistChanges();
                srnTo.persistChanges();
            }
            else{
                //Update record
                SolutionRecord srTo = Util.getSolutionRecord(componentStorageEntity, toWarehouseInventoryComponentDataID, ses);
                whf.addInventoryComponent(srTo, componentAmount);
                srFrom.persistChanges();
                srTo.persistChanges();
            }    
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
        }
    }

    private void moveDevice(int SelectedDeviceDataID, int toWarehouseDataID, Session ses){
        try{
            SolutionRecord deviceSR = Util.getSolutionRecord(deviceEntity, SelectedDeviceDataID, ses);
            SolutionRecord warehouseSR = Util.getSolutionRecord(warehouseEntity, toWarehouseDataID, ses);
            DeviceFunctions dev = new DeviceFunctions();
            if(dev.isSetAtStoppoint(deviceSR)){
                setItemStatus(98);
                return;
            }
            dev.setStorage(deviceSR, warehouseSR);
            deviceSR.persistChanges();
        }
        catch(IllegalArgumentException e){
            setItemStatus(98);
        }
        catch(Exception e){
        }
    }
    
    private void setupBattery(int SelectedDeviceDataID, int stoppointDataID, Session ses){
        try{
            SolutionRecord batterySR = Util.getSolutionRecord(deviceEntity, SelectedDeviceDataID, ses);
            SolutionRecord stoppointSR = Util.getSolutionRecord(stoppointEntity, stoppointDataID, ses);

            DeviceFunctions df = new DeviceFunctions();
            if(!df.isBattery(batterySR)){
                setItemStatus(98);
                return;
            }
            df.setStoppoint(batterySR, stoppointSR);
            batterySR.persistChanges();

        }
        catch(Exception e){
            setItemStatus(98);
        }
    }
    
    private void removeBattery(int batteryOnStoppointDataID, int toWarehouseDataID, Session ses){
        try{
            SolutionRecord batterySR = Util.getSolutionRecord(deviceEntity, batteryOnStoppointDataID, ses);
            SolutionRecord storageSR = Util.getSolutionRecord(warehouseEntity, toWarehouseDataID, ses);
            
            DeviceFunctions df = new DeviceFunctions();
            if(!df.isBattery(batterySR)){
                setItemStatus(98);
                return;
            }
            df.setStorage(batterySR, storageSR);
            batterySR.persistChanges();

        }
        catch(Exception e){
            setItemStatus(98);
        }
    }

    

    
    

    

  
    
    
    
    
    

    
        
    //This should be a general function in the future, for now its there
    private SolutionRecordNew createSetupActivity(Session ses,int caseDataID, SolutionRecord stoppointSR,SolutionRecord deviceSR) throws Exception{

        SolutionRecord caseSR = Util.getSolutionRecord(caseEntity, caseDataID,ses);

        if(stoppointSR.getInstanceID() != 0 && deviceSR.getInstanceID() != 0 && caseSR.getInstanceID() != 0){
            SolutionRecordNew srn = ses.getSolutionRecordNew(activityEntity);
            srn.setReference(activityCase, caseSR);
            srn.setReference(activityDevice, deviceSR);
            srn.setReference(activitySelectedDevice, deviceSR);

            srn.setReference(activityStoppoint, stoppointSR);
            srn.setValueInteger(activityActivity, 171);

            return srn;
        }
        else{
            throw new IllegalArgumentException("Invalid DataIDs");
        }
        
    }
    
    
    

    

    
    
    
    
}
