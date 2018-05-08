package com.dao.bean;

import java.util.Date;

public class D_vUnbalanceCurvePoKey {
    private Long sysId;

    private Long ldId;

    private String dataDate;

    private String dataType;

    public Long getSysId() {
        return sysId;
    }

    public void setSysId(Long sysId) {
        this.sysId = sysId;
    }

    public Long getLdId() {
        return ldId;
    }

    public void setLdId(Long ldId) {
        this.ldId = ldId;
    }

    public String getDataDate() {
        return dataDate;
    }

    public void setDataDate(String dataDate) {
        this.dataDate = dataDate;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType == null ? null : dataType.trim();
    }
}