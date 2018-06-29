package com.zres.util.getChineseUtil.service;

import com.zres.util.getChineseUtil.bean.Setting;
import com.zres.util.getChineseUtil.constant.Const;
import com.zres.util.getChineseUtil.util.RegexUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import javax.swing.filechooser.FileSystemView;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;


@SpringBootApplication
public class GetChineseUtilApplication {

    public static StringBuffer result = new StringBuffer();

    public static void main(String[] args) {
        SpringApplication.run(GetChineseUtilApplication.class, args);

    }

    public static void start(Setting setting){
        File desktopDir = FileSystemView.getFileSystemView().getHomeDirectory();
        //设置路径和文件名，默认放在桌面
        if(setting.getDirPath()=="") {
            setting.setDirPath(desktopDir.getAbsolutePath());
        }
        if(setting.getTextPath()=="") {
          setting.setTextPath(desktopDir.getAbsolutePath());
        }
        //设置文件名，默认fileNote.txt
        if(setting.getTextName()=="") {
            setting.setTextPath("\\fileNote.txt");
        }else if(setting.getTextName().charAt(0)!='\\'){
            setting.setTextName('\\'+setting.getTextName());
        }
        //补充后缀名.txt
        if(setting.getTextName().indexOf('.')==-1) {
            setting.setTextName(setting.getTextName()+".txt");
        }
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(setting.getTextPath()+setting.getTextName(), "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        readFile(setting.getDirPath());
        writer.println(result);
        writer.close();
    }
    public static void readFile(String dir) {
        File or = new File(dir);
        File[] files = or.listFiles();
        if(files != null) {
            int fileLength = files.length;
            for(int i = 0; i < fileLength; i++) {
                File file = files[i];
                if(file.isFile()) {
                    parseFile(file);
                } else if(file.isDirectory()) {
                    readFile(file.getAbsolutePath());
                }
            }
        }
    }


    public static void parseFile(File file) {
        String filePath = file.getAbsolutePath();
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader br = new BufferedReader(reader);
        try {
            String line = br.readLine();
            int lineNum = 0;
            String fileType = RegexUtil.getFileType(filePath);
            //只查找java，js，html，xml，properties文件
            if(!Const.isValidFileType(fileType)) {
                return;
            }
            if(!Const.isValidFilePath(filePath)) {
                return;
            }
            boolean isBetweenAnnotation = false;
            while(line != null) {
                lineNum ++;
                int lineLength = line.length();
                //过滤掉注释部分内容
                if(isBetweenAnnotation) {
                    int annotationEndIndex = line.indexOf(Const.getAnnotationEnd(fileType));
                    if(annotationEndIndex >= 0) {
                        isBetweenAnnotation = false;
                        line = line.substring(annotationEndIndex + 1, lineLength);
                    }else{
                        line = br.readLine();
                        continue;
                    }
                }else{
                    int annotationBeginIndex = line.indexOf(Const.getAnnotationBegin(fileType));
                    int annotationWithNoEndIndex = line.indexOf(Const.getAnnotationWithNoEnd(fileType));
                    int minBeginIndex = 100000;
                    if(annotationBeginIndex < 0) {
                        annotationBeginIndex = 100000;
                    }
                    if(annotationWithNoEndIndex < 0) {
                        annotationWithNoEndIndex = 100000;
                    }
                    if(annotationBeginIndex < annotationWithNoEndIndex) {
                        isBetweenAnnotation = true;
                        minBeginIndex = annotationBeginIndex;
                    }
                    if(annotationBeginIndex > annotationWithNoEndIndex) {
                        minBeginIndex = annotationWithNoEndIndex;
                    }
                    try {
                        if(minBeginIndex < 100000) {
                            line = line.substring(0, minBeginIndex);
                        }
                    }catch (Exception e){
                        System.out.print("错误信息："+line+":"+minBeginIndex);
                        System.out.println();
                    }
                }
                //提取中文
                lineLength = line.length();
                int chineseCharBegin = 0;
                int chineseCharEnd = 0;
                boolean isContinue = false;
                for(int j=0; j<lineLength; j++) {
                    if(RegexUtil.isChineseCharacter(line.charAt(j)) && !isContinue) {
                        chineseCharBegin = j;
                        isContinue = true;
                    }else if((!RegexUtil.isChineseCharacter(line.charAt(j)) && isContinue)) {
                        chineseCharEnd = j;
                        isContinue = false;
                        String chineseCharStr = line.substring(chineseCharBegin,chineseCharEnd);
                        result.append(chineseCharStr + "\t" + filePath + "："+ lineNum + "\n");
                    }else if(RegexUtil.isChineseCharacter(line.charAt(j)) && isContinue && j == lineLength - 1) {
                        chineseCharEnd = j;
                        isContinue = false;
                        String chineseCharStr = line.substring(chineseCharBegin,chineseCharEnd + 1);
                        result.append(chineseCharStr + "\t" + filePath + "："+ lineNum + "\n");
                    }
                }
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
