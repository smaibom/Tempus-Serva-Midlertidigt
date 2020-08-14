/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package activities;

import dk.tempusserva.api.Session;
import dk.tempusserva.api.SolutionRecord;

/**
 *
 * @author XSMA
 */
public class DeviceFunctions {
    

    private final String deviceStopPoint = "INSTALLEDATSTOPPOINT";
    private final String deviceStorageLoc = "STORAGELOCATION";
    
        
    /**
     * Sets the storage location reference to nothing and the stoppoint reference to the given Record
     * Does not persist any changes
     * @param deviceSR SolutionRecord of the device
     * @param stoppointSR SolutionRecord of the stoppoint
     * @throws IllegalArgumentException If either of the DataIDs are not valid records
     * @throw Exception General systems error 
     */
    public void setStoppoint(SolutionRecord deviceSR,SolutionRecord stoppointSR) throws Exception{
        if(stoppointSR.getInstanceID() != 0 && deviceSR.getInstanceID() != 0){
            deviceSR.setValue(deviceStorageLoc, "");
            deviceSR.setReference(deviceStopPoint, stoppointSR);
        }
        else{
            throw new IllegalArgumentException("Invalid DataIDs");
        }
    }

    
    /**
     * Sets the storage field reference to the given warehouse record and the stoppoint reference to nothing
     * Does not persist any changes
     * @param deviceSR SolutionRecord of the device record
     * @param warehouseSR SolutionRecord of the warehouse record
     * @throws IllegalArgumentException If either of the DataIDs are not valid records
     * @throw Exception General systems error 
     */
    public void setStorage(SolutionRecord deviceSR,SolutionRecord warehouseSR) throws Exception{
        if(warehouseSR.getInstanceID() != 0 && deviceSR.getInstanceID() != 0){
            deviceSR.setValue(deviceStopPoint, "");
            deviceSR.setReference(deviceStorageLoc, warehouseSR);
        }
        else{
            throw new IllegalArgumentException("Invalid DataIDs");
        }
        
    }
    
    
    
    
    
    
    
}
