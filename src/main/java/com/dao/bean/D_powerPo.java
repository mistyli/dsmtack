package com.dao.bean;

public class D_powerPo extends D_powerPoKey {
    private Double maxPower;

    private String maxPowerTime;

    private Double minPower;

    private String minPowerTime;

    private Double avgPower;

    public String getMaxPowerTime() {
        return maxPowerTime;
    }

    public void setMaxPowerTime(String maxPowerTime) {
        this.maxPowerTime = maxPowerTime;
    }

    public String getMinPowerTime() {
        return minPowerTime;
    }

    public void setMinPowerTime(String minPowerTime) {
        this.minPowerTime = minPowerTime;
    }

    public Double getMaxPower() {
        return maxPower;
    }

    public void setMaxPower(Double maxPower) {
        this.maxPower = maxPower;
    }

    public Double getMinPower() {
        return minPower;
    }

    public void setMinPower(Double minPower) {
        this.minPower = minPower;
    }

    public Double getAvgPower() {
        return avgPower;
    }

    public void setAvgPower(Double avgPower) {
        this.avgPower = avgPower;
    }
}