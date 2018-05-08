package com.jobs;

import com.dao.bean.D_freqCurvePo;
import com.dao.bean.D_powerCurvePo;
import com.dao.bean.D_powerPo;
import com.dao.mapper.CurveDao;
import com.dao.mapper.D_freqCurvePoMapper;
import com.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by misty on 2018/3/27.
 * 频率曲线
 */
@Component
public class FreqCurve implements BaseCurve {

    @Autowired
    CurveDao curve;

    @Autowired
    D_freqCurvePoMapper mapper;

    @Scheduled(cron = "0 1/5 * * * ? ")
    public void work(){
//        curve.insertPowerCurve("c_mmxu_freq_c", "freq_", "352");//C_MMXU_FREQ_C
//        curve.insertPowerCurve("c_mmxu_freq_c", "freq_", "350");//C_MMXU_FREQ_C
//        curve.insertPowerCurve("c_mmxu_freq_c", "freq_", "330");//C_MMXU_FREQ_C
        System.out.println("----------频率曲线--------------");

//        List<D_freqCurvePo> freqs = mapper.selectByPrimaryKey("2018-01-05");
//        if(freqs.size() > 0){
//            int point = DateUtil.getPoint();
//            curve.insertStatistics("dsm_msta_freq_d", getMostValue(freqs, point));
//        }
        curve.statisticUpdate("dsm_msta_freq_d", "c_mmxu_freq_c", "freq_", "max_freq", "min_freq", "max_freq_time", "min_freq_time", "avg_freq");
    }

    /**
     * 每月的前三天各执行一次
     */
    @Scheduled(cron = "0 0 0 1-3 1/1 ? ")
    public void workMonth(){
        curve.monthStatistic("dsm_msta_freq_m","dsm_msta_freq_d", "max_freq", "min_freq", "max_freq_time", "min_freq_time", "avg_freq");
    }

    @Override
    public void workMultiMonth(String start_date, String end_date) {
        curve.multiMonth("dsm_msta_freq_m","dsm_msta_freq_d", "max_freq", "min_freq", "max_freq_time", "min_freq_time", "avg_freq", start_date, end_date);
    }

    /**
     * 每年的第一个月的前三天每天计算一次
     */
    @Scheduled(cron = "0 0 0 1-3 1 ? ")
    public void workYear(){
        curve.monthStatistic("dsm_msta_freq_y","dsm_msta_freq_m", "max_freq", "min_freq", "max_freq_time", "min_freq_time", "avg_freq");
    }

    @Override
    public void workMultiYear(String start_date, String end_date) {
        curve.multiYear("dsm_msta_freq_y","dsm_msta_freq_m", "max_freq", "min_freq", "max_freq_time", "min_freq_time", "avg_freq", start_date, end_date);
    }

    /**
     * 启动计算曲线和统计数据
     */
    public void workStatistic(){
//        List<D_freqCurvePo> freqs = mapper.selectByPrimaryKey("2018-01-05");
        List<D_freqCurvePo> freqs = mapper.selectByPrimaryKey(DateUtil.getDateNow());

        if(freqs.size() > 0){
            int point = DateUtil.getPoint();
            curve.insertStatistics("dsm_msta_freq_d", getMostValue(freqs, point));
        }
    }

    @Override
    @Async
    public void workStatistic(String start_date, String end_date) {
        List<D_freqCurvePo> freqs = mapper.selectBy2Date(start_date, end_date);
        if(freqs.size() > 0){
            curve.insertStatistics("dsm_msta_freq_d", getMostValue(freqs, 288));
        }
        System.out.println("----------------------频率补算------------------------------------");
    }


    /**
     * 获取最值对象
     * @param powers
     * @param point
     * @return
     */
    private List<D_powerPo> getMostValue(List<D_freqCurvePo> powers, int point){
        List<Future<D_powerPo>> results = new ArrayList();
        List<D_powerPo> list = new ArrayList<>();
//        DecimalFormat df = new DecimalFormat("00.0000");
//        Double temp = 0d;
//        Double max = 0d;
//        Double min = 0d;
//        Double avg = 0d;
//        int max_point = 1;
//        int min_point = 1;
//
//        if(temp == null){
//            return null;
//        }
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        for(int j=0; j<powers.size(); j++){
            D_freqCurvePo power = powers.get(j);
            Future<D_powerPo> future = executorService.submit(new FreqMostValue(power, point));
            results.add(future);
//            max = 0d;
//            min = 0d;
//            avg = 0d;
//            for(int i=1; i<=point; i++){
//                Map<String,Object> map = new HashMap<String, Object>();
//                String evl = "power.getFreq" + i + "()";
//                map.put("power", power);
//                Object tempB = DateUtil.convertToCode(evl, map);
//                if(tempB!=null && tempB!=""){
//                    temp = Double.parseDouble(tempB.toString());
//                    if(i==1){
//                        min = temp;
//                    }
//                    if(max<temp){
//                        max = temp;
//                        max_point = i;
//                    }
//                    else if(min>temp){
//                        min = temp;
//                        min_point = i;
//                    }
//                    avg += temp;
//                }
//            }
//            D_powerPo po = new D_powerPo();
//            po.setMaxPower(Double.valueOf(df.format(max)));
//            po.setMinPower(Double.valueOf(df.format(min)));
//            po.setMaxPowerTime(DateUtil.pointCovert2Date(power.getDataDate(), max_point));
//            po.setMinPowerTime(DateUtil.pointCovert2Date(power.getDataDate(), min_point));
//            po.setAvgPower(Double.valueOf(df.format(avg / point)));
//            po.setDataType(power.getDataType());
//            po.setDsmLdId(power.getLdId());
//            po.setDsmSysId(power.getSysId());
//            po.setDataDate(power.getDataDate());
//            list.add(po);
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
        return list;
    }

}

class FreqMostValue implements Callable<D_powerPo>{
    DecimalFormat df = new DecimalFormat("00.0000");

    D_freqCurvePo power;
    int point;

    public FreqMostValue(D_freqCurvePo t, int point){
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

        if(temp == null){
            return null;
        }

        for(int i=1; i<=point; i++){
            Map<String,Object> map = new HashMap<String, Object>();
            String evl = "power.getFreq" + i + "()";
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
        po.setMaxPowerTime(DateUtil.pointCovert2Date(power.getDataDate(), max_point));
        po.setMinPowerTime(DateUtil.pointCovert2Date(power.getDataDate(), min_point));
        po.setAvgPower(Double.valueOf(df.format(avg / point)));
        po.setDataType(power.getDataType());
        po.setDsmLdId(power.getLdId());
        po.setDsmSysId(power.getSysId());
        po.setDataDate(power.getDataDate());

        return po;
    }
}
