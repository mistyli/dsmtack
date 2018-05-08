package com.dao.provider;

import com.dao.bean.D_powerPo;
import com.util.DateUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by misty on 2018/4/3.
 * 启动时执行一波replace into操作，继续执行时用updata操作，执行效率更高
 */
public class StartProvider {

    private static final Logger log = LogManager.getLogger(StartProvider.class.getName());

    /**
     * 曲线数据
     * @return
     */
    public String startCurveDay(String table_name, String field_name, String code){

        String date_date = DateUtil.getDateNow();
        int point = DateUtil.getPoint();
        StringBuilder sbu = new StringBuilder();
        StringBuilder sub = new StringBuilder();
        StringBuilder sub_field = new StringBuilder();

        StringBuilder fields = new StringBuilder();

        for(int i=1; i<point; i++){
            fields.append(field_name).append(i).append(", ");
            sub_field.append("c.").append(field_name).append(i).append(", ");
            sub.append("sum(vv.").append(field_name).append(i).append("*if(vv.cal_sign = 0, 1, -1)*vv.cal_ratio) ").append(field_name).append(i).append(",");
        }
        sub.append("sum(vv.").append(field_name).append(point).append("*if(vv.cal_sign = 0, 1, -1)*vv.cal_ratio) ").append(field_name).append(point);
        sub_field.append("c.").append(field_name).append(point);
        fields.append(field_name).append(point);

        sbu.append("replace into ").append(table_name).append("(dsm_sys_id,dsm_ld_id,data_date,data_type,data_point,").append(fields)
                .append(") select vv.dsm_sys_id,vv.calobj_id,vv.data_date,vv.data_type,vv.data_point, ")
                .append(sub).append(" from(")
                .append(" select grp.caltype_code, grp.calobj_id, grp.bycaltype_code, grp.bycalobj_id, grp.cal_sign, grp.cal_ratio," +
                        "c.dsm_sys_id,c.dsm_ld_id,c.data_point, c.data_type, c.data_date,")
                .append(sub_field).append(" from dsm_grp_obj_calculation_info grp inner join ").append(table_name)
                .append(" c ON grp.bycalobj_id = c.dsm_ld_id and grp.caltype_code=")
                .append(code).append(" where data_date='").append(date_date).append("') vv group by calobj_id,data_type");
        log.info("------启动执行曲线计算------" + table_name);
        return sbu.toString();
    }

    /**
     * 最大值最小值平均值 插入数据库
     * @param map mybatis的providerc传参只能是基本数据类型和类以及Map,所以传List的话要从Map中取
     * @return
     */
    public String insertStatistics(Map map){
        String table_name = (String)map.get("0");
//        System.out.println("=========" + table_name);
        List<D_powerPo> mostValue = (List<D_powerPo>)map.get("1");
        StringBuilder sbu = new StringBuilder();
        sbu.append("replace into " + table_name + " values ");
        Iterator ite = mostValue.iterator();
        while(ite.hasNext()){
            D_powerPo power = (D_powerPo)ite.next();
            sbu.append("(" + power.getDsmSysId() + ", " + power.getDsmLdId() + ", '" + power.getDataDate() + "', '" +
                    power.getDataType() + "', " + power.getMaxPower() + ", '" + power.getMaxPowerTime() + "', " +
                    power.getMinPower() + ", '" + power.getMinPowerTime() + "', " + power.getAvgPower() + "),");
        }
        int index = sbu.lastIndexOf(",");

        log.info("------启动执行统计计算------" + table_name);
        if(index<0){
            return "";
        }
        return sbu.substring(0, index);
    }

}
