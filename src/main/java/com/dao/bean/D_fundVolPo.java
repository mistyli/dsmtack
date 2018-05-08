package com.dao.bean;

import java.math.BigDecimal;
import java.util.Date;

public class D_fundVolPo extends D_fundVolPoKey {
    private BigDecimal maxFundVol;

    private Date maxFundVolTime;

    private BigDecimal minFundVol;

    private Date minFundVolTime;

    private BigDecimal avgFundVol;

    public BigDecimal getMaxFundVol() {
        return maxFundVol;
    }

    public void setMaxFundVol(BigDecimal maxFundVol) {
        this.maxFundVol = maxFundVol;
    }

    public Date getMaxFundVolTime() {
        return maxFundVolTime;
    }

    public void setMaxFundVolTime(Date maxFundVolTime) {
        this.maxFundVolTime = maxFundVolTime;
    }

    public BigDecimal getMinFundVol() {
        return minFundVol;
    }

    public void setMinFundVol(BigDecimal minFundVol) {
        this.minFundVol = minFundVol;
    }

    public Date getMinFundVolTime() {
        return minFundVolTime;
    }

    public void setMinFundVolTime(Date minFundVolTime) {
        this.minFundVolTime = minFundVolTime;
    }

    public BigDecimal getAvgFundVol() {
        return avgFundVol;
    }

    public void setAvgFundVol(BigDecimal avgFundVol) {
        this.avgFundVol = avgFundVol;
    }
}