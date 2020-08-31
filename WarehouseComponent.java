/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package activities;

import dk.tempusserva.api.SolutionRecord;
import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;

/**
 * This class is designed as a wrapper to get/set field values of a Warehouse Component Entity Record
 * @author XSMA
 */
public class WarehouseComponent {

    private SolutionRecord sr;
    
    public WarehouseComponent(SolutionRecord warehouseCompSR){
        if(warehouseCompSR.getInstanceID() == 0){
            throw new IllegalArgumentException("Invalid DataID");
        }
        sr = warehouseCompSR;
    }
    
    
    /**
     * Gets the amount of units in inventory
     * @return Int value with amount in inventory
     * @throws Exception On system error
     */
    public int getInventoryAmount() throws Exception{
        return sr.getValueInteger(TSValues.COMPONENTSTORAGE_AMOUNT);
    }
    
    /**
     * Adds a given amount of components to a given component storage  record, 
     * Does not persist any data
     * @param componentAmount int value with the amount of components being added
     * @throws IllegalArgumentException If solution record is not valid
     * @throws Exception on system error
     */
    public void addInventoryComponent(int componentAmount) throws Exception{
        int newAmount = sr.getValueInteger(TSValues.COMPONENTSTORAGE_AMOUNT) + componentAmount;
        sr.setValueInteger(TSValues.COMPONENTSTORAGE_AMOUNT, newAmount);
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
        int newAmount = sr.getValueInteger(TSValues.COMPONENTSTORAGE_AMOUNT) - componentAmount;
        if(newAmount < 0){
            throw new ValueException("Extracted more items than was in storage");
        }
        sr.setValueInteger(TSValues.COMPONENTSTORAGE_AMOUNT, newAmount);
    }
    
    /**
     * Gets the DataID of the component type the record holds
     * @return Int value with the DataID of the component Type
     * @throws Exception On System error
     */
    public int getComponentDataID() throws Exception{
        return sr.getValueInteger(TSValues.COMPONENTSTORAGE_COMPONENT);
    }
    
    /**
     * Gets the DataID of the warehouse 
     * @return int value with the DataID of the warehouse record
     * @throws Exception  
     */
    public int getWarehouseDataID() throws Exception{
        return sr.getValueInteger(TSValues.COMPONENTSTORAGE_WAREHOUSE);
    }
    
    
    /**
     * Persist changes of the warehouse component record
     * @throws Exception On system error
     */
    public void persistChanges() throws Exception{
        sr.persistChanges();
    }
    
    
}
