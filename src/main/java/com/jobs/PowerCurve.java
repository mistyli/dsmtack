package com.jobs;

import com.dao.bean.D_powerCurvePo;
import com.dao.bean.D_powerPo;
import com.dao.mapper.CurveDao;
import com.dao.mapper.D_powerCurvePoMapper;
import com.util.DateUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by misty on 2018/3/22.
 */
@Component
public class PowerCurve {

    @Autowired
    CurveDao curve;

    @Autowired
    D_powerCurvePoMapper mapper;

    /**
     * 功率 5分钟更新一次
     */
    @Scheduled(cron = "0 1/5 * * * ? ")
    public void work(){
        System.out.println("----------功率--------------");

        int point = DateUtil.getPoint();
        if(point<=1){//第一个点 插入
            curve.moveCuvePc("c_mmxu_power_c", "dsm_mmxu_power_c", "power_", 1);
            curve.startDay("dsm_mmxu_power_c", "power_", "352");
            curve.startDay("dsm_mmxu_power_c", "power_", "350");
            curve.startDay("dsm_mmxu_power_c", "power_", "330");
//            插入日统计数据
            curve.insertFirstPoint("dsm_msta_power_d", "dsm_mmxu_power_c", "power_", "max_power", "min_power", "max_power_time", "min_power_time", "avg_power", 1);
        }
        else{//其他点更新
            curve.updatePointCurveForPc("c_mmxu_power_c", "dsm_mmxu_power_c", "power_", 1);
            curve.insertPowerCurve("dsm_mmxu_power_c", "power_", "352");//C_MMXU_POWER_C变为dsm_mmxu_power_c
            curve.insertPowerCurve("dsm_mmxu_power_c", "power_", "350");
            curve.insertPowerCurve("dsm_mmxu_power_c", "power_", "330");
            //仅更新操作 更新日统计数据 数据来源于日曲线数据
//            curve.statisticUpdate("dsm_msta_power_d", "dsm_mmxu_power_c", "power_", "max_power", "min_power", "max_power_time", "min_power_time", "avg_power");
            curve.statisticUpdateDsm("dsm_msta_power_d", "dsm_mmxu_power_c", "power_", "max_power", "min_power", "max_power_time", "min_power_time", "avg_power");
        }
//        workStatistic();
    }

    /**
     * 计算功率统计数据 补算时调用 取出一行数据 求出最大值最小值和平均值
     * 插入操作
     */
    @Async
    public void workStatistic(String sdate, String edate){
        SimpleDateFormat sdf_sec = new SimpleDateFormat("yyyy-MM-dd HH;mm:ss");

        System.out.println("----------------------功率补算曲线数据------------------------------------");

        //先将曲线数据从c_mmxu_power_c移动到dsm_mmxu_power_c
        curve.calCurvePcMultiDays("c_mmxu_power_c", "dsm_mmxu_power_c", "power_", sdate, edate, 1);

        //多日曲线数据重新计算
        curve.multiDay("dsm_mmxu_power_c", "power_", "352", sdate, edate);
        curve.multiDay("dsm_mmxu_power_c", "power_", "350", sdate, edate);
        curve.multiDay("dsm_mmxu_power_c", "power_", "330", sdate, edate);

        System.out.println("----------------------功率补算统计数据获取多日数据开始------------------------------------");
        //多日统计数据 最大值 最小值
        List<D_powerCurvePo> powers = mapper.selectBy2Date(sdate, edate);
        System.out.println("----------------------功率补算统计数据获取多日数据结束------------------------------------" + powers.size());

        if(powers.size()>0){
            System.out.println("^^^^^^^开始获取功率统计最值^^^^^^^" + sdf_sec.format(new Date()));
            List<D_powerPo> mostValue = getMostValue(powers, 288);

            System.out.println("^^^^^^^获取功率统计最值结束^^^^^^^" + sdf_sec.format(new Date()));
            curve.insertStatistics("dsm_msta_power_d", mostValue);
        }
        System.out.println("---------------------功率补算统计(插入操作)数据结束------------------------------------" + sdf_sec.format(new Date()));
    }

    /**
     * 启动时计算
     * 单日功率统计数据
     */
    public void workStatistic(){
        curve.moveCuvePc("c_mmxu_power_c", "dsm_mmxu_power_c", "power_", 1);
        curve.startDay("dsm_mmxu_power_c", "power_", "352");
        curve.startDay("dsm_mmxu_power_c", "power_", "350");
        curve.startDay("dsm_mmxu_power_c", "power_", "330");

        //多日统计数据 最大值 最小值
        int point = DateUtil.getPoint();
        String date_now = DateUtil.getDateNow();
//        查询所有曲线数据
//        List<D_powerCurvePo> powers = mapper.selectByDate("2018-01-05");
        List<D_powerCurvePo> powers = mapper.selectByDate(DateUtil.getDateNow());
        if(powers.size()>0){
            List<D_powerPo> mostValue = getMostValue(powers, point);
            curve.insertStatistics("dsm_msta_power_d", mostValue);
        }
    }

