package com.dao.bean;

import java.math.BigDecimal;
import java.util.Date;

public class D_fundCurPo extends D_fundCurPoKey {
    private BigDecimal maxFundCur;

    private Date maxFundCurTime;

    private BigDecimal minFundCur;

    private Date minFundCurTime;

    private BigDecimal avgFundCur;

    public BigDecimal getMaxFundCur() {
        return maxFundCur;
    }

    public void setMaxFundCur(BigDecimal maxFundCur) {
        this.maxFundCur = maxFundCur;
    }

    public Date getMaxFundCurTime() {
        return maxFundCurTime;
    }

    public void setMaxFundCurTime(Date maxFundCurTime) {
        this.maxFundCurTime = maxFundCurTime;
    }

    public BigDecimal getMinFundCur() {
        return minFundCur;
    }

    public void setMinFundCur(BigDecimal minFundCur) {
        this.minFundCur = minFundCur;
    }

    public Date getMinFundCurTime() {
        return minFundCurTime;
    }

    public void setMinFundCurTime(Date minFundCurTime) {
        this.minFundCurTime = minFundCurTime;
    }

    public BigDecimal getAvgFundCur() {
        return avgFundCur;
    }

    public void setAvgFundCur(BigDecimal avgFundCur) {
        this.avgFundCur = avgFundCur;
    }
}