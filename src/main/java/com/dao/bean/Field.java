package com.dao.bean;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by misty on 2018/3/28.
 */
public class Field {

    private Long sys_id;

    private Long ld_id;

    private Date data_date;

    private String data_type;

    public Long getSys_id() {
        return sys_id;
    }

    public void setSys_id(Long sys_id) {
        this.sys_id = sys_id;
    }

    public Long getLd_id() {
        return ld_id;
    }

    public void setLd_id(Long ld_id) {
        this.ld_id = ld_id;
    }

    public Date getData_date() {
        return data_date;
    }

    public void setData_date(Date data_date) {
        this.data_date = data_date;
    }

    public String getData_type() {
        return data_type;
    }

    public void setData_type(String data_type) {
        this.data_type = data_type;
    }


}
