/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework.cache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import org.apache.felix.framework.Logger;
import org.apache.felix.framework.cache.BundleCache;
import org.apache.felix.framework.cache.Content;
import org.apache.felix.framework.cache.ContentDirectoryContent;
import org.apache.felix.framework.util.Util;
import org.apache.felix.framework.util.WeakZipFileFactory;

public class JarContent
implements Content {
    private static final transient String EMBEDDED_DIRECTORY = "-embedded";
    private static final transient String LIBRARY_DIRECTORY = "-lib";
    private final Logger m_logger;
    private final Map m_configMap;
    private final WeakZipFileFactory m_zipFactory;
    private final Object m_revisionLock;
    private final File m_rootDir;
    private final File m_file;
    private final WeakZipFileFactory.WeakZipFile m_zipFile;
    private final boolean m_isZipFileOwner;
    private Map m_nativeLibMap;

    public JarContent(Logger logger, Map configMap, WeakZipFileFactory zipFactory, Object revisionLock, File rootDir, File file, WeakZipFileFactory.WeakZipFile zipFile) {
        this.m_logger = logger;
        this.m_configMap = configMap;
        this.m_zipFactory = zipFactory;
        this.m_revisionLock = revisionLock;
        this.m_rootDir = rootDir;
        this.m_file = file;
        if (zipFile == null) {
            try {
                this.m_zipFile = this.m_zipFactory.create(this.m_file);
            }
            catch (IOException ex) {
                throw new RuntimeException("Unable to open JAR file, probably deleted: " + ex.getMessage());
            }
        } else {
            this.m_zipFile = zipFile;
        }
        this.m_isZipFileOwner = zipFile == null;
    }

    protected void finalize() {
        this.close();
    }

    @Override
    public void close() {
        try {
            if (this.m_isZipFileOwner) {
                this.m_zipFile.close();
            }
        }
        catch (Exception ex) {
            this.m_logger.log(1, "JarContent: Unable to close JAR file.", ex);
        }
    }

    @Override
    public boolean hasEntry(String name) {
        try {
            ZipEntry ze = this.m_zipFile.getEntry(name);
            return ze != null;
        }
        catch (Exception ex) {
            return false;
        }
    }

    @Override
    public boolean isDirectory(String name) {
        try {
            ZipEntry ze = this.m_zipFile.getEntry(name);
            return ze != null && ze.isDirectory();
        }
        catch (Exception ex) {
            return false;
        }
    }

    @Override
    public Enumeration<String> getEntries() {
        Enumeration<String> e = this.m_zipFile.names();
        return e.hasMoreElements() ? e : null;
    }

    @Override
    public byte[] getEntryAsBytes(String name) throws IllegalStateException {
        try {
            ZipEntry ze = this.m_zipFile.getEntry(name);
            if (ze == null) {
                return null;
            }
            return BundleCache.read(this.m_zipFile.getInputStream(ze), ze.getSize());
        }
        catch (Exception ex) {
            this.m_logger.log(1, "JarContent: Unable to read bytes for file " + name + " in ZIP file " + this.m_file.getAbsolutePath(), ex);
            return null;
        }
    }

    @Override
    public InputStream getEntryAsStream(String name) throws IllegalStateException, IOException {
        InputStream is = null;
        try {
            ZipEntry ze = this.m_zipFile.getEntry(name);
            if (ze == null) {
                return null;
            }
            is = this.m_zipFile.getInputStream(ze);
            if (is == null) {
                return null;
            }
        }
        catch (Exception ex) {
            return null;
        }
        return is;
    }

    @Override
    public URL getEntryAsURL(String name) {
        if (this.hasEntry(name)) {
            try {
                return new URL("jar:" + this.m_file.toURI().toURL().toExternalForm() + "!/" + name);
            }
            catch (MalformedURLException e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public long getContentTime(String urlPath) {
        try {
            ZipEntry ze = this.m_zipFile.getEntry(urlPath);
            return ze.getTime();
        }
        catch (Exception ex) {
            return -1L;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Content getEntryAsContent(String entryName) {
        if (entryName.equals(".")) {
            return new JarContent(this.m_logger, this.m_configMap, this.m_zipFactory, this.m_revisionLock, this.m_rootDir, this.m_file, this.m_zipFile);
        }
        String string = entryName = entryName.startsWith("/") ? entryName.substring(1) : entryName;
        if (entryName.trim().startsWith(".." + File.separatorChar) || entryName.contains(File.separator + ".." + File.separatorChar) || entryName.trim().endsWith(File.separator + "..") || entryName.trim().equals("..")) {
            return null;
        }
        File embedDir = new File(this.m_rootDir, this.m_file.getName() + EMBEDDED_DIRECTORY);
        ZipEntry ze = this.m_zipFile.getEntry(entryName);
        if (ze != null && ze.isDirectory()) {
            return new ContentDirectoryContent(this, entryName);
        }
        if (ze != null && ze.getName().endsWith(".jar")) {
            File extractJar = new File(embedDir, entryName);
            try {
                if (!BundleCache.getSecureAction().fileExists(extractJar)) {
                    Object object = this.m_revisionLock;
                    synchronized (object) {
                        if (!BundleCache.getSecureAction().fileExists(extractJar)) {
                            File jarDir = extractJar.getParentFile();
                            if (!BundleCache.getSecureAction().fileExists(jarDir) && !BundleCache.getSecureAction().mkdirs(jarDir)) {
                                throw new IOException("Unable to create embedded JAR directory.");
                            }
                            BundleCache.copyStreamToFile(this.m_zipFile.getInputStream(ze), extractJar);
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getEntryAsNativeLibrary(String entryName) {
        String result = null;
        String string = entryName = entryName.startsWith("/") ? entryName.substring(1) : entryName;
        if (entryName.trim().startsWith(".." + File.separatorChar) || entryName.contains(File.separator + ".." + File.separatorChar) || entryName.trim().endsWith(File.separator + "..") || entryName.trim().equals("..")) {
            return null;
        }
        File libDir = new File(this.m_rootDir, this.m_file.getName() + LIBRARY_DIRECTORY);
        ZipEntry ze = this.m_zipFile.getEntry(entryName);
        if (ze != null && !ze.isDirectory()) {
            Object object = this.m_revisionLock;
            synchronized (object) {
                Integer libCount;
                if (this.m_nativeLibMap == null) {
                    this.m_nativeLibMap = new HashMap();
                }
                libCount = (libCount = (Integer)this.m_nativeLibMap.get(entryName)) == null ? new Integer(0) : new Integer(libCount + 1);
                this.m_nativeLibMap.put(entryName, libCount);
                File libFile = new File(libDir, libCount.toString() + File.separatorChar + entryName);
                if (!BundleCache.getSecureAction().fileExists(libFile)) {
                    if (!BundleCache.getSecureAction().fileExists(libFile.getParentFile()) && !BundleCache.getSecureAction().mkdirs(libFile.getParentFile())) {
                        this.m_logger.log(1, "Unable to create library directory.");
                    } else {
                        try {
                            BundleCache.copyStreamToFile(this.m_zipFile.getInputStream(ze), libFile);
                            String command = (String)this.m_configMap.get("org.osgi.framework.command.execpermission");
                            if (command != null) {
                                Properties props = new Properties();
                                props.setProperty("abspath", libFile.toString());
                                command = Util.substVars(command, "command", null, props);
                                Process p = BundleCache.getSecureAction().exec(command);
                                Thread stdOut = new Thread(new DevNullRunnable(p.getInputStream()));
                                Thread stdErr = new Thread(new DevNullRunnable(p.getErrorStream()));
                                stdOut.setDaemon(true);
                                stdErr.setDaemon(true);
                                stdOut.start();
                                stdErr.start();
                                p.waitFor();
                                stdOut.join();
                                stdErr.join();
                            }
                            result = BundleCache.getSecureAction().getAbsolutePath(libFile);
                        }
                        catch (Exception ex) {
                            this.m_logger.log(1, "Extracting native library.", ex);
                        }
                    }
                } else {
                    result = BundleCache.getSecureAction().getAbsolutePath(libFile);
                }
            }
        }
        return result;
    }

    public String toString() {
        return "JAR " + this.m_file.getPath();
    }

    public File getFile() {
        return this.m_file;
    }

    static class DevNullRunnable
    implements Runnable {
        private final InputStream m_in;

        public DevNullRunnable(InputStream in) {
            this.m_in = in;
        }

        @Override
        public void run() {
            try {
                try {
                    while (this.m_in.read() != -1) {
                    }
                }
                finally {
                    this.m_in.close();
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }
}

