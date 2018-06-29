package com.zres.util.getChineseUtil.bean;

import java.util.HashMap;

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
    /**其他文件和注释类型*/
    private HashMap<String,String> other;

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

    public HashMap<String, String> getOther() {
        return other;
    }

    public void setOther(HashMap<String, String> other) {
        this.other = other;
    }

}