package com.dao.bean;

import java.math.BigDecimal;
import java.util.Date;

public class D_freqPo extends D_freqPoKey {
    private BigDecimal maxFreq;

    private Date maxFreqTime;

    private BigDecimal minFreq;

    private Date minFreqTime;

    private BigDecimal avgFreq;

    public BigDecimal getMaxFreq() {
        return maxFreq;
    }

    public void setMaxFreq(BigDecimal maxFreq) {
        this.maxFreq = maxFreq;
    }

    public Date getMaxFreqTime() {
        return maxFreqTime;
    }

    public void setMaxFreqTime(Date maxFreqTime) {
        this.maxFreqTime = maxFreqTime;
    }

    public BigDecimal getMinFreq() {
        return minFreq;
    }

    public void setMinFreq(BigDecimal minFreq) {
        this.minFreq = minFreq;
    }

    public Date getMinFreqTime() {
        return minFreqTime;
    }

    public void setMinFreqTime(Date minFreqTime) {
        this.minFreqTime = minFreqTime;
    }

    public BigDecimal getAvgFreq() {
        return avgFreq;
    }

    public void setAvgFreq(BigDecimal avgFreq) {
        this.avgFreq = avgFreq;
    }
}