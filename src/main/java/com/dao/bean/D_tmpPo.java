package com.dao.bean;

import java.math.BigDecimal;
import java.util.Date;

public class D_tmpPo extends D_tmpPoKey {
    private BigDecimal maxTmp;

    private Date maxTmpTime;

    private BigDecimal minTmp;

    private Date minTmpTime;

    private BigDecimal avgTmp;

    public BigDecimal getMaxTmp() {
        return maxTmp;
    }

    public void setMaxTmp(BigDecimal maxTmp) {
        this.maxTmp = maxTmp;
    }

    public Date getMaxTmpTime() {
        return maxTmpTime;
    }

    public void setMaxTmpTime(Date maxTmpTime) {
        this.maxTmpTime = maxTmpTime;
    }

    public BigDecimal getMinTmp() {
        return minTmp;
    }

    public void setMinTmp(BigDecimal minTmp) {
        this.minTmp = minTmp;
    }

    public Date getMinTmpTime() {
        return minTmpTime;
    }

    public void setMinTmpTime(Date minTmpTime) {
        this.minTmpTime = minTmpTime;
    }

    public BigDecimal getAvgTmp() {
        return avgTmp;
    }

    public void setAvgTmp(BigDecimal avgTmp) {
        this.avgTmp = avgTmp;
    }
}