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
public class StoppointComponent {
 
    
    private final String stoppointInvEntity = "stoppointinventory";
    private final String stoppointInvStoppoint = "STOPPOINT";
    private final String stoppointInvComponent = "COMPONENT";
    private final String stoppointInvAmount = "AMOUNT";
    

    
    /**
     * Adds a given amount of components to a specific stoppoint inventory record, 
     * Does not persist any data
     * @param stoppointComponentInvSR SolutionRecord of the specific stoppoint component inventory being updated
     * @param componentAmount int value with the amount of components being added
     * @throws IllegalArgumentException on invalid DataID
     * @throws Exception on system error
     */
    public void addStoppointInvComponent(SolutionRecord stoppointComponentInvSR, int componentAmount) throws Exception{
        
        if(stoppointComponentInvSR.getInstanceID() == 0){
            throw new IllegalArgumentException("Invalid DataIDs");
        }
        
        int newAmount = stoppointComponentInvSR.getValueInteger(stoppointInvAmount) + componentAmount;
        stoppointComponentInvSR.setValueInteger(stoppointInvAmount, newAmount);

    }
    
    
    /**
     * Removes a given amount of components to a specific stoppoint inventory record
     * Does not persist any data
     * @param stoppointComponentInvSR SolutionRecord of the specific stoppoint component inventory being updated    
     * @param componentAmount int value with the amount of components being removed
     * @throws IllegalArgumentException on invalid DataID
     * @throws ValueException If the amount of components is higher than what is in the inventory
     * @throws Exception on system error
     */
    public void removeStoppointInvComponent(SolutionRecord stoppointComponentInvSR, int componentAmount) throws Exception{
        
        if(stoppointComponentInvSR.getInstanceID() == 0){
            throw new IllegalArgumentException("Invalid DataIDs");
        }
        
        int newAmount = stoppointComponentInvSR.getValueInteger(stoppointInvAmount) - componentAmount;
        if(newAmount < 0){
            throw new ValueException("Extracted more items than was in storage");
        }
        stoppointComponentInvSR.setValueInteger(stoppointInvAmount, newAmount);

    }

    
    
    
    
    
    
    
    
    
    
}
