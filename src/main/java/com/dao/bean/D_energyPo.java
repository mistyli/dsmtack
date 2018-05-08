package com.dao.bean;

import java.math.BigDecimal;

public class D_energyPo extends D_energyPoKey {
    private BigDecimal energyTot;

    private BigDecimal energyFee1;

    private BigDecimal energyFee2;

    private BigDecimal energyFee3;

    private BigDecimal energyFee4;

    public BigDecimal getEnergyTot() {
        return energyTot;
    }

    public void setEnergyTot(BigDecimal energyTot) {
        this.energyTot = energyTot;
    }

    public BigDecimal getEnergyFee1() {
        return energyFee1;
    }

    public void setEnergyFee1(BigDecimal energyFee1) {
        this.energyFee1 = energyFee1;
    }

    public BigDecimal getEnergyFee2() {
        return energyFee2;
    }

    public void setEnergyFee2(BigDecimal energyFee2) {
        this.energyFee2 = energyFee2;
    }

    public BigDecimal getEnergyFee3() {
        return energyFee3;
    }

    public void setEnergyFee3(BigDecimal energyFee3) {
        this.energyFee3 = energyFee3;
    }

    public BigDecimal getEnergyFee4() {
        return energyFee4;
    }

    public void setEnergyFee4(BigDecimal energyFee4) {
        this.energyFee4 = energyFee4;
    }
}