/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.buf.UriUtil
 */
package org.apache.catalina.webresources;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.zip.ZipInputStream;
import org.apache.catalina.webresources.AbstractArchiveResource;
import org.apache.catalina.webresources.AbstractArchiveResourceSet;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.UriUtil;

public class JarWarResource
extends AbstractArchiveResource {
    private static final Log log = LogFactory.getLog(JarWarResource.class);
    private final String archivePath;

    public JarWarResource(AbstractArchiveResourceSet archiveResourceSet, String webAppPath, String baseUrl, JarEntry jarEntry, String archivePath) {
        super(archiveResourceSet, webAppPath, "jar:war:" + baseUrl + UriUtil.getWarSeparator() + archivePath + "!/", jarEntry, "war:" + baseUrl + UriUtil.getWarSeparator() + archivePath);
        this.archivePath = archivePath;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected AbstractArchiveResource.JarInputStreamWrapper getJarInputStreamWrapper() {
        JarFile warFile = null;
        ZipInputStream jarIs = null;
        JarEntry entry = null;
        try {
            warFile = this.getArchiveResourceSet().openJarFile();
            JarEntry jarFileInWar = warFile.getJarEntry(this.archivePath);
            InputStream isInWar = warFile.getInputStream(jarFileInWar);
            jarIs = new JarInputStream(isInWar);
            entry = ((JarInputStream)jarIs).getNextJarEntry();
            while (entry != null && !entry.getName().equals(this.getResource().getName())) {
                entry = ((JarInputStream)jarIs).getNextJarEntry();
            }
            if (entry == null) {
                AbstractArchiveResource.JarInputStreamWrapper jarInputStreamWrapper = null;
                return jarInputStreamWrapper;
            }
            AbstractArchiveResource.JarInputStreamWrapper jarInputStreamWrapper = new AbstractArchiveResource.JarInputStreamWrapper(entry, jarIs);
            return jarInputStreamWrapper;
        }
        catch (IOException e) {
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("jarResource.getInputStreamFail", new Object[]{this.getResource().getName(), this.getBaseUrl()}), (Throwable)e);
            }
            entry = null;
            AbstractArchiveResource.JarInputStreamWrapper jarInputStreamWrapper = null;
            return jarInputStreamWrapper;
        }
        finally {
            if (entry == null) {
                if (jarIs != null) {
                    try {
                        jarIs.close();
                    }
                    catch (IOException iOException) {}
                }
                if (warFile != null) {
                    this.getArchiveResourceSet().closeJarFile();
                }
            }
        }
    }

    @Override
    protected Log getLog() {
        return log;
    }
}

