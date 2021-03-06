/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package activities;

/**
 * Class containing all string names and int values of items in TS. Change and add here changing stuff on website
 * @author XSMA
 */
public class TSValues {
    
    
    /** Device Entity id */
    public static final String DEVICE_ENTITY = "devices";
    /** Stoppoint field id in the device entity */
    public static final String DEVICE_STOPPPOINT = "INSTALLEDATSTOPPOINT";
    /** Storage Location field id in the device entity */
    public static final String DEVICE_STORAGELOC = "STORAGELOCATION";
    /** Category field id in the device entity */
    public static final String DEVICE_CAT = "DEVICECATEGORY";

    
    /** Stoppoint Inventory Entity id */
    public static final String STOPPOINTINV_ENTITY = "stoppointinventory";
    /** Stoppoint field id in the stoppoint inventory entity */
    public static final String STOPPOINTINV_STOPPOINT = "STOPPOINT";
    /** Component field id in the stoppoint inventory entity  */
    public static final String STOPPOINTINV_COMPONENT = "COMPONENT";
    /** Amount field id in the stoppoint inventory entity  */
    public static final String STOPPOINTINV_AMOUNT = "AMOUNT";
    
    
    /** Component Storage Entity id */
    public static final String COMPONENTSTORAGE_ENTITY = "warehouseallocation";
    /** Amount field id in the component storage entity */
    public static final String COMPONENTSTORAGE_AMOUNT = "AMOUNT";
    /** Warehouse field id in the component storage entity */
    public static final String COMPONENTSTORAGE_WAREHOUSE = "WAREHOUSE";
    /** Component field id in the component storage entity */
    public static final String COMPONENTSTORAGE_COMPONENT = "COMPONENT";
    
    
    //Strings for case entity
    public static final String CASE_ENTITY = "workorder";
    
    
    //Strings for stoppoint entity
    public static final String STOPPOINT_ENTITY = "stoppoint";
    
    //Strings for warehouse entity
    public static final String WAREHOUSE_ENTITY = "warehouse";
    
    //Strings for component entity
    public static final String COMPONENT_ENTITY = "components";
    
    
    //Strings for the activity entity
    public static final String ACTIVITY_ENTITY = "activities";
    public static final String ACTIVITY_CASE = "WORKORDER";
    public static final String ACTIVITY_STOPPOINT = "STOPPOINT";
    public static final String ACTIVITY_DEVICE = "DEVICE";
    public static final String ACTIVITY_ACTIVITY = "ACTIVITY";
    public static final String ACTIVITY_COMPONENTINSTALLED = "COMPONETTYPEINSTALLED";
    public static final String ACTIVITY_COMPONENTAMOUNT = "COMPONETTYPEINSTALLEDAMOUNT";
    public static final String ACTIVITY_SELECTEDDEVICE = "SELECTEDDEVICE";
    public static final String ACTIVITY_DEVICEONSTOPPOINT = "DEVICEONSTOPPOINT";
    public static final String ACTIVITY_TOINVENTORY = "TOINVENTORY";
    public static final String ACTIVITY_FROMINVENTORY = "FROMINVENTORY";
    public static final String ACTIVITY_INVENTORYCOMPONENT = "INVENTORYCOMPONENT";
    public static final String ACTIVITY_BATTERYONSTOPPOINT = "BATTERYONSTOPPOINT";
    public static final String ACTIVITY_SUPPLIER = "SUPPLIER";
    public static final String ACTIVITY_SELECTEDDEVICETWO = "SELECTEDDEVICE2";
    public static final String ACTIVITY_SETUPBATTERY = "SETUPBATTERY";
    public static final String ACTIVITY_SETUPMODULE = "SETUPMODULE";

    
    //public static final String batteryDef = "Batteri";
    //public static final String countdownModuleDef = "Countdown";
    
    
    public static final String ACTIVITIYCODE_DEVICE_SETUP  = "171";
    public static final String ACTIVITIYCODE_DEVICE_TAKEDOWN  = "172";
    public static final String ACTIVITIYCODE_DEVICE_RESTART  = "174";
    public static final String ACTIVITIYCODE_DEVICE_CABLECHECK  = "175";
    public static final String ACTIVITIYCODE_BATTERY_SETUP = "168";
    public static final String ACTIVITIYCODE_BATTERY_TAKEDOWN = "169";
    public static final String ACTIVITIYCODE_COMPONENT_SETUP = "176";
    public static final String ACTIVITIYCODE_COMPONENT_TAKEDOWN = "177";
    public static final String ACTIVITIYCODE_STORAGEMOVE_COMPONENT = "178";
    public static final String ACTIVITIYCODE_STORAGEMOVE_DEVICE = "179";
    
    
    public static final String STATUSID = "StatusID";
    public static final int ACTIVITIES_STATUS_INIT = 83;
    public static final int ACTIVITIES_STATUS_USERERROR = 98;
    public static final int ACTIVITIES_STATUS_APPROVED = 100;
    public static final int ACTIVITIES_STATUS_SYSTEMERROR = 101;
    public static final int ACTIVITIES_STATUS_AWAITADMIN = 103;
    public static final int ACTIVITIES_STATUS_DELETE = 108;

    

    
    
}
