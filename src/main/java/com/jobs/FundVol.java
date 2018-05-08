package com.jobs;

import com.dao.bean.D_fundVolCurvePo;
import com.dao.bean.D_powerPo;
import com.dao.mapper.CurveDao;
import com.dao.mapper.D_fundVolCurvePoMapper;
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
 * 基波电压
 */

@Component
public class FundVol implements BaseCurve {

    @Autowired
    CurveDao curve;

    @Autowired
    D_fundVolCurvePoMapper mapper;

    /**
     * 每5分钟更新一波日统计表的最值
     */
    @Scheduled(cron = "0 1/5 * * * ? ")
    public void work(){
        curve.statisticUpdate("dsm_mhai_fundvol_d", "c_mhai_fundvol_c", "fund_vol_", "max_fund_vol", "min_fund_vol", "max_fund_vol_time", "min_fund_vol_time", "avg_fund_vol");
    }

    /**
     * 起始计算日统计数据
     */
    @Override
    public void workStatistic() {
        String date_now = DateUtil.getDateNow();
        List<D_fundVolCurvePo> funds =  mapper.selectFundCurveByDate(date_now);
        if(funds.size() > 0){
            int point = DateUtil.getPoint();
            curve.insertStatistics("dsm_mhai_fundvol_d", getMostValue(funds, point));
        }
    }

    /**
     * 补算多日统计数据
     * @param start_date
     * @param end_date
     */
    @Override
    @Async
    public void workStatistic(String start_date, String end_date) {
        SimpleDateFormat sdf_sec = new SimpleDateFormat("yyyy-MM-dd HH;mm:ss");
        System.out.println("----------------------基波电压补算获取曲线数据开始------------------------------------" + sdf_sec.format(new Date()));
        List<D_fundVolCurvePo> curs =  mapper.selectFundCurveBy2Date(start_date, end_date);
        System.out.println("----------------------基波电压补算统计获取曲线数据结束------------------------------------" + curs.size());
        if(curs.size() > 0){
            System.out.println("----------------------基波电压补算最值获取开始------------------------------------" + sdf_sec.format(new Date()));
            curve.insertStatistics("dsm_mhai_fundvol_d", getMostValue(curs, 288));
        }
        System.out.println("----------------------基波电压补算统计数据(最值获取及插入操作)结束------------------------------------" + sdf_sec.format(new Date()));
    }

    /**
     * 定时计算月统计数据
     */
    @Scheduled(cron = "0 0 0 1-3 1/1 ? ")
    @Override
    public void workMonth() {
        curve.monthStatistic("dsm_mhai_fundvol_m", "dsm_mhai_fundvol_d", "max_fund_vol", "min_fund_vol", "max_fund_vol_time", "min_fund_vol_time", "avg_fund_vol");
    }

    /**
     * 补算 多月统计数据
     * @param start_date
     * @param end_date
     */
    @Override
    public void workMultiMonth(String start_date, String end_date) {
        curve.multiMonth("dsm_mhai_fundvol_m", "dsm_mhai_fundvol_d", "max_fund_vol", "min_fund_vol", "max_fund_vol_time", "min_fund_vol_time", "avg_fund_vol", start_date, end_date);
    }

    /**
     * 定时计算年统计数据
     */
    @Scheduled(cron = "0 0 0 1-3 1 ? ")
    @Override
    public void workYear() {
        curve.monthStatistic("dsm_mhai_fundvol_y","dsm_mhai_fundvol_m", "max_fund_vol", "min_fund_vol", "max_fund_vol_time", "min_fund_vol_time", "avg_fund_vol");
    }

    /**
     * 补算 多年统计数据
     * @param start_date
     * @param end_date
     */
    @Override
    public void workMultiYear(String start_date, String end_date) {
        curve.multiYear("dsm_mhai_fundvol_y","dsm_mhai_fundvol_m", "max_fund_vol", "min_fund_vol", "max_fund_vol_time", "min_fund_vol_time", "avg_fund_vol", start_date, end_date);
    }

    private List<D_powerPo> getMostValue(List<D_fundVolCurvePo> funds, int point){
        DecimalFormat df = new DecimalFormat("00.0000");
        List<D_powerPo> list = new ArrayList<>();

        List<Future<D_powerPo>> results = new ArrayList();

        ExecutorService executorService = Executors.newFixedThreadPool(20);
        System.out.println("---------创建线程池-------------------");

        for(int j=0; j<funds.size(); j++){
            D_fundVolCurvePo fund = funds.get(j);
            Future<D_powerPo> future = executorService.submit(new MostValueFund(fund, point));
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
        System.out.println("---------基波电压线程全部执行完毕-------------------" + results.size() + "--list--" + list.size());
        return list;
    }

}

class MostValueFund implements Callable<D_powerPo> {

    DecimalFormat df = new DecimalFormat("00.0000");
    D_fundVolCurvePo fund;
    int point;

    public MostValueFund(D_fundVolCurvePo fund, int point){
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
            String evl = "fund.getFundVol" + i + "()";
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