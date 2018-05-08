package com.dao.provider;

import com.util.DateUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by misty on 2018/4/2.
 */
public class BatchOperationProvider {

    /**
     * 批量计算曲线多日数据
     * 曲线多日数据要走一波replace into操作
     * @param table_name 表名
     * @param field_name 字段名
     * @param code 330 350 352
     * @param start_date 其实日期
     * @param end_date 结束日期
     * @return
     */
    public String calCurveMultiDays(String table_name, String field_name, String code, String start_date, String end_date){

        int point = DateUtil.getPoint() + 1;
        StringBuilder sbu = new StringBuilder();
        StringBuilder sub = new StringBuilder();
//        StringBuilder sub_field = new StringBuilder();
        StringBuilder fields = new StringBuilder();

        for(int i=1; i<288; i++){
            fields.append(field_name).append(i).append(", ");
//            sub_field.append("c.").append(field_name).append(i).append(", ");
            sub.append("sum(vv.").append(field_name).append(i).append(")*if(vv.cal_sign = 0, 1, -1) ").append(field_name).append(i).append(",");
        }

        sub.append("sum(vv.").append(field_name).append(288).append(")*if(vv.cal_sign = 0, 1, -1) ").append(field_name).append(288);
//        sub_field.append("c.").append(field_name).append(288);
        fields.append(field_name).append(288);

        sbu.append("replace into ").append(table_name).append("(dsm_sys_id,dsm_ld_id,data_date,data_type,data_interval,data_point,").append(fields)
                .append(") select vv.dsm_sys_id,vv.calobj_id,vv.data_date,vv.data_type,vv.data_interval,vv.data_point, ").append(sub).append(" from(")
                .append(" select grp.caltype_code, grp.calobj_id, grp.bycaltype_code, grp.bycalobj_id, grp.cal_sign, grp.cal_ratio,c.* ")
                .append(" from dsm_grp_obj_calculation_info grp inner join ").append(table_name).append(" c ON grp.bycalobj_id = c.dsm_ld_id and grp.caltype_code=")
                .append(code).append(" where data_date<='").append(end_date).append("' and data_date>='").append(start_date)
                .append("') vv group by calobj_id,data_type,data_date");
//        sbu.append("replace into ").append(table_name)
//                .append(" select vv.sys_id,vv.calobj_id,vv.data_date,vv.data_type,vv.data_interval,vv.data_point, ").append(sub).append(" from(")
//                .append(" select grp.caltype_code, grp.calobj_id, grp.bycaltype_code, grp.bycalobj_id, grp.cal_sign, grp.cal_ratio,c.*")
//                .append(" from dsm_grp_obj_calculation_info grp inner join ").append(table_name).append(" c ON grp.bycalobj_id = c.ld_id and grp.caltype_code=")
//                .append(code).append(" where data_date<='").append(end_date).append("' and data_date>='").append(start_date)
//                .append("') vv group by calobj_id,data_type,data_date");

        System.out.println("--------多日曲线补算数据---------" + table_name);
        return sbu.toString();
    }

    /**
     * 仅用于电量曲线数据
     * 多日曲线统计数据
     * @return
     */
    public String calEnergyMultiDays(String start_date, String end_date){
//        int point = DateUtil.getPoint() - 1;
        StringBuilder sql = new StringBuilder();
        StringBuilder sbf = new StringBuilder();
        StringBuilder sub = new StringBuilder();
        sql.append("replace into DSM_MMTR_ENERGY_C (");
        sub.append("energy_1,");

        for(int i=2; i<288; i++){
            sbf.append("(cc1.energy_").append(i).append("-cc1.energy_").append(i-1).append(")*ems.pt*ems.ct energy_").append(i).append(",");
            sub.append("energy_").append(i).append(",");
        }
        sbf.append("(cc1.energy_288").append("-cc1.energy_287").append(")*ems.pt*ems.ct energy_288");
        sub.append("energy_288");

        sql.append("dsm_sys_id,dsm_ld_id,data_date,data_type,data_point,data_interval,").append(sub);
        sql.append(") select cc1.sys_id,cc1.ld_id,cc1.data_date,cc1.data_type,cc1.data_point,cc1.data_interval," +
                " (cc1.energy_1-cc2.energy_288)*ems.pt*ems.ct energy_1,").append(sbf).append(" from C_MMTR_ENERGY_C cc1 left join C_MMTR_ENERGY_C cc2 " +
                " on cc1.sys_id=cc2.sys_id and cc1.ld_id=cc2.ld_id and cc1.data_type=cc2.data_type " +
                " and DATE_ADD(cc2.data_date,INTERVAL 1 day)=cc1.data_date " +
                " left join C_LD_EMEAS ems on cc1.sys_id=ems.sys_id and cc1.ld_id=ems.ld_id and ems.is_valid=1 " +
                " where cc1.data_date>='").append(start_date).append("' and cc1.data_date<='").append(end_date).append("'");
        System.out.println("--曲线相减多日电量数据--DSM_MMTR_ENERGY_C");
        return sql.toString();
    }

