package com.example.sewsmart.show_order;

public class OrderBean
{
    int oid;
    String dress;
    String wrkr;
    int qty;
    int bill;
    String dod;
    String status;

    public int getOid() {
        return oid;
    }

    public void setOid(int oid) {
        this.oid = oid;
    }

    public String getDress() {
        return dress;
    }

    public void setDress(String dress) {
        this.dress = dress;
    }

    public String getWrkr() {
        return wrkr;
    }

    public void setWrkr(String wrkr) {
        this.wrkr = wrkr;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public int getBill() {
        return bill;
    }

    public void setBill(int bill) {
        this.bill = bill;
    }

    public String getDod() {
        return dod;
    }

    public void setDod(String dod) {
        this.dod = dod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public OrderBean(int oid, String dress, String dod, String status) {
        this.oid = oid;
        this.dress = dress;
        this.dod = dod;
        this.status = status;
    }

    public OrderBean(int oid, String dress, String wrkr, String dod, String status) {
        this.oid = oid;
        this.dress = dress;
        this.wrkr = wrkr;
        this.dod = dod;
        this.status = status;
    }

    public OrderBean(int oid, String dress, String wrkr, int qty, int bill, String dod, String status) {
        this.oid = oid;
        this.dress = dress;
        this.wrkr = wrkr;
        this.qty = qty;
        this.bill = bill;
        this.dod = dod;
        this.status = status;
    }

    public OrderBean() {
    }
}
