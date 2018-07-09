package com.zres.util.getChineseUtil.bean;

import com.zres.util.getChineseUtil.util.RegexUtil;

/**
 * Created by wang xu on 2018/7/9.
 */
public class FileInfo {
    private String context;
    private String fileType;
    private String filePath;
    private long version;
    private String lastAuthor;

    public String getContext() {
        return context;
    }

    public void setContext(String line) {
        this.context = line;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getLastAuthor() {
        return lastAuthor;
    }

    public void setLastAuthor(String lastAuthor) {
        this.lastAuthor = lastAuthor;
    }
}
