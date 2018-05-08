package com.jobs;

import com.dao.bean.D_iUnbalanceCurvePo;
import com.dao.bean.D_powerPo;
import com.dao.mapper.CurveDao;
import com.dao.mapper.D_iUnbalanceCurvePoMapper;
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
 * 电流不平衡曲线
 */
@Component
public class CurUnbalanceCurve {

    @Autowired
    CurveDao curve;

    @Autowired
    D_iUnbalanceCurvePoMapper mapper;

    @Scheduled(cron = "0 1/5 * * * ? ")
    public void work(){
//        curve.insertPowerCurve("c_msqi_i_unbalance_c", "i_unbalance_", "352");//C_MSQI_I_UNBALANCE_C
//        curve.insertPowerCurve("c_msqi_i_unbalance_c", "i_unbalance_", "350");//C_MSQI_I_UNBALANCE_C
//        curve.insertPowerCurve("c_msqi_i_unbalance_c", "i_unbalance_", "330");//C_MSQI_I_UNBALANCE_C
        System.out.println("----------电流不平衡--------------");

//        List<D_iUnbalanceCurvePo> icps = mapper.selectIunbalanceByDate("2018-01-05");
//        if(icps.size() > 0){
//            int point = DateUtil.getPoint();
//            curve.insertStatistics("dsm_msqi_i_unbalance_d", getMostValue(icps, point));
//        }
        curve.statisticUpdate("dsm_msqi_i_unbalance_d", "c_msqi_i_unbalance_c", "i_unbalance_","max_i_unbalance", "min_i_unbalance", "max_i_unbalance_t", "min_i_unbalance_t", "avg_i_unbalance");
    }

    /**
     * 启动时调用
     */
    public void workStatistic(){

//        List<D_iUnbalanceCurvePo> icps = mapper.selectIunbalanceByDate("2018-01-05");
        List<D_iUnbalanceCurvePo> icps = mapper.selectIunbalanceByDate(DateUtil.getDateNow());
        if(icps.size() > 0){
            int point = DateUtil.getPoint();
            curve.insertStatistics("dsm_msqi_i_unbalance_d", getMostValue(icps, point));
        }
    }

    /**
     * 补算时计算日统计数据
     * @param start_date
     * @param end_date
     */
    @Async
    public void workStatistic(String start_date, String end_date){
        System.out.println("----------------------补算统计数据------------------------------------电流不平衡");
        List<D_iUnbalanceCurvePo> icps = mapper.selectIunbalanceBy2Date(start_date, end_date);
        if(icps.size() > 0){
            int point = DateUtil.getPoint();
            curve.insertStatistics("dsm_msqi_i_unbalance_d", getMostValue(icps, point));
        }
    }

    @Scheduled(cron = "0 0 0 1-3 1/1 ? ")
    public void workMonth(){
        curve.monthStatistic("dsm_msqi_i_unbalance_m", "dsm_msqi_i_unbalance_d","max_i_unbalance", "min_i_unbalance", "max_i_unbalance_t", "min_i_unbalance_t", "avg_i_unbalance");
    }

    /**
     * 每年的第一个月的前三天每天计算一次
     */
    @Scheduled(cron = "0 0 0 1-3 1 ? ")
    public void workYear(){
        curve.monthStatistic("dsm_msqi_i_unbalance_y","dsm_msqi_i_unbalance_m", "max_i_unbalance", "min_i_unbalance", "max_i_unbalance_t", "min_i_unbalance_t", "avg_i_unbalance");
    }

    /**
     * 多月计算
     * @param start_date
     * @param end_date
     */
    public void workMultiMonth(String start_date, String end_date) {
        curve.multiMonth("dsm_msqi_i_unbalance_m", "dsm_msqi_i_unbalance_d","max_i_unbalance", "min_i_unbalance", "max_i_unbalance_t", "min_i_unbalance_t", "avg_i_unbalance", start_date, end_date);
    }

    /**
     * 多年数据计算
     * @param start_date
     * @param end_date
     */
    public void workMultiYear(String start_date, String end_date){
        curve.multiYear("dsm_msqi_i_unbalance_y","dsm_msqi_i_unbalance_m", "max_i_unbalance", "min_i_unbalance", "max_i_unbalance_t", "min_i_unbalance_t", "avg_i_unbalance", start_date, end_date);
    }

    /**
     * 获取最值对象
     * @param powers
     * @param point
     * @return
     */
    private List<D_powerPo> getMostValue(List<D_iUnbalanceCurvePo> powers, int point){
//        DecimalFormat df = new DecimalFormat("00.0000");
        List<Future<D_powerPo>> results = new ArrayList();
        List<D_powerPo> list = new ArrayList<>();
//        Double temp = 0d;
//
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
        System.out.println("---------创建线程池-------------------");
        for(int j=0; j<powers.size(); j++){
            D_iUnbalanceCurvePo power = powers.get(j);
            Future<D_powerPo> future = executorService.submit(new CucMostValue(power, point));
            results.add(future);
//            for(int i=1; i<=point; i++){
//                Map<String,Object> map = new HashMap<String, Object>();
//                String evl = "power.getiUnbalance" + i + "()";
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

class CucMostValue implements Callable<D_powerPo>{
    DecimalFormat df = new DecimalFormat("00.0000");
    D_iUnbalanceCurvePo power;
    int point;

    public CucMostValue(D_iUnbalanceCurvePo t, int point){
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
            String evl = "power.getiUnbalance" + i + "()";
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