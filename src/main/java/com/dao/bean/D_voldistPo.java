package com.dao.bean;

import java.math.BigDecimal;
import java.util.Date;

public class D_voldistPo extends D_voldistPoKey {
    private BigDecimal maxHarVoldist;

    private Date maxHarVoldistTime;

    private BigDecimal minHarVoldist;

    private Date minHarVoldistTime;

    private BigDecimal avgHarVoldist;

    public BigDecimal getMaxHarVoldist() {
        return maxHarVoldist;
    }

    public void setMaxHarVoldist(BigDecimal maxHarVoldist) {
        this.maxHarVoldist = maxHarVoldist;
    }

    public Date getMaxHarVoldistTime() {
        return maxHarVoldistTime;
    }

    public void setMaxHarVoldistTime(Date maxHarVoldistTime) {
        this.maxHarVoldistTime = maxHarVoldistTime;
    }

    public BigDecimal getMinHarVoldist() {
        return minHarVoldist;
    }

    public void setMinHarVoldist(BigDecimal minHarVoldist) {
        this.minHarVoldist = minHarVoldist;
    }

    public Date getMinHarVoldistTime() {
        return minHarVoldistTime;
    }

    public void setMinHarVoldistTime(Date minHarVoldistTime) {
        this.minHarVoldistTime = minHarVoldistTime;
    }

    public BigDecimal getAvgHarVoldist() {
        return avgHarVoldist;
    }

    public void setAvgHarVoldist(BigDecimal avgHarVoldist) {
        this.avgHarVoldist = avgHarVoldist;
    }
}