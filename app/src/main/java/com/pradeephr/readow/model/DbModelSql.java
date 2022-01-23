package com.pradeephr.readow.model;

public class DbModelSql {
    private int dataId;
    private String AgencyName;
    private String AgencyCategory;
    private  String AgencyLink;

    public int getDataId() {
        return dataId;
    }

    public void setDataId(int dataId) {
        this.dataId = dataId;
    }

    public String getAgencyName() {
        return AgencyName;
    }

    public void setAgencyName(String agencyName) {
        AgencyName = agencyName;
    }

    public String getAgencyCategory() {
        return AgencyCategory;
    }

    public void setAgencyCategory(String agencyCategory) {
        AgencyCategory = agencyCategory;
    }

    public String getAgencyLink() {
        return AgencyLink;
    }

    public void setAgencyLink(String agencyLink) {
        AgencyLink = agencyLink;
    }

    public DbModelSql(int dataId, String agencyName, String agencyCategory, String agencyLink) {
        this.dataId = dataId;
        AgencyName = agencyName;
        AgencyCategory = agencyCategory;
        AgencyLink = agencyLink;
    }

    public DbModelSql(){
        //temp
    }

}
