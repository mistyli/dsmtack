package com.util;

import com.bean.Fields;
import com.dao.bean.D_curCurvePo;
import com.dao.bean.D_powerCurvePo;
import com.dao.bean.D_powerPo;
import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by misty on 2018/3/23.
 */
public class DateUtil {

//    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//    static SimpleDateFormat sdf_sec = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 获取当日期
     * @return
     */
    public static String getDateNow(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }

    /**
     * 获取当前日期 精确到毫秒
     * @return
     */
    public static String getDateSecNow(){
        SimpleDateFormat sdf_sec = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf_sec.format(new Date());
    }

    /**
     * 获取当前时间对应的5分钟点数
     * @return
     */
    public static int getPoint(){
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int point = (hour*60 + minute)/5;
        return  point;
    }

    public static Object convertToCode(String jexlExp, Map<String,Object> map){
        JexlEngine jexl = new JexlEngine();
        Expression e = jexl.createExpression(jexlExp);
        JexlContext jc = new MapContext();
        for(String key : map.keySet()){
            jc.set(key, map.get(key));
        }
        if(null==e.evaluate(jc)){
            return "";
        }
        return e.evaluate(jc);
    }

    /**
     * 根据点数计算时间
     * @param point
     * @return
     */
    public static String pointCovert2Date(String data_date, int point){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf_sec = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int count = point * 5;
        int hour = count / 60;
        int minute = count % 60;
        Calendar cal = new GregorianCalendar();
        try{
            cal.setTime(sdf.parse(data_date));
            cal.set(Calendar.HOUR_OF_DAY, hour);
            cal.set(Calendar.MINUTE, minute);
        }catch (Exception e){
            e.printStackTrace();
        }
        return sdf_sec.format(cal.getTime());
    }

    public static void main(String[] args){
        System.out.println(2/5);
    }

}
