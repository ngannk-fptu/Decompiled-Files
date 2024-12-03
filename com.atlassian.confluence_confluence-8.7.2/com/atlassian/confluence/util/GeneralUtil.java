/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.util.BootstrapUtils
 *  com.atlassian.core.filters.ServletContextThreadLocal
 *  com.atlassian.dc.filestore.api.compat.FilesystemPath
 *  com.atlassian.dc.filestore.impl.filesystem.FilesystemFileStore
 *  com.atlassian.seraph.config.SecurityConfigFactory
 *  com.atlassian.spring.container.ContainerManager
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.struts2.ServletActionContext
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.util;

import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.setup.SetupContext;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.actions.SpaceActionForDecorating;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.SafeGeneralUtil;
import com.atlassian.confluence.util.SetupLocaleDummyWiredAction;
import com.atlassian.confluence.util.zip.FileUnzipper;
import com.atlassian.confluence.util.zip.Unzipper;
import com.atlassian.confluence.util.zip.UrlUnzipper;
import com.atlassian.core.filters.ServletContextThreadLocal;
import com.atlassian.dc.filestore.api.compat.FilesystemPath;
import com.atlassian.dc.filestore.impl.filesystem.FilesystemFileStore;
import com.atlassian.seraph.config.SecurityConfigFactory;
import com.atlassian.spring.container.ContainerManager;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Date;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GeneralUtil
extends SafeGeneralUtil {
    public static final GeneralUtil INSTANCE = new GeneralUtil();
    private static final Logger LOG = LoggerFactory.getLogger(GeneralUtil.class);
    private static UserAccessor userAccessor;

    public static void setSystemStartupTime(Long systemStartupTime) {
        GeneralUtil.systemStartupTime = systemStartupTime;
    }

    public static File createTempDirectoryInConfluenceTemp(String prefix) {
        Date date = new Date();
        String pattern = "_{0,date,MMddyyyy}_{1,time,HHmmss}";
        String uniqueRandomFileName = MessageFormat.format(pattern, date, date);
        Path basePath = Path.of(BootstrapUtils.getBootstrapManager().getFilePathProperty("struts.multipart.saveDir"), new String[0]);
        FilesystemPath fileBasePath = FilesystemFileStore.forPath((Path)basePath);
        FilesystemPath canonicalFilePath = fileBasePath.path(new String[]{prefix + uniqueRandomFileName});
        return canonicalFilePath.asJavaFile();
    }

    public static File createTempFile(String directory) {
        Date date = new Date();
        String pattern = "_{0,date,MMddyyyy}_{1,time,HHmmss}";
        String uniqueRandomFileName = MessageFormat.format(pattern, date, date);
        return new File(directory, uniqueRandomFileName);
    }

    public static Cookie setCookie(String key, String value) {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        int cookieAge = 31104000;
        String path = request.getContextPath();
        if (StringUtils.isEmpty((CharSequence)path)) {
            path = "/";
        }
        return GeneralUtil.setCookie(request, response, key, value, cookieAge, path);
    }

    private static Cookie setCookie(HttpServletRequest request, HttpServletResponse response, String name, String value, int maxAge, String path) {
        boolean isInsecureCookie = SecurityConfigFactory.getInstance().isInsecureCookie();
        if (LOG.isDebugEnabled()) {
            LOG.debug("setCookie " + name + ":" + value + " path : " + path + " maxAge : " + maxAge + (isInsecureCookie ? " insecure" : " secure"));
        }
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setPath(path);
        if (!isInsecureCookie) {
            cookie.setSecure(request.isSecure());
        }
        response.addCookie(cookie);
        return cookie;
    }

    public static void unzipFile(File zipFile, File dirToExtractTo) throws IOException {
        Unzipper fileUnzipper = GeneralUtil.getUnzipper(zipFile, dirToExtractTo);
        fileUnzipper.unzip();
    }

    public static Unzipper getUnzipper(File zipFile, File dirToExtractTo) throws IOException {
        if (!zipFile.isFile()) {
            throw new IOException("Zip file doesn't exist or Confluence doesn't have read access to it. backupedFile=" + zipFile);
        }
        return new FileUnzipper(zipFile, dirToExtractTo);
    }

    public static void unzipUrl(URL zipUrl, File dirToExtractTo) throws IOException {
        Unzipper urlUnzipper = GeneralUtil.getUnzipper(zipUrl, dirToExtractTo);
        urlUnzipper.unzip();
    }

    public static Unzipper getUnzipper(URL zipUrl, File dirToExtractTo) {
        return new UrlUnzipper(zipUrl, dirToExtractTo);
    }

    @Deprecated
    public static UserAccessor getUserAccessor() {
        if (userAccessor == null) {
            userAccessor = (UserAccessor)ContainerManager.getComponent((String)"userAccessor");
        }
        return userAccessor;
    }

    @Deprecated(since="8.0")
    public static ConfluenceActionSupport newWiredConfluenceActionSupport() {
        return GeneralUtil.newWiredConfluenceActionSupport(null);
    }

    @Deprecated(since="8.0")
    public static ConfluenceActionSupport newWiredConfluenceActionSupport(@Nullable Space space) {
        ConfluenceActionSupport dummy;
        ConfluenceActionSupport confluenceActionSupport = dummy = space == null ? new ConfluenceActionSupport() : new SpaceActionForDecorating(space);
        if (ContainerManager.isContainerSetup()) {
            ContainerManager.autowireComponent((Object)dummy);
        } else if (ServletContextThreadLocal.getRequest() != null && SetupContext.get() != null) {
            dummy = new SetupLocaleDummyWiredAction();
            SetupContext.get().getBeanFactory().autowireBeanProperties((Object)new SetupLocaleDummyWiredAction(), 1, false);
        }
        return dummy;
    }

    @Deprecated
    public static File getConfluenceTempDirectory() {
        return GeneralUtil.getLocalTempDirectory();
    }

    @Deprecated
    public static File getLocalTempDirectory() {
        BootstrapManager bootstrapManager = (BootstrapManager)BootstrapUtils.getBootstrapManager();
        return GeneralUtil.createOrGetTempDirectory(bootstrapManager.getLocalHome());
    }

    @Deprecated
    public static File getSharedTempDirectory() {
        BootstrapManager bootstrapManager = (BootstrapManager)BootstrapUtils.getBootstrapManager();
        return GeneralUtil.createOrGetTempDirectory(bootstrapManager.getSharedHome());
    }

    private static File createOrGetTempDirectory(File baseDir) {
        File tempDir = new File(baseDir, "temp");
        tempDir.mkdirs();
        if (!tempDir.exists()) {
            throw new RuntimeException("Could not create temp directory '" + tempDir + "'");
        }
        return tempDir;
    }
}

