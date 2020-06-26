/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moveitems;

import dk.p2e.blanket.form.handler.QueryPart;
import dk.p2e.util.Parser;
import dk.p2e.util.Systemout;
import dk.tempusserva.api.Session;
import dk.tempusserva.api.SolutionQuery;
import dk.tempusserva.api.SolutionQueryResultSet;
import dk.tempusserva.api.SolutionRecord;
import dk.tempusserva.api.SolutionRecordNew;

/**
 *
 * @author XSMA
 * 
 */
public class MoveItems {
    
    
    /**
     * Contains generic functions for moving things around on the TempusServa platform
     * Functions assumes that you give it an open session and close it down afterwards
     */
    
    
    /**
     * Finds a record data id for a component type in an specific warehouse 
     * 
     * Assumptions are that records in the Warehouse Holdings Entity is only created and modified
     * by the system it self and not by users. A specific warehous has upto 1 record for a given component type
     * 
     * 
     * @param session is the opem session to the TempusServa platform
     * @param storageID is a string id of the specific storage
     * @param componentTypeID is a string id of the specific component type
     * @return The int record ID if it exist, otherwise returns -1 to specify no existing record
     * @throws Exception if the system failed to perform the search
     */
    public static int GetWarehouseHoldingsRecordDataId(Session session, String storageID, String componentTypeID) throws Exception{

        
        //We leave these for now, should put them somewhere central to be able to change easily TODO
        String storageNameField = "WAREHOUSE";
        String componentTypeField = "COMPONENT";
        String warehouseHoldingsEntityName = "warehouseallocation";
        
        try{
            SolutionQuery query = session.getSolutionQuery(warehouseHoldingsEntityName);
            query.addWhereCriterion(storageNameField, QueryPart.EQUALS, storageID);
            query.addWhereCriterion(componentTypeField, QueryPart.EQUALS, componentTypeID);
            SolutionQueryResultSet res = query.executeQuery();
            //Assumption is we only got upto 1 record for a given component in a given warehouse
            if(res.size() > 0){
                //Check if its the correct one
                return res.getRecord(0).getInstanceID();
            }
        }
        catch(Exception e){
            //How to log??
            Systemout.println("GetWarehouseHoldingsRecordDataID failed");
            throw new Exception("System failed for whatever reason");
        }        
        
        return -1;
    }
    
    
    /**
     * Creates a new record object and fills out the required fields, does not write the data to database
     * 
     * @param session is the open session to the TempusServa platform
     * @param storageID is a string id of the specific storage
     * @param componentTypeID is a string id of the specific component type
     * @param amount the integer value to be set in the amount field
     * @return a SolutionRecordNew object with pre filled values, no database commits are made
     * @throws Exception 
     */
    public static SolutionRecordNew CreateWarehouseHoldingsRecord(Session session,  String storageID, String componentTypeID, int amount) throws Exception{
        String storageNameField = "WAREHOUSE";
        String componentTypeField = "COMPONENT";
        String componentAmountField = "AMOUNT";
        
        String warehouseHoldingsEntityName = "warehouseallocation";
        String storageEntityName = "warehouse";
        String componentEntityName = "components";
        
        SolutionRecordNew recordNew;
       
        
        try{
            
            int storagePageID = session.getSolutionID(storageEntityName);
            int componentPageID = session.getSolutionID(componentEntityName);
                    
            recordNew = session.getSolutionRecordNew(warehouseHoldingsEntityName);
            
            SolutionRecord storageRecord = session.getSolutionRecord(storagePageID, Parser.getInteger(storageID));
            SolutionRecord componentRecord = session.getSolutionRecord(componentPageID, Parser.getInteger(componentTypeID));

            recordNew.setReference(storageNameField, storageRecord);
            recordNew.setReference(componentTypeField, componentRecord);
            recordNew.setValueInteger(componentAmountField, amount);
        }
        catch(Exception e){
            throw new Exception("TODO: Set proper exception handling in CreateWarehouseHoldingsRecord");
        }
        
        
        return recordNew;
    }

    
    public Boolean TransferStoreToStore(){
        return true;
    }
    
}
