package com.dao.bean;

import java.math.BigDecimal;
import java.util.Date;

public class D_mhaiCurPo extends D_mhaiCurPoKey {
    private BigDecimal maxHarCur;

    private Date maxHarCurTime;

    private BigDecimal minHarCur;

    private Date minHarCurTime;

    private BigDecimal avgHarCur;

    public BigDecimal getMaxHarCur() {
        return maxHarCur;
    }

    public void setMaxHarCur(BigDecimal maxHarCur) {
        this.maxHarCur = maxHarCur;
    }

    public Date getMaxHarCurTime() {
        return maxHarCurTime;
    }

    public void setMaxHarCurTime(Date maxHarCurTime) {
        this.maxHarCurTime = maxHarCurTime;
    }

    public BigDecimal getMinHarCur() {
        return minHarCur;
    }

    public void setMinHarCur(BigDecimal minHarCur) {
        this.minHarCur = minHarCur;
    }

    public Date getMinHarCurTime() {
        return minHarCurTime;
    }

    public void setMinHarCurTime(Date minHarCurTime) {
        this.minHarCurTime = minHarCurTime;
    }

    public BigDecimal getAvgHarCur() {
        return avgHarCur;
    }

    public void setAvgHarCur(BigDecimal avgHarCur) {
        this.avgHarCur = avgHarCur;
    }
}