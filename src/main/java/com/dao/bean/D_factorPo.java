package com.dao.bean;

import java.math.BigDecimal;
import java.util.Date;

public class D_factorPo extends D_factorPoKey {
    private BigDecimal maxFactor;

    private Date maxFactorTime;

    private BigDecimal minFactor;

    private Date minFactorTime;

    private BigDecimal avgFactor;

    public BigDecimal getMaxFactor() {
        return maxFactor;
    }

    public void setMaxFactor(BigDecimal maxFactor) {
        this.maxFactor = maxFactor;
    }

    public Date getMaxFactorTime() {
        return maxFactorTime;
    }

    public void setMaxFactorTime(Date maxFactorTime) {
        this.maxFactorTime = maxFactorTime;
    }

    public BigDecimal getMinFactor() {
        return minFactor;
    }

    public void setMinFactor(BigDecimal minFactor) {
        this.minFactor = minFactor;
    }

    public Date getMinFactorTime() {
        return minFactorTime;
    }

    public void setMinFactorTime(Date minFactorTime) {
        this.minFactorTime = minFactorTime;
    }

    public BigDecimal getAvgFactor() {
        return avgFactor;
    }

    public void setAvgFactor(BigDecimal avgFactor) {
        this.avgFactor = avgFactor;
    }
}