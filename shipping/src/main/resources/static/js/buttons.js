$("#checkPriceButton").click(_checkPrice);
$("#sendShippingButton").click(_sendShipping);
$("#sendShippingButton").prop( "disabled", true );

function _checkPrice(){
    let originSelected = $("#originSelect option:selected").val();
    let destinationSelected = $("#destinationSelect option:selected").val();
    let sizeOptionSelected = $('#sizeSelect option:selected').val();
    let typeOptionSelected = $('#typeSelect option:selected').val();
    let timeOptionSelected = $('#timeSelect option:selected').val();
    let transportOptionSelected = $('#transportSelect option:selected').val();
    if(!_areOptionValuesValid(sizeOptionSelected, typeOptionSelected, timeOptionSelected, transportOptionSelected, originSelected, destinationSelected)){
        $("#sendShippingButton").prop( "disabled", true );
        alert({"responseText":"You need to select a valid size, type, time or transport"});
        return;
    }
    let checkPricePayload = {
        "size":sizeOptionSelected,
        "type":typeOptionSelected,
        "time":timeOptionSelected,
        "transport":transportOptionSelected
    };
    rest("POST", config.rest.checkPrice, _setPrice, checkPricePayload);
}

function _areOptionValuesValid(sizeOptionSelected, typeOptionSelected, timeOptionSelected, transportOptionSelected, originSelected, destinationSelected){
    return sizeOptionSelected !== "empty" && typeOptionSelected !== "empty" && timeOptionSelected !== "empty" 
            && transportOptionSelected !== "empty" && originSelected !== "empty" && destinationSelected !== "empty";
}

function _setPrice(price){
    $('#price').val(price);
    $("#sendShippingButton").prop( "disabled", false );
}

function _sendShipping(){
    let sizeOptionSelected = $('#sizeSelect option:selected').val();
    let typeOptionSelected = $('#typeSelect option:selected').val();
    let timeOptionSelected = $('#timeSelect option:selected').val();
    let transportOptionSelected = $('#transportSelect option:selected').val();
    let shippingPrice = $('#price').val();
    let cityPath = $('#cityPath').val();
    if(!_areOptionValuesValid(sizeOptionSelected, typeOptionSelected, timeOptionSelected, transportOptionSelected)){
        alert({"responseText":"You need to select a valid size, type, time or transport"});
        return;
    }
    let shippingPayload = {
        "size":sizeOptionSelected,
        "type":typeOptionSelected,
        "time":timeOptionSelected,
        "transport":transportOptionSelected,
        "price":shippingPrice,
        "path":cityPath
    };
    rest("POST", config.rest.sendShipping, _fillShippingTable, shippingPayload);
}

function _fillShippingTable(shippingInformation){
    fillShippingTable(shippingInformation);
}