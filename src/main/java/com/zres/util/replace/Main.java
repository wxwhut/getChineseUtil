package com.zres.util.replace;

import java.io.*;

/**
 * Created by wang xu on 2018/7/10.
 * 用来替换常量类里的中文
 */
public class Main {
    private static String dirPath="C:\\Users\\lenovo\\Desktop\\res-device";
    private static String constantClass="DeviceMessage";
    private static String newStr="ResMessageSourceUtil.getMessage(\"devicecore.*\")";
    public static void main(String[] args){
        findClass(dirPath);
    }
    public static void findClass(String dir){
        File or = new File(dir);
        File[] files = or.listFiles();
        if(files != null) {
            int fileLength = files.length;
            for(int i = 0; i < fileLength; i++) {
                File file = files[i];
                if(file.isFile()&&!file.getAbsolutePath().contains(".svn")) {
                    repalce(file);
                } else if(file.isDirectory()) {
                    findClass(file.getAbsolutePath());
                }
            }
        }
    }
    public static void repalce(File file){
        InputStreamReader reader =null;
        OutputStreamWriter writer = null;
        try {
            File temp = new File(file.getParent()+"/temp");
            reader = new InputStreamReader(new FileInputStream(file));
            writer = new OutputStreamWriter(new FileOutputStream(temp));
            BufferedReader br = new BufferedReader(reader);
            BufferedWriter wr = new BufferedWriter(writer);
            String line = null;
            boolean flag = false;
            while ((line = br.readLine()) != null) {
                if(line.contains(constantClass+".")){
                    flag=true;
                    String old = getOld(line);
                    String n = getnew(old);
                    line=line.replace(old,n);
                }
                wr.write(line);
                wr.write("\r\n");
            }
            br.close();
            wr.close();
            if(flag) {
                String name = file.getAbsolutePath();
                file.delete();
                temp.renameTo(new File(name));
            }else {
                temp.delete();
            }
        }catch (IOException e){
            e.printStackTrace();
        }

    }
    public static String getOld(String line){
        int start=line.indexOf(constantClass);
        int i=start;
        char[] c =line.toCharArray();
        for (i=start+constantClass.length()+1;i<c.length;i++){
            if (!((c[i]>='a'&&c[i]<='z')||(c[i]>='A'&&c[i]<='z')||(c[i]=='_')||(c[i]>='0'&&c[i]<='9'))){
                break;
            }
        }
        String old = new String(c).substring(start,i);
        return old;
    }
    public static String getnew(String old){
        String s=old.split("\\.")[1];
        String newString=newStr.replace("*",s);
        return newString;
    }
}
