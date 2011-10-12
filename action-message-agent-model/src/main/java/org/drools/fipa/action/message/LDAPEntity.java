/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package org.drools.fipa.action.message;

import java.io.Serializable;

/**
 *
 * @author salaboy
 * {"cn", "mobile", "employeeNumber", "displayName", "gender"}
 */
public class LDAPEntity implements Serializable{
    private String cn;
    private String mobile;
    private String employeeNumber;
    private String displayName;
    private String gender;

    public LDAPEntity(String cn) {
        this.cn = cn;
    }

    
    public LDAPEntity(String cn, String mobile, String employeeNumber, String displayName, String gender) {
        this.cn = cn;
        this.mobile = mobile;
        this.employeeNumber = employeeNumber;
        this.displayName = displayName;
        this.gender = gender;
    }

    public String getCn() {
        return cn;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Override
    public String toString() {
        return "LDAPEntity{" + "cn=" + cn + ", mobile=" + mobile + ", employeeNumber=" + employeeNumber + ", displayName=" + displayName + ", gender=" + gender + '}';
    }
    
      
           
}
