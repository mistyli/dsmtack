package com.dao.bean;

import java.util.Date;

public class D_curPoKey {
    private Long dsmSysId;

    private Long dsmLdId;

    private String dataDate;

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