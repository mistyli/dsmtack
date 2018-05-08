/**
 * Created by misty on 2018/4/2.
 */
$(function(){

    $('input[type=radio][name=radio2]').change(function() {
        if (this.value == 'd') {
            $("#day").css("display", "block");
            $("#month").css("display", "none");
            $("#year").css("display", "none");
        }
        else if (this.value == 'm') {
            $("#month").css("display", "block");
            $("#day").css("display", "none");
            $("#year").css("display", "none");
        }
        else if (this.value == 'y') {
            $("#year").css("display", "block");
            $("#month").css("display", "none");
            $("#day").css("display", "none");
        }
    });
    initDate();

})

function initDate(){
    //initDay();initMonth();initYear();
    var myDate = new Date();
    myDate.setDate(myDate.getDate()-1);
    var y = myDate.getFullYear();
    var m = (myDate.getMonth()+1)<10?"0"+(myDate.getMonth()+1):(myDate.getMonth()+1);//获取当前月份的日期，不足10补0
    var d = myDate.getDate()<10?"0"+myDate.getDate():myDate.getDate(); //获取当前几号，不足10补0
    var yesDate=y+"-"+m+"-"+d;

    $('#date_day_start').daterangepicker({ singleDatePicker: true,format :"YYYY-MM-DD",startDate:yesDate,endDate:'2099-12-31'
        ,minDate: '2014-01-01',maxDate : moment(), 'locale': locale}, function(start, end, label) {
        console.log(start.toISOString(), end.toISOString(), label);
    });

    $('#date_day_end').daterangepicker({ singleDatePicker: true,format :"YYYY-MM-DD",startDate:yesDate,endDate:'2099-12-31',
        minDate: '2014-01-01',maxDate : moment(),'locale': locale}, function(start, end, label) {
        console.log(start.toISOString(), end.toISOString(), label);
    });

    $('#date_day_start').val(yesDate);
    $('#date_day_end').val(yesDate);

    var month = y + "-" + m;
    $( "#date_month_start").datepicker({
            format: "yyyy-mm",
            startDate: "2015-1",
            endDate: month,
            startView: 1,
            minViewMode: 1,
            maxViewMode: 2,
            language: "zh-CN",
            orientation: "bottom auto",
            autoclose: true
        }
    );

    $( "#date_month_end" ).datepicker({
            format: "yyyy-mm",
            startDate: "2015-1",
            endDate: month,
            startView: 1,
            minViewMode: 1,
            maxViewMode: 2,
            language: "zh-CN",
            orientation: "bottom auto",
            autoclose: true
        }
    );

    $( "#date_month_end").val(month);
    $( "#date_month_start").val(month);


    $( "#date_year_start" ).datepicker({
            format: "yyyy",
            startDate: "2010",
            endDate: "2099",
            startView: 2,
            startView: 2,
            minViewMode: 2,
            maxViewMode: 2,
            clearBtn: false,
            language: "zh-CN",
            orientation: "bottom auto",
            autoclose:true
        }
    );
    $( "#date_year_end" ).datepicker({
            format: "yyyy",
            startDate: "2010",
            endDate: "2099",
            startView: 2,
            minViewMode: 2,
            maxViewMode: 2,
            clearBtn: false,
            language: "zh-CN",
            orientation: "bottom auto",
            autoclose:true
        }
    );

    $("#date_year_start").val(y);
    $("#date_year_end").val(y);
}

var locale = {
    "format": 'YYYY-MM-DD',
    "separator": " -222 ",
    "applyLabel": "确定",
    "cancelLabel": "取消",
    "fromLabel": "起始时间",
    "toLabel": "结束时间'",
    "customRangeLabel": "自定义",
    "weekLabel": "W",
    "daysOfWeek": ["日", "一", "二", "三", "四", "五", "六"],
    "monthNames": ["一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"],
    "firstDay": 1
};

function addCal(){
    var checked = $("input[type=radio]:checked").val();
    var date_start = "";
    var date_end = "";

    if(checked=="d"){
        date_start = $("#date_day_start").val();
        date_end = $("#date_day_end").val();
    } else if(checked=="m"){
        date_start = $("#date_month_start").val();
        date_end = $("#date_month_end").val();
    } else if(checked=="y"){
        date_start = $("#date_year_start").val();
        date_end = $("#date_year_end").val();
    }

    if(date_start==""||date_end==""){
        alert("请选择起始日期和结束日期");
        return ;
    }

    $('#bus').attr('disabled', true);

    $.ajax({
        dataType:"json",
        type: "post",
        async: true,
        url: "/cal",
        data:{
            "type": checked,
            "start_date": date_start,
            "end_date": date_end
        },
        success: function(json){
            if(json.result=="ok"){
                alert("计算成功");
                $("#bus").removeAttr("disabled");
            }
        },
        error:function(err){
            $("#bus").removeAttr("disabled");
            alert("发生异常，请联系管理员");
        }
    })

}

