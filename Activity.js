
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
    try {
    ShowPage('20');
    $("#PageSelector_").hide();

        document.getElementsByClassName("updateSubmit btn btn-primary form-control")[1].type="hidden";
    }
    catch(err) {

    }

    setTimeout(a, 100);

    validateHiddenToTrue(fieldNames);
});

function validateHiddenToTrue(fields){
    alert('test');
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


$('#VB_DATA_ACTIVITY').on('change',function{validateHiddenToTrue(fieldNames);});



$('#VB_DATA_SETUPBATTERY').on('change',function(){
    var isHidden = $('#VB_DATA_SELECTEDDEVICE2').css("display") == "none";
    if(isHidden){
        try { validate('DATA_SELECTEDDEVICE2','text','','','',true); } catch (err) {};
    }
    else{
        try { validate('DATA_SELECTEDDEVICE2','text','','','',false); } catch (err) {};
    }
});

$('#VB_DATA_SETUPMODULE').on('change',function(){
    var isHidden = $('#VB_DATA_SELECTEDDEVICE').css("display") == "none";
    if(isHidden){
        try { validate('DATA_SELECTEDDEVICE','text','','','',true); } catch (err) {};
    }
    else{
        try { validate('DATA_SELECTEDDEVICE','text','','','',false); } catch (err) {};
    }
});



function a(){
try {
    var url = new URL(window.location.href);
    var error = url.searchParams.get("errormsg");
    if(error.length > 0){
        alert(error);
    }
}
catch(err){}
};
