/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package changeitem;

import dk.p2e.blanket.codeunit.CodeunitFormevents;
import dk.p2e.blanket.form.Command;
import dk.p2e.blanket.form.handler.QueryPart;
import dk.p2e.util.Parser;
import dk.p2e.util.Systemout;
import dk.tempusserva.api.Session;
import dk.tempusserva.api.SessionFactory;
import dk.tempusserva.api.SolutionQuery;
import dk.tempusserva.api.SolutionQueryResultSet;
import dk.tempusserva.api.SolutionRecord;
import dk.tempusserva.api.SolutionRecordNew;

/**
 *
 * @author XSMA
 */
public class MoveItem extends CodeunitFormevents {

    /**
     * This is the codeunit class for exchange item, it handles moving items
     * back and forth a vehicle and a storehouse
     */
    /*
    private boolean pageEdit = false;
    
    @Override
    public void beforeSelectList() throws Exception {
                Systemout.println("a");
        
                
        Systemout.println(String.valueOf(c.COMMAND));
    }
    @Override
    public void beforeRenderList() throws Exception {
                        Systemout.println("b");

        Systemout.println(String.valueOf(c.COMMAND));
    }
    @Override
    public void beforeSelectItem() throws Exception {
                        Systemout.println("c");

        Systemout.println(String.valueOf(c.COMMAND));
    }
    @Override
    public void beforeChangeItem() throws Exception {
                        Systemout.println("d");

        Systemout.println(String.valueOf(c.COMMAND));
    }

    @Override
    public void beforeRenderItem() throws Exception {
                        Systemout.println("e");

        Systemout.println(String.valueOf(c.COMMAND));
    }
    @Override
    public void afterUpdateItem() throws Exception {
                        Systemout.println("f");
        Systemout.println(String.valueOf(c.COMMAND));
    }
    */


    
    

    
    @Override
    public void beforeUpdateItem() {

        //Tempus Serva field and entity names in system
        
        //Transaction page fields
        String storageItemField = "STORAGE";
        String carField = "CAR";
        String amountField = "AMOUNT";
 
        String itemEntity = "vare";
        String itemAmountField = "MNGDE";
 
        String carEntity = "bil";
        String carPlateField = "NUMMERPLADE";
        
        String exchangeEntity = "vareudtrk";
        String itemField = "VARE";
        String typeField = "TYPE";
        String carExchangeField = "BIL";

        //The fields where the user typed the input, references are int values to dataIds
        String carDataId = c.fields.getElementByFieldName(carField).FieldValue;
        String itemDataId = c.fields.getElementByFieldName(storageItemField).FieldValue;
        int amount = Parser.getInteger(c.fields.getElementByFieldName(amountField).FieldValue);
        
        Session session = SessionFactory.getSession(this);
        
        if (amount > 0) {
            Systemout.println(String.valueOf(amount));
            try {

                //Check if the item has enough in storage
                int itemPageId = session.getSolutionID(itemEntity);
                SolutionRecord itemRecord = session.getSolutionRecord(itemPageId, Parser.getInteger(itemDataId));


                if (itemRecord.getValueInteger(itemAmountField) >= amount) {
                    //Lookup the string value of the cars dataId
                    int carPageId = session.getSolutionID(carEntity);
                    SolutionRecord carRecord = session.getSolutionRecord(carPageId, Parser.getInteger(carDataId));
                    String carName = carRecord.getValue(carPlateField);
                    
                    SolutionQuery itemExchangeQuery = session.getSolutionQuery(exchangeEntity);
                    itemExchangeQuery.addWhereCriterion(carExchangeField, QueryPart.EQUALS, carName);
                    itemExchangeQuery.addWhereCriterion(itemField, QueryPart.EQUALS, itemRecord.getValue(typeField));
                    SolutionQueryResultSet itemExchangeRecords = itemExchangeQuery.executeQuery();
                    
                    SolutionRecord record;
                    if(itemExchangeRecords.size() > 0){
                        record = itemExchangeRecords.getRecord(0);

                    }
                    else{
                        //Stuff dosent exist, create me
                        int exchangeEntityId = session.getSolutionID(exchangeEntity);
                        SolutionRecordNew recordNew = session.getSolutionRecordNew(exchangeEntityId);
                        recordNew.setValueInteger(itemAmountField, 0);
                        recordNew.setReference(carExchangeField, carRecord);
                        recordNew.setReference(itemField, itemRecord);
                        recordNew.persistChanges();
                        record = recordNew.getSolutionRecord();
                        
                    }
                    
                    record.setValueInteger(itemAmountField, amount + record.getValueInteger(itemAmountField));
                    itemRecord.setValueInteger(itemAmountField, itemRecord.getValueInteger(itemAmountField) - amount);
                    itemRecord.persistChanges();
                    record.persistChanges();


                }
                
                
                
            }
            catch(Exception e){
                Systemout.println("Error");
                Systemout.println(e.getMessage());
            }
            
            
            
        }
        
        
        //String carField = "BIL";
        //String amountField = "MNGDE";
        String plateField = "NUMMERPLADE";

        session.close();
        //Entity validates if there is data in the fields
        /*
        if (amount > 0) {



                if (itemRecord.getValueInteger(amountField) >= amount) {

                    //Lookup if we have an existing record for an exchange of an item to a specific car
                    SolutionQuery itemExchangeQuery = session.getSolutionQuery(exchangeEntity);
                    itemExchangeQuery.addWhereCriterion(carField, QueryPart.EQUALS, carName);
                    itemExchangeQuery.addWhereCriterion(itemField, QueryPart.EQUALS, itemRecord.getValue(typeField));
                    SolutionQueryResultSet itemExchangeRecord = itemExchangeQuery.executeQuery();

                    //If a record exist we just change the existing record to have updated values
                    if (itemExchangeRecord.size() > 0) {
                        SolutionRecord record = itemExchangeRecord.getRecord(0);
                        int curInv = record.getValueInteger(amountField);
                        record.setValueInteger(amountField, curInv + amount);
                        record.persistChanges();

                        //TODO: Prevent new record from being created
                    }
                    else{

                        
                    }

                    //Update storage with removed items
                    itemRecord.setValueInteger(amountField, itemRecord.getValueInteger(amountField) - amount);
                    itemRecord.persistChanges();

                } else {
                    Systemout.println("Not enough items");
                }

            } catch (Exception e) {
                Systemout.println("BLA");
            }
        } else {
            Systemout.println("ERROR");
        }
        */
        

    }

}
