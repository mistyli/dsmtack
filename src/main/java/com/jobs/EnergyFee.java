package com.jobs;

import com.dao.mapper.CurveDao;
import com.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by misty on 2018/4/12.
 * 电费统计
 * 电量肯定是上日电量 只有日 月 年
 * 其他统计数据是当日数据
 */
@Component
public class EnergyFee implements BaseCurve {

    @Autowired
    CurveDao curve;

    /**
     * 定时计算日统计电费
     * 每天算几次前一天的日电费统计数据
     * 每天0点、5点、10点、17点的0分和3分各计算一次上日电费
     * 即一天计算
     */
    @Scheduled(cron = "0 3 0,5,10,17 1/1 * ? ")
    public void work(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, -1);
        String data_date = sdf.format(cal.getTime());
        curve.lastDayFee(data_date, data_date);
    }

    @Override
    public void workStatistic() {

    }

    /**
     * 补算 多日电费数据
     * @param start_date
     * @param end_date
     */
    @Override
    @Async
    public void workStatistic(String start_date, String end_date) {
        System.out.println("----------------------补算统计数据------------------------------------电费");
        curve.lastDayFee(start_date, end_date);
    }

    /**
     * 定时计算月统计电费
     */
    @Scheduled(cron = "0 0 0 1-3 1/1 ? ")
    @Override
    public void workMonth() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        cal.add(Calendar.MONTH, -1);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        String data_date = year + "-" + month;
        curve.lastMonthFee(data_date, data_date);
    }

    @Override
    public void workMultiMonth(String start_date, String end_date) {
        curve.lastMonthFee(start_date, end_date);
    }

    /**
     * 定时计算年统计电费
     */
    @Scheduled(cron = "0 0 0 1-3 1 ? ")
    @Override
    public void workYear() {
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        cal.add(Calendar.YEAR, -1);
        String year = cal.get(Calendar.YEAR) + "";
        curve.lastYearFee(year, year);
    }

    /**
     * 多年电费补算
     * @param start_date
     * @param end_date
     */
    @Override
    public void workMultiYear(String start_date, String end_date) {
        curve.lastYearFee(start_date, end_date);
    }
}