    /**
     * 每月头3天各算几次 如2018/4/1,2018/4/2,2018/4/3
     * 每天00:03分执行
     * 上月统计数据按sys_id,ld_id,data_date,type累加
     */
    @Scheduled(cron = "0 11 16 1-30 3 ? ")
    public void workMonth() {
        curve.monthStatistic("dsm_msta_power_m", "dsm_msta_power_d", "max_power", "min_power", "max_power_time", "min_power_time", "avg_power");
    }

    /**
     * 每年前3天各计算一次
     * 如2019/1/1,2019/1/2,2019/1/3,如2020/1/1,2020/1/2,2020/1/3
     */
    @Scheduled(cron = "0 23 0 1-3 1 ? ")
    public void workYear(){
        curve.yearStatistic("dsm_msta_power_y", "dsm_msta_power_m", "max_power", "min_power", "max_power_time", "min_power_time", "avg_power");
    }

    /**
     * 多月统计数据
     * @param start_date
     * @param end_date
     */
    public void workMultiMonth(String start_date, String end_date) {
        curve.multiMonth("dsm_msta_power_m", "dsm_msta_power_d", "max_power", "min_power", "max_power_time", "min_power_time", "avg_power", start_date, end_date);
    }

    /**
     * 多年数据统计
     * @param start_date
     * @param end_date
     */
    public void workMultiYear(String start_date, String end_date){
        curve.multiYear("dsm_msta_power_y", "dsm_msta_power_m", "max_power", "min_power", "max_power_time", "min_power_time", "avg_power", start_date, end_date);
    }

    /**
     * 获取最值对象
     * @param powers
     * @param point
     * @return
     */
    private List<D_powerPo> getMostValue(List<D_powerCurvePo> powers, int point){
//        DecimalFormat df = new DecimalFormat("00.0000");
        List<Future<D_powerPo>> results = new ArrayList();
        List<D_powerPo> list = new ArrayList();

        ExecutorService executorService = Executors.newFixedThreadPool(20);
        System.out.println("---------创建线程池-------------------");

        for(int j=0; j<powers.size(); j++){
            D_powerCurvePo power = powers.get(j);
            Future<D_powerPo> future = executorService.submit(new MostValue(power, point));
//            System.out.println("---------线程" + j + "执行完毕-------------------");
            results.add(future);
        }
        executorService.shutdown();

        for(Future<D_powerPo> fus : results) {
            try {
                list.add(fus.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        System.out.println("---------线程全部执行完毕-------------------" + results.size() + "--list--" + list.size());
        return list;
    }


}

class MostValue implements Callable<D_powerPo>{

    DecimalFormat df = new DecimalFormat("00.0000");

    D_powerCurvePo power;
    int point;

    public MostValue(D_powerCurvePo t, int point){
        power = t;
        this.point = point;
    }

    @Override
    public D_powerPo call() throws Exception {

        Double temp = 0d;
        Double max = 0d;
        Double min = 0d;
        Double avg = 0d;

        int max_point = 1;
        int min_point = 1;

        for(int i=1; i<=point; i++){
            Map<String, Object> map = new HashMap<String, Object>();
            String evl = "power.getPower" + i + "()";
            map.put("power", power);
            Object tempB = DateUtil.convertToCode(evl, map);
            if(tempB!=null && tempB!=""){
                temp = Double.parseDouble(tempB.toString());
                if(i==1){
                    min = temp;
                }
                if(max<temp){
                    max = temp;
                    max_point = i;
                }
                else if(min>temp){
                    min = temp;
                    min_point = i;
                }
                avg += temp;
            }
        }
        D_powerPo po = new D_powerPo();
        po.setMaxPower(Double.valueOf(df.format(max)));
        po.setMinPower(Double.valueOf(df.format(min)));
        String data_date = power.getDataDate();
        po.setMaxPowerTime(DateUtil.pointCovert2Date(data_date, max_point));
        po.setMinPowerTime(DateUtil.pointCovert2Date(data_date, min_point));
//        System.out.println("线程计算时间" + data_date + "--" + max_point + "--" + min_point);
        po.setAvgPower(Double.valueOf(df.format(avg / point)));
        po.setDataType(power.getDataType());
        po.setDsmLdId(power.getLdId());
        po.setDsmSysId(power.getSysId());
        po.setDataDate(power.getDataDate());
        return po;
    }
}