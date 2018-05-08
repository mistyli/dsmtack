package com.dao.mapper;

import com.dao.bean.D_powerPo;
import com.dao.bean.Field;
import com.dao.provider.BatchOperationProvider;
import com.dao.provider.CurveDaoProvider;
import com.dao.provider.StartProvider;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by misty on 2018/3/27.
 */
public interface CurveDao {

//    ------------------------定时任务运行时，只更新相关操作-----------------------------------------

    /**
     * 更新曲线操作
     * @param table_name
     * @param field_name
     * @param code
     */
    @InsertProvider(type= CurveDaoProvider.class, method = "powerCal")
    public void insertPowerCurve(String table_name, String field_name, String code);

    @InsertProvider(type= CurveDaoProvider.class, method = "updatePointCurveForPc")
    public void updatePointCurveForPc(String sourceTable, String targetTable, String fieldName, int type);

    @InsertProvider(type= CurveDaoProvider.class, method = "updatePointCurve")
    public void updatePointCurve(String sourceTable, String targetTable, String fieldName);

    @UpdateProvider(type= CurveDaoProvider.class, method = "statisticUpdate")
    public void statisticUpdate(String sta_name, String curve_name, String field_name, String max, String min, String max_time, String min_time, String avg);

    @UpdateProvider(type= CurveDaoProvider.class, method = "statisticUpdateDsm")
    public void statisticUpdateDsm(String sta_name, String curve_name, String field_name, String max, String min, String max_time, String min_time, String avg);


    @UpdateProvider(type= CurveDaoProvider.class, method = "insertFirstPoint")
    public void insertFirstPoint(String sta_name, String curve_name, String field_name, String max, String min, String max_time, String min_time, String avg, int type);

    @InsertProvider(type=CurveDaoProvider.class, method="monthStatistic")
    public void monthStatistic(String table_name,String d_table_name, String max, String min, String max_time, String min_time, String avg);

    @InsertProvider(type=CurveDaoProvider.class, method="yearStatistic")
    public void yearStatistic(String table_name, String m_table_name, String max, String min, String max_time, String min_time, String avg);

//    ---------------------------------------这里是分割线(应用启动调用)-------------------------------------------------------------

    @InsertProvider(type= CurveDaoProvider.class, method = "moveCurve")
    public void moveCurve(String sourceTable, String targetTable, String fieldName);

    @InsertProvider(type= CurveDaoProvider.class, method = "moveCuvePc")
    public void moveCuvePc(String sourceTable, String targetTable, String fieldName, int type);

    /**
     * 应用启动 插入曲线数据
     * @param table_name
     * @param field_name
     * @param code
     */
    @InsertProvider(type = StartProvider.class, method="startCurveDay")
    public void startDay(String table_name, String field_name, String code);

    /**
     * 启动时插入日统计数据
     * @param table_name
     * @param mostValue
     */
//    @InsertProvider(type=CurveDaoProvider.class, method="insertStatistics")
    @InsertProvider(type=StartProvider.class, method="insertStatistics")
    public void insertStatistics(String table_name, List<D_powerPo> mostValue);

//    ---------------------------------------这是分割线（涉及multi计算）--------------------------------------------------
    @InsertProvider(type = BatchOperationProvider.class, method="calEnergyMultiDays")
    public void multiDayEnergy(String start_date, String end_date);

    @InsertProvider(type = BatchOperationProvider.class, method="calCurveMultiDays")
    public void multiDay(String table_name, String field_name, String code, String start_date, String end_date);

    @InsertProvider(type = BatchOperationProvider.class, method="calMultiMonth")
    public void multiMonth(String table_name, String d_table_name, String max, String min, String max_time, String min_time, String avg, String start_month, String end_month);

    @InsertProvider(type = BatchOperationProvider.class, method="calMultiYear")
    public void multiYear(String table_name,String m_table_name, String max, String min, String max_time, String min_time, String avg, String start_year, String end_year);

    @InsertProvider(type = BatchOperationProvider.class, method="calCurvePcMultiDays")
    public void calCurvePcMultiDays(String sourceTable, String targetTable, String fieldName, String start_date, String end_date, int type);

//    -----------------------------------电费上日、上月、上年统计数据------------------------------------------------

    @InsertProvider(type= CurveDaoProvider.class, method = "lastDayFee")
    public void lastDayFee(String start_date, String end_date);

    @InsertProvider(type= CurveDaoProvider.class, method = "lastMonthFee")
    public void lastMonthFee(String start_month, String end_month);

    @InsertProvider(type= CurveDaoProvider.class, method = "lastYearFee")
    public void lastYearFee(String start_year, String end_year);

//    -----------------------------------------------电压数据移动------------------------------------------------------

    @InsertProvider(type= CurveDaoProvider.class, method = "moveVolData")
    public void moveVolData(String sourceTable, String targetTable, String start_date, String end_date);

    @InsertProvider(type= CurveDaoProvider.class, method = "moveEnergyData")
    public void moveEnergyData(String sourceTable, String targetTable, String start_date, String end_date);
}
