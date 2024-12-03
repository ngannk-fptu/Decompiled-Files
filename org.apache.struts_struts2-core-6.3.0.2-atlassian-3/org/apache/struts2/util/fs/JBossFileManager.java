/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.util.fs;

import com.opensymphony.xwork2.util.fs.DefaultFileManager;
import com.opensymphony.xwork2.util.fs.FileRevision;
import com.opensymphony.xwork2.util.fs.JarEntryRevision;
import com.opensymphony.xwork2.util.fs.Revision;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JBossFileManager
extends DefaultFileManager {
    private static final Logger LOG = LogManager.getLogger(JBossFileManager.class);
    private static final String JBOSS5_VFS = "vfs";
    private static final String JBOSS5_VFSZIP = "vfszip";
    private static final String JBOSS5_VFSMEMORY = "vfsmemory";
    private static final String JBOSS5_VFSFILE = "vfsfile";
    private static final String VFS_JBOSS7 = "org.jboss.vfs.VirtualFile";
    private static final String VFS_JBOSS5 = "org.jboss.virtual.VirtualFile";

    @Override
    public boolean support() {
        boolean supports;
        boolean bl = supports = this.isJBoss7() || this.isJBoss5();
        if (supports) {
            LOG.debug("JBoss server detected, Struts 2 will use [{}] to support file system operations!", (Object)JBossFileManager.class.getSimpleName());
        }
        return supports;
    }

    private boolean isJBoss5() {
        try {
            Class.forName(VFS_JBOSS5);
            return true;
        }
        catch (ClassNotFoundException e) {
            LOG.debug("Cannot load [{}] class, not a JBoss 5!", (Object)VFS_JBOSS5);
            return false;
        }
    }

    private boolean isJBoss7() {
        try {
            Class.forName(VFS_JBOSS7);
            return true;
        }
        catch (ClassNotFoundException e) {
            LOG.debug("Cannot load [{}] class, not a JBoss 7!", (Object)VFS_JBOSS7);
            return false;
        }
    }

    @Override
    public void monitorFile(URL fileUrl) {
        if (this.reloadingConfigs && this.isJBossUrl(fileUrl)) {
            String fileName = fileUrl.toString();
            LOG.debug("Creating revision for URL: {}", (Object)fileName);
            URL normalizedUrl = this.normalizeToFileProtocol(fileUrl);
            LOG.debug("Normalized URL for [{}] is [{}]", (Object)fileName, (Object)normalizedUrl);
            Revision revision = "file".equals(normalizedUrl.getProtocol()) ? FileRevision.build(normalizedUrl) : ("jar".equals(normalizedUrl.getProtocol()) ? JarEntryRevision.build(normalizedUrl) : Revision.build(normalizedUrl));
            files.put(fileName, revision);
        } else {
            super.monitorFile(fileUrl);
        }
    }

    @Override
    public URL normalizeToFileProtocol(URL url) {
        if (this.isJBossUrl(url)) {
            try {
                return this.getJBossPhysicalUrl(url);
            }
            catch (IOException e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error(e.getMessage(), (Throwable)e);
                }
                return null;
            }
        }
        return super.normalizeToFileProtocol(url);
    }

    @Override
    public Collection<? extends URL> getAllPhysicalUrls(URL url) throws IOException {
        if (this.isJBossUrl(url)) {
            return this.getAllJBossPhysicalUrls(url);
        }
        return super.getAllPhysicalUrls(url);
    }

    protected boolean isJBossUrl(URL fileUrl) {
        String protocol = fileUrl.getProtocol();
        return JBOSS5_VFSZIP.equals(protocol) || JBOSS5_VFSMEMORY.equals(protocol) || JBOSS5_VFS.equals(protocol) || "true".equals(System.getProperty("jboss.vfs.forceVfsJar")) && JBOSS5_VFSFILE.equals(protocol);
    }

    protected URL getJBossPhysicalUrl(URL url) throws IOException {
        Object content = url.openConnection().getContent();
        String classContent = content.getClass().toString();
        LOG.debug("Reading physical URL for [{}]", (Object)url);
        if (classContent.startsWith("class org.jboss.vfs.VirtualFile")) {
            File physicalFile = this.readJBossPhysicalFile(content);
            return physicalFile.toURI().toURL();
        }
        if (classContent.startsWith("class org.jboss.virtual.VirtualFile")) {
            return this.readJBoss5Url(content);
        }
        return url;
    }

    private List<URL> getAllJBossPhysicalUrls(URL url) throws IOException {
        ArrayList<URL> urls = new ArrayList<URL>();
        Object content = url.openConnection().getContent();
        String classContent = content.getClass().toString();
        if (classContent.startsWith("class org.jboss.vfs.VirtualFile")) {
            File physicalFile = this.readJBossPhysicalFile(content);
            if (physicalFile != null) {
                this.readFile(urls, physicalFile);
                this.readFile(urls, physicalFile.getParentFile());
            }
        } else if (classContent.startsWith("class org.jboss.virtual.VirtualFile")) {
            URL physicalUrl = this.readJBoss5Url(content);
            if (physicalUrl != null) {
                urls.add(physicalUrl);
            }
        } else {
            urls.add(url);
        }
        return urls;
    }

    private File readJBossPhysicalFile(Object content) {
        try {
            Method method = content.getClass().getDeclaredMethod("getPhysicalFile", new Class[0]);
            return (File)method.invoke(content, new Object[0]);
        }
        catch (NoSuchMethodException e) {
            LOG.error("Provided class content [{}] is not a JBoss VirtualFile, getPhysicalFile() method not found!", (Object)content.getClass().getSimpleName(), (Object)e);
        }
        catch (InvocationTargetException e) {
            LOG.error("Cannot invoke getPhysicalFile() method!", (Throwable)e);
        }
        catch (IllegalAccessException e) {
            LOG.error("Cannot access getPhysicalFile() method!", (Throwable)e);
        }
        return null;
    }

    private URL readJBoss5Url(Object content) {
        try {
            Method method = content.getClass().getDeclaredMethod("getHandler", new Class[0]);
            method.setAccessible(true);
            Object handler = method.invoke(content, new Object[0]);
            method = handler.getClass().getMethod("getRealURL", new Class[0]);
            return (URL)method.invoke(handler, new Object[0]);
        }
        catch (NoSuchMethodException e) {
            LOG.error("Provided class content [{}] is not a JBoss VirtualFile, getHandler() or getRealURL() method not found!", (Object)content.getClass().getSimpleName(), (Object)e);
        }
        catch (InvocationTargetException e) {
            LOG.error("Cannot invoke getHandler() or getRealURL() method!", (Throwable)e);
        }
        catch (IllegalAccessException e) {
            LOG.error("Cannot access getHandler() or getRealURL() method!", (Throwable)e);
        }
        return null;
    }

    private void readFile(List<URL> urls, File physicalFile) throws MalformedURLException {
        File[] files = physicalFile.listFiles();
        if (physicalFile.isDirectory() && files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    this.addIfAbsent(urls, file.toURI().toURL());
                    continue;
                }
                if (!file.isDirectory()) continue;
                this.readFile(urls, file);
            }
        }
    }

    private void addIfAbsent(List<URL> urls, URL fileUrl) {
        if (!urls.contains(fileUrl)) {
            urls.add(fileUrl);
        }
    }
}

