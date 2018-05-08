package com.jobs;

import com.dao.mapper.CurveDao;
import com.dao.mapper.D_volCurvePoMapper;
import com.util.DateUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by misty on 2018/3/26.
 * 电压日、月统计数据是采集来的，年统计数据从月统计表计算而来
 * 这里只有年统计数据计算
 */
@Component
public class VolCurve implements BaseCurve {

    private static final Logger log = LogManager.getLogger(VolCurve.class.getName());

    @Autowired
    CurveDao curve;

    @Autowired
    D_volCurvePoMapper mapper;

    /**
     * 5分钟更新电压曲线C_MMXU_VOL_C的一个点到DSM_MMXU_VOL_C
     */
    @Scheduled(cron = "0 1/5 * * * ? ")
    public void work(){
        int point = DateUtil.getPoint();

        if(point<=1){//第一个点要插入数据
            curve.moveCuvePc("c_mmxu_vol_c", "dsm_mmxu_vol_c", "vol_", 2);
        }
        else{//每次更新一个曲线点
            curve.updatePointCurveForPc("c_mmxu_vol_c", "dsm_mmxu_vol_c", "vol_", 2);
        }
    }

    /**
     * 电压日统计数据从表C_MSTA_VOL_D到DSM_MSTA_VOL_D
     * 移动昨天的数据
     * 每天00:01和12:01移动一次
     */
//    @Scheduled(cron = "0 0 0 1-3 1/1 ? ")
    @Scheduled(cron = "0 1 0,12 1/1 * ? ")
    public void workDay(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_MONTH, -1);
        String dateStr = sdf.format(cal.getTime());
//        dateStr = "2018-01-02";
        curve.moveVolData("c_msta_vol_d", "dsm_msta_vol_d", dateStr, dateStr);
    }

    /**
     * 启动时调用
     */
    @Override
    public void workStatistic() {
        curve.moveCuvePc("c_mmxu_vol_c", "dsm_mmxu_vol_c", "vol_", 2);
        workDay();
        workMonth();
    }

    /**
     * 补算
     * @param start_date
     * @param end_date
     */
    @Override
    @Async
    public void workStatistic(String start_date, String end_date) {
        System.out.println("----------电压曲线补算开始--------------");
        curve.calCurvePcMultiDays("c_mmxu_vol_c", "dsm_mmxu_vol_c", "vol_", start_date, end_date, 2);
        curve.moveVolData("c_msta_vol_d", "dsm_msta_vol_d", start_date, end_date);
    }

    /**
     * 每月数据移动 C_MSTA_VOL_M-> DSM_MSTA_VOL_M
     * 每月1、2、3、15、25号的00:03分各算一次
     */
    @Scheduled(cron = "0 0,3 0 1,2,3,15,25 1/1 ? ")
    @Override
    public void workMonth() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        cal.set(Calendar.DAY_OF_MONTH, 1);//每月第一天
        String dateStr = sdf.format(cal.getTime());
//        dateStr = "2018-01-01";
        curve.moveVolData("c_msta_vol_m", "dsm_msta_vol_m", dateStr, dateStr);
    }

    /**
     * 补算
     * @param start_date
     * @param end_date
     */
    @Override
    public void workMultiMonth(String start_date, String end_date) {
        curve.moveVolData("c_msta_vol_m", "dsm_msta_vol_m", start_date + "-01", end_date + "-01");
    }

    @Scheduled(cron = "0 23 0 1-3 1 ? ")
    @Override
    public void workYear() {
        curve.yearStatistic("dsm_msta_vol_y", "dsm_msta_vol_m",  "max_vol", "min_vol", "max_vol_time", "min_vol_time", "avg_vol");
    }

    @Override
    public void workMultiYear(String start_date, String end_date) {
        curve.multiYear("dsm_msta_vol_y", "dsm_msta_vol_m", "max_vol", "min_vol", "max_vol_time", "min_vol_time", "avg_vol", start_date, end_date);
    }

}
