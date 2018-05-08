package com.dao.bean;

import java.math.BigDecimal;
import java.util.Date;

public class D_curRatioPo extends D_curRatioPoKey {
    private BigDecimal maxHarCurratio;

    private Date maxHarCurratioTime;

    private BigDecimal minHarCurratio;

    private Date minHarCurratioTime;

    private BigDecimal avgHarCurratio;

    public BigDecimal getMaxHarCurratio() {
        return maxHarCurratio;
    }

    public void setMaxHarCurratio(BigDecimal maxHarCurratio) {
        this.maxHarCurratio = maxHarCurratio;
    }

    public Date getMaxHarCurratioTime() {
        return maxHarCurratioTime;
    }

    public void setMaxHarCurratioTime(Date maxHarCurratioTime) {
        this.maxHarCurratioTime = maxHarCurratioTime;
    }

    public BigDecimal getMinHarCurratio() {
        return minHarCurratio;
    }

    public void setMinHarCurratio(BigDecimal minHarCurratio) {
        this.minHarCurratio = minHarCurratio;
    }

    public Date getMinHarCurratioTime() {
        return minHarCurratioTime;
    }

    public void setMinHarCurratioTime(Date minHarCurratioTime) {
        this.minHarCurratioTime = minHarCurratioTime;
    }

    public BigDecimal getAvgHarCurratio() {
        return avgHarCurratio;
    }

    public void setAvgHarCurratio(BigDecimal avgHarCurratio) {
        this.avgHarCurratio = avgHarCurratio;
    }
}