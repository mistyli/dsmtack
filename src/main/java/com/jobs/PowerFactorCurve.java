package com.jobs;

import com.dao.bean.D_factorCurvePo;
import com.dao.bean.D_powerPo;
import com.dao.mapper.CurveDao;
import com.dao.mapper.D_factorCurvePoMapper;
import com.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by misty on 2018/3/26.
 * 功率因数计算
 */
@Component
public class PowerFactorCurve implements BaseCurve {

    @Autowired
    CurveDao curve;

    @Autowired
    D_factorCurvePoMapper mapper;

    /**
     * 功率因素
     */
    @Scheduled(cron = "0 1/5 * * * ? ")
    public void work(){
//        curve.insertPowerCurve("c_mmxu_pfactor_c", "pf_", "352");
//        curve.insertPowerCurve("c_mmxu_pfactor_c", "pf_", "350");
//        curve.insertPowerCurve("c_mmxu_pfactor_c", "pf_", "330");
        System.out.println("--------------功率因数--------------");

//        List<D_factorCurvePo> factors = mapper.selectFactorCurveByDate("2018-01-05");
//        if(factors.size() > 0){
//            int point = DateUtil.getPoint();
//            curve.insertStatistics("dsm_msta_factor_d", getMostValue(factors, point));
//        }
        curve.statisticUpdate("dsm_msta_factor_d", "c_mmxu_pfactor_c","pf_", "max_factor", "min_factor", "max_factor_time", "min_factor_time", "avg_factor");
    }

    /**
     * 启动时调用
     */
    @Override
    public void workStatistic() {
//        List<D_factorCurvePo> factors = mapper.selectFactorCurveByDate("2018-01-05");
        List<D_factorCurvePo> factors = mapper.selectFactorCurveByDate(DateUtil.getDateNow());
        if(factors.size() > 0){
            int point = DateUtil.getPoint();
            curve.insertStatistics("dsm_msta_factor_d", getMostValue(factors, point));
        }
    }

    /**
     * 补算时调用
     * @param start_date
     * @param end_date
     */
    @Override
    @Async
    public void workStatistic(String start_date, String end_date) {
        System.out.println("----------------------补算统计数据------------------------------------功率因数");

        List<D_factorCurvePo> factors = mapper.selectFactorCurveBy2Date(start_date, end_date);
        if(factors.size() > 0){
            curve.insertStatistics("dsm_msta_factor_d", getMostValue(factors, 288));
        }
    }

    /**
     * 每月的前三天各执行一次
     */
    @Scheduled(cron = "0 0 0 1-3 1/1 ? ")
    public void workMonth(){
        curve.monthStatistic("dsm_msta_factor_m", "dsm_msta_factor_d","max_factor", "min_factor", "max_factor_time", "min_factor_time", "avg_factor");
    }

    @Override
    public void workMultiMonth(String start_date, String end_date) {
        curve.multiMonth("dsm_msta_factor_m", "dsm_msta_factor_d","max_factor", "min_factor", "max_factor_time", "min_factor_time", "avg_factor", start_date, end_date);
    }

    /**
     * 每年前3天各计算一次
     * 如2019/1/1,2019/1/2,2019/1/3,如2020/1/1,2020/1/2,2020/1/3
     */
    @Scheduled(cron = "0 0 0 1-3 1 ? ")
    public void workYear(){
        curve.monthStatistic("dsm_msta_factor_y","dsm_msta_factor_m", "max_factor", "min_factor", "max_factor_time", "min_factor_time", "avg_factor");
    }

    @Override
    public void workMultiYear(String start_date, String end_date) {
        curve.multiYear("dsm_msta_factor_y","dsm_msta_factor_m", "max_factor", "min_factor", "max_factor_time", "min_factor_time", "avg_factor", start_date, end_date);
    }

    /**
     * 获取最值对象
     * @param powers
     * @param point
     * @return
     */
    private List<D_powerPo> getMostValue(List<D_factorCurvePo> powers, int point){
        List<D_powerPo> list = new ArrayList<>();
        List<Future<D_powerPo>> results = new ArrayList();
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
            D_factorCurvePo power = powers.get(j);
            Future<D_powerPo> future = executorService.submit(new PfcMostValue(power, point));
            results.add(future);
//            max = 0d;
//            min = 0d;
//            avg = 0d;

//            for(int i=1; i<=point; i++){
//                Map<String,Object> map = new HashMap<String, Object>();
//                String evl = "power.getPower" + i + "()";
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

class PfcMostValue implements Callable<D_powerPo>{
    DecimalFormat df = new DecimalFormat("00.0000");

    D_factorCurvePo power;
    int point;

    public PfcMostValue(D_factorCurvePo t, int point){
        this.power = t;
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
