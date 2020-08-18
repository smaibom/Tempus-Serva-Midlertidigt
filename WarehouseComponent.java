/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package activities;

import dk.tempusserva.api.SolutionRecord;
import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;

/**
 *
 * @author XSMA
 */
public class WarehouseComponent {
    
    
    private final String componentStorageEntity = "warehouseallocation";
    private final String componentStorageAmount = "AMOUNT";
    private final String componentStorageWarehouse = "WAREHOUSE";
    private final String componentStorageComponent = "COMPONENT";
    
    private SolutionRecord sr;
    
    public WarehouseComponent(SolutionRecord warehouseCompSR){
        if(warehouseCompSR.getInstanceID() == 0){
            throw new IllegalArgumentException("Invalid DataID");
        }
        sr = warehouseCompSR;
    }
    
    
    
    /**
     * Adds a given amount of components to a given component storage  record, 
     * Does not persist any data
     * @param componentAmount int value with the amount of components being added
     * @throws IllegalArgumentException If solution record is not valid
     * @throws Exception on system error
     */
    public void addInventoryComponent(int componentAmount) throws Exception{
        int newAmount = sr.getValueInteger(componentStorageAmount) + componentAmount;
        sr.setValueInteger(componentStorageAmount, newAmount);
    }
    
    
    /**
     * Removes a given amount of components to a given warehouse inventory record, 
     * Does not persist any data
     * @param componentAmount int value with the amount of components being added
     * @throws IllegalArgumentException on invalid DataID
     * @throws ValueException If the amount of components is below 0 after substraction
     * @throws Exception on system error
     */
    public void removeComponentsFromInventory(int componentAmount) throws Exception{
        
       
        int newAmount = sr.getValueInteger(componentStorageAmount) - componentAmount;
        if(newAmount < 0){
            throw new ValueException("Extracted more items than was in storage");
        }
        sr.setValueInteger(componentStorageAmount, newAmount);

    }
    
    /**
     * Persist changes of the warehouse component record
     * @throws Exception On system error
     */
    public void persistChanges() throws Exception{
        sr.persistChanges();
    }
    
    
}
