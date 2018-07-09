package com.zres.util.testSvnKit.option;

import com.zres.util.getChineseUtil.util.RegexUtil;
import com.zres.util.testSvnKit.bean.RepositoryInfo;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.*;

import java.io.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by wang xu on 2018/7/7.
 */
public class Option {
    private SVNClientManager ourClientManager;
    private SVNURL repositoryOptUrl;
    private String userName;
    private String password;
    private static File outDir=new File("C:\\Users\\lenovo\\Desktop\\wangxu.txt");
    public Option(String userName,String password){
        this.userName = userName;
        this.password = password;
    }
    private void setUpSVNClient(String userName,String passwd){
        SVNRepositoryFactoryImpl.setup();
        ISVNOptions options = SVNWCUtil.createDefaultOptions(true);
        ourClientManager = SVNClientManager.newInstance(
                (DefaultSVNOptions) options, userName, passwd);
    }
    public void downloadModel(String downloadModelName,String dirPath){
        setUpSVNClient(userName,password);
        File outDir=new File(dirPath+"/"+downloadModelName);
        //outDir.mkdirs();//创建目录
        SVNUpdateClient updateClient=ourClientManager.getUpdateClient();
        updateClient.setIgnoreExternals(false);
        try {
            repositoryOptUrl=SVNURL.parseURIEncoded(RepositoryInfo.storeUrl+downloadModelName);
            updateClient.doExport(repositoryOptUrl, outDir, SVNRevision.HEAD, SVNRevision.HEAD, "downloadModel",true,true);
        } catch (SVNException e) {
            e.printStackTrace();
        }
    }
    /**
     * 确定path是否是一个工作空间
     */
    public static boolean isWorkingCopy(File path) {
        if(!path.exists()) {
            return false;
        }
        try {
            if(null == SVNWCUtil.getWorkingCopyRoot(path, false)) {
                return false;
            }
        } catch (SVNException e) {
        }
        return true;
    }

    /**
     * 确定一个URL在SVN上是否存在
     */
    public static boolean isURLExist(SVNURL url, String username, String password) {
        try {
            SVNRepository svnRepository = SVNRepositoryFactory.create(url);
            ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(username, password);
            svnRepository.setAuthenticationManager(authManager);
            SVNNodeKind nodeKind = svnRepository.checkPath("", -1); //遍历SVN,获取结点。
            return nodeKind == SVNNodeKind.NONE ? false : true;
        } catch (SVNException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static void readSvnFile(SVNRepository svnRepository,String path) {
        Collection entry = null;
        try {
            entry = svnRepository.getDir(path, -1 ,null,(Collection)null);
        } catch (SVNException e) {
            e.printStackTrace();
        }
        Iterator iterator = entry.iterator();
        while(iterator.hasNext()){
            SVNDirEntry svnDirEntry = (SVNDirEntry)iterator.next();
            if(svnDirEntry.getKind() == SVNNodeKind.FILE){
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                SVNProperties svnProperties = new SVNProperties();
                //若svnProperties对象非空，使用svnProperties属性接收文件的属性
                try {
                    svnRepository.getFile(path+'/'+svnDirEntry.getName(),-1,svnProperties ,byteArrayOutputStream);
                    byteArrayOutputStream.writeTo(new FileOutputStream(outDir));
                } catch (SVNException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if(svnDirEntry.getKind() == SVNNodeKind.DIR){
                String tempPath = (path.equals("") ? svnDirEntry.getName() : path + "/" + svnDirEntry.getName()) ;
                readSvnFile(svnRepository,tempPath);
            }
        }
    }
}
