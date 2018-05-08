package com.dao.provider;

import com.dao.bean.D_powerPo;
import com.util.DateUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by misty on 2018/3/27.
 */
public class CurveDaoProvider {

    private static final Logger log = LogManager.getLogger(CurveDaoProvider.class.getName());

    /**
     * 仅电量用
     * 电量数据计算 当前点-前一个点=当前点 然后乘以C_EMEAS_PARAM_TG表的pt、ct参数(现在临时用表C_LD_EMEAS的pt、ct)
     * 启动时更新一天的数据点 启动后每次更新一个点
     * @param sourceTable 源表
     * @param targetTable 目标表
     * @param fieldName 字段
     * @return 源表数据计算后存入目标表
     */
    public String moveCurve(String sourceTable, String targetTable, String fieldName){

        int point = DateUtil.getPoint();

        StringBuilder sql = new StringBuilder();
        StringBuilder sbf = new StringBuilder();
        StringBuilder sub = new StringBuilder();
        sub.append(fieldName).append("1");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String data_date = sdf.format(new Date());
//        data_date = "2018-01-02";

//        sbf.append("replace into ").append(targetTable);
        sbf.append("(cc1.").append(fieldName).append("1-cc2.").append(fieldName).append("288)*ems.pt*ems.ct ");
        if(point > 1){
            sub.append(",");
            sbf.append(",");
            for(int i=2; i<point; i++){
                sbf.append("(cc1.").append(fieldName).append(i).append("-cc1.").append(fieldName).append(i - 1).append(")*ems.pt*ems.ct ").append(fieldName).append(i).append(",");
                sub.append(fieldName).append(i).append(",");
            }
            sbf.append("(cc1.").append(fieldName).append(point).append("-cc1.").append(fieldName).append(point-1).append(")*ems.pt*ems.ct ").append(fieldName).append(point);
            sub.append(fieldName).append(point);
        }

//        sql.append("replace into ").append(targetTable).append("(").append("dsm_sys_id,dsm_ld_id,data_date,data_type,data_interval,data_point,")
//                .append(sub).append(") select cc1.sys_id,cc1.ld_id,cc1.data_date,cc1.data_type,cc1.data_interval," +
//                " cc1.data_point,(cc1.").append(fieldName).append("1-cc2.").append(fieldName).append("288)*ems.pt*ems.ct,").append(sbf)
//                .append(" from C_MMTR_ENERGY_C cc1 left join C_MMTR_ENERGY_C cc2 " +
//                        "on cc1.sys_id=cc2.sys_id and cc1.ld_id=cc2.ld_id and cc1.data_type=cc2.data_type " +
//                        " and DATE_ADD(cc2.data_date,INTERVAL 1 day)=cc1.data_date " +
//                        " left join C_LD_EMEAS ems on cc1.sys_id=ems.sys_id and cc1.ld_id=ems.ld_id and ems.is_valid=1 " +
//                        " where cc1.data_date='").append(data_date).append("'");
        sql.append("replace into ").append(targetTable).append("(").append("dsm_sys_id,dsm_ld_id,data_date,data_type,data_interval,data_point,")
                .append(sub).append(") select cc1.sys_id,cc1.ld_id,cc1.data_date,cc1.data_type,cc1.data_interval," +
                " cc1.data_point,").append(sbf)
                .append(" from C_MMTR_ENERGY_C cc1 left join C_MMTR_ENERGY_C cc2 " +
                        "on cc1.sys_id=cc2.sys_id and cc1.ld_id=cc2.ld_id and cc1.data_type=cc2.data_type " +
                        " and DATE_ADD(cc2.data_date,INTERVAL 1 day)=cc1.data_date " +
                        " left join C_LD_EMEAS ems on cc1.sys_id=ems.sys_id and cc1.ld_id=ems.ld_id and ems.is_valid=1 " +
                        " where cc1.data_date='").append(data_date).append("'");
        log.info("--电量曲线数据移动--" + sourceTable + "-" + targetTable);
        return sql.toString();
    }

