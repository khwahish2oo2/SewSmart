package com.example.sewsmart.manage_salary;

public class ManageSalaryBean
{
    private String name;
    private String doj;
    private int present;
    private int absent;
    private int monthlySalary;
    private int netSalary;
    private int workload;
    private String status;

    public ManageSalaryBean(String name, int workload, String status) {
        this.name = name;
        this.workload = workload;
        this.status = status;
    }

    public int getWorkload() {
        return workload;
    }

    public void setWorkload(int workload) {
        this.workload = workload;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ManageSalaryBean(String name, String doj, int present, int absent, int monthlySalary, int netSalary) {
        this.name = name;
        this.doj=doj;
        this.present = present;
        this.absent = absent;
        this.monthlySalary = monthlySalary;
        this.netSalary = netSalary;
    }

    public String getDoj() {
        return doj;
    }

    public void setDoj(String doj) {
        this.doj = doj;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPresent() {
        return present;
    }

    public void setPresent(int present) {
        this.present = present;
    }

    public int getAbsent() {
        return absent;
    }

    public void setAbsent(int absent) {
        this.absent = absent;
    }

    public int getMonthlySalary() {
        return monthlySalary;
    }

    public void setMonthlySalary(int monthlySalary) {
        this.monthlySalary = monthlySalary;
    }

    public int getNetSalary() {
        return netSalary;
    }

    public void setNetSalary(int netSalary) {
        this.netSalary = netSalary;
    }
}
