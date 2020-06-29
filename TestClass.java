/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moveitems;

import dk.p2e.blanket.codeunit.CodeunitFormevents;
import dk.p2e.blanket.codeunit.CodeunitPagecontent;
import dk.p2e.blanket.form.Command;
import dk.p2e.blanket.form.Security;
import dk.p2e.util.Parser;
import dk.p2e.util.Systemout;
import dk.tempusserva.api.Session;
import dk.tempusserva.api.SessionFactory;
import dk.tempusserva.api.SolutionRecord;
import dk.tempusserva.api.SolutionRecordNew;
import dk.tempusserva.external.service.FieldValue;
import java.util.Hashtable;

/**
 *
 * @author XSMA
 */
public class TestClass extends CodeunitFormevents{


    
    @Override
    public void beforeUpdateItem(){
        String typeField = c.fields.getElementByFieldName("TYPE").FieldValue;
        
        switch(typeField){
            case "122":
                storageToStorage();
                break;
            case "123":
                changeSerialComponentLocation();
                break;
            default:
                break;
        } 
        
    }
    
    private void storageToStorage(){
        String fromStorage = c.fields.getElementByFieldName("STORAGEFROM").FieldValue;
        String toStorage = c.fields.getElementByFieldName("STORAGETO").FieldValue;
        String componentStorage = c.fields.getElementByFieldName("COMPONENT").FieldValue;
        int amount = Parser.getInteger(c.fields.getElementByFieldName("AMOUNT").FieldValue);
        String storageRecordAmount = "AMOUNT";
        Session session = SessionFactory.getSession(this);
            try{
                
                int componentStorageSolutionID = session.getSolutionID("warehouseallocation");
                SolutionRecord fromRecord = session.getSolutionRecord(componentStorageSolutionID, Parser.getInteger(componentStorage));
                if(fromRecord.getValueInteger(storageRecordAmount) >= amount){
                    String componentTypeID = fromRecord.getValue("COMPONENT");
                    int toRecordId = MoveItems.GetWarehouseHoldingsRecordDataId(session, toStorage, componentTypeID);
                    //Record of item at new warehouse does not exist
                    if(toRecordId == -1){
                        SolutionRecordNew toRecord = MoveItems.CreateWarehouseHoldingsRecord(session, toStorage, componentTypeID, amount);
                        toRecord.persistChanges();
                    }
                    else{
                        SolutionRecord toRecord = session.getSolutionRecord(componentStorageSolutionID, toRecordId);
                        int newAmount = toRecord.getValueInteger("AMOUNT") + amount; 
                        toRecord.setValueInteger("AMOUNT", newAmount);
                        toRecord.persistChanges();
                    }
                    fromRecord.setValueInteger("AMOUNT", fromRecord.getValueInteger(storageRecordAmount) - amount);
                    fromRecord.persistChanges();
                    
                    this.setItemStatus(37);
                }
                else{
                    this.setItemStatus(36);
                }
                
                StringBuilder redirect = new StringBuilder();
                redirect.append("main?SagID=");
                redirect.append(String.valueOf(c.SagID));
                redirect.append("&DataID=");
                redirect.append(String.valueOf(c.DataID));
                redirect.append("&command=show");
                this.setRedirect(redirect.toString());
                
            }
            catch(Exception e){
                Systemout.println(e.toString());
            }
        
        session.close();
    }
    
    
    private void changeSerialComponentLocation(){
        
        
        //Get record
        int serialItemId = Parser.getInteger(c.fields.getElementByFieldName("SERIALITEM").FieldValue);
        int location = Parser.getInteger(c.fields.getElementByFieldName("WAREHOUSEORSTOPPOINT").FieldValue);
        Session session = SessionFactory.getSession(this);
        try{
            int serialPageId = session.getSolutionID("komponentmedserienr");
            SolutionRecord record = session.getSolutionRecord(serialPageId, serialItemId);
            
            //Remove current references
            record.setValue("BIL", "");
            record.setValue("STOPPESTED", "");
            
            //Warehouse
            //
            if(location == 124){
                int warehousePageId = session.getSolutionID("warehouse");
                int warehouseDataId = Parser.getInteger(c.fields.getElementByFieldName("WAREHOUSE").FieldValue);
                SolutionRecord newRecord = session.getSolutionRecord(warehousePageId, warehouseDataId);
                record.setReference("BIL", newRecord);
            }
            
            if(location == 125){
                int stoppointPageId = session.getSolutionID("stoppoint");
                int stoppointDataId = Parser.getInteger(c.fields.getElementByFieldName("STOPPOINT").FieldValue);
                SolutionRecord newRecord = session.getSolutionRecord(stoppointPageId, stoppointDataId);
                record.setReference("STOPPESTED", newRecord);
            }
            
            record.persistChanges();
            
            
            //record.persistChanges();
        }
        catch(Exception e){
            
        }

        Systemout.println("Test");
        
        session.close();
        
        //Remove parent
        
        
        // Add new parent
        
    }
    
}
