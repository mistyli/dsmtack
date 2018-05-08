package com.controller;

import com.jobs.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by misty on 2018/3/30.
 * 手动补算
 */
@Controller
public class SetCal {

    @Autowired
    PowerCurve pc;

    @Autowired
    CurCurve cc;

    @Autowired
    EnergyCurve ec;

    @Autowired
    FreqCurve fc;

    @Autowired
    MhaiCurCurve mcc;

    @Autowired
    MhaiCurDistCurve mcdc;

    @Autowired
    MhalVolCurve mvc;

    @Autowired
    MharVolDistCurve mvdc;

    @Autowired
    PowerFactorCurve pfc;

    @Autowired
    UnbalanceCurve vuc;

    @Autowired
    CurUnbalanceCurve cuc;

    @Autowired
    TmpCurve tmp;

    @Autowired
    FundVol fundVol;

    @Autowired
    FundCur fundCur;

    @Autowired
    VolRatio volRatio;

    @Autowired
    CurRatio curRatio;

    @Autowired
    EnergyFee fee;

    @Autowired
    VolCurve vol;

    /**
     * 补算日月年
     * 日：起始日期-结束日期
     * 月：起始月-结束月
     * 年：起始年-结束年
     * @param type
     * @param start_date
     * @param end_date
     * @return
     */
    @ApiOperation(value="补算数据", notes="日期参数")
    @RequestMapping(value = "/cal", method = RequestMethod.POST)
    @ResponseBody
    public Map makeUpCal(String type, String start_date, String end_date){
        String[] st = start_date.split("-");
        String[] et = end_date.split("-");
        int ys = Integer.parseInt(st[0]);
        int es = Integer.parseInt(et[0]);

        if(st.length==3){
            if(ys <= es){
                int sm = Integer.parseInt(st[1]);
                int em = Integer.parseInt(st[1]);
                if(sm <= em){
                    int sd = Integer.parseInt(st[2]);
                    int ed = Integer.parseInt(et[2]);
                    if(sd > ed){
                        String temp = start_date;
                        start_date = end_date;
                        end_date = temp;
                    }
                }
                else{
                    String temp = start_date;
                    start_date = end_date;
                    end_date = temp;
                }
            }
            else{
                String temp = start_date;
                start_date = end_date;
                end_date = temp;
            }
        }
        else if(st.length==2){
            if(ys <= es){
                int sm = Integer.parseInt(st[1]);
                int em = Integer.parseInt(st[1]);
                if(sm > em){
                    String temp = start_date;
                    start_date = end_date;
                    end_date = temp;
                }
            }
            else{
                String temp = start_date;
                start_date = end_date;
                end_date = temp;
            }
        }
        else{
            if(ys >= es){
                String temp = start_date;
                start_date = end_date;
                end_date = temp;
            }
        }

        if(type.equals("d")){
            pc.workStatistic(start_date, end_date);//功率曲线和统计
            cc.workStatistic(start_date, end_date);//电流统计数据
            ec.workStatistic(start_date, end_date);//电量曲线和统计数据
            vol.workStatistic(start_date, end_date);//电压曲线统计数据
            fc.workStatistic(start_date, end_date);//频率统计数据
            mcc.workStatistic(start_date, end_date);//电流谐波统计数据
            mcdc.workStatistic(start_date, end_date);//电流谐波畸变统计数据
            mvc.workStatistic(start_date, end_date);//电压谐波统计数据
            mvdc.workStatistic(start_date, end_date);//电压谐波畸变统计数据
            pfc.workStatistic(start_date, end_date);//功率因数统计数据
            vuc.workStatistic(start_date, end_date);//电压不平衡统计数据
            cuc.workStatistic(start_date, end_date);//电流不平衡统计数据
            tmp.workStatistic(start_date, end_date);//温度统计数据
            fundVol.workStatistic(start_date, end_date);//基波电压统计
            fundCur.workStatistic(start_date, end_date);//基波电流
            curRatio.workStatistic(start_date, end_date);//谐波电流
            volRatio.workStatistic(start_date, end_date);//谐波电压
            fee.workStatistic(start_date, end_date);//电费
        } else if(type.equals("m")){
            pc.workMultiMonth(start_date, end_date);
            cc.workMultiMonth(start_date, end_date);
            ec.workMultiMonth(start_date, end_date);
            vol.workMultiMonth(start_date, end_date);
            fc.workMultiMonth(start_date, end_date);
            mcc.workMultiMonth(start_date, end_date);
            mcdc.workMultiMonth(start_date, end_date);
            mvc.workMultiMonth(start_date, end_date);
            mvdc.workMultiMonth(start_date, end_date);
            pfc.workMultiMonth(start_date, end_date);
            vuc.workMultiMonth(start_date, end_date);
            cuc.workMultiMonth(start_date, end_date);
            tmp.workMultiMonth(start_date, end_date);
            fundVol.workMultiMonth(start_date, end_date);
            fundCur.workMultiMonth(start_date, end_date);
            curRatio.workMultiMonth(start_date, end_date);
            volRatio.workMultiMonth(start_date, end_date);
            fee.workMultiMonth(start_date, end_date);
        } else if(type.equals("y")){
            pc.workMultiYear(start_date, end_date);
            cc.workMultiYear(start_date, end_date);
            vol.workMultiYear(start_date, end_date);
            fc.workMultiYear(start_date, end_date);
            mcc.workMultiYear(start_date, end_date);
            mcdc.workMultiYear(start_date, end_date);
            mvc.workMultiYear(start_date, end_date);
            mvdc.workMultiYear(start_date, end_date);
            pfc.workMultiYear(start_date, end_date);
            vuc.workMultiYear(start_date, end_date);
            cuc.workMultiYear(start_date, end_date);
            tmp.workMultiYear(start_date, end_date);
            fundVol.workMultiYear(start_date, end_date);
            fundCur.workMultiYear(start_date, end_date);
            curRatio.workMultiYear(start_date, end_date);
            volRatio.workMultiYear(start_date, end_date);
            fee.workMultiYear(start_date, end_date);
        }
        System.out.println(type + "-" + start_date + "-" + end_date);
        Map<String, String> map = new HashMap<>();
        map.put("result", "ok");
        return map;
    }

    @RequestMapping("/")
    public String hello(){
        return "MakeUpCal";
    }

//    @Async
//    @GetMapping(value = "/async")
//    public String testAsync() {
//        /*
//            123-->异步,132-->同步
//         */
//        System.out.println("1");
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        System.out.println("2");
//        return "test";
//    }

}