    /**
     * 只用于更新电量
     * 用于更新电量曲线的5分钟点
     * 服务启动之后每5分钟更新一个曲线点
     * @return
     */
    public String updatePointCurve(String sourceTable, String targetTable, String fieldName){
        int point = DateUtil.getPoint();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String data_date = sdf.format(new Date());

//        日期切换 插入数据
        if(point<=1){
            return moveCurve(sourceTable, targetTable, fieldName);
        }
        StringBuilder sbu = new StringBuilder();
        sbu.append("update ").append(targetTable).append(" cc, ( " +
                " select cc1.sys_id,cc1.ld_id,cc1.data_date,cc1.data_type,(cc1.").append(fieldName).append(point)
                .append("-cc1.").append(fieldName).append(point-1).append(")*ems.pt*ems.ct ").append(fieldName).append(point)
                .append(" from ").append(sourceTable).append(" cc1 " +
                " left join C_LD_EMEAS ems on cc1.sys_id=ems.sys_id and cc1.ld_id=ems.ld_id and ems.is_valid=1 " +
                " where data_date='").append(data_date).append("') cc2 set cc.")
                .append(fieldName).append(point).append("=cc2.").append(fieldName).append(point)
                .append(" where cc.dsm_sys_id=cc2.sys_id and cc.dsm_ld_id=cc2.ld_id " +
                "and cc.data_date=cc2.data_date and cc.data_type=cc2.data_type");
        log.info("--更新电量曲线数据--" + sourceTable + "-" + targetTable);
        return sbu.toString();
    }

    /**
     * 功率、电压、电流曲线每5分钟更新一个点
     * @param sourceTable
     * @param targetTable
     * @param fieldName
     * @param type 1.功率 2.电压 3.电流
     * @return
     */
    public String updatePointCurveForPc(String sourceTable, String targetTable, String fieldName, int type){
        StringBuilder sbu = new StringBuilder();
        StringBuilder sbf = new StringBuilder();
        StringBuilder sub = new StringBuilder();
        int point = DateUtil.getPoint();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String data_date = sdf.format(new Date());

        if(type==1){
            sbf.append("cc.").append(fieldName).append(point).append("*ems.pt*ems.ct ").append(fieldName).append(point);
            sbu.append(fieldName).append(point);
        }
        else if(type==2){
            sbf.append("cc.").append(fieldName).append(point).append("*ems.pt ").append(fieldName).append(point);
            sbu.append(fieldName).append(point);
        }
        else if(type==3){
            sbf.append("cc.").append(fieldName).append(point).append("*ems.ct ").append(fieldName).append(point);
            sbu.append(fieldName).append(point);
        }

        sub.append("update ").append(targetTable).append(" cc1, ( " +
                " select cc.sys_id,cc.ld_id,cc.data_date,cc.data_type,").append(sbf).append(" from ")
                .append(sourceTable).append(" cc left join C_LD_EMEAS ems on cc.sys_id=ems.sys_id and cc.ld_id=ems.ld_id " +
                " and ems.is_valid=1 where cc.data_date='").append(data_date).append("' ) cc2 set cc1.").append(sbu).append("=cc2.").append(sbu)
                .append(" where cc1.dsm_sys_id=cc2.sys_id and cc1.dsm_ld_id=cc2.ld_id " +
                        " and cc1.data_date=cc2.data_date and cc1.data_type=cc2.data_type");
        log.info("--5分钟更新一个曲线点--" + sourceTable + "-" + targetTable);
        return sub.toString();
    }

