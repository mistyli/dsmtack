package com.jobs;

import com.dao.bean.D_curCurvePo;
import com.dao.bean.D_powerPo;
import com.dao.mapper.CurveDao;
import com.dao.mapper.D_curCurvePoMapper;
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
 * Created by misty on 2018/3/26.
 * 电流曲线
 */
@Component
public class CurCurve {

    @Autowired
    CurveDao curve;

    @Autowired
    D_curCurvePoMapper mapper;

    @Scheduled(cron = "0 1/5 * * * ? ")
    public void work(){
//        curve.insertPowerCurve("c_mmxu_cur_c", "cur_", "352");//C_MMXU_CUR_C
//        curve.insertPowerCurve("c_mmxu_cur_c", "cur_", "350");//C_MMXU_CUR_C
//        curve.insertPowerCurve("c_mmxu_cur_c", "cur_", "330");//C_MMXU_CUR_C
        int point = DateUtil.getPoint();
//        曲线数据从c_mmxu_cur_c移动到dsm_mmxu_cur_c
        if(point<=1){
            curve.moveCuvePc("c_mmxu_cur_c", "dsm_mmxu_cur_c", "cur_", 3);
            curve.insertFirstPoint("dsm_msta_cur_d","dsm_mmxu_cur_c","cur_", "max_cur", "min_cur", "max_cur_time", "min_cur_time", "avg_cur", 3);
        }
        else{
            curve.updatePointCurveForPc("c_mmxu_cur_c", "dsm_mmxu_cur_c", "cur_", 3);
//            curve.statisticUpdate("dsm_msta_cur_d","dsm_mmxu_cur_c","cur_", "max_cur", "min_cur", "max_cur_time", "min_cur_time", "avg_cur");
            curve.statisticUpdateDsm("dsm_msta_cur_d","dsm_mmxu_cur_c","cur_", "max_cur", "min_cur", "max_cur_time", "min_cur_time", "avg_cur");
        }
        System.out.println("----------电流--------------");

//        String date_now = DateUtil.getDateNow();
//        List<D_curCurvePo> curs =  mapper.selectCurByDate("2018-01-05");
//        if(curs.size() > 0){
//            int point = DateUtil.getPoint();
//            curve.insertStatistics("dsm_msta_cur_d", getMostValue(curs, point));
//        }
    }

    /**
     * 多日统计数据 最大值 最小值
     * @param start_date
     * @param end_date
     */
    @Async
    public void workStatistic(String start_date, String end_date){
        //先将电流曲线数据从c_mmxu_cur_c移动到dsm_mmxu_cur_c
        curve.calCurvePcMultiDays("c_mmxu_cur_c", "dsm_mmxu_cur_c", "cur_", start_date, end_date, 3);

        SimpleDateFormat sdf_sec = new SimpleDateFormat("yyyy-MM-dd HH;mm:ss");
        System.out.println("----------------------电流补算获取曲线数据开始------------------------------------" + sdf_sec.format(new Date()));
        List<D_curCurvePo> curs =  mapper.selectCurBy2Date(start_date, end_date);
        System.out.println("----------------------电流补算统计获取曲线数据结束------------------------------------" + curs.size());
        if(curs.size() > 0){
            System.out.println("----------------------电流补算最值获取开始------------------------------------" + sdf_sec.format(new Date()));
            curve.insertStatistics("dsm_msta_cur_d", getMostValue(curs, 288));
        }
        System.out.println("----------------------电流补算统计数据(最值获取及插入操作)结束------------------------------------" + sdf_sec.format(new Date()));
    }

    /**
     * 应用启动计算电量统计数据
     */
    public void workStatistic(){
        curve.moveCuvePc("c_mmxu_cur_c", "dsm_mmxu_cur_c", "cur_", 3);
        String date_now = DateUtil.getDateNow();
//        List<D_curCurvePo> curs =  mapper.selectCurByDate("2018-01-05");
        List<D_curCurvePo> curs =  mapper.selectCurByDate(date_now);
        if(curs.size() > 0){
            int point = DateUtil.getPoint();
            curve.insertStatistics("dsm_msta_cur_d", getMostValue(curs, point));
        }
    }

    /**
     * 每月前三天每天计算一次
     */
    @Scheduled(cron = "0 0 0 1-3 1/1 ? ")
    public void workMonth(){
        curve.monthStatistic("dsm_msta_cur_m","dsm_msta_cur_d", "max_cur", "min_cur", "max_cur_time", "min_cur_time", "avg_cur");
    }

    /**
     * 每年的第一个月的前三天每天计算一次
     */
    @Scheduled(cron = "0 0 0 1-3 1 ? ")
    public void workYear(){
        curve.monthStatistic("dsm_msta_cur_y","dsm_msta_cur_m", "max_cur", "min_cur", "max_cur_time", "min_cur_time", "avg_cur");
    }

    /**
     * 多月数据
     * @param start_date
     * @param end_date
     */
    public void workMultiMonth(String start_date, String end_date) {
        curve.multiMonth("dsm_msta_cur_m","dsm_msta_cur_d", "max_cur", "min_cur", "max_cur_time", "min_cur_time", "avg_cur", start_date, end_date);
    }

    /**
     * 多年
     * @param start_date
     * @param end_date
     */
    public void workMultiYear(String start_date, String end_date){
        curve.multiYear("dsm_msta_cur_y","dsm_msta_cur_m", "max_cur", "min_cur", "max_cur_time", "min_cur_time", "avg_cur", start_date, end_date);
    }

    private List<D_powerPo> getMostValue(List<D_curCurvePo> curs, int point){
        DecimalFormat df = new DecimalFormat("00.0000");
        List<D_powerPo> list = new ArrayList<>();

        List<Future<D_powerPo>> results = new ArrayList();

        ExecutorService executorService = Executors.newFixedThreadPool(20);
        System.out.println("---------创建线程池-------------------");

        for(int j=0; j<curs.size(); j++){
            D_curCurvePo cur = curs.get(j);
            Future<D_powerPo> future = executorService.submit(new MostValueCur(cur, point));
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
        System.out.println("---------电流线程全部执行完毕-------------------" + results.size() + "--list--" + list.size());
        return list;
    }

}

class MostValueCur implements Callable<D_powerPo>{

    DecimalFormat df = new DecimalFormat("00.0000");
    D_curCurvePo cur;
    int point;

    public MostValueCur(D_curCurvePo cur, int point){
        this.cur = cur;
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
            String evl = "cur.getCur" + i + "()";
            map.put("cur", cur);
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
        po.setMaxPowerTime(DateUtil.pointCovert2Date(cur.getDataDate(), max_point));
        po.setMinPowerTime(DateUtil.pointCovert2Date(cur.getDataDate(), min_point));
        po.setAvgPower(Double.valueOf(df.format(avg / point)));
        po.setDataType(cur.getDataType());
        po.setDsmLdId(cur.getLdId());
        po.setDsmSysId(cur.getSysId());
        po.setDataDate(cur.getDataDate());
        return po;
    }
}
