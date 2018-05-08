package com.util;

import com.dao.bean.D_iUnbalanceCurvePo;
import com.dao.bean.D_powerPo;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by misty on 2018/3/29.
 */
public class ToolUtil<T> {

    private List<D_powerPo> getMostValue(List<T> powers, int point, String field_name){
        DecimalFormat df = new DecimalFormat("00.0000");
        List<D_powerPo> list = new ArrayList<>();
        Double temp = 0d;

        Double max = 0d;
        Double min = 0d;
        Double avg = 0d;
        int max_point = 1;
        int min_point = 1;

        if(temp == null){
            return null;
        }
        for(int j=0; j<powers.size(); j++){
            max = 0d;
            min = 0d;
            avg = 0d;
            T power = powers.get(j);
            for(int i=1; i<=point; i++){
                Map<String,Object> map = new HashMap<String, Object>();
//                String evl = "power.getiUnbalance" + i + "()";
                String evl = "power." + field_name + i + "()";
                map.put("power", power);
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
//            po.setMaxPowerTime(DateUtil.pointCovert2Date(power.getDataDate(), max_point));
//            po.setMinPowerTime(DateUtil.pointCovert2Date(power.getDataDate(), min_point));
//            po.setAvgPower(Double.valueOf(df.format(avg / point)));
//            po.setDataType(power.getDataType());
//            po.setDsmLdId(power.getLdId());
//            po.setDsmSysId(power.getSysId());
//            po.setDataDate(power.getDataDate());
            list.add(po);
        }
        return list;
    }

}
