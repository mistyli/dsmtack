package com.dao.bean;

import java.math.BigDecimal;
import java.util.Date;

public class D_volRatioPo extends D_volRatioPoKey {
    private BigDecimal maxHarVolratio;

    private Date maxHarVolratioTime;

    private BigDecimal minHarVolratio;

    private Date minHarVolratioTime;

    private BigDecimal avgHarVolratio;

    public BigDecimal getMaxHarVolratio() {
        return maxHarVolratio;
    }

    public void setMaxHarVolratio(BigDecimal maxHarVolratio) {
        this.maxHarVolratio = maxHarVolratio;
    }

    public Date getMaxHarVolratioTime() {
        return maxHarVolratioTime;
    }

    public void setMaxHarVolratioTime(Date maxHarVolratioTime) {
        this.maxHarVolratioTime = maxHarVolratioTime;
    }

    public BigDecimal getMinHarVolratio() {
        return minHarVolratio;
    }

    public void setMinHarVolratio(BigDecimal minHarVolratio) {
        this.minHarVolratio = minHarVolratio;
    }

    public Date getMinHarVolratioTime() {
        return minHarVolratioTime;
    }

    public void setMinHarVolratioTime(Date minHarVolratioTime) {
        this.minHarVolratioTime = minHarVolratioTime;
    }

    public BigDecimal getAvgHarVolratio() {
        return avgHarVolratio;
    }

    public void setAvgHarVolratio(BigDecimal avgHarVolratio) {
        this.avgHarVolratio = avgHarVolratio;
    }
}