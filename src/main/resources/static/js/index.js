$(document).ready(function () {
    //初始化提示框
    $(function () {
        $('#js').popover({
            html:true,
            trigger: 'hover',
            placement:'top',
            content:"后缀名 : js<br/>注释开始符 : "+"/*"+"<br/>注释结束符 ："+"*/"+"<br/>行注释符 ：//"
        })
    });
    $(function () {
        $('#java').popover({
            html:true,
            trigger: 'hover',
            placement:'top',
            content:"后缀名 : java<br/>注释开始符 : /*<br/>注释结束符 ：*/<br/>行注释符 ：//"
        })
    });
    $(function () {
        $('#xml').popover({
            html:true,
            trigger: 'hover',
            placement:'top',
            content:"后缀名 : xml<br/>注释开始符 : &lt;!--<br/>注释结束符 ：-->"
        })
    });
    $(function () {
        $('#html').popover({
            html:true,
            trigger: 'hover',
            placement:'top',
            content:"后缀名 : xml<br/>注释开始符 : &lt;!--<br/>注释结束符 ：-->"
        })
    });
    $(function () {
        $('#properties').popover({
            html:true,
            trigger: 'hover',
            placement:'top',
            content:"后缀名 : properties<br/>行注释符 ：#"
        })
    });
    num=0;
    var options = {
        success:function (data) {
            if(data.length==0) {
                alert("获取成功!");
                window.location.reload();
            }else {
                    $('#load').modal('hide');
                    $('#load').one('shown.bs.modal',function () {
                    $('#load').modal('hide');
                    });
                    var flag= false;
                    for(var i=0;i<data.length;i++){
                        if(data[i]=="dirPath"){
                            $("#dirPath").addClass("has-error");
                            flag=true;
                        }
                        if(data[i]=="filePath"){
                            $("#textPath").addClass("has-error");
                            flag=true;
                        }
                    }
                    if(flag){
                        alert("获取失败，请检查参数!");
                    }else{
                        alert("抱歉,服务器出错了");
                    }
                }
            },
    };
    $("#form").submit(function () {
        event.preventDefault();
        $("#load").modal("show");
        $(".has-error").removeClass("has-error");
        $("#dirPath").removeClass("");
        $("#textPath")
        $("#form").ajaxSubmit(options);
    });
    return false;

});
window.onresize = function(){
    $("#form").css("margin-left",($(window).width()-$("#form").width())/2);
};
function a() {
    $("#form").css("margin-left",($(window).width()-$("#form").width())/2);
}
//全选
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
}
function deleteFilter(e) {
    $(e.offsetParent).remove();
}
function deleteFile(e) {
    $(e.offsetParent).remove();
    num--;
}
function addFilter() {
    var template=`<div class="input-group mb-3 input-group-sm" style="float:left;width: 50%">
    <input type="text" class="form-control" name="filter" placeholder="过滤路径">
    <span class="input-group-btn">
    <button class="btn btn-outline-info btn-sm" type="button" onclick="deleteFilter(this)">删除</button>&nbsp;&nbsp;
    </span>
    </div>`;
$("#otherFilter").append(template);
}
function addFile() {
    var template=` <div class="input-group mb-3 input-group-sm">
                    <input type="text" class="form-control"  name="otherFile[${num}].name" placeholder="文件后缀名" style="width: 22%"/>
                    <input type="text" class="form-control"  name="otherFile[${num}].start" placeholder="注释开始符" style="width: 22%"/>
                    <input type="text" class="form-control"  name="otherFile[${num}].end" placeholder="注释结束符" style="width: 22%"/>
                    <input type="text" class="form-control"  name="otherFile[${num}].line" placeholder="行注释符" style="width: 22%"/>
                    <span class="input-group-btn">
                        <button class="btn btn-outline-info btn-sm" type="button" onclick="deleteFile(this)">删除</button>&nbsp;&nbsp;
                    </span>
                </div>`;
    $("#otherFile").append(template);
    num++;
}
function server() {
    $("#userName").hide();
    $("#password").hide();
    $("input[name='dirPath']").attr("placeholder","默认桌面");
    $("input[name='type']").val("0");

}
function downloadSVN() {
    $("#userName").show();
    $("#password").show();
    $("input[name='dirPath']").attr("placeholder","输入完整SVN地址");
    $("input[name='type']").val("1");
}
function onlineSVN() {
    $("#userName").show();
    $("#password").show();
    $("input[name='dirPath']").attr("placeholder","输入完整SVN地址");
    $("input[name='type']").val("2");
}