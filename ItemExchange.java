/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package changeitem;

import dk.p2e.blanket.codeunit.CodeunitFormevents;
import dk.p2e.blanket.form.handler.QueryPart;
import dk.p2e.util.Parser;
import dk.p2e.util.Systemout;
import dk.tempusserva.api.Session;
import dk.tempusserva.api.SessionFactory;
import dk.tempusserva.api.SolutionQuery;
import dk.tempusserva.api.SolutionQueryResultSet;
import dk.tempusserva.api.SolutionRecord;

/**
 *
 * @author XSMA
 */
public class ItemExchange extends CodeunitFormevents {
    /**
     * This is the codeunit class for exchange item, it handles moving items back and forth
     * a vehicle and a storehouse
    */
   

        @Override
        public void beforeUpdateItem() {

            
            //Tempus Serva field and entity names in system
            String carField = "BIL";
            String itemField = "VARE";
            String amountField = "MNGDE";
            String typeField = "TYPE";
            String plateField = "NUMMERPLADE";
            String carEntity = "bil";
            String itemEntity = "vare";
            String exchangeEntity = "vareudtrk";
            
            
            //The fields where the user typed the input, references are int values to dataIds
            String carDataId = c.fields.getElementByFieldName(carField).FieldValue;
            String itemDataId = c.fields.getElementByFieldName(itemField).FieldValue;
            int amount = Parser.getInteger(c.fields.getElementByFieldName(amountField).FieldValue);
            
            
            
            Session session = SessionFactory.getSession(this);
            
            //Entity validates if there is data in the fields
            if(amount > 0){

                try{
                    
                    //Check if the item has enough in storage
                    int itemPageId = session.getSolutionID(itemEntity);
                    SolutionRecord itemRecord = session.getSolutionRecord(itemPageId, Parser.getInteger(itemDataId));
                    
                    if(itemRecord.getValueInteger(amountField) >= amount){
                        
                        //Lookup the string value of the cars dataId
                        int carPageId = session.getSolutionID(carEntity);
                        SolutionRecord carRecord = session.getSolutionRecord(carPageId, Parser.getInteger(carDataId));
                        String carName = carRecord.getValue(plateField);
                        
                        //Lookup if we have an existing record for an exchange of an item to a specific car
                        SolutionQuery itemExchangeQuery = session.getSolutionQuery(exchangeEntity);
                        itemExchangeQuery.addWhereCriterion(carField, QueryPart.EQUALS, carName);
                        itemExchangeQuery.addWhereCriterion(itemField,QueryPart.EQUALS, itemRecord.getValue(typeField));
                        SolutionQueryResultSet itemExchangeRecord = itemExchangeQuery.executeQuery();
                        
                        //If a record exist we just change the existing record to have updated values
                        if(itemExchangeRecord.size() > 0){
                            SolutionRecord record = itemExchangeRecord.getRecord(0);
                            int curInv = record.getValueInteger(amountField);
                            record.setValueInteger(amountField, curInv+amount);
                            record.persistChanges();
                            
                            
                            //TODO: Prevent new record from being created
                        }
                        
                        
                        
                        //Update storage with removed items
                        itemRecord.setValueInteger(amountField, itemRecord.getValueInteger(amountField)-amount);
                        itemRecord.persistChanges();
                        
                    }
                    else{
                        Systemout.println("Not enough items");
                    }

                }
                catch(Exception e){
                    Systemout.println("BLA");
                }
            }
            else{
                Systemout.println("ERROR");
            }
                       

            session.close();
       
        }     

           

    
}
