package com.dao.provider;

/**
 * Created by misty on 2018/4/8.
 */
public class PoDataProvider {

    /**
     *
     * @param table_name 日统计表
     * @param c_table_name 曲线表
     * @param max 最大值
     * @param min 最小值
     * @param max_time 最大值发生时间
     * @param min_time 最小值发生时间
     * @param avg 平均值
     * @param start_date 起始日期
     * @param end_date 结束日期
     * @return
     */
    public String powerPo(String table_name,String c_table_name,String field_name, String max, String min, String max_time, String min_time, String avg, String start_date, String end_date){

        String sql = "";
        StringBuilder sbu_sql = new StringBuilder();
        StringBuilder sbu_greatest = new StringBuilder();
        sbu_greatest.append("GREATEST(");
        StringBuilder sbu_least = new StringBuilder();
        sbu_least.append("LEAST(");
        StringBuilder sbu_sum = new StringBuilder();
        sbu_sum.append("(");

        for(int i=1; i<288; i++){
            sbu_greatest.append(field_name).append(i).append(",");
            sbu_least.append(field_name).append(i).append(",");
            sbu_sum.append(field_name).append(i).append("+");
        }
        sbu_greatest.append(field_name).append(288).append(") max_").append(field_name);
        sbu_least.append(field_name).append(288).append(") min_").append(field_name);
        sbu_sum.append(field_name).append(288).append(")/288 avg").append(field_name);




        return "";
    }

}
