package com.dao.bean;

import java.math.BigDecimal;
import java.util.Date;

public class D_curdistPo extends D_curdistPoKey {
    private BigDecimal maxHarCurdist;

    private Date maxHarCurdistTime;

    private BigDecimal minHarCurdist;

    private Date minHarCurdistTime;

    private BigDecimal avgHarCurdist;

    public BigDecimal getMaxHarCurdist() {
        return maxHarCurdist;
    }

    public void setMaxHarCurdist(BigDecimal maxHarCurdist) {
        this.maxHarCurdist = maxHarCurdist;
    }

    public Date getMaxHarCurdistTime() {
        return maxHarCurdistTime;
    }

    public void setMaxHarCurdistTime(Date maxHarCurdistTime) {
        this.maxHarCurdistTime = maxHarCurdistTime;
    }

    public BigDecimal getMinHarCurdist() {
        return minHarCurdist;
    }

    public void setMinHarCurdist(BigDecimal minHarCurdist) {
        this.minHarCurdist = minHarCurdist;
    }

    public Date getMinHarCurdistTime() {
        return minHarCurdistTime;
    }

    public void setMinHarCurdistTime(Date minHarCurdistTime) {
        this.minHarCurdistTime = minHarCurdistTime;
    }

    public BigDecimal getAvgHarCurdist() {
        return avgHarCurdist;
    }

    public void setAvgHarCurdist(BigDecimal avgHarCurdist) {
        this.avgHarCurdist = avgHarCurdist;
    }
}