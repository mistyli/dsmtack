package com.jobs;

import com.dao.bean.D_curRatioCurvePo;
import com.dao.bean.D_powerPo;
import com.dao.mapper.CurveDao;
import com.dao.mapper.D_curRatioCurvePoMapper;
import com.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by misty on 2018/4/12.
 * 谐波电流
 */
@Component
public class CurRatio implements BaseCurve {

    @Autowired
    CurveDao curve;

    @Autowired
    D_curRatioCurvePoMapper mapper;

    @Scheduled(cron = "0 1/5 * * * ? ")
    public void work(){
        System.out.println("---------------------------------谐波电流日统计数据更新--------------------------");
        curve.statisticUpdate("dsm_mhai_curratio_d", "c_mhai_curratio_c", "har_curratio_", "max_har_curratio", "min_har_curratio", "max_har_curratio_time", "min_har_curratio_time", "avg_har_curratio");
    }

    @Override
    public void workStatistic() {
        String date_now = DateUtil.getDateNow();
        List<D_curRatioCurvePo> curRatios =  mapper.selectCurRationByDate(date_now);
        if(curRatios.size() > 0){
            int point = DateUtil.getPoint();
            curve.insertStatistics("dsm_mhai_curratio_d", getMostValue(curRatios, point));
        }
    }

    @Override
    @Async
    public void workStatistic(String start_date, String end_date) {
        SimpleDateFormat sdf_sec = new SimpleDateFormat("yyyy-MM-dd HH;mm:ss");
        System.out.println("----------------------谐波电流补算获取曲线数据开始------------------------------------" + sdf_sec.format(new Date()));
        List<D_curRatioCurvePo> curRatios =  mapper.selectCurRationBy2Date(start_date, end_date);
        System.out.println("----------------------谐波电流补算统计获取曲线数据结束------------------------------------" + curRatios.size());
        if(curRatios.size() > 0){
            System.out.println("----------------------谐波电流补算最值获取开始------------------------------------" + sdf_sec.format(new Date()));
            curve.insertStatistics("dsm_mhai_curratio_d", getMostValue(curRatios, 288));
        }
        System.out.println("----------------------谐波电流补算统计数据(最值获取及插入操作)结束------------------------------------" + sdf_sec.format(new Date()));
    }

    @Scheduled(cron = "0 0 0 1-3 1/1 ? ")
    @Override
    public void workMonth() {
        curve.monthStatistic("dsm_mhai_curratio_m", "dsm_mhai_curratio_d", "max_har_curratio", "min_har_curratio", "max_har_curratio_time", "min_har_curratio_time", "avg_har_curratio");
    }

    @Override
    public void workMultiMonth(String start_date, String end_date) {
        curve.multiMonth("dsm_mhai_curratio_m", "dsm_mhai_curratio_d", "max_har_curratio", "min_har_curratio", "max_har_curratio_time", "min_har_curratio_time", "avg_har_curratio", start_date, end_date);
    }

    @Scheduled(cron = "0 0 0 1-3 1 ? ")
    @Override
    public void workYear() {
        curve.monthStatistic("dsm_mhai_curratio_y", "dsm_mhai_curratio_m", "max_har_curratio", "min_har_curratio", "max_har_curratio_time", "min_har_curratio_time", "avg_har_curratio");
    }

    @Override
    public void workMultiYear(String start_date, String end_date) {
        curve.multiYear("dsm_mhai_curratio_y", "dsm_mhai_curratio_m", "max_har_curratio", "min_har_curratio", "max_har_curratio_time", "min_har_curratio_time", "avg_har_curratio", start_date, end_date);
    }

    private List<D_powerPo> getMostValue(List<D_curRatioCurvePo> curRatios, int point){
        List<D_powerPo> list = new ArrayList<>();

        List<Future<D_powerPo>> results = new ArrayList();

        ExecutorService executorService = Executors.newFixedThreadPool(20);
        System.out.println("---------创建线程池-------------------");

        for(int j=0; j<curRatios.size(); j++){
            D_curRatioCurvePo curRatio = curRatios.get(j);
            Future<D_powerPo> future = executorService.submit(new MostValueCurRatio(curRatio, point));
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
        System.out.println("---------谐波电流线程全部执行完毕-------------------" + results.size() + "--list--" + list.size());
        return list;
    }
}

class MostValueCurRatio implements Callable<D_powerPo>{
    DecimalFormat df = new DecimalFormat("00.0000");
    D_curRatioCurvePo fund;
    int point;

    public MostValueCurRatio(D_curRatioCurvePo fund, int point){
        this.fund = fund;
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

        max = 0d;
        min = 0d;
        avg = 0d;

        for(int i=1; i<=point; i++){
            Map<String,Object> map = new HashMap<String, Object>();
            String evl = "fund.getHarCurratio" + i + "()";
            map.put("fund", fund);
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
        String data_date = fund.getDataDate();
        po.setMaxPowerTime(DateUtil.pointCovert2Date(data_date, max_point));
        po.setMinPowerTime(DateUtil.pointCovert2Date(data_date, min_point));
        po.setAvgPower(Double.valueOf(df.format(avg / point)));
        po.setDataType(fund.getDataType());
        po.setDsmLdId(fund.getLdId());
        po.setDsmSysId(fund.getSysId());
        po.setDataDate(data_date);
        return po;
    }
}
