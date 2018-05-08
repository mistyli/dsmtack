package com.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by misty on 2018/3/26.
 */
public class SqlUtil {

    @Autowired
    static JdbcTemplate jdbcTemplate;

    /**
     * 计算功率
     * @param type_code
     * @param table_name
     * @param field_name
     */
    public static void executePower(String type_code, String table_name, String field_name){

        int point = DateUtil.getPoint();

        //352,350,330
        String sql = "update " + table_name + " c, " +
                " ( " +
                " select vv.calobj_id,vv.data_type,vv.data_date,sum(vv.power_2)*if(vv.cal_sign=0, 1, -1) " + field_name + point +
                " from " +
                " ( " +
                " select grp.caltype_code,grp.calobj_id,grp.bycaltype_code,grp.bycalobj_id,grp.cal_sign, " +
                " grp.cal_ratio,c.data_type,c.data_date,c." + field_name + point +
                " from dsm_grp_obj_calculation_info grp inner join " + table_name + " c " +
                " on grp.bycalobj_id=c.ld_id and grp.caltype_code=" + type_code + " where c.data_date='2018-01-05' " +
                " ) vv " +
                " group by calobj_id,data_type " +
                " ) g set c." + field_name + point + "=g." + field_name + point +
                " where c.ld_id=g.calobj_id and c.data_type=g.data_type and c.data_date=g.data_date";

        //第一个点 插入整条数据；否则每次更新一个点的数据
        if(point<=133){
//            sql = "insert into " + table_name + "(sys_id,ld_id,data_date,data_type,data_point," + field_name + "1) " +
//                    " select vv.sys_id,vv.calobj_id,vv.data_date,vv.data_type,vv.data_point,sum(vv." + field_name + "1)*if(vv.cal_sign=0, 1, -1) " +
//                    " from " +
//                    " ( " +
//                    " select grp.caltype_code,grp.calobj_id,grp.bycaltype_code,grp.bycalobj_id,grp.cal_sign,grp.cal_ratio,c.* " +
//                    " from dsm_grp_obj_calculation_info grp inner join " + table_name + " c " +
//                    " on c.ld_id=grp.bycalobj_id and grp.caltype_code=" + type_code + " where data_date='2018-01-06' " +
//                    " ) vv " +
//                    " group by calobj_id,data_type ";
            sql = "insert into " + table_name + "(sys_id,ld_id,data_date,data_type,data_point," + field_name + point + ") " +
                    " select vv.sys_id,vv.calobj_id,vv.data_date,vv.data_type,vv.data_point,sum(vv." + field_name + point + ")*if(vv.cal_sign=0, 1, -1) " +
                    " from " +
                    " ( " +
                    " select grp.caltype_code,grp.calobj_id,grp.bycaltype_code,grp.bycalobj_id,grp.cal_sign,grp.cal_ratio,c.* " +
                    " from dsm_grp_obj_calculation_info grp inner join " + table_name + " c " +
                    " on c.ld_id=grp.bycalobj_id and grp.caltype_code=" + type_code + " where data_date='2018-01-05' " +
                    " ) vv " +
                    " group by calobj_id,data_type ";
        }
        System.out.println("-------" + sql);
        jdbcTemplate.execute(sql);
    }

}
