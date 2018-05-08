package com.jobs;

/**
 * Created by misty on 2018/4/4.
 */
public interface BaseCurve {

    public void workStatistic();

    public void workStatistic(String start_date, String end_date);

    public void workMonth();

    public void workMultiMonth(String start_date, String end_date);

    public void workYear();

    public void workMultiYear(String start_date, String end_date);
}
