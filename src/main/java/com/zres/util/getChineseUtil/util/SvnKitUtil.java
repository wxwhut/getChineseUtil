package com.zres.util.getChineseUtil.util;

import com.zres.util.getChineseUtil.bean.Setting;
import com.zres.util.testSvnKit.option.Option;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import java.io.File;

/**
 * Created by wang xu on 2018/7/9.
 */
public class SvnKitUtil {
  public static SVNRepository start(Setting setting){
    //1.根据访问协议初始化工厂
        DAVRepositoryFactory.setup();;
    //2.初始化仓库
    String url = "http://10.45.53.12:8484/svn/rescloud/trunk/resmaster/product/res-device";
    SVNRepository svnRepository;
      svnRepository = null;
      try {
        svnRepository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(setting.getDirPath()));
    } catch (SVNException e) {
        e.printStackTrace();
    }
    //3.创建一个访问的权限
    String username="wang.xu2";
    String password="1234";
    ISVNAuthenticationManager authenticationManager = SVNWCUtil.createDefaultAuthenticationManager(username,password);
    svnRepository.setAuthenticationManager(authenticationManager);
    //修订版本号，-1代表一个无效的修订版本号，代表必须是最新的修订版
    //long revisionNum = -1;
    return svnRepository;
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
            e.printStackTrace();
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

}