    /**
     * 日期切换到新的一天时 要重新插入统计数据
     * 对所有统计数据 都要重新插入一条数据 以第一个点作为最大最小值及平均值
     *
     * @param sta_name 统计表
     * @param curve_name 曲线表
     * @param field_name 字段
     * @param max
     * @param min
     * @param max_time
     * @param min_time
     * @param avg
     * @return
     */
    public String insertFirstPoint(String sta_name, String curve_name, String field_name, String max, String min, String max_time, String min_time, String avg, int type){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = sdf.format(new Date());
//        StringBuilder sub = new StringBuilder();
        StringBuilder sbu = new StringBuilder();
//        if(type==1||type==4){
//            sub.append(" cc.").append(field_name).append("1*ems.pt*ems.ct ");
//        }
//        else if(type==2){
//            sub.append("cc.").append(field_name).append("1*ems.pt ");
//        }
//        else if(type==3){
//            sub.append("cc.").append(field_name).append("1*ems.ct ");
//        }
//        else {//除功率 电量 电压 电流 其他都不用乘以pt或ct
//            sbu.append("insert into ").append(sta_name).append(" (dsm_sys_id,dsm_ld_id,data_date,data_type,")
//                    .append(max).append(",").append(max_time).append(",").append(min).append(",").append(min_time).append(",").append(avg)
//                    .append(") select dsm_sys_id,dsm_ld_id,data_date,data_type,").append(field_name).append("1,'").append(dateStr)
//                    .append("',").append(field_name).append("1,'").append(dateStr).append("',").append(field_name).append("1 from ").append(curve_name)
//                    .append(" where data_date='").append(dateStr.substring(0,10)).append("'");
//            System.out.println("--插入第一个点不乘pt,ct--" + sbu);
//            return sbu.toString();
//        }
//        sbu.append("insert into ").append(sta_name).append(" (dsm_sys_id,dsm_ld_id,data_date,data_type,")
//                .append(max).append(",").append(max_time).append(",").append(min).append(",").append(min_time).append(",").append(avg)
//                .append(") select cc.dsm_sys_id,cc.dsm_ld_id,cc.data_date,cc.data_type,").append(sub).append(",'").append(dateStr)
//                .append("',").append(sub).append(",'").append(dateStr).append("',").append(sub).append(" from ").append(curve_name)
//                .append(" cc left join C_LD_EMEAS ems on cc.dsm_sys_id=ems.sys_id and cc.dsm_ld_id=ems.ld_id and ems.is_valid=1" +
//                        " where data_date='").append(dateStr.substring(0,10)).append("'");
//        System.out.println("--插入第一个点pt,ct--" + sbu);
        sbu.append("insert into ").append(sta_name).append(" (dsm_sys_id,dsm_ld_id,data_date,data_type,")
                .append(max).append(",").append(max_time).append(",").append(min).append(",").append(min_time).append(",").append(avg)
                .append(") select dsm_sys_id,dsm_ld_id,data_date,data_type,").append(field_name).append("1,'").append(dateStr)
                .append("',").append(field_name).append("1,'").append(dateStr).append("',").append(field_name).append("1 from ").append(curve_name)
                .append(" where data_date='").append(dateStr.substring(0,10)).append("'");
        log.info("--插入第一个点--" + curve_name + "-" + sta_name);
        return sbu.toString();
    }

