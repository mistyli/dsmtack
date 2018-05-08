package com.jobs;

import com.dao.bean.D_powerPo;
import com.dao.bean.D_vUnbalanceCurvePo;
import com.dao.mapper.CurveDao;
import com.dao.mapper.D_vUnbalanceCurvePoMapper;
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
 * 电压不平衡曲线
 */
@Component
public class UnbalanceCurve implements BaseCurve {

    @Autowired
    CurveDao curve;

    @Autowired
    D_vUnbalanceCurvePoMapper mapper;

    @Scheduled(cron = "0 1/5 * * * ? ")
    public void work(){
//        curve.insertPowerCurve("c_msqi_u_unbalance_c", "u_unbalance_", "352");//C_MSQI_U_UNBALANCE_C
//        curve.insertPowerCurve("c_msqi_u_unbalance_c", "u_unbalance_", "350");//C_MSQI_U_UNBALANCE_C
//        curve.insertPowerCurve("c_msqi_u_unbalance_c", "u_unbalance_", "330");//C_MSQI_U_UNBALANCE_C
        System.out.println("----------电压不平衡--------------");

//        List<D_vUnbalanceCurvePo> vbps = mapper.selectVunbalanceByDate("2018-01-05");
//        if(vbps.size() > 0){
//            int point = DateUtil.getPoint();
//            curve.insertStatistics("dsm_msqi_u_unbalance_d", getMostValue(vbps, point));
//        }
        curve.statisticUpdate("dsm_msqi_u_unbalance_d","c_msqi_u_unbalance_c","u_unbalance_", "max_u_unbalance", "min_u_unbalance", "max_u_unbalance_t", "min_u_unbalance_t", "avg_u_unbalance");
    }

    @Override
    public void workStatistic() {
//        List<D_vUnbalanceCurvePo> vbps = mapper.selectVunbalanceByDate("2018-01-05");
        List<D_vUnbalanceCurvePo> vbps = mapper.selectVunbalanceByDate(DateUtil.getDateNow());
        if(vbps.size() > 0){
            int point = DateUtil.getPoint();
            curve.insertStatistics("dsm_msqi_u_unbalance_d", getMostValue(vbps, point));
        }
    }

    @Override
    @Async
    public void workStatistic(String start_date, String end_date) {
        System.out.println("----------------------补算统计数据------------------------------------电压不平衡");
        List<D_vUnbalanceCurvePo> vbps = mapper.selectVunbalanceBy2Date(start_date, end_date);
        if(vbps.size() > 0){
            int point = DateUtil.getPoint();
            curve.insertStatistics("dsm_msqi_u_unbalance_d", getMostValue(vbps, point));
        }
    }

    /**
     * 每月的前三天各执行一次
     */
    @Scheduled(cron = "0 0 0 1-3 1/1 ? ")
    public void workMonth(){
        curve.monthStatistic("dsm_msqi_u_unbalance_m","dsm_msqi_u_unbalance_d", "max_u_unbalance", "min_u_unbalance", "max_u_unbalance_t", "min_u_unbalance_t", "avg_u_unbalance");
    }

    @Override
    public void workMultiMonth(String start_date, String end_date) {
        curve.multiMonth("dsm_msqi_u_unbalance_m","dsm_msqi_u_unbalance_d", "max_u_unbalance", "min_u_unbalance", "max_u_unbalance_t", "min_u_unbalance_t", "avg_u_unbalance", start_date, end_date);
    }

    /**
     * 每年前3天各计算一次
     * 如2019/1/1,2019/1/2,2019/1/3,如2020/1/1,2020/1/2,2020/1/3
     */
    @Scheduled(cron = "0 0 0 1-3 1 ? ")
    public void workYear(){
        curve.monthStatistic("dsm_msqi_u_unbalance_y","dsm_msqi_u_unbalance_m", "max_u_unbalance", "min_u_unbalance", "max_u_unbalance_t", "min_u_unbalance_t", "avg_u_unbalance");
    }

    @Override
    public void workMultiYear(String start_date, String end_date) {
        curve.multiYear("dsm_msqi_u_unbalance_y","dsm_msqi_u_unbalance_m", "max_u_unbalance", "min_u_unbalance", "max_u_unbalance_t", "min_u_unbalance_t", "avg_u_unbalance", start_date, end_date);
    }

    /**
     * 获取最值对象
     * @param powers
     * @param point
     * @return
     */
    private List<D_powerPo> getMostValue(List<D_vUnbalanceCurvePo> powers, int point){
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
            D_vUnbalanceCurvePo power = powers.get(j);
            Future<D_powerPo> future = executorService.submit(new UbcMostValue(power, point));
            results.add(future);
//            max = 0d;
//            min = 0d;
//            avg = 0d;
//            for(int i=1; i<=point; i++){
//                Map<String,Object> map = new HashMap<String, Object>();
//                String evl = "power.getuUnbalance" + i + "()";
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

class UbcMostValue implements Callable<D_powerPo>{
    DecimalFormat df = new DecimalFormat("00.0000");

    D_vUnbalanceCurvePo power;
    int point;

    public UbcMostValue(D_vUnbalanceCurvePo t, int point){
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
            String evl = "power.getuUnbalance" + i + "()";
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
        return null;
    }
}