    /**
     *
     * 计算功率、电压，电流多日曲线数据
     * @param sourceTable
     * @param targetTable
     * @param fieldName
     * @param start_date
     * @param end_date
     * @param type 1.功率 2.电压 3.电流
     * @return
     */
    public String calCurvePcMultiDays(String sourceTable, String targetTable, String fieldName, String start_date, String end_date, int type){
        StringBuilder sbu = new StringBuilder();
        StringBuilder sbf = new StringBuilder();
        StringBuilder sub = new StringBuilder();

        if(type==1){
            for(int i=1; i< 288; i++){
                sbf.append("cc.").append(fieldName).append(i).append("*ems.pt*ems.ct ").append(fieldName).append(i).append(",");
                sub.append(fieldName).append(i).append(",");
            }
            sbf.append("cc.").append(fieldName).append(288).append("*ems.pt*ems.ct ").append(fieldName).append(288);
            sub.append(fieldName).append(288);
        }
        else if(type==2){
            for(int i=1; i< 288; i++){
                sbf.append("cc.").append(fieldName).append(i).append("*ems.pt ").append(fieldName).append(i).append(",");
                sub.append(fieldName).append(i).append(",");
            }
            sbf.append("cc.").append(fieldName).append(288).append("*ems.pt ").append(fieldName).append(288);
            sub.append(fieldName).append(288);
        }
        else if(type==3){
            for(int i=1; i< 288; i++){
                sbf.append("cc.").append(fieldName).append(i).append("*ems.ct ").append(fieldName).append(i).append(",");
                sub.append(fieldName).append(i).append(",");
            }
            sbf.append("cc.").append(fieldName).append(288).append("*ems.ct ").append(fieldName).append(288);
            sub.append(fieldName).append(288);
        }

        sbu.append("replace into ").append(targetTable).append(" (dsm_sys_id,dsm_ld_id,data_date,data_type,data_interval,data_point,")
                .append(sub)
                .append(") select cc.sys_id,cc.ld_id,cc.data_date,cc.data_type,cc.data_interval,cc.data_point,").append(sbf)
                .append(" from ").append(sourceTable).append(" cc left join C_LD_EMEAS ems on cc.sys_id=ems.sys_id " +
                " and cc.ld_id=ems.ld_id and ems.is_valid=1 where cc.data_date>='").append(start_date).append("' and cc.data_date<='")
                .append(end_date).append("'");
        return sbu.toString();
    }

