package com.zres.util.getChineseUtil;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@SpringBootApplication
public class getChineseUtilApplication {

    public static StringBuffer result = new StringBuffer();
    public static String status = new String();
    public static void main(String[] args) {
        SpringApplication.run(getChineseUtilApplication.class, args);

    }

    public static String start(Setting setting){
        result = new StringBuffer();
        status = "success";
        File desktopDir = FileSystemView.getFileSystemView().getHomeDirectory();
        //设置路径和文件名，默认放在桌面
        if(setting.getDirPath().equals("")) {
            setting.setDirPath(desktopDir.getAbsolutePath());
        }
        if(setting.getTextPath().equals("")) {
          setting.setTextPath(desktopDir.getAbsolutePath());
        }
        //设置文件名，默认fileNote.txt
        if(setting.getTextName().equals("")) {
            setting.setTextName("\\fileNote.txt");
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
            return "filePath";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "encoding";
        }
        //处理自定义文件后缀
        List<String> list;
        if(setting.getFile()==null){
            list = new ArrayList<>();
        }else{
            List<String> l = Arrays.asList(setting.getFile());
            list = new ArrayList(l);
        }
        if(setting.getOtherFile()!=null) {
            for (int i = 0; i < setting.getOtherFile().size(); i++) {
                list.add(setting.getOtherFile().get(i).getName());
            }
        }
        setting.setFile(list.toArray(new String[list.size()]));
        readFile(setting.getDirPath(),setting);
        writer.println(result);
        writer.close();
        return status;
    }
    public static void readFile(String dir,Setting setting) {
        File or = new File(dir);
        File[] files = or.listFiles();
        if(files != null) {
            int fileLength = files.length;
            for(int i = 0; i < fileLength; i++) {
                File file = files[i];
                if(file.isFile()) {
                    parseFile(file,setting);
                } else if(file.isDirectory()) {
                    readFile(file.getAbsolutePath(),setting);
                }
            }
        }else{
            status="dirPath";
        }
    }


    public static void parseFile(File file,Setting setting) {
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
            if(!Const.isValidFileType(fileType,setting)) {
                return;
            }
            if(!Const.isValidFilePath(filePath,setting)) {
                return;
            }
            //获取自定义文件的注释
            String startAnnotation="";
            String endAnnotation="";
            String lineAnnotation="";
            if(setting.getOtherFile()!=null) {
                for (int i = 0; i < setting.getOtherFile().size(); i++) {
                    if (setting.getOtherFile().get(i).getName().equalsIgnoreCase(fileType)) {
                        startAnnotation = setting.getOtherFile().get(i).getStart();
                        endAnnotation = setting.getOtherFile().get(i).getEnd();
                        lineAnnotation = setting.getOtherFile().get(i).getLine();
                    }
                }
            }
            boolean isBetweenAnnotation = false;
            while(line != null) {
                lineNum ++;
                int lineLength = line.length();
                //过滤掉注释部分内容
                if(isBetweenAnnotation) {
                    int annotationEndIndex;
                    if(endAnnotation.equals("")){
                         annotationEndIndex = line.indexOf(Const.getAnnotationEnd(fileType));
                    }else{
                         annotationEndIndex = line.indexOf(endAnnotation);
                    }
                    if(annotationEndIndex >= 0) {
                        isBetweenAnnotation = false;
                        line = line.substring(annotationEndIndex + 1, lineLength);
                    }else{
                        line = br.readLine();
                        continue;
                    }
                }else{
                    int annotationBeginIndex;
                    int annotationWithNoEndIndex;
                    if(startAnnotation.equals("")){
                        annotationBeginIndex = line.indexOf(Const.getAnnotationBegin(fileType));
                    }else{
                        annotationBeginIndex = line.indexOf(startAnnotation);
                    }
                    if (lineAnnotation.equals("")){
                        annotationWithNoEndIndex = line.indexOf(Const.getAnnotationWithNoEnd(fileType));
                    }else{
                        annotationWithNoEndIndex = line.indexOf(lineAnnotation);
                    }
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
                    for (int j = 0; j < lineLength; j++) {
                        if (RegexUtil.isChineseCharacter(line.charAt(j)) && !isContinue) {
                            chineseCharBegin = j;
                            isContinue = true;
                            if(j==lineLength-1){
                                result.append(line.charAt(lineLength-1) + "\t" + filePath + "：第"+ lineNum + "行\n");
                            }
                        } else if ((!RegexUtil.isChineseCharacter(line.charAt(j)) && isContinue)) {
                            chineseCharEnd = j;
                            isContinue = false;
                            String chineseCharStr = line.substring(chineseCharBegin, chineseCharEnd);
                            result.append(chineseCharStr + "\t" + filePath + "：第" + lineNum + "行\n");
                        } else if (RegexUtil.isChineseCharacter(line.charAt(j)) && isContinue && j == lineLength - 1) {
                            chineseCharEnd = j;
                            isContinue = false;
                            String chineseCharStr = line.substring(chineseCharBegin, chineseCharEnd + 1);
                            result.append(chineseCharStr + "\t" + filePath + "：第" + lineNum + "行\n");
                        }
                    }
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            status="error";
        }


    }
}