    /**
     * 功率乘以pt、ct,电压乘pt,电流乘ct 电量需要减所以把电量单独分开
     * @param sourceTable
     * @param targetTable
     * @param fieldName
     * @param type 1.功率 2.电压 3.电流
     * @return
     */
    public String moveCuvePc(String sourceTable, String targetTable, String fieldName, int type){
        StringBuilder sbu = new StringBuilder();
        StringBuilder sbf = new StringBuilder();
        StringBuilder sub = new StringBuilder();
        int point = DateUtil.getPoint();

        Calendar cal = new GregorianCalendar();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String data_date = sdf.format(new Date());
//        data_date = "2018-01-04";
        if(type==1){
            for(int i=1; i< point; i++){
                sbf.append("cc.").append(fieldName).append(i).append("*ems.pt*ems.ct ").append(fieldName).append(i).append(",");
                sbu.append(fieldName).append(i).append(",");
            }
            sbf.append("cc.").append(fieldName).append(point).append("*ems.pt*ems.ct ").append(fieldName).append(point);
            sbu.append(fieldName).append(point);
        }
        else if(type==2){
            for(int i=1; i< point; i++){
                sbf.append("cc.").append(fieldName).append(i).append("*ems.pt ").append(fieldName).append(i).append(",");
                sbu.append(fieldName).append(i).append(",");
            }
            sbf.append("cc.").append(fieldName).append(point).append("*ems.pt ").append(fieldName).append(point);
            sbu.append(fieldName).append(point);
        }
        else if(type==3){
            for(int i=1; i< point; i++){
                sbf.append("cc.").append(fieldName).append(i).append("*ems.ct ").append(fieldName).append(i).append(",");
                sbu.append(fieldName).append(i).append(",");
            }
            sbf.append("cc.").append(fieldName).append(point).append("*ems.ct ").append(fieldName).append(point);
            sbu.append(fieldName).append(point);
        }

        sub.append("replace into ").append(targetTable).append(" (dsm_sys_id,dsm_ld_id,data_date,data_type,data_interval,data_point,")
                .append(sbu)
                .append(") select cc.sys_id,cc.ld_id,cc.data_date,cc.data_type,cc.data_interval,cc.data_point,").append(sbf)
                .append(" from ").append(sourceTable).append(" cc left join C_LD_EMEAS ems on cc.sys_id=ems.sys_id " +
                " and cc.ld_id=ems.ld_id and ems.is_valid=1 where cc.data_date='").append(data_date).append("'");
        log.info("--功率电压电流--" + sourceTable + "-" + targetTable);
        return sub.toString();
    }


    /**
     * 曲线表5分钟更新一个点 所以后面的352/350/330也每5分钟更新一个点
     * 功率电量乘以pt ct 该方法只有电量和功率使用
     * @param table_name
     * @param field_name
     * @param code
     * @return
     */
    public String powerCal(String table_name, String field_name, String code){

        String data_date = DateUtil.getDateNow();
        int point = DateUtil.getPoint();

        String sql = "update " + table_name + " c, " +
                " ( " +
                " select vv.calobj_id,vv.data_type,vv.data_date,sum(ifnull(vv." + field_name + point + ",0)*if(vv.cal_sign=0, 1, -1)*vv.cal_ratio) " + field_name + point +
                " from " +
                " ( " +
                " select grp.caltype_code,grp.calobj_id,grp.bycaltype_code,grp.bycalobj_id,grp.cal_sign, " +
                " grp.cal_ratio,c.data_type,c.data_date,c." + field_name + point +
                " from dsm_grp_obj_calculation_info grp inner join " + table_name + " c " +
                " on grp.bycalobj_id=c.dsm_ld_id and grp.caltype_code=" + code + " where c.data_date='" + data_date + "' " +
                " ) vv " +
                " group by calobj_id,data_type " +
                " ) g set c." + field_name + point + "=g." + field_name + point +
                " where c.dsm_ld_id=g.calobj_id and c.data_type=g.data_type and c.data_date=g.data_date ";
        log.info("---------更新曲线数据--------" + table_name);
        return sql;
    }


