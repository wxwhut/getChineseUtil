$(document).ready(function () {

});
function checkAllFile() {
    var all = $('#allFile');//获取到点击全选的那个复选框的id
    var one = $("input[name='file']");//获取到复选框的名称
    one.map(function (a,b) {
        b.checked=all[0].checked;
    })
}
function checkAllFilter() {
    var all = $('#allFilter');//获取到点击全选的那个复选框的id
    var one = $("input[name='filter']");//获取到复选框的名称
    one.map(function (a,b) {
        b.checked=all[0].checked;
    })
    function getChinese() {
        
    }
}