    /**
     * 月数据 多月计算
     * 加group by 去重复
     * @param table_name 要更新的月统计表表名
     * @param d_table_name 月统计表数据来源——日统计表表名
     * @param max 最大值字段
     * @param min 最小值字段
     * @param max_time 最大值发生时间字段
     * @param min_time 最小值发生时间字段
     * @param avg 平均值字段
     * @param start_month 查询起始月
     * @param end_month 查询截止月
     * @return
     */
    public String calMultiMonth(String table_name, String d_table_name, String max, String min, String max_time, String min_time, String avg, String start_month, String end_month){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = new GregorianCalendar();

        String start_m_day_start = start_month + "-01";
        String satrt_m_day_end = "";
        String end_m_day_start = end_month + "-01";
        String end_m_day_end = "";
        try{
            cal.setTime(sdf.parse(start_m_day_start));
            cal.add(Calendar.MONTH, 1);
            cal.add(Calendar.DAY_OF_MONTH, -1);
            satrt_m_day_end = sdf.format(cal.getTime());

            cal.setTime(sdf.parse(end_m_day_start));
            cal.add(Calendar.MONTH, 1);
            cal.add(Calendar.DAY_OF_MONTH, -1);
            end_m_day_end = sdf.format(cal.getTime());
        }catch (Exception e){
            e.printStackTrace();
        }

//        System.out.println(start_m_day_start + "--" + satrt_m_day_end + "======" + end_m_day_start + "-" + end_m_day_end);

        StringBuilder sbr = new StringBuilder();
        sbr.append("replace into ").append(table_name).append(" select cc.dsm_sys_id,cc.dsm_ld_id,CONCAT(datad,'-01') data_date,cc.data_type, cc.max_power,dd.")
                .append(max_time).append(",cc.min_power,ee.").append(min_time).append(",cc.avg_power/31 from ")
                .append(" (select dsm_sys_id,dsm_ld_id,data_type,DATE_FORMAT(data_date, '%Y-%m') datad,max(").append(max)
                .append(") max_power,min(").append(min).append(") min_power,sum(").append(avg).append(") avg_power from ").append(d_table_name)
                .append(" where DATE_FORMAT(data_date, '%Y-%m')>='").append(start_month)
                .append("' and DATE_FORMAT(data_date, '%Y-%m')<='").append(end_month)
                .append("' group by dsm_sys_id,dsm_ld_id,data_type, datad) cc ").append(" left join ")
                .append(d_table_name).append(" dd on cc.dsm_sys_id=dd.dsm_sys_id and cc.dsm_ld_id=dd.dsm_ld_id and cc.data_type=dd.data_type and cc.max_power=dd.")
                .append(max).append(" and cc.datad=DATE_FORMAT(dd.DATA_DATE,'%Y-%m')")
                .append(" left join ").append(d_table_name)
                .append(" ee on cc.dsm_sys_id=ee.dsm_sys_id and cc.dsm_ld_id=ee.dsm_ld_id and cc.data_type=ee.data_type and cc.min_power=ee.").append(min)
                .append(" and cc.datad=DATE_FORMAT(ee.DATA_DATE,'%Y-%m')").append(" group by dsm_sys_id,dsm_ld_id,data_date,data_type");
        System.out.println("------多月统计数据------" + table_name);
        return sbr.toString();
    }


    /**
     * 多年统计数据计算
     * @param table_name 月统计表
     * @param m_table_name 年统计表
     * @param max 最大值字段名
     * @param min 最小值字段名
     * @param max_time 最大值发生时间字段名
     * @param min_time 最小值发生时间字段名
     * @param avg 平均值字段名
     * @param start_year 起始年
     * @param end_year 截止年
     * @return
     */
    public String calMultiYear(String table_name,String m_table_name, String max, String min, String max_time, String min_time, String avg, String start_year, String end_year){

        StringBuilder sbd = new StringBuilder();
        sbd.append("replace into ").append(table_name).append(" select cc.dsm_sys_id,cc.dsm_ld_id, CONCAT(dated, '-01-01') data_date,cc.data_type,cc.max_power,dd.").append(max_time)
                .append(", cc.min_power ,dd.").append(min_time).append(",cc.avg_power/12 from ")
                .append(" (select dsm_sys_id,dsm_ld_id, DATE_FORMAT(data_date,'%Y') dated,data_type,max(").append(max).append(") max_power,min(").append(min)
                .append(") min_power,sum(").append(avg).append(") avg_power ")
                .append(" from " ).append(m_table_name).append(" where DATE_FORMAT(data_date,'%Y')>='").append(start_year).append("' and DATE_FORMAT(data_date,'%Y') <= '")
                .append(end_year).append("' GROUP BY dsm_sys_id, dsm_ld_id, data_type, dated) cc")
                .append(" left join ").append(m_table_name)
                .append(" dd on cc.dsm_sys_id=dd.dsm_sys_id and cc.dsm_ld_id=dd.dsm_ld_id ")
                .append(" and cc.data_type=dd.data_type and cc.max_power=dd.").append(max).append(" and cc.dated=DATE_FORMAT(dd.DATA_DATE,'%Y') ")
                .append(" left join ").append(m_table_name).append(" ee on cc.dsm_sys_id=ee.dsm_sys_id and cc.dsm_ld_id=ee.dsm_ld_id ")
                .append(" and cc.data_type=ee.data_type and cc.min_power=dd.").append(min).append(" and cc.dated=DATE_FORMAT(ee.DATA_DATE,'%Y')")
                .append("group by dsm_sys_id,dsm_ld_id,data_date,data_type");
        System.out.println("------multiYear------" + table_name);
        return sbd.toString();
    }
}
