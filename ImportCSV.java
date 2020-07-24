/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moveitems;


import au.com.bytecode.opencsv.CSVReader;
import java.io.FileNotFoundException;
import java.io.FileReader;import au.com.bytecode.opencsv.CSVReader;
import dk.p2e.blanket.codeunit.CodeunitPagecontent;
import dk.p2e.blanket.form.Command;
import dk.p2e.blanket.form.Security;
import dk.p2e.blanket.form.handler.QueryPart;
import dk.p2e.util.Systemout;
import dk.tempusserva.api.Session;
import dk.tempusserva.api.SessionFactory;
import dk.tempusserva.api.SolutionQuery;
import dk.tempusserva.api.SolutionQueryResultSet;
import dk.tempusserva.api.SolutionRecord;
import dk.tempusserva.api.SolutionRecordNew;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author XSMA
 */
public class ImportCSV extends CodeunitPagecontent{


    @Override
    public String execute(Command cmnd, Security scrt, Hashtable hshtbl) {

        
        Session session = SessionFactory.getSession(this);
        try {
            
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream("C:\\TempoServaTest\\netbeans\\MoveItems\\src\\moveitems\\CountdownCSV.csv"), Charset.forName("UTF-8"));
            CSVReader reader = new CSVReader(inputStreamReader,';');
                List<String[]> rows = reader.readAll();
                
                for(int i = 1; i < rows.size(); i++){
                    int sagid = 89;
                    //SolutionRecordNew srn = session.getSolutionRecordNew(sagid);
                    //srn.setValue("DEVICECATEGORY", "Countdown");
                    String[] row = rows.get(i);
                    String type = row[2];
                    String moduleID = row[9];
                    String payingYear = row[18];
                    String payer = row[19];
                    String code = row[4];
                    String payerM;

                    if(moduleID.length() > 0){
                        SolutionQuery exist = session.getSolutionQuery(sagid);
                        exist.addWhereCriterion("SERIALNO",QueryPart.EQUALS, moduleID);
                        SolutionQueryResultSet existrs = exist.executeQuery();
                        if(existrs.size() > 0){
                            
                        SolutionRecord srn = existrs.getRecord(0);

                        Systemout.println(moduleID);
                        //srn.setValue("SERIALNO", moduleID);
                        //srn.setValueInteger("DEVICECATEGORY", 167);
                        
                        /*
                        
                        if(code.length() > 0){
                            HashMap<String,Integer> codeMap = new HashMap<>();
                            
                            codeMap.put("AX-B",169);
                            codeMap.put("MQ-B",169);
                            codeMap.put("AX-MG",171);
                            codeMap.put("MQ-MG",171);
                            codeMap.put("AX-G",170);
                            codeMap.put("MQ-G",170);
                            codeMap.put("MQ-UD",168);
                            
                            if(codeMap.containsKey(code)){
                                srn.setValueInteger("COLOR", codeMap.get(code));
                            }
                            
                        }
                        
                        
                        
                        
                        
                        
                        
                        
                        
                        
                        
                        
                        //Countdown module type
                        if(type.length() > 0){
                            HashMap<String,Integer> typeMap = new HashMap<>();
                            typeMap.put("SSLCD", 172);
                            typeMap.put("DSLCD", 173);
                            typeMap.put("G1", 175);
                            typeMap.put("G2", 176);
                            if(typeMap.containsKey(type)){
                                int typeCode = typeMap.get(type);
                                srn.setValueInteger("DEVICETYPE", typeCode);
                                if(typeCode == 172 || typeCode == 173){
                                    SolutionRecord sr = session.getSolutionRecord(69, 4527);
                                    srn.setReference("SUPPLIER", sr);
                                }
                                else{
                                    SolutionRecord sr = session.getSolutionRecord(69, 4528);
                                    srn.setReference("SUPPLIER", sr);
                                    
                                }
                                
                            }
                            

                            
                            
                        }
                        */
                        
                        
                        /*
                        if(payingYear.length() > 0){
                            srn.setValue("BUDGETYEAR", payingYear);
                        }
                        */
                        
                        
                        //<editor-fold defaultstate="collapsed" desc="Payer if statement">
                        if(payer.length() > 0){
                            SolutionQuery q = session.getSolutionQuery(73);
                            HashMap<String,String> mappingTable = new HashMap<>();
                            mappingTable.put("Region H", "Region Hovedstaden");
                            mappingTable.put("Lokaltog","Lokaltog");
                            mappingTable.put("København", "Københavns Kommune");
                            mappingTable.put("Høje Taastrup","Høje-Taastrup Kommune");
                            mappingTable.put("Movia Yellow Spot", "Movia");
                            mappingTable.put("Movia", "Movia");
                            
                            String[] multipleBuyers = payer.split(" / ");
                            if("Helsingørmotorvejen".equals(payer)){
                                Systemout.println("PASSSSSSSSSSSSSSSS");
                            }
                            else{
                                if(multipleBuyers.length > 1){
                                    String payerOne, payerTwo;
                                    if(mappingTable.containsKey(multipleBuyers[0])){
                                        payerOne = mappingTable.get(multipleBuyers[0]);
                                    }
                                    else{
                                        payerOne = multipleBuyers[0] + " Kommune";
                                    }
                                    if(mappingTable.containsKey(multipleBuyers[1])){
                                        payerTwo = mappingTable.get(multipleBuyers[1]);
                                    }
                                    else{
                                        payerTwo = multipleBuyers[1] + " Kommune";
                                    }
                                    q.addWhereCriterion("NAVN", QueryPart.EQUALS, payerOne );
                                    SolutionQueryResultSet rs = q.executeQuery();

                                    SolutionQuery qTwo = session.getSolutionQuery(73);
                                    qTwo.addWhereCriterion("NAVN", QueryPart.EQUALS, payerTwo );
                                    SolutionQueryResultSet rsTwo = qTwo.executeQuery();

                                    if(rs.size() == 0 || rsTwo.size() == 0){
                                        Systemout.println("No result" );
                                        Systemout.println(multipleBuyers[0]);
                                        Systemout.println(multipleBuyers[1]);
                                    }
                                    else if(rs.size() > 1 || rsTwo.size() > 1){
                                        Systemout.println("This sohuldnt happen");
                                    } else {
                                        srn.setReference("OWNER", rs.getRecord(0));
                                        srn.setReference("OWNER2", rsTwo.getRecord(0));
                                    }

                                }
                                else{
                                    if(mappingTable.containsKey(payer)){
                                        payerM = mappingTable.get(payer);
                                    }
                                    else{
                                        payerM = payer + " Kommune";
                                    }
                                    q.addWhereCriterion("NAVN", QueryPart.EQUALS, payerM );
                                    SolutionQueryResultSet rs = q.executeQuery();
                                    if(rs.size() == 0){
                                        Systemout.println(payer);
                                        Systemout.println(String.valueOf(payer.length()));
                                    }
                                    else if(rs.size() > 1){
                                        Systemout.println("This sohuldnt happen");
                                    }
                                    else{
                                        //srn.setReference("OWNER", rs.getRecord(0));
                                    }
                                }
                            }   
                        }
                    //</editor-fold>
                    
                   /*     
                   */
                    srn.persistChanges();
                    
                    }
                    }
                    
                } 
            } 
         catch (Exception ex) {
            Systemout.println("Test");
            Systemout.println(ex.getMessage());
        }
        finally{
            session.close();
        }
        return "";
    }
    
    
    
}
