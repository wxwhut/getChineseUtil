package com.zres.util.testSvnKit;

import com.zres.util.testSvnKit.option.Option;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

/**
 * Created by wang xu on 2018/7/7.
 */
public class test {
    public static void main(String[] args){
        //1.根据访问协议初始化工厂
        DAVRepositoryFactory.setup();;
        //2.初始化仓库
        String url = "http://10.45.53.12:8484/svn/rescloud/trunk/resmaster/product/res-device";
        SVNRepository svnRepository = null;
        try {
            svnRepository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(url));
        } catch (SVNException e) {
            e.printStackTrace();
        }
        //3.创建一个访问的权限
        String username="wang.xu2";
        String password="1234";
        ISVNAuthenticationManager authenticationManager = SVNWCUtil.createDefaultAuthenticationManager(username,password);
        svnRepository.setAuthenticationManager(authenticationManager);

        String checkUrl = "";
        //修订版本号，-1代表一个无效的修订版本号，代表必须是最新的修订版
        long revisionNum = -1;
//        Option option = new Option(username,password);
//        option.downloadModel("","C:\\Users\\lenovo\\Desktop\\wangxu");
        try {
            Option.readSvnFile(svnRepository,checkUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
