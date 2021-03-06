package com.zres.util.getChineseUtil;

import com.zres.util.getChineseUtil.bean.FileInfo;
import com.zres.util.getChineseUtil.bean.Setting;
import com.zres.util.getChineseUtil.constant.Const;
import com.zres.util.getChineseUtil.util.RegexUtil;
import com.zres.util.getChineseUtil.util.SvnKitUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.io.SVNRepository;


import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.util.*;


@SpringBootApplication
public class getChineseUtilApplication {

    public static StringBuffer result = new StringBuffer();
    /**记录运行状态*/
    public static ArrayList<String> status = new ArrayList<>();
    public static void main(String[] args) {
        SpringApplication.run(getChineseUtilApplication.class, args);
    }

    public static ArrayList<String> start(Setting setting){
        result = new StringBuffer();
        status = new ArrayList<>();
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
        writer = setup(setting);
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
        if(setting.getType().equals("0")){
            readFile(setting.getDirPath(),setting);
        }else if(setting.getType().equals("2")){
            SVNRepository svnRepository = SvnKitUtil.start(setting);
            if (svnRepository==null){
                status.add("SVN");
                return status;
            }else{
            readSvnFile(svnRepository, "", setting);
            }
        }else if(setting.getType().equals("1")){
            try {
                SvnKitUtil.downloadModel(setting);
            } catch (SVNException e) {
                status.add("SVN");
                e.printStackTrace();
                return status;
            }
            String[] path = setting.getDirPath().split("\\/").length>0?setting.getDirPath().split("\\/"):setting.getDirPath().split("\\\\");
            StringBuffer pathName = new StringBuffer();
            pathName.append(setting.getTextPath()).append("/getChineseSVNCache");
            for(int i=3;i<path.length;i++){
                pathName.append("/"+path[i]);
            }
            readFile(pathName.toString(),setting);
        }
        if(writer!=null) {
            writer.println(result);
            writer.close();
        }
        return status;
    }
    public static PrintWriter setup(Setting setting){
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(setting.getTextPath()+setting.getTextName(), "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            status.add("filePath");
            return writer;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            status.add("encoding") ;
            return writer;
        }
        return writer;
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
            status.add("dirPath");
        }
    }
    //SVN路径
    public static void readSvnFile(SVNRepository svnRepository, String path ,Setting setting){
        Collection entry = null;
        try {
            entry = svnRepository.getDir(path, -1 ,null,(Collection)null);
        } catch (SVNException e) {
            e.printStackTrace();
        }
        Iterator iterator = null;
        if(entry!=null){
            iterator = entry.iterator();
        }
        while(iterator.hasNext()){
            SVNDirEntry svnDirEntry = (SVNDirEntry)iterator.next();
            if(svnDirEntry.getKind() == SVNNodeKind.FILE){
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                SVNProperties svnProperties = new SVNProperties();
                //若svnProperties对象非空，使用svnProperties属性接收文件的属性
                try {
                    FileInfo fileInfo = new FileInfo();
                    svnRepository.getFile(path+'/'+svnDirEntry.getName(),-1,svnProperties ,byteArrayOutputStream);
                    fileInfo.setContext(byteArrayOutputStream.toString());
                    fileInfo.setFileType(RegexUtil.getFileType(svnDirEntry.getName()));
                    fileInfo.setFilePath(path+'/'+svnDirEntry.getName());
                    fileInfo.setVersion(svnDirEntry.getRevision());
                    fileInfo.setLastAuthor(svnDirEntry.getAuthor());
                    parseSvnFile(fileInfo,setting);
                } catch (SVNException e) {
                    e.printStackTrace();
                }
            }else if(svnDirEntry.getKind() == SVNNodeKind.DIR){
                String tempPath = (path.equals("") ? svnDirEntry.getName() : path + "/" + svnDirEntry.getName()) ;
                readSvnFile(svnRepository,tempPath,setting);
            }
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
            status.add("error");
        }
    }
    public static void parseSvnFile(FileInfo fileInfo,Setting setting){
        String filePath = fileInfo.getFilePath();
        String fileType = fileInfo.getFileType();
        BufferedReader br = new BufferedReader(new StringReader(fileInfo.getContext()));
        try {
            String line = br.readLine();
            int lineNum = 0;
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
            status.add("SVNERROR");
        }
    }
}
