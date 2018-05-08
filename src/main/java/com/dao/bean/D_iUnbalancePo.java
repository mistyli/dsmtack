package com.dao.bean;

import java.math.BigDecimal;
import java.util.Date;

public class D_iUnbalancePo extends D_iUnbalancePoKey {
    private BigDecimal maxIUnbalance;

    private Date maxIUnbalanceT;

    private BigDecimal minIUnbalance;

    private Date minIUnbalanceT;

    private BigDecimal avgIUnbalance;

    public BigDecimal getMaxIUnbalance() {
        return maxIUnbalance;
    }

    public void setMaxIUnbalance(BigDecimal maxIUnbalance) {
        this.maxIUnbalance = maxIUnbalance;
    }

    public Date getMaxIUnbalanceT() {
        return maxIUnbalanceT;
    }

    public void setMaxIUnbalanceT(Date maxIUnbalanceT) {
        this.maxIUnbalanceT = maxIUnbalanceT;
    }

    public BigDecimal getMinIUnbalance() {
        return minIUnbalance;
    }

    public void setMinIUnbalance(BigDecimal minIUnbalance) {
        this.minIUnbalance = minIUnbalance;
    }

    public Date getMinIUnbalanceT() {
        return minIUnbalanceT;
    }

    public void setMinIUnbalanceT(Date minIUnbalanceT) {
        this.minIUnbalanceT = minIUnbalanceT;
    }

    public BigDecimal getAvgIUnbalance() {
        return avgIUnbalance;
    }

    public void setAvgIUnbalance(BigDecimal avgIUnbalance) {
        this.avgIUnbalance = avgIUnbalance;
    }
}