    /**
     * 更新日统计数据 曲线数据每更新一个point 则统计数据也更新最大值最小值等数据
     * 日统计数据电量 功率需要乘以pt ct，电压需要乘以pt，电流需要乘以ct  -----------------是不是加个字段判断一下是功率、电量、电流、电压 ；算了，还是重写
     * @param sta_name 日统计表名
     * @param curve_name 日统计表来源——曲线表名
     * @param field_name 曲线表字段名
     * @return
     */
    public String statisticUpdate(String sta_name, String curve_name, String field_name, String max, String min, String max_time, String min_time, String avg){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        int point = DateUtil.getPoint();
        String now = sdf.format(new Date());
        String m_date = DateUtil.pointCovert2Date(now, point);
        log.info("-------------日统计数据更新最大最小值发生时间-----------" + now + "****" + m_date + "*****************" + point);
//        String data_date = now;

        StringBuilder sbu = new StringBuilder();
        sbu.append("update ").append(sta_name).append(" cd,(select sys_id, ld_id, data_date, data_type, ").append(field_name).append(point)
                .append(" from ").append(curve_name).append(" where data_date='").append(now).append("') dc set cd.").append(max).append("=if(cd.").append(max).append(">dc.")
                .append(field_name).append(point).append(", cd.").append(max).append(", dc.").append(field_name).append(point)
                .append("),cd.").append(max_time).append("=if(cd.").append(max).append(">dc.")
                .append(field_name).append(point).append(", cd.").append(max_time).append(", '").append(m_date).append("'),cd.").append(min).append("=if(cd.").append(min).append("<dc.")
                .append(field_name).append(point).append(", cd.").append(min).append(", dc.").append(field_name).append(point).append("),cd.").append(min_time).append("=if(cd.")
                .append(min).append("<dc.").append(field_name).append(point).append(", cd.").append(min_time).append(", '").append(m_date).append("'),cd.")
                .append(avg).append("=(cd.").append(avg).append(" * ").append(point)
                .append(" + dc.").append(field_name).append(point).append(")/").append(point+1)
                .append(" where cd.dsm_sys_id=dc.sys_id and cd.dsm_ld_id=dc.ld_id and cd.data_date=dc.data_date and cd.data_type=dc.data_type");
        log.info("--------日统计数据更新------" + sta_name);
//        System.out.println("--------日统计数据更新------" + sbu);
        return sbu.toString();
    }

    /**
     * 更新日统计数据 曲线数据每更新一个point 则统计数据也更新最大值最小值等数据
     * 日统计数据电量 功率需要乘以pt ct，电压需要乘以pt，电流需要乘以ct  -----------------是不是加个字段判断一下是功率、电量、电流、电压 ；算了，还是重写
     * @param sta_name 日统计表名
     * @param curve_name 日统计表来源——曲线表名
     * @param field_name 曲线表字段名
     * @return
     */
    public String statisticUpdateDsm(String sta_name, String curve_name, String field_name, String max, String min, String max_time, String min_time, String avg){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        int point = DateUtil.getPoint();
        String now = sdf.format(new Date());
        String m_date = DateUtil.pointCovert2Date(now, point);
        log.info("-------------日统计数据更新最大最小值发生时间-----------" + now + "****" + m_date + "*****************" + point);

        StringBuilder sbu = new StringBuilder();
        sbu.append("update ").append(sta_name).append(" cd,(select dsm_sys_id, dsm_ld_id, data_date, data_type, ").append(field_name).append(point)
                .append(" from ").append(curve_name).append(" where data_date='").append(now).append("') dc set cd.").append(max).append("=if(cd.").append(max).append(">dc.")
                .append(field_name).append(point).append(", cd.").append(max).append(", dc.").append(field_name).append(point)
                .append("),cd.").append(max_time).append("=if(cd.").append(max).append(">dc.")
                .append(field_name).append(point).append(", cd.").append(max_time).append(", '").append(m_date).append("'),cd.").append(min).append("=if(cd.").append(min).append("<dc.")
                .append(field_name).append(point).append(", cd.").append(min).append(", dc.").append(field_name).append(point).append("),cd.").append(min_time).append("=if(cd.")
                .append(min).append("<dc.").append(field_name).append(point).append(", cd.").append(min_time).append(", '").append(m_date).append("'),cd.")
                .append(avg).append("=(cd.").append(avg).append(" * ").append(point)
                .append(" + dc.").append(field_name).append(point).append(")/").append(point+1)
                .append(" where cd.dsm_sys_id=dc.dsm_sys_id and cd.dsm_ld_id=dc.dsm_ld_id and cd.data_date=dc.data_date and cd.data_type=dc.data_type");
        log.info("--------日统计数据更新------" + sta_name);
//        System.out.println("--------日统计数据更新------" + sbu);
        return sbu.toString();
    }

