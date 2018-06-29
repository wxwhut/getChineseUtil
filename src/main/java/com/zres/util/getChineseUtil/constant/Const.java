package com.zres.util.getChineseUtil.constant;

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

    public static boolean isValidFileType (String fileType) {
        if("js,java,html,xml,properties".indexOf(fileType) > -1) {
            return true;
        }
        return false;
    }


    public static boolean isValidFilePath (String filePath) {
        if(filePath.contains("twaver") || filePath.contains("echarts") || filePath.contains("fish-desktop-locale")
                || (filePath.contains("arcgis_js_api")) || (filePath.contains("city.data")) || (filePath.contains("geoCoord.js"))
                || (filePath.contains("layer\\v3.0.3\\demo.html"))) {
            return false;
        }
        return true;
    }
}
