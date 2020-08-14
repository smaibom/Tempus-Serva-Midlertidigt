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
public class Util {
    
    /**
     * Returns a SolutionRecord from a given entity name and a DataID. This is a wrapper function
     * as TS does not offer getSolutionRecord without searching for the solutionID first
     * @param entity String name of the entity 
     * @param DataID Int DataID of the record
     * @param ses Session object, must be open
     * @return SolutionRecord you must validate that it is valid(DataID != 0) as TS does not throw a catchable error on wrong dataID
     */
    public static SolutionRecord getSolutionRecord(String entity, int DataID, Session ses){
        int solutionID = ses.getSolutionID(entity);
        SolutionRecord sr = ses.getSolutionRecord(solutionID, DataID);
        return sr;
    }
    
}
