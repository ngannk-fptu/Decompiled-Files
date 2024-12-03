/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.buf.UriUtil
 *  org.apache.tomcat.util.compat.JreCompat
 */
package org.apache.catalina.webresources;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.webresources.AbstractArchiveResourceSet;
import org.apache.tomcat.util.buf.UriUtil;
import org.apache.tomcat.util.compat.JreCompat;

public abstract class AbstractSingleArchiveResourceSet
extends AbstractArchiveResourceSet {
    private volatile Boolean multiRelease;

    public AbstractSingleArchiveResourceSet() {
    }

    public AbstractSingleArchiveResourceSet(WebResourceRoot root, String webAppMount, String base, String internalPath) throws IllegalArgumentException {
        this.setRoot(root);
        this.setWebAppMount(webAppMount);
        this.setBase(base);
        this.setInternalPath(internalPath);
        if (this.getRoot().getState().isAvailable()) {
            try {
                this.start();
            }
            catch (LifecycleException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected Map<String, JarEntry> getArchiveEntries(boolean single) {
        Object object = this.archiveLock;
        synchronized (object) {
            if (this.archiveEntries == null && !single) {
                JarFile jarFile = null;
                this.archiveEntries = new HashMap();
                try {
                    jarFile = this.openJarFile();
                    Enumeration<JarEntry> entries = jarFile.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        this.archiveEntries.put(entry.getName(), entry);
                    }
                }
                catch (IOException ioe) {
                    this.archiveEntries = null;
                    throw new IllegalStateException(ioe);
                }
                finally {
                    if (jarFile != null) {
                        this.closeJarFile();
                    }
                }
            }
            return this.archiveEntries;
        }
    }

    @Override
    protected JarEntry getArchiveEntry(String pathInArchive) {
        JarFile jarFile = null;
        try {
            jarFile = this.openJarFile();
            JarEntry jarEntry = jarFile.getJarEntry(pathInArchive);
            return jarEntry;
        }
        catch (IOException ioe) {
            throw new IllegalStateException(ioe);
        }
        finally {
            if (jarFile != null) {
                this.closeJarFile();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected boolean isMultiRelease() {
        if (this.multiRelease == null) {
            Object object = this.archiveLock;
            synchronized (object) {
                if (this.multiRelease == null) {
                    JarFile jarFile = null;
                    try {
                        jarFile = this.openJarFile();
                        this.multiRelease = JreCompat.getInstance().jarFileIsMultiRelease(jarFile);
                    }
                    catch (IOException ioe) {
                        throw new IllegalStateException(ioe);
                    }
                    finally {
                        if (jarFile != null) {
                            this.closeJarFile();
                        }
                    }
                }
            }
        }
        return this.multiRelease;
    }

    @Override
    protected void initInternal() throws LifecycleException {
        try (JarFile jarFile = JreCompat.getInstance().jarFileNewInstance(this.getBase());){
            this.setManifest(jarFile.getManifest());
        }
        catch (IOException ioe) {
            throw new IllegalArgumentException(ioe);
        }
        try {
            this.setBaseUrl(UriUtil.buildJarSafeUrl((File)new File(this.getBase())));
        }
        catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }
}

