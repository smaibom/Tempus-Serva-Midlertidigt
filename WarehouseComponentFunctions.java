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
 *
 * @author XSMA
 */
public class WarehouseComponentFunctions {
    
    
    private final String componentStorageEntity = "warehouseallocation";
    private final String componentStorageAmount = "AMOUNT";
    private final String componentStorageWarehouse = "WAREHOUSE";
    private final String componentStorageComponent = "COMPONENT";
    
    
    
    
    
    /**
     * Adds a given amount of components to a given component storage  record, 
     * Does not persist any data
     * @param componentInventorySR SolutionRecord warehouse component Record
     * @param componentAmount int value with the amount of components being added
     * @throws IllegalArgumentException If solution record is not valid
     * @throws Exception on system error
     */
    public void addInventoryComponent(SolutionRecord componentInventorySR, int componentAmount) throws Exception{
        
        if(componentInventorySR.getInstanceID() == 0){
            throw new IllegalArgumentException("Invalid DataIDs");
        }
        
        int newAmount = componentInventorySR.getValueInteger(componentStorageAmount) + componentAmount;
        componentInventorySR.setValueInteger(componentStorageAmount, newAmount);
    }
    
    
    
        
    /**
     * Finds the DataID to the record of a given component type stored at a specific warehouse
     * @param componentDataID int value with DataID to the record of the component
     * @param warehouseDataID int value with DataID to the record of the stoppoint
     * @param ses Open Session object
     * @return int value higher than 0 if a record is found, 0 otherwise
     */
    public int findWarehouseComponentDataID(int componentDataID,int warehouseDataID, Session ses){
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
     * Removes a given amount of components to a given warehouse inventory record, 
     * Does not persist any data
     * @param componentInventorySR SolutionRecord Record of the specific component in a specific inventory  
     * @param componentAmount int value with the amount of components being added
     * @throws IllegalArgumentException on invalid DataID
     * @throws ValueException If the amount of components is below 0 after substraction
     * @throws Exception on system error
     */
    public void removeComponentsFromInventory(SolutionRecord componentInventorySR, int componentAmount) throws Exception{
        
        if(componentInventorySR.getInstanceID() == 0){
            throw new IllegalArgumentException("Invalid DataIDs");
        }
        
        int newAmount = componentInventorySR.getValueInteger(componentStorageAmount) - componentAmount;
        if(newAmount < 0){
            throw new ValueException("Extracted more items than was in storage");
        }
        componentInventorySR.setValueInteger(componentStorageAmount, newAmount);

    }
    
   
    /**
     * Creates a new record in the stoppoint inventory entity with the given component and amount and a stoppoint.
     * Does not persist data
     * @param warehouseSR int value with the stoppoint DataID
     * @param componentSR int value with the component DataID
     * @param componentAmount int value with the amount being added
     * @param ses Open Session Object
     * @return SolutionRecordNew Object with the fields set
     * @throws IllegalArgumentException On invalid dataIDs
     * @throws Exception on system error
     */
    public SolutionRecordNew createWarehouseInvComponentRecord(SolutionRecord warehouseSR, SolutionRecord componentSR, int componentAmount, Session ses) throws Exception{
        
        if(warehouseSR.getInstanceID() == 0 || componentSR.getInstanceID() == 0){
            throw new IllegalArgumentException("Invalid DataIDs");
        }

        SolutionRecordNew srn = ses.getSolutionRecordNew(componentStorageEntity);
        srn.setReference(componentStorageWarehouse, warehouseSR);
        srn.setReference(componentStorageComponent, componentSR);
        srn.setValueInteger(componentStorageAmount, componentAmount);
        return srn;
    }
    
    
    
}
