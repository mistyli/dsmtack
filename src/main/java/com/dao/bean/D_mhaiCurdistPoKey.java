package com.dao.bean;

import java.util.Date;

public class D_mhaiCurdistPoKey {
    private Long dsmSysId;

    private Long dsmLdId;

    private Date dataDate;

    private String dataType;

    public Long getDsmSysId() {
        return dsmSysId;
    }

    public void setDsmSysId(Long dsmSysId) {
        this.dsmSysId = dsmSysId;
    }

    public Long getDsmLdId() {
        return dsmLdId;
    }

    public void setDsmLdId(Long dsmLdId) {
        this.dsmLdId = dsmLdId;
    }

    public Date getDataDate() {
        return dataDate;
    }

    public void setDataDate(Date dataDate) {
        this.dataDate = dataDate;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType == null ? null : dataType.trim();
    }
}