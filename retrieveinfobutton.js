var fieldValues =
	{
		'DATA_DEVICE_NEW' : $('#DATA_DEVICE').val(),
	 	'DATA_TYPE_NEW' : $('#DATA_TYPE').val(),
		'DATA_COMMENTS_NEW' : $('#DATA_COMMENTS').val(),
		'DATA_CAUSE_NEW' : $('#DATA_CAUSE').val(),
 	}

var url = window.location.href;

$.each(fieldValues, function(key, value){
	if(value != ""){
		url += key + "=" + value;
	}
});
window.location.href = url;
