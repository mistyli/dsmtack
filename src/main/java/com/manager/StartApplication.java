package com.manager;

import com.jobs.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by misty on 2018/4/3.
 */
@Component
public class StartApplication implements InitializingBean {

    @Autowired
    PowerCurve power;

    @Autowired
    CurCurve cur;

    @Autowired
    CurUnbalanceCurve ubc;

    @Autowired
    EnergyCurve ec;

    @Autowired
    FreqCurve fc;

    @Autowired
    MhaiCurCurve mcc;

    @Autowired
    MhaiCurDistCurve mcdc;

    @Autowired
    MhalVolCurve mvc;

    @Autowired
    MharVolDistCurve mvdc;

    @Autowired
    PowerFactorCurve pfc;

    @Autowired
    UnbalanceCurve vbc;

    @Autowired
    TmpCurve tmp;

    @Autowired
    FundVol fundVol;

    @Autowired
    FundCur fundCur;

    @Autowired
    CurRatio curRatio;

    @Autowired
    VolRatio volRatio;

    @Autowired
    VolCurve volCurve;

    @Override
    public void afterPropertiesSet() throws Exception {
        power.workStatistic();//功率曲线及统计数据
        cur.workStatistic();//电流统计数据
        volCurve.workStatistic();//电压曲线和日统计数据
        ubc.workStatistic();//电流不平衡统计数据
        ec.workStatistic();//电量曲线数据
        fc.workStatistic();//频率统计数据
        mcc.workStatistic();//电流谐波与间谐波统计数据
        mcdc.workStatistic();//电流畸变统计数据
        mvc.workStatistic();//电压谐波与间谐波统计数据
        mvdc.workStatistic();//电压畸变统计数据
        pfc.workStatistic();//功率因数统计数据
        vbc.workStatistic();//电压不平衡统计数据
        tmp.workStatistic();//温度统计数据
        fundVol.workStatistic();//基波电压统计数据
        fundCur.workStatistic();//基波电流统计数据
        curRatio.workStatistic();//电流谐波
        volRatio.workStatistic();//电压谐波
    }

}
