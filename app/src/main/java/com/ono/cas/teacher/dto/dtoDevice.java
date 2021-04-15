package com.ono.cas.teacher.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class dtoDevice {
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("devtype")
    @Expose
    private String devtype;
    @SerializedName("devname")
    @Expose
    private String devname;
    @SerializedName("peerid")
    @Expose
    private String peerid;
    @SerializedName("sid")
    @Expose
    private String sid;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof dtoDevice)) return false;
        dtoDevice device = (dtoDevice) o;
        return getName().equals(device.getName()) &&
                getDevtype().equals(device.getDevtype()) &&
                getDevname().equals(device.getDevname()) &&
                getPeerid().equals(device.getPeerid()) &&
                getSid().equals(device.getSid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getDevtype(), getDevname(), getPeerid(), getSid());
    }

    @Override
    public String toString() {
        return "dtoDevice{" +
                "name='" + name + '\'' +
                ", devtype='" + devtype + '\'' +
                ", devname='" + devname + '\'' +
                ", peerid='" + peerid + '\'' +
                ", sid='" + sid + '\'' +
                '}';
    }

    public dtoDevice() {

    }

    public dtoDevice(String name, String devtype, String devname, String peerid, String sid) {
        this.name = name;
        this.devtype = devtype;
        this.devname = devname;
        this.peerid = peerid;
        this.sid = sid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDevtype() {
        return devtype;
    }

    public void setDevtype(String devtype) {
        this.devtype = devtype;
    }

    public String getDevname() {
        return devname;
    }

    public void setDevname(String devname) {
        this.devname = devname;
    }

    public String getPeerid() {
        return peerid;
    }

    public void setPeerid(String peerid) {
        this.peerid = peerid;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }
}
