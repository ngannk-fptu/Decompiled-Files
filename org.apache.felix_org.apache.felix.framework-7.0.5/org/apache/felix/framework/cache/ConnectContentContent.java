/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework.cache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import org.apache.felix.framework.Logger;
import org.apache.felix.framework.cache.BundleCache;
import org.apache.felix.framework.cache.Content;
import org.apache.felix.framework.cache.ContentDirectoryContent;
import org.apache.felix.framework.cache.JarContent;
import org.apache.felix.framework.util.WeakZipFileFactory;
import org.osgi.framework.connect.ConnectContent;

public class ConnectContentContent
implements Content {
    private static final transient String EMBEDDED_DIRECTORY = "-embedded";
    private static final transient String LIBRARY_DIRECTORY = "-lib";
    private final Logger m_logger;
    private final WeakZipFileFactory m_zipFactory;
    private final Map m_configMap;
    private final String m_name;
    private final File m_rootDir;
    private final Object m_revisionLock;
    private final ConnectContent m_content;

    public ConnectContentContent(Logger logger, WeakZipFileFactory zipFactory, Map configMap, String name, File rootDir, Object revisionLock, ConnectContent content) throws IOException {
        this.m_logger = logger;
        this.m_zipFactory = zipFactory;
        this.m_configMap = configMap;
        this.m_name = name;
        this.m_rootDir = rootDir;
        this.m_revisionLock = revisionLock;
        this.m_content = content;
    }

    @Override
    public void close() {
    }

    @Override
    public boolean hasEntry(String name) {
        return this.m_content.getEntry(name).isPresent();
    }

    @Override
    public boolean isDirectory(String name) {
        return this.m_content.getEntry(name).map(entry -> entry.getName().endsWith("/")).orElse(false);
    }

    @Override
    public Enumeration<String> getEntries() {
        try {
            final Iterator<String> entries = this.m_content.getEntries().iterator();
            return new Enumeration<String>(){

                @Override
                public boolean hasMoreElements() {
                    return entries.hasNext();
                }

                @Override
                public String nextElement() {
                    return (String)entries.next();
                }
            };
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public byte[] getEntryAsBytes(String name) {
        return this.m_content.getEntry(name).flatMap(entry -> {
            try {
                return Optional.of(entry.getBytes());
            }
            catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }).orElse(null);
    }

    @Override
    public InputStream getEntryAsStream(String name) throws IOException {
        return this.m_content.getEntry(name).flatMap(entry -> {
            try {
                return Optional.of(entry.getInputStream());
            }
            catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }).orElse(null);
    }

    public ClassLoader getClassLoader() {
        return this.m_content.getClassLoader().orElse(null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Content getEntryAsContent(String name) {
        String dir;
        if (".".equals(name) || "".equals(name)) {
            return this;
        }
        String string = dir = name.endsWith("/") ? name : name + "/";
        if (this.hasEntry(dir)) {
            return new ContentDirectoryContent(this, name);
        }
        if (this.hasEntry(name) && name.endsWith(".jar")) {
            File embedDir = new File(this.m_rootDir, this.m_name + EMBEDDED_DIRECTORY);
            File extractJar = new File(embedDir, name);
            try {
                if (!BundleCache.getSecureAction().fileExists(extractJar)) {
                    Object object = this.m_revisionLock;
                    synchronized (object) {
                        if (!BundleCache.getSecureAction().fileExists(extractJar)) {
                            File jarDir = extractJar.getParentFile();
                            if (!BundleCache.getSecureAction().fileExists(jarDir) && !BundleCache.getSecureAction().mkdirs(jarDir)) {
                                throw new IOException("Unable to create embedded JAR directory.");
                            }
                            BundleCache.copyStreamToFile(this.m_content.getEntry(name).get().getInputStream(), extractJar);
                        }
                    }
                }
                return new JarContent(this.m_logger, this.m_configMap, this.m_zipFactory, this.m_revisionLock, extractJar.getParentFile(), extractJar, null);
            }
            catch (Exception ex) {
                this.m_logger.log(1, "Unable to extract embedded JAR file.", ex);
            }
        }
        return null;
    }

    @Override
    public String getEntryAsNativeLibrary(String entryName) {
        return null;
    }

    @Override
    public URL getEntryAsURL(String name) {
        return null;
    }

    @Override
    public long getContentTime(String urlPath) {
        return this.m_content.getEntry(urlPath).flatMap(entry -> Optional.of(entry.getLastModified())).orElse(-1L);
    }
}

