/*
This Class is created to represent one row of the table customer_details
Set get,set values of each columns
*/
package org.apache.hive.jdbc;

/**
 *
 * @author gfp2ram
 */
public class CustomerDetails {

    private String phNum;
    private String plan;
    private String region;
    private String imei;
    private String status;
    private String balance;
    private String date;

    /**
     * @return the phNum
     */
    public String getPhNum() {
        return phNum;
    }

    /**
     * @param phNum the phNum to set
     */
    public void setPhNum(String phNum) {
        this.phNum = phNum;
    }

    /**
     * @return the plan
     */
    public String getPlan() {
        return plan;
    }

    /**
     * @param plan the plan to set
     */
    public void setPlan(String plan) {
        this.plan = plan;
    }

    /**
     * @return the region
     */
    public String getRegion() {
        return region;
    }

    /**
     * @param region the region to set
     */
    public void setRegion(String region) {
        this.region = region;
    }

    /**
     * @return the imei
     */
    public String getImei() {
        return imei;
    }

    /**
     * @param imei the imei to set
     */
    public void setImei(String imei) {
        this.imei = imei;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the balance
     */
    public String getBalance() {
        return balance;
    }

    /**
     * @param balance the balance to set
     */
    public void setBalance(String balance) {
        this.balance = balance;
    }

    /**
     * @return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(String date) {
        this.date = date;
    }

}
