package com.zres.util.getChineseUtil.util;

/**
 * Created by wang xu on 2018/6/7.
 */
public class RegexUtil {
    public static String getFileType(String filePath) {
        String fileType = "";
        int filePathLength = filePath.length();
        if(filePathLength > 0) {
            for(int i = filePathLength - 1; i > 0; i--) {
                if(filePath.charAt(i) == '.') {
                    fileType = filePath.substring(i + 1,filePathLength);
                    break;
                }
            }
        }
        return fileType;
    }

    public static boolean isChineseCharacter(char c) {
        if(c >= 0x4e00 && c <= 0x9fbb) {
            return true;
        }
        return false;
    }


}
