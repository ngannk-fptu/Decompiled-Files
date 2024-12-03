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
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResource;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.webresources.AbstractArchiveResourceSet;
import org.apache.catalina.webresources.JarWarResource;
import org.apache.catalina.webresources.TomcatJarInputStream;
import org.apache.tomcat.util.buf.UriUtil;
import org.apache.tomcat.util.compat.JreCompat;

public class JarWarResourceSet
extends AbstractArchiveResourceSet {
    private final String archivePath;

    public JarWarResourceSet(WebResourceRoot root, String webAppMount, String base, String archivePath, String internalPath) throws IllegalArgumentException {
        this.setRoot(root);
        this.setWebAppMount(webAppMount);
        this.setBase(base);
        this.archivePath = archivePath;
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

    @Override
    protected WebResource createArchiveResource(JarEntry jarEntry, String webAppPath, Manifest manifest) {
        return new JarWarResource(this, webAppPath, this.getBaseUrlString(), jarEntry, this.archivePath);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected Map<String, JarEntry> getArchiveEntries(boolean single) {
        Object object = this.archiveLock;
        synchronized (object) {
            if (this.archiveEntries == null) {
                JarFile warFile = null;
                InputStream jarFileIs = null;
                this.archiveEntries = new HashMap();
                boolean multiRelease = false;
                try {
                    warFile = this.openJarFile();
                    JarEntry jarFileInWar = warFile.getJarEntry(this.archivePath);
                    jarFileIs = warFile.getInputStream(jarFileInWar);
                    try (TomcatJarInputStream jarIs = new TomcatJarInputStream(jarFileIs);){
                        String value;
                        JarEntry entry = jarIs.getNextJarEntry();
                        while (entry != null) {
                            this.archiveEntries.put(entry.getName(), entry);
                            entry = jarIs.getNextJarEntry();
                        }
                        Manifest m = jarIs.getManifest();
                        this.setManifest(m);
                        if (m != null && JreCompat.isJre9Available() && (value = m.getMainAttributes().getValue("Multi-Release")) != null) {
                            multiRelease = Boolean.parseBoolean(value);
                        }
                        if ((entry = jarIs.getMetaInfEntry()) != null) {
                            this.archiveEntries.put(entry.getName(), entry);
                        }
                        if ((entry = jarIs.getManifestEntry()) != null) {
                            this.archiveEntries.put(entry.getName(), entry);
                        }
                    }
                    if (multiRelease) {
                        this.processArchivesEntriesForMultiRelease();
                    }
                }
                catch (IOException ioe) {
                    this.archiveEntries = null;
                    throw new IllegalStateException(ioe);
                }
                finally {
                    if (warFile != null) {
                        this.closeJarFile();
                    }
                    if (jarFileIs != null) {
                        try {
                            jarFileIs.close();
                        }
                        catch (IOException iOException) {}
                    }
                }
            }
            return this.archiveEntries;
        }
    }

    protected void processArchivesEntriesForMultiRelease() {
        int targetVersion = JreCompat.getInstance().jarFileRuntimeMajorVersion();
        HashMap<String, VersionedJarEntry> versionedEntries = new HashMap<String, VersionedJarEntry>();
        Iterator iter = this.archiveEntries.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = iter.next();
            String name = (String)entry.getKey();
            if (!name.startsWith("META-INF/versions/")) continue;
            iter.remove();
            int i = name.indexOf(47, 18);
            if (i <= 0) continue;
            String baseName = name.substring(i + 1);
            int version = Integer.parseInt(name.substring(18, i));
            if (version > targetVersion) continue;
            VersionedJarEntry versionedJarEntry = (VersionedJarEntry)versionedEntries.get(baseName);
            if (versionedJarEntry == null) {
                versionedEntries.put(baseName, new VersionedJarEntry(version, (JarEntry)entry.getValue()));
                continue;
            }
            if (version <= versionedJarEntry.getVersion()) continue;
            versionedEntries.put(baseName, new VersionedJarEntry(version, (JarEntry)entry.getValue()));
        }
        for (Map.Entry versionedJarEntry : versionedEntries.entrySet()) {
            this.archiveEntries.put((String)versionedJarEntry.getKey(), ((VersionedJarEntry)versionedJarEntry.getValue()).getJarEntry());
        }
    }

    @Override
    protected JarEntry getArchiveEntry(String pathInArchive) {
        throw new IllegalStateException(sm.getString("jarWarResourceSet.codingError"));
    }

    @Override
    protected boolean isMultiRelease() {
        return false;
    }

    @Override
    protected void initInternal() throws LifecycleException {
        try (JarFile warFile = new JarFile(this.getBase());){
            JarEntry jarFileInWar = warFile.getJarEntry(this.archivePath);
            InputStream jarFileIs = warFile.getInputStream(jarFileInWar);
            try (JarInputStream jarIs = new JarInputStream(jarFileIs);){
                this.setManifest(jarIs.getManifest());
            }
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

    private static final class VersionedJarEntry {
        private final int version;
        private final JarEntry jarEntry;

        VersionedJarEntry(int version, JarEntry jarEntry) {
            this.version = version;
            this.jarEntry = jarEntry;
        }

        public int getVersion() {
            return this.version;
        }

        public JarEntry getJarEntry() {
            return this.jarEntry;
        }
    }
}

