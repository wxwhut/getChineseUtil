package com.zres.util.getChineseUtil.bean;

import java.util.List;

/**
 * Created by wang xu on 2018/6/29.
 */
public class Setting {
    /**目录路径*/
    private String dirPath;
    /**生成文件路径*/
    private String textPath;
    /**生成文件名*/
    private String textName;
    /**搜索文件后缀*/
    private String[] file;
    /**过滤条件*/
    private String[] filter;
    /**其他文件类型*/
    private List<Detail> otherFile;
    /**SVN用户名*/
    private String userName;
    /**SVN密码*/
    private String password;
    /**SVN路径*/
    private String svnUrl;
    /**查询路径类型 0:本机 1:SVN */
    private int style;

    public String getDirPath() {
        return dirPath;
    }

    public void setDirPath(String dirPath) {
        this.dirPath = dirPath;
    }

    public String getTextPath() {
        return textPath;
    }

    public void setTextPath(String textPath) {
        this.textPath = textPath;
    }

    public String getTextName() {
        return textName;
    }

    public void setTextName(String textName) {
        this.textName = textName;
    }

    public String[] getFile() {
        return file;
    }

    public void setFile(String[] file) {
        this.file = file;
    }

    public String[] getFilter() {
        return filter;
    }

    public void setFilter(String[] filter) {
        this.filter = filter;
    }

    public List<Detail> getOtherFile() {
        return otherFile;
    }

    public void setOtherFile(List<Detail> otherFile) {
        this.otherFile = otherFile;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSvnUrl() {
        return svnUrl;
    }

    public void setSvnUrl(String svnUrl) {
        this.svnUrl = svnUrl;
    }

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }
}