package com.jobs;

import com.dao.bean.D_powerPo;
import com.dao.bean.D_volRatioCurvePo;
import com.dao.mapper.CurveDao;
import com.dao.mapper.D_volRatioCurvePoMapper;
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
 * 谐波电压含有率
 */
@Component
public class VolRatio implements BaseCurve {

    @Autowired
    CurveDao curve;

    @Autowired
    D_volRatioCurvePoMapper mapper;

    /**
     * 每5分钟更新一波日统计表的最值
     */
    @Scheduled(cron = "0 1/5 * * * ? ")
    public void work(){
        curve.statisticUpdate("dsm_mhai_volratio_d", "c_mhai_volratio_c", "har_volratio_", "max_har_volratio", "min_har_volratio", "max_har_volratio_time", "min_har_volratio_time", "avg_har_volratio");
    }

    @Override
    public void workStatistic() {
        String date_now = DateUtil.getDateNow();
        List<D_volRatioCurvePo> volRatios =  mapper.selectVolRatioCurveByDate(date_now);
        if(volRatios.size() > 0){
            int point = DateUtil.getPoint();
            curve.insertStatistics("dsm_mhai_volratio_d", getMostValue(volRatios, point));
        }
    }

    @Override
    @Async
    public void workStatistic(String start_date, String end_date) {
        SimpleDateFormat sdf_sec = new SimpleDateFormat("yyyy-MM-dd HH;mm:ss");
        System.out.println("----------------------谐波电压补算获取曲线数据开始------------------------------------" + sdf_sec.format(new Date()));
        List<D_volRatioCurvePo> curs =  mapper.selectVolRatioCurveBy2Date(start_date, end_date);
        System.out.println("----------------------谐波电压补算统计获取曲线数据结束------------------------------------" + curs.size());
        if(curs.size() > 0){
            System.out.println("----------------------谐波电压补算最值获取开始------------------------------------" + sdf_sec.format(new Date()));
            curve.insertStatistics("dsm_mhai_volratio_d", getMostValue(curs, 288));
        }
        System.out.println("----------------------谐波电压补算统计数据(最值获取及插入操作)结束------------------------------------" + sdf_sec.format(new Date()));
    }

    @Scheduled(cron = "0 0 0 1-3 1/1 ? ")
    @Override
    public void workMonth() {
        curve.monthStatistic("dsm_mhai_volratio_m", "dsm_mhai_volratio_d", "max_har_volratio", "min_har_volratio", "max_har_volratio_time", "min_har_volratio_time", "avg_har_volratio");
    }

    @Override
    public void workMultiMonth(String start_date, String end_date) {
        curve.multiMonth("dsm_mhai_volratio_m", "dsm_mhai_volratio_d", "max_har_volratio", "min_har_volratio", "max_har_volratio_time", "min_har_volratio_time", "avg_har_volratio", start_date, end_date);
    }

    @Scheduled(cron = "0 0 0 1-3 1 ? ")
    @Override
    public void workYear() {
        curve.monthStatistic("dsm_mhai_volratio_y", "dsm_mhai_volratio_m", "max_har_volratio", "min_har_volratio", "max_har_volratio_time", "min_har_volratio_time", "avg_har_volratio");
    }

    @Override
    public void workMultiYear(String start_date, String end_date) {
        curve.multiYear("dsm_mhai_volratio_y", "dsm_mhai_volratio_m", "max_har_volratio", "min_har_volratio", "max_har_volratio_time", "min_har_volratio_time", "avg_har_volratio", start_date, end_date);
    }

    private List<D_powerPo> getMostValue(List<D_volRatioCurvePo> volRatios, int point){
        List<D_powerPo> list = new ArrayList<>();

        List<Future<D_powerPo>> results = new ArrayList();

        ExecutorService executorService = Executors.newFixedThreadPool(20);
        System.out.println("---------创建线程池-------------------");

        for(int j=0; j<volRatios.size(); j++){
            D_volRatioCurvePo volRatio = volRatios.get(j);
            Future<D_powerPo> future = executorService.submit(new MostValueVolRatio(volRatio, point));
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
        System.out.println("---------谐波电压线程全部执行完毕-------------------" + results.size() + "--list--" + list.size());
        return list;
    }
}

class MostValueVolRatio implements Callable<D_powerPo> {

    DecimalFormat df = new DecimalFormat("00.0000");
    D_volRatioCurvePo fund;
    int point;

    public MostValueVolRatio(D_volRatioCurvePo fund, int point){
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
            String evl = "fund.getHarVolratio" + i + "()";
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
