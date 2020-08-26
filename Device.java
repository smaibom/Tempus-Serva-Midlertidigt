/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package activities;

import dk.tempusserva.api.SolutionRecord;

/**
 * A wrapper class for the sr entity on TempusServa, contains functions for setting and getting information of a given record
 * @author XSMA
 */
public class Device {
    

    private final String batteryDef = "Batteri";
    private final String countdownModuleDef = "Modul";
    
    
    
    private SolutionRecord sr;
    
    /**
     * Creates a wrapper for a given SolutionRecord to get easy access to get/set and validation functions
     * @param devSR SolutionRecord Object of the specific record that is being manipulated on
     */
    public Device(SolutionRecord devSR){
        if(devSR.getInstanceID() == 0){
            throw new IllegalArgumentException("Invalid DataID");
        }
        sr = devSR;
    }
        
    /**
     * Sets the storage location reference to nothing and the stoppoint reference to the given Record
     * Does not persist any changes
     * @param stoppointSR SolutionRecord of the stoppoint
     * @throws IllegalArgumentException If either of the DataIDs are not valid records
     * @throw Exception General systems error 
     */
    public void setStoppoint(SolutionRecord stoppointSR) throws Exception{
        if(stoppointSR.getInstanceID() != 0){
            sr.setValue(TSValues.DEVICE_STORAGELOC, "");
            sr.setReference(TSValues.DEVICE_STOPPPOINT, stoppointSR);
        }
        else{
            throw new IllegalArgumentException("Invalid DataIDs");
        }
    }

    
    /**
     * Sets the storage field reference to the given warehouse record and the stoppoint reference to nothing
     * Does not persist any changes
     * @param warehouseSR SolutionRecord of the warehouse record
     * @throws IllegalArgumentException If either of the DataIDs are not valid records
     * @throw Exception General systems error 
     */
    public void setStorage(SolutionRecord warehouseSR) throws Exception{
        if(warehouseSR.getInstanceID() != 0 ){
            sr.setValue(TSValues.DEVICE_STOPPPOINT, "");
            sr.setReference(TSValues.DEVICE_STORAGELOC, warehouseSR);
        }
        else{
            throw new IllegalArgumentException("Invalid DataIDs");
        }
        
    }
    
    /**
     * Checks if sr is located at a stoppoint
     * @return True if it is set at a stoppoint, False otherwise
     * @throws Exception On System error
     */
    public Boolean isSetAtStoppoint() throws Exception{
        return (sr.getValueInteger(TSValues.DEVICE_STOPPPOINT) != 0);
    }    
    
    /**
     * Checks if sr is a battery type
     * @return True if it is a multiQ battery gen one or two, false otherwise
     * @throws Exception On System error
     */
    public Boolean isBattery() throws Exception{
        return batteryDef.equals(sr.getValue(TSValues.DEVICE_CAT));
    }
    
    /**
     * Checks if sr is a countdown module type
     * @return True if it is a multiQ battery gen one or two, false otherwise
     * @throws Exception On System error
     */
    public Boolean isCountdownModule() throws Exception{
        return countdownModuleDef.equals(sr.getValue(TSValues.DEVICE_CAT));
    }
    
    /**
     * Persist changes of the sr record
     * @throws Exception On system error
     */
    public void persistChanges() throws Exception{
        sr.persistChanges();
    }
    
}
