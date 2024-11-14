package com.sqs.resourceshare_android.entity;



import com.sqs.resourceshare_android.util.StringUtils;

import java.util.Date;


public class File {
    public static final String ALIAS = "file";
    private Long id;
    private Long parentId;
    private String fileName;
    private String type;
    private String size;
    private Long resourceId;
    private String path;

    private Boolean operation=false;

    private String createTime ;
    private String updateTime ;
    private Double downloadProgress;//下载进度
    private int statue;//状态 1.下载中 2。暂停 3.停止 4.完成
    private String shareCode;

    private String identifier;
    public String getShareCode() {
        return shareCode;
    }

    public void setShareCode(String shareCode) {
        this.shareCode = shareCode;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }



    //拥有用户
    private String owner;

    public int getStatue() {
        return statue;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    /* public static String  getStateStr(int statue) {
            switch (statue) {
                case 1:
                    return "下载中";
                case 2:
                    return "暂停";
                case 3:
                    return "停止";
                case 4:
                default:
                    return "完成";
            }

        }
    */

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getSizeStr(){
        return StringUtils.formatSize(size==null?0:Double.valueOf(size).longValue());
    }
    public String getStatueStr() {
        switch (statue) {
            case 1:
                return "下载中";
            case 2:
                return "暂停";
            case 3:
                return "停止";
            case 4:
            default:
                return "完成";
        }

    }

    public void setStatue(int statue) {
        this.statue = statue;
    }

    public Double getDownloadProgress() {
        return downloadProgress;
    }

    public void setDownloadProgress(Double downloadProgress) {
        this.downloadProgress = downloadProgress;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public Boolean getOperation() {
        return operation;
    }

    public void setOperation(Boolean operation) {
        this.operation = operation;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
