package com.jschaf.xfa;

/**
 * Created by joe on 9/17/2015.
 */
public class Student {
    private String lastName;

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleInital() {
        return middleInital;
    }

    public void setMiddleInital(String middleInital) {
        this.middleInital = middleInital;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getMos() {
        return mos;
    }

    public void setMos(String mos) {
        this.mos = mos;
    }

    private String firstName;
    private String middleInital;
    private String ssn;
    private String grade;
    private String branch;
    private String mos;

}
