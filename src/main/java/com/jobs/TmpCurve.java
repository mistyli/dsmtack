package com.jobs;

import com.dao.bean.D_powerCurvePo;
import com.dao.bean.D_powerPo;
import com.dao.bean.D_tmpCurvePo;
import com.dao.mapper.CurveDao;
import com.dao.mapper.D_tmpCurvePoMapper;
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
 * Created by misty on 2018/4/4.
 * 温度曲线和统计
 */
@Component
public class TmpCurve implements BaseCurve {

    @Autowired
    CurveDao curve;

    @Autowired
    D_tmpCurvePoMapper mapper;

    @Scheduled(cron = "0 1/5 * * * ? ")
    public void work(){
        System.out.println("--------------------温度统计数据----------------------");
        curve.statisticUpdate("dsm_msta_tmp_d", "c_stmp_tmp_c", "tmp_", "max_tmp", "min_tmp", "max_tmp_time", "min_tmp_time", "avg_tmp");
    }

    /**
     * 启动时调用
     */
    @Override
    public void workStatistic() {
//        List<D_tmpCurvePo> tmps = mapper.selectByDate("2018-01-10");
        List<D_tmpCurvePo> tmps = mapper.selectByDate(DateUtil.getDateNow());
        if(tmps.size() > 0){
            int point = DateUtil.getPoint();
            curve.insertStatistics("dsm_msta_tmp_d", getMostValue(tmps, point));
        }
    }

    @Override
    @Async
    public void workStatistic(String start_date, String end_date) {
        System.out.println("----------------------补算统计数据------------------------------------温度");

        List<D_tmpCurvePo> tmps = mapper.selectBy2Date(start_date, end_date);
        System.out.println("----------------------温度补算统计数据------------------------------------" + tmps.size());
        if(tmps.size() > 0){
            curve.insertStatistics("dsm_msta_tmp_d", getMostValue(tmps, 288));
        }
    }

    @Scheduled(cron = "0 11 16 1-30 3 ? ")
    @Override
    public void workMonth() {
        curve.monthStatistic("dsm_msta_tmp_m", "dsm_msta_tmp_d",  "max_tmp", "min_tmp", "max_tmp_time", "min_tmp_time", "avg_tmp");
    }

    @Override
    public void workMultiMonth(String start_date, String end_date) {
        curve.multiMonth("dsm_msta_tmp_m", "dsm_msta_tmp_d",  "max_tmp", "min_tmp", "max_tmp_time", "min_tmp_time", "avg_tmp", start_date, end_date);
    }

    @Scheduled(cron = "0 23 0 1-3 1 ? ")
    @Override
    public void workYear() {
        curve.yearStatistic("dsm_msta_tmp_y", "dsm_msta_tmp_m",  "max_tmp", "min_tmp", "max_tmp_time", "min_tmp_time", "avg_tmp");
    }

    @Override
    public void workMultiYear(String start_date, String end_date) {
        curve.multiYear("dsm_msta_tmp_y", "dsm_msta_tmp_m", "max_tmp", "min_tmp", "max_tmp_time", "min_tmp_time", "avg_tmp", start_date, end_date);
    }

    /**
     * 获取最值对象
     * @param powers
     * @param point
     * @return
     */
    private List<D_powerPo> getMostValue(List<D_tmpCurvePo> powers, int point){
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
            D_tmpCurvePo power = powers.get(j);
            Future<D_powerPo> future = executorService.submit(new TmpMostValue(power, point));
            results.add(future);
//            max = 0d;
//            min = 0d;
//            avg = 0d;
//            for(int i=1; i<=point; i++){
//                Map<String,Object> map = new HashMap<String, Object>();
//                String evl = "power.getTmp" + i + "()";
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

class TmpMostValue implements Callable<D_powerPo>{
    DecimalFormat df = new DecimalFormat("00.0000");

    D_tmpCurvePo power;
    int point;

    public TmpMostValue(D_tmpCurvePo t, int point){
        this.power = t;
        this.point = point;
    }

    @Override
    public D_powerPo call() throws Exception {
        DecimalFormat df = new DecimalFormat("00.0000");
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
            String evl = "power.getTmp" + i + "()";
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