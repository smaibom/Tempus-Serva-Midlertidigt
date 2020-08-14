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
public class StoppointComponentFunctions {
 
    
    private final String stoppointInvEntity = "stoppointinventory";
    private final String stoppointInvStoppoint = "STOPPOINT";
    private final String stoppointInvComponent = "COMPONENT";
    private final String stoppointInvAmount = "AMOUNT";
    
    /**
     * Finds the DataID to the record of a given component type setup at a specific stoppoint
     * @param componentDataID int value with DataID to the record of the component
     * @param stoppointDataID int value with DataID to the record of the stoppoint
     * @param ses Open Session object
     * @return int value higher than 0 if a record is found, 0 otherwise
     */
    public int findStoppointComponentDataID(int componentDataID,int stoppointDataID, Session ses){
        SolutionQuery q = ses.getSolutionQuery(stoppointInvEntity);
        q.addWhereCriterion(stoppointInvStoppoint, QueryPart.EQUALS, String.valueOf(stoppointDataID));
        q.addWhereCriterion(stoppointInvComponent, QueryPart.EQUALS, String.valueOf(componentDataID));
        SolutionQueryResultSet rs = q.executeQuery();
        if(rs.size() == 1){
            return rs.getRecord(0).getInstanceID();
        }
        return 0;
    }
    
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
    
    /**
     * Creates a new record in the stoppoint inventory entity with the given component and amount and a stoppoint.
     * Does not persist data
     * @param stoppointSR SolutionRecord of a specific stoppoint
     * @param componentSR SolutionRecord of the component
     * @param componentAmount int value with the amount being added
     * @param ses Open Session Object
     * @return SolutionRecordNew Object with the fields set
     * @throws IllegalArgumentException On invalid dataIDs
     * @throws Exception on system error
     */
    public SolutionRecordNew createStoppointInvComponentRecord(SolutionRecord stoppointSR, SolutionRecord componentSR, int componentAmount, Session ses) throws Exception{
        
        
        if(stoppointSR.getInstanceID() == 0 || componentSR.getInstanceID() == 0){
            throw new IllegalArgumentException("Invalid DataIDs");
        }

        SolutionRecordNew srn = ses.getSolutionRecordNew(stoppointInvEntity);
        srn.setReference(stoppointInvStoppoint, stoppointSR);
        srn.setReference(stoppointInvComponent, componentSR);
        srn.setValueInteger(stoppointInvAmount, componentAmount);
        return srn;
    }
    
    
    
    
    
    
    
    
    
    
}
