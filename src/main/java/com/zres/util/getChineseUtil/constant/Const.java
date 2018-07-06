package com.zres.util.getChineseUtil.constant;

import com.zres.util.getChineseUtil.bean.Setting;

/**
 * Created by tallenty on 2018/6/7.
 */
public class Const {
    public static final String JS = "js";
    public static final String JAVA = "java";
    public static final String XML = "xml";
    public static final String HTML = "html";
    public static final String PROPERTIES = "properties";

    public static String getAnnotationBegin(String fileType) {
        String annotationBeginStr = "@$%&^*";
        switch (fileType) {
            case JS :
            case JAVA :
                annotationBeginStr = "/*";
                break;
            case HTML :
            case XML :
                annotationBeginStr = "<!--";
                break;
        }
        return annotationBeginStr;
    }


    public static String getAnnotationEnd(String fileType) {
        String annotationEndStr = "@$%&^*";
        switch (fileType) {
            case JS :
            case JAVA :
                annotationEndStr = "*/";
                break;
            case HTML :
            case XML :
                annotationEndStr = "-->";
                break;
        }
        return annotationEndStr;
    }

    public static String getAnnotationWithNoEnd(String fileType) {
        String annotationStr = "@$%&^*";
        switch (fileType) {
            case JS :
            case JAVA :
                annotationStr = "//";
                break;
            case PROPERTIES :
                annotationStr = "#";
                break;
        }
        return annotationStr;
    }

    public static boolean isValidFileType (String fileType, Setting setting) {
        if(setting.getFile()!=null) {
            for (int i = 0; i < setting.getFile().length; i++) {
                if (setting.getFile()[i].equalsIgnoreCase(fileType)) {
                    return true;
                }
            }
        }
        return false;
    }


    public static boolean isValidFilePath (String filePath,Setting setting) {
        if(setting.getFilter()!=null) {
            for (int i = 0; i < setting.getFilter().length; i++) {
                if (filePath.contains(setting.getFilter()[i])&&!setting.getFilter()[i].equals("")) {
                    return false;
                }
            }
        }
        return true;
    }
}