    /**
     * 取出一个月的数据比较 最大值 最小值 平均值
     * 计算上月的统计数据
     * @return
     */
    public String monthStatistic(String table_name,String d_table_name, String max, String min, String max_time, String min_time, String avg){

        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        cal.add(Calendar.MONTH, -1);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        String data_date = year + "-" + month;
//        System.out.println("--" + data_date);

        StringBuilder sbr = new StringBuilder();
        sbr.append("replace into ").append(table_name).append(" select cc.dsm_sys_id,cc.dsm_ld_id,'").append(data_date).append("-01',cc.data_type,cc.max_power,dd.").append(max_time)
                .append(",cc.min_power,ee.").append(min_time).append(",cc.avg_power/31 from ").append(" (select dsm_sys_id,dsm_ld_id,data_type,max(").append(max)
                .append(") max_power,min(").append(min).append(") min_power,sum(").append(avg).append(") avg_power from ").append(d_table_name)
                .append(" where DATE_FORMAT(data_date, '%Y-%m')='").append(data_date).append("' group by dsm_sys_id,dsm_ld_id,data_type) cc ").append(" left join ")
                .append(d_table_name).append(" dd on cc.dsm_sys_id=dd.dsm_sys_id and cc.dsm_ld_id=dd.dsm_ld_id and cc.data_type=dd.data_type and cc.max_power=dd.").append(max)
                .append(" left join ").append(d_table_name)
                .append(" ee on cc.dsm_sys_id=ee.dsm_sys_id and cc.dsm_ld_id=ee.dsm_ld_id and cc.data_type=ee.data_type and cc.min_power=ee.").append(min);
//        String sql = "replace into " + table_name +
//                " select cc.dsm_sys_id,cc.dsm_ld_id,'2018-01-01',cc.data_type,cc.max_power,dd." + max_time + ",cc.min_power,ee." + min_time + ",cc.avg_power/31 from " +
//                " (select dsm_sys_id,dsm_ld_id,data_type,max(" + max + ") max_power,min(" + min + ") min_power,sum(" + avg + ") avg_power from " + table_name +
//                " where data_date>='2018-01-01' and data_date<='2018-01-31' group by dsm_sys_id,dsm_ld_id,data_type) cc " +
//                " left join " + table_name + " dd on cc.dsm_sys_id=dd.dsm_sys_id and cc.dsm_ld_id=dd.dsm_ld_id and cc.data_type=dd.data_type and cc.max_power=dd." + max +
//                " left join " + table_name + " ee on cc.dsm_sys_id=ee.dsm_sys_id and cc.dsm_ld_id=ee.dsm_ld_id and cc.data_type=ee.data_type and cc.min_power=ee." + min;
        log.info("----------上月统计数据-----------" + table_name);
        return sbr.toString();
    }

