package com.example.elijah.skyranch_draft;

public class ProductGroup {

    private String groupNo;
    private String groupName;

    public ProductGroup() {

    }

    public ProductGroup(String groupNo, String groupName) {
        this.groupNo = groupNo;
        this.groupName = groupName;
    }

    public String getGroupNo() {
        return groupNo;
    }

    public void setGroupNo(String groupNo) {
        this.groupNo = groupNo;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public String toString() {
        return this.groupName;
    }
}
