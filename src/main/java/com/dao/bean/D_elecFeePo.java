package com.dao.bean;

import java.math.BigDecimal;

public class D_elecFeePo extends D_elecFeePoKey {
    private BigDecimal elecFee;

    private BigDecimal elecFeeT;

    private BigDecimal elecFeeP;

    private BigDecimal elecFeeN;

    private BigDecimal elecFeeY;

    public BigDecimal getElecFee() {
        return elecFee;
    }

    public void setElecFee(BigDecimal elecFee) {
        this.elecFee = elecFee;
    }

    public BigDecimal getElecFeeT() {
        return elecFeeT;
    }

    public void setElecFeeT(BigDecimal elecFeeT) {
        this.elecFeeT = elecFeeT;
    }

    public BigDecimal getElecFeeP() {
        return elecFeeP;
    }

    public void setElecFeeP(BigDecimal elecFeeP) {
        this.elecFeeP = elecFeeP;
    }

    public BigDecimal getElecFeeN() {
        return elecFeeN;
    }

    public void setElecFeeN(BigDecimal elecFeeN) {
        this.elecFeeN = elecFeeN;
    }

    public BigDecimal getElecFeeY() {
        return elecFeeY;
    }

    public void setElecFeeY(BigDecimal elecFeeY) {
        this.elecFeeY = elecFeeY;
    }
}