    /**
     * 计算年数据 从月数据来
     * @return
     */
    public String yearStatistic(String table_name,String m_table_name, String max, String min, String max_time, String min_time, String avg){

        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        cal.add(Calendar.YEAR, -1);//去年的数据
        int data_date = cal.get(Calendar.YEAR);
//        String start_date = year + "-01-01";
//        String end_date = year + "-12-31";
//        System.out.println(start_date + "--" + end_date);

        StringBuilder sbd = new StringBuilder();
        sbd.append("replace into ").append(table_name).append(" select cc.dsm_sys_id,cc.dsm_ld_id, '").append(data_date).append("-01-01',cc.data_type,cc.max_power,dd.").append(max_time)
                .append(", cc.min_power ,dd.").append(min_time).append(",cc.avg_power/12 from ")
                .append(" (select dsm_sys_id,dsm_ld_id,data_date,data_type,max(").append(max).append(") max_power,min(").append(min)
                .append(") min_power,sum(").append(avg).append(") avg_power ")
                .append(" from " + m_table_name + " where date_format(data_date,'%Y')='").append(data_date).append("' group by dsm_sys_id,dsm_ld_id,data_type) cc ")
                .append(" left join ").append(m_table_name)
                .append(" dd on cc.dsm_sys_id=dd.dsm_sys_id and cc.dsm_ld_id=dd.dsm_ld_id ")
                .append(" and cc.data_type=dd.data_type and cc.max_power=dd.").append(max)
                .append(" left join ").append(m_table_name).append(" ee on cc.dsm_sys_id=ee.dsm_sys_id and cc.dsm_ld_id=ee.dsm_ld_id ")
                .append(" and cc.data_type=ee.data_type and cc.min_power=dd.").append(min);

//        String sql = "replace into " + table_name +
//                " select cc.dsm_sys_id,cc.dsm_ld_id, '2018-01-01',cc.max_power,dd." + max_time + ", cc.min_power ,dd." + min_time + ",cc.avg_power from " +
//                " (select dsm_sys_id,dsm_ld_id,data_date,data_type,max(" + max + ") max_power,min(" + min + ") min_power,sum(" + avg + ") avg_power " +
//                " from " + table_name + " where data_date>='2018-01-01' and data_date<='2018-12-31' group by dsm_sys_id,dsm_ld_id,data_type) cc " +
//                " left join " + table_name + " dd on cc.dsm_sys_id=dd.dsm_sys_id and cc.dsm_ld_id=dd.dsm_ld_id " +
//                " and cc.data_type=dd.data_type and cc.max_power=dd." + max +
//                " left join " + table_name + " ee on cc.dsm_sys_id=ee.dsm_sys_id and cc.dsm_ld_id=ee.dsm_ld_id " +
//                " and cc.data_type=ee.data_type and cc.min_power=dd." + min;

        log.info("-----上年统计数据-------" + table_name);
        return sbd.toString();
    }


//    ---------------------------------------------以下内容只涉及电费----------------------------------------------------

    /**
     * 上日统计 日期相等
     * 多日补算 日期不等
     * 2018/05/08修改日电量统计数据c_mmtr_energy_d为dsm_mmtr_energy_d
     * @param start_date
     * @param end_date
     * @return
     */
    public String lastDayFee(String start_date, String end_date){
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        Calendar cal = new GregorianCalendar();
//        cal.setTime(new Date());
//        cal.add(Calendar.DATE, -1);
//        String data_date = sdf.format(cal.getTime());

        StringBuilder sbu = new StringBuilder();
        sbu.append("replace into dsm_msta_elec_fee_d " +
                "select egy.dsm_sys_id, egy.dsm_ld_id, egy.data_date, egy.data_type, " +
                " egy.energy_tot*(rate.fee_tip+rate.fee_peak+rate.fee_valley+rate.fee_normal) energy_tot, " +
                " egy.energy_fee_1*rate.fee_tip energy_fee_1, egy.energy_fee_2*rate.fee_peak energy_fee_2, " +
                " egy.energy_fee_3*rate.fee_valley energy_fee_3, egy.energy_fee_4*rate.fee_normal energy_fee_4 " +
                " from dsm_mmtr_energy_d egy left join dsm_a_ld_electricity ald " +
                " on egy.dsm_sys_id=ald.dsm_sys_id and egy.dsm_ld_id=ald.dsm_ld_id " +
                " and egy.data_date<=ald.disable_date and egy.data_date>=ald.enable_date " +
                " left join dsm_sys_electricity_rate rate on rate.fee_id=ald.fee_id and rate.dsm_sys_id=ald.dsm_sys_id and rate.is_valid=1 " +
                " where egy.data_date<='").append(end_date).append("' and egy.data_date>='").append(start_date).append("'");
        log.info("--电费--dsm_msta_elec_fee_d");
        return sbu.toString();
    }

