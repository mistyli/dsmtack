package com.dao.bean;

import java.util.Date;

public class D_fundVolCurvePoKey {
    private Long ldId;

    private Long sysId;

    private String dataDate;

    private String dataType;

    public Long getLdId() {
        return ldId;
    }

    public void setLdId(Long ldId) {
        this.ldId = ldId;
    }

    public Long getSysId() {
        return sysId;
    }

    public void setSysId(Long sysId) {
        this.sysId = sysId;
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