
//Dictionary with the fields that is checked/unchecked for validation when they are hidden or shown. Is of type: {String - Child id of the element : Element - Jquery variable of the parent element}
var fieldNames = {'DATA_TOINVENTORY' : $('#VB_DATA_TOINVENTORY'),
              'DATA_STOPPOINT' : $('#VB_DATA_STOPPOINT'),
              'DATA_DEVICEONSTOPPOINT' : $('#VB_DATA_DEVICEONSTOPPOINT'),
              'DATA_COMPONETTYPEINSTALLEDAMOUNT' : $('#VB_DATA_COMPONETTYPEINSTALLEDAMOUNT'),
              'DATA_SELECTEDDEVICE' : $('#VB_DATA_SELECTEDDEVICE'),
              'DATA_INVENTORYCOMPONENT' : $('#VB_DATA_INVENTORYCOMPONENT'),
              'DATA_FROMINVENTORY' : $('#VB_DATA_FROMINVENTORY'),
              'DATA_SELECTEDDEVICE2' : $('#VB_DATA_SELECTEDDEVICE2'),
              'DATA_COMPONETTYPEINSTALLED' : $('#VB_DATA_COMPONETTYPEINSTALLED'),
              'DATA_BATTERYONSTOPPOINT' : $('#VB_DATA_BATTERYONSTOPPOINT')};



$().ready( function() {
    try {ShowPage('20'); $("#PageSelector_").hide(); } catch(err) {};


    try{ document.getElementsByClassName("updateSubmit btn btn-primary form-control")[1].type="hidden"; } catch(err) {};

    
    setTimeout(errorMsg, 100);

    validateHiddenToTrue(fieldNames);
});


/**
 * Function to "hack" Tempusservas validation when using javascript dependancies
 * It sets the validation of given fields to true when they are Hidden and
 * attempts to validate them to false when showing again(This will fail if the user already provided valid input)
 * @param  { String : Element } Dictionary each entry key is the String value of the child of the Jquery element that is the value of the record.
 */
function validateHiddenToTrue(fields){
     $.each(fields, function(key,value){
        var isTrue = value.attr("class") != "FieldValue tsValidateFalse"
        if(value.css("display") == "none"){
            try { validate(key,'text','','','',true); } catch (err) {};
        }else{
            if(isTrue){
                   try { validate(key,'text','','','',false); } catch (err) {};
            }
        }
    });
};


/**
* On change function to validate all the fields when a user changes input in the activity field
*/
$('#VB_DATA_ACTIVITY').on('change',function{validateHiddenToTrue(fieldNames);});



/**
* Onchange function for the setupbattery checkbox. Sets validation of the field  that gets hidden or shown to true/false
* As this function does not have any value using the val() function, it checks if the field it is validating is hidden or shown instead
**/

$('#VB_DATA_SETUPBATTERY').on('change',function(){
    var isHidden = $('#VB_DATA_SELECTEDDEVICE2').css("display") == "none";
    if(isHidden){
        try { validate('DATA_SELECTEDDEVICE2','text','','','',true); } catch (err) {};
    }
    else{
        try { validate('DATA_SELECTEDDEVICE2','text','','','',false); } catch (err) {};
    }
});

/**
* Onchange function for the setupbattery checkbox. Sets validation of the field  that gets hidden or shown to true/false
* As this function does not have any value using the val() function, it checks if the field it is validating is hidden or shown instead
**/
$('#VB_DATA_SETUPMODULE').on('change',function(){
    var isHidden = $('#VB_DATA_SELECTEDDEVICE').css("display") == "none";
    if(isHidden){
        try { validate('DATA_SELECTEDDEVICE','text','','','',true); } catch (err) {};
    }
    else{
        try { validate('DATA_SELECTEDDEVICE','text','','','',false); } catch (err) {};
    }
});



/**
*
**/
function errorMsg (){
try {
    var url = new URL(window.location.href);
    var error = url.searchParams.get("errormsg");
    if(error.length > 0){
        alert(error);
    }
}
catch(err){}
};