    /**
     * 月份相同 计算上月统计数据
     * 月份不同 补算多月统计数据
     * @param start_month
     * @param end_month
     * @return
     */
    public String lastMonthFee(String start_month, String end_month){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        cal.add(Calendar.MONTH, -1);
        String data_date = sdf.format(cal.getTime());

        StringBuilder sbu = new StringBuilder();
        sbu.append("replace into dsm_msta_elec_fee_m " +
                " select dsm_sys_id,dsm_ld_id,data_date,data_type,sum(elec_fee),sum(elec_fee_t),sum(elec_fee_p),sum(elec_fee_n),sum(elec_fee_y) " +
                " from dsm_msta_elec_fee_d where DATE_FORMAT(data_date,'%Y-%m')<='").append(end_month).append("'").append(" and DATE_FORMAT(data_date,'%Y-%m')>='")
                .append(start_month).append("' group by dsm_sys_id,dsm_ld_id,DATE_FORMAT(data_date,'%Y-%m'),data_type");
        return sbu.toString();
    }

    public String lastYearFee(String start_year, String end_year){
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
//        Calendar cal = new GregorianCalendar();
//        cal.setTime(new Date());
//        cal.add(Calendar.MONTH, -1);
//        String data_date = sdf.format(cal.getTime());

        StringBuilder sbu = new StringBuilder();
        sbu.append("replace into dsm_msta_elec_fee_y " +
                " select dsm_sys_id,dsm_ld_id,data_date,data_type,sum(elec_fee),sum(elec_fee_t),sum(elec_fee_p),sum(elec_fee_n),sum(elec_fee_y) " +
                " from dsm_msta_elec_fee_m where DATE_FORMAT(data_date,'%Y')<='").append(end_year).append("'").append(" and DATE_FORMAT(data_date,'%Y')>='")
                .append(start_year).append("' group by dsm_sys_id,dsm_ld_id,DATE_FORMAT(data_date,'%Y'),data_type");
        return sbu.toString();
    }

//    -----------------------------------------------电压日统计数据移动-----------------------------------------------------------------------

    /**
     * 电压的日月统计数据来源于采集，要将采集数据移到dsm系统中
     * @param sourceTable
     * @param targetTable
     * @param start_date
     * @param end_date
     * @return
     */
    public String moveVolData(String sourceTable, String targetTable, String start_date, String end_date){
        StringBuilder sbu = new StringBuilder();
        sbu.append("replace into ").append(targetTable).append(" select vv.sys_id,vv.ld_id,vv.data_date,vv.data_type," +
                "vv.max_vol*ems.pt, vv.max_vol_time, vv.min_vol*ems.pt, vv.min_vol_time, vv.avg_vol*ems.pt from ").append(sourceTable)
                .append(" vv left join C_LD_EMEAS ems on vv.sys_id=ems.sys_id and vv.ld_id=ems.ld_id and ems.is_valid=1 where vv.data_date<='")
                .append(end_date).append("' ").append(" and ")
                .append(" vv.data_date>='").append(start_date).append("'");
        log.info("--电压日统计数据移动--" + sourceTable + "-" + targetTable);
        return sbu.toString();
    }

    //    -----------------------------------------------电能量日统计数据移动-----------------------------------------------------------------------

    /**
     * 电量的日月统计数据来源于采集，要将采集数据移到dsm系统中
     * @param sourceTable 源表
     * @param targetTable 目标表
     * @param start_date
     * @param end_date
     * @return
     */
    public String moveEnergyData(String sourceTable, String targetTable, String start_date, String end_date){
        StringBuilder sbu = new StringBuilder();
        sbu.append("replace into ").append(targetTable).append(" select vv.sys_id,vv.ld_id,vv.data_date,vv.data_type," +
                " vv.energy_tot*ems.pt*ems.ct,vv.energy_fee_1*ems.pt*ems.ct, " +
                " vv.energy_fee_2*ems.pt*ems.ct, vv.energy_fee_3*ems.pt*ems.ct, vv.energy_fee_4*ems.pt*ems.ct " +
                " from ").append(sourceTable).append(" vv left join C_LD_EMEAS ems on vv.sys_id=ems.sys_id and vv.ld_id=ems.ld_id and ems.is_valid=1 " +
                " where vv.data_date<='").append(end_date).append("' ").append(" and ")
                .append(" vv.data_date>='").append(start_date).append("'");
        log.info("--电量日统计数据移动--" + sourceTable + "-" + targetTable);
        return sbu.toString();
    }
}
