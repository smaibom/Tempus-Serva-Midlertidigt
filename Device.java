/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package activities;

import dk.tempusserva.api.SolutionRecord;

/**
 * A wrapper class for the device entity on TempusServa, contains functions for setting and getting information of a given record
 * @author XSMA
 */
public class Device {
    

    private final String deviceStopPoint = "INSTALLEDATSTOPPOINT";
    private final String deviceStorageLoc = "STORAGELOCATION";
    private final String deviceCat = "DEVICECATEGORY";
    private final String batteryDef = "Batteri";
    private final String countdownModuleDef = "Countdown";
    private SolutionRecord device;
    
    
    public Device(SolutionRecord dev){
        if(dev.getInstanceID() == 0){
            throw new IllegalArgumentException("Invalid DataID");
        }
        device = dev;
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
            device.setValue(deviceStorageLoc, "");
            device.setReference(deviceStopPoint, stoppointSR);
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
            device.setValue(deviceStopPoint, "");
            device.setReference(deviceStorageLoc, warehouseSR);
        }
        else{
            throw new IllegalArgumentException("Invalid DataIDs");
        }
        
    }
    
    /**
     * Checks if device is located at a stoppoint
     * @return True if it is set at a stoppoint, False otherwise
     * @throws Exception On System error
     */
    public Boolean isSetAtStoppoint() throws Exception{
        return (device.getValueInteger(deviceStopPoint) != 0);
    }    
    
    /**
     * Checks if device is a battery type
     * @return True if it is a multiQ battery gen one or two, false otherwise
     * @throws Exception On System error
     */
    public Boolean isBattery() throws Exception{
        return batteryDef.equals(device.getValue(deviceCat));
    }
    
    /**
     * Checks if device is a countdown module type
     * @return True if it is a multiQ battery gen one or two, false otherwise
     * @throws Exception On System error
     */
    public Boolean isCountdownModule() throws Exception{
        return countdownModuleDef.equals(device.getValue(deviceCat));
    }
    
    /**
     * Persist changes of the device record
     * @throws Exception On system error
     */
    public void persistChanges() throws Exception{
        device.persistChanges();
    }
    
}
