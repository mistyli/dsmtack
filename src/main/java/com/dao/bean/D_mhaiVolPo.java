package com.dao.bean;

import java.math.BigDecimal;
import java.util.Date;

public class D_mhaiVolPo extends D_mhaiVolPoKey {
    private BigDecimal maxHarVol;

    private Date maxHarVolTime;

    private BigDecimal minHarVol;

    private Date minHarVolTime;

    private BigDecimal avgHarVol;

    public BigDecimal getMaxHarVol() {
        return maxHarVol;
    }

    public void setMaxHarVol(BigDecimal maxHarVol) {
        this.maxHarVol = maxHarVol;
    }

    public Date getMaxHarVolTime() {
        return maxHarVolTime;
    }

    public void setMaxHarVolTime(Date maxHarVolTime) {
        this.maxHarVolTime = maxHarVolTime;
    }

    public BigDecimal getMinHarVol() {
        return minHarVol;
    }

    public void setMinHarVol(BigDecimal minHarVol) {
        this.minHarVol = minHarVol;
    }

    public Date getMinHarVolTime() {
        return minHarVolTime;
    }

    public void setMinHarVolTime(Date minHarVolTime) {
        this.minHarVolTime = minHarVolTime;
    }

    public BigDecimal getAvgHarVol() {
        return avgHarVol;
    }

    public void setAvgHarVol(BigDecimal avgHarVol) {
        this.avgHarVol = avgHarVol;
    }
}