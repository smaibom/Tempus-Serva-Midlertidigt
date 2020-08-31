/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package activities;


import dk.tempusserva.api.SolutionRecord;
import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;

/**
 * This class is designed as a wrapper to get/set field values of a Stoppoint Component Entity Record
 * @author XSMA
 */
public class StoppointComponent {
 

    private SolutionRecord sr;

    /**
     * Creates a stoppoint component class
     * @param StoppointComponentSR SolutionRecord of the stoppoint component
     */
    public StoppointComponent(SolutionRecord StoppointComponentSR){
        if(StoppointComponentSR.getInstanceID() == 0){
            throw new IllegalArgumentException("Invalid DataIDs");
        }
        sr = StoppointComponentSR;
    }
    
    /**
     * Adds a given amount of components to a specific stoppoint inventory record, 
     * Does not persist any data
     * @param componentAmount int value with the amount of components being added
     * @throws IllegalArgumentException on invalid DataID
     * @throws Exception on system error
     */
    public void addStoppointInvComponent(int componentAmount) throws Exception{
        int newAmount = sr.getValueInteger(TSValues.STOPPOINTINV_AMOUNT) + componentAmount;
        sr.setValueInteger(TSValues.STOPPOINTINV_AMOUNT, newAmount);
    }
    
    
    /**
     * Removes a given amount of components to a specific stoppoint inventory record
     * Does not persist any data
     * @param componentAmount int value with the amount of components being removed
     * @throws IllegalArgumentException on invalid DataID
     * @throws ValueException If the amount of components is higher than what is in the inventory
     * @throws Exception on system error
     */
    public void removeStoppointInvComponent(int componentAmount) throws Exception{
        int newAmount = sr.getValueInteger(TSValues.STOPPOINTINV_AMOUNT) - componentAmount;
        if(newAmount < 0){
            throw new ValueException("Extracted more items than was in storage");
        }
        sr.setValueInteger(TSValues.STOPPOINTINV_AMOUNT, newAmount);

    }

    
    
    /**
     * Persist changes of the stoppoint component record
     * @throws Exception On system error
     */
    public void persistChanges() throws Exception{
        sr.persistChanges();
    }
    
    
    
    
    
    
    
    
}
