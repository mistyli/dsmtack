package com.jobs;

import com.dao.bean.D_mhaiVolCurvePo;
import com.dao.bean.D_powerPo;
import com.dao.mapper.CurveDao;
import com.dao.mapper.D_mhaiVolCurvePoMapper;
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
 * Created by misty on 2018/3/27.
 * 谐波与间谐波电压曲线
 */
@Component
public class MhalVolCurve implements BaseCurve {

    @Autowired
    CurveDao curve;

    @Autowired
    D_mhaiVolCurvePoMapper mapper;

    @Scheduled(cron = "0 1/5 * * * ? ")
    public void work(){
//        curve.insertPowerCurve("c_mhai_vol_c", "har_vol_", "352");//C_MHAI_VOL_C
//        curve.insertPowerCurve("c_mhai_vol_c", "har_vol_", "350");//C_MHAI_VOL_C
//        curve.insertPowerCurve("c_mhai_vol_c", "har_vol_", "330");//C_MHAI_VOL_C
        System.out.println("----------谐波与间谐波电压--------------");

//        List<D_mhaiVolCurvePo> volcs = mapper.selectMhaiVolCurveByDate("2018-01-05");
//        if(volcs.size() > 0){
//            int point = DateUtil.getPoint();
//            curve.insertStatistics("dsm_mhai_vol_d", getMostValue(volcs, point));
//        }
        curve.statisticUpdate("dsm_mhai_vol_d","c_mhai_vol_c", "har_vol_", "max_har_vol", "min_har_vol", "max_har_vol_time", "min_har_vol_time", "avg_har_vol");
    }

    /**
     * 启动时调用
     */
    @Override
    public void workStatistic() {
//        List<D_mhaiVolCurvePo> volcs = mapper.selectMhaiVolCurveByDate("2018-01-05");
        List<D_mhaiVolCurvePo> volcs = mapper.selectMhaiVolCurveByDate(DateUtil.getDateNow());
        if(volcs.size() > 0){
            int point = DateUtil.getPoint();
            curve.insertStatistics("dsm_mhai_vol_d", getMostValue(volcs, point));
        }
    }

    @Override
    @Async
    public void workStatistic(String start_date, String end_date) {
        System.out.println("----------------------补算统计数据------------------------------------谐波与间谐波电压");

        List<D_mhaiVolCurvePo> volcs = mapper.selectMhaiVolCurveBy2Date(start_date, end_date);
        if(volcs.size() > 0){
            curve.insertStatistics("dsm_mhai_vol_d", getMostValue(volcs, 288));
        }
    }

    /**
     * 每月的前三天各执行一次
     */
    @Scheduled(cron = "0 0 0 1-3 1/1 ? ")
    public void workMonth(){
        curve.monthStatistic("dsm_mhai_vol_m","dsm_mhai_vol_d", "max_har_vol", "min_har_vol", "max_har_vol_time", "min_har_vol_time", "avg_har_vol");
    }

    @Override
    public void workMultiMonth(String start_date, String end_date) {
        curve.multiMonth("dsm_mhai_vol_m","dsm_mhai_vol_d", "max_har_vol", "min_har_vol", "max_har_vol_time", "min_har_vol_time", "avg_har_vol", start_date, end_date);
    }

    /**
     * 每年的第一个月的前三天每天计算一次
     */
    @Scheduled(cron = "0 0 0 1-3 1 ? ")
    public void workYear(){
        curve.monthStatistic("dsm_mhai_vol_y","dsm_mhai_vol_m", "max_har_vol", "min_har_vol", "max_har_vol_time", "min_har_vol_time", "avg_har_vol");
    }

    @Override
    public void workMultiYear(String start_date, String end_date) {
        curve.multiYear("dsm_mhai_vol_y","dsm_mhai_vol_m", "max_har_vol", "min_har_vol", "max_har_vol_time", "min_har_vol_time", "avg_har_vol", start_date, end_date);
    }

    /**
     * 获取最值对象
     * @param powers
     * @param point
     * @return
     */
    private List<D_powerPo> getMostValue(List<D_mhaiVolCurvePo> powers, int point){
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
//            max = 0d;
//            min = 0d;
//            avg = 0d;
            D_mhaiVolCurvePo power = powers.get(j);
            Future<D_powerPo> future = executorService.submit(new MhvMostValue(power, point));
            results.add(future);
//            for(int i=1; i<=point; i++){
//                Map<String,Object> map = new HashMap<String, Object>();
//                String evl = "power.getHarVol" + i + "()";
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

class MhvMostValue implements Callable<D_powerPo>{
    DecimalFormat df = new DecimalFormat("00.0000");

    D_mhaiVolCurvePo power;
    int point;

    public MhvMostValue(D_mhaiVolCurvePo t, int point){
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
            String evl = "power.getHarVol" + i + "()";
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
