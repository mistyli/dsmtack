package com.jobs;

import com.dao.bean.D_energyCurvePo;
import com.dao.bean.D_powerCurvePo;
import com.dao.bean.D_powerPo;
import com.dao.mapper.CurveDao;
import com.dao.mapper.D_energyCurvePoMapper;
import com.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by misty on 2018/3/27.
 * 电能量曲线
 * 79上面统计数据列出的表中没有电能量
 * 三相计量_电能量_年
 *
 * 20180504电量曲线表从c_mmtr_energy_c搬到dsm_mmtr_energy_c 前一个点要减去后一个点
 * 然后计算352 350 330的数据
 */
@Component
public class EnergyCurve implements BaseCurve {

    @Autowired
    CurveDao curve;

    @Autowired
    D_energyCurvePoMapper mapper;

    @Scheduled(cron = "0 1/5 * * * ? ")
    public void work(){
        //电量曲线(前一个点)-(后一个点)
        curve.updatePointCurve("c_mmtr_energy_c", "dsm_mmtr_energy_c", "energy_");

        int point = DateUtil.getPoint();
        if(point<=1){//插入数据新的一天的数据
            curve.startDay("dsm_mmtr_energy_c", "energy_", "352");
            curve.startDay("dsm_mmtr_energy_c", "energy_", "350");
            curve.startDay("dsm_mmtr_energy_c", "energy_", "330");
        }
        else{//更新数据
            curve.insertPowerCurve("dsm_mmtr_energy_c", "energy_", "352");//C_MMTR_ENERGY_C变为DSM_MMTR_ENERGY_C
            curve.insertPowerCurve("dsm_mmtr_energy_c", "energy_", "350");//C_MMTR_ENERGY_C
            curve.insertPowerCurve("dsm_mmtr_energy_c", "energy_", "330");//C_MMTR_ENERGY_C
        }
        System.out.println("----------电能量曲线--------------");
    }

    /**
     * 启动计算曲线数据
     * 没有统计数据
     * 曲线数据从c_mmtr_energy_c表移到dsm_mmtr_energy_c
     */
    public void workStatistic(){
        curve.moveCurve("c_mmtr_energy_c", "dsm_mmtr_energy_c", "energy_");//移动曲线数据到dsm系统
        curve.startDay("dsm_mmtr_energy_c", "energy_", "352");//计算间接采集点曲线
        curve.startDay("dsm_mmtr_energy_c", "energy_", "350");
        curve.startDay("dsm_mmtr_energy_c", "energy_", "330");
        workDay();
        workMonth();
    }

    /**
     * 电量多日曲线数据
     * @param start_date
     * @param end_date
     */
    @Async
    public void workStatistic(String start_date, String end_date){
        System.out.println("----------电能量曲线补算开始--------------");
        curve.multiDayEnergy(start_date, end_date);
        curve.multiDay("dsm_mmtr_energy_c", "energy_", "352", start_date, end_date);
        curve.multiDay("dsm_mmtr_energy_c", "energy_", "350", start_date, end_date);
        curve.multiDay("dsm_mmtr_energy_c", "energy_", "330", start_date, end_date);
        System.out.println("----------电能量曲线补算结束--------------");
    }

    /**
     * 日电量统计数据从C_MMTR_ENERGY_D移动到DSM_MMTR_ENERGY_D
     * 日期为上日
     */
    public void workDay(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_MONTH, -1);
        String dateStr = sdf.format(cal.getTime());
        curve.moveEnergyData("c_mmtr_energy_d", "dsm_mmtr_energy_d", dateStr, dateStr);
    }

    /**
     * 月电量采集数据C_MMTR_ENERGY_M乘以pt,ct移动到dsm_MMTR_ENERGY_M
     * 单月移动
     */
    @Override
    public void workMonth() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        cal.set(Calendar.DAY_OF_MONTH, 1);//日期为 每月第一天
        String dateStr = sdf.format(cal.getTime());
        curve.moveEnergyData("c_mmtr_energy_m", "dsm_mmtr_energy_m", dateStr, dateStr);
    }

    @Override
    public void workMultiMonth(String start_date, String end_date) {
        curve.moveEnergyData("c_mmtr_energy_m", "dsm_mmtr_energy_m", start_date + "-01", end_date + "-01");
    }

//    @Scheduled(cron = "0 0 0 1-3 1 ? ")
    @Override
    public void workYear() {

    }

    @Override
    public void workMultiYear(String start_date, String end_date) {

    }

}


