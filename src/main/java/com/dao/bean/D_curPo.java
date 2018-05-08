package com.dao.bean;

import java.math.BigDecimal;
import java.util.Date;

public class D_curPo extends D_curPoKey {
    private Double maxCur;

    private String maxCurTime;

    private Double minCur;

    private String minCurTime;

    private Double avgCur;

    public Double getMaxCur() {
        return maxCur;
    }

    public void setMaxCur(Double maxCur) {
        this.maxCur = maxCur;
    }

    public String getMaxCurTime() {
        return maxCurTime;
    }

    public void setMaxCurTime(String maxCurTime) {
        this.maxCurTime = maxCurTime;
    }

    public Double getMinCur() {
        return minCur;
    }

    public void setMinCur(Double minCur) {
        this.minCur = minCur;
    }

    public String getMinCurTime() {
        return minCurTime;
    }

    public void setMinCurTime(String minCurTime) {
        this.minCurTime = minCurTime;
    }

    public Double getAvgCur() {
        return avgCur;
    }

    public void setAvgCur(Double avgCur) {
        this.avgCur = avgCur;
    }
}