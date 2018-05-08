package com.dao.bean;

import java.math.BigDecimal;
import java.util.Date;

public class D_vUnbalancePo extends D_vUnbalancePoKey {
    private BigDecimal maxUUnbalance;

    private Date maxUUnbalanceT;

    private BigDecimal minUUnbalance;

    private Date minUUnbalanceT;

    private BigDecimal avgUUnbalance;

    public BigDecimal getMaxUUnbalance() {
        return maxUUnbalance;
    }

    public void setMaxUUnbalance(BigDecimal maxUUnbalance) {
        this.maxUUnbalance = maxUUnbalance;
    }

    public Date getMaxUUnbalanceT() {
        return maxUUnbalanceT;
    }

    public void setMaxUUnbalanceT(Date maxUUnbalanceT) {
        this.maxUUnbalanceT = maxUUnbalanceT;
    }

    public BigDecimal getMinUUnbalance() {
        return minUUnbalance;
    }

    public void setMinUUnbalance(BigDecimal minUUnbalance) {
        this.minUUnbalance = minUUnbalance;
    }

    public Date getMinUUnbalanceT() {
        return minUUnbalanceT;
    }

    public void setMinUUnbalanceT(Date minUUnbalanceT) {
        this.minUUnbalanceT = minUUnbalanceT;
    }

    public BigDecimal getAvgUUnbalance() {
        return avgUUnbalance;
    }

    public void setAvgUUnbalance(BigDecimal avgUUnbalance) {
        this.avgUUnbalance = avgUUnbalance;
    }
}