/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework.cache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import org.apache.felix.framework.Logger;
import org.apache.felix.framework.cache.BundleCache;
import org.apache.felix.framework.cache.Content;
import org.apache.felix.framework.cache.JarContent;
import org.apache.felix.framework.util.Util;
import org.apache.felix.framework.util.WeakZipFileFactory;

public class DirectoryContent
implements Content {
    private static final int BUFSIZE = 4096;
    private static final transient String EMBEDDED_DIRECTORY = "-embedded";
    private static final transient String LIBRARY_DIRECTORY = "-lib";
    private final Logger m_logger;
    private final Map m_configMap;
    private final WeakZipFileFactory m_zipFactory;
    private final Object m_revisionLock;
    private final File m_rootDir;
    private final File m_dir;
    private Map m_nativeLibMap;
    private final String m_canonicalRoot;

    public DirectoryContent(Logger logger, Map configMap, WeakZipFileFactory zipFactory, Object revisionLock, File rootDir, File dir) {
        this.m_logger = logger;
        this.m_configMap = configMap;
        this.m_zipFactory = zipFactory;
        this.m_revisionLock = revisionLock;
        this.m_rootDir = rootDir;
        this.m_dir = dir;
        String canonicalPath = null;
        try {
            canonicalPath = BundleCache.getSecureAction().getCanonicalPath(this.m_dir);
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        if (!canonicalPath.endsWith(File.separator)) {
            canonicalPath = canonicalPath + File.separator;
        }
        this.m_canonicalRoot = canonicalPath;
    }

    public File getFile() {
        return this.m_dir;
    }

    @Override
    public void close() {
    }

    @Override
    public boolean hasEntry(String name) throws IllegalStateException {
        name = this.getName(name);
        File file = null;
        try {
            file = this.getFile(name);
        }
        catch (IOException e) {
            return false;
        }
        return BundleCache.getSecureAction().fileExists(file) && (!name.endsWith("/") || BundleCache.getSecureAction().isFileDirectory(file));
    }

    @Override
    public boolean isDirectory(String name) {
        name = this.getName(name);
        File file = null;
        try {
            file = this.getFile(name);
        }
        catch (IOException e) {
            return false;
        }
        return BundleCache.getSecureAction().isFileDirectory(file);
    }

    @Override
    public Enumeration<String> getEntries() {
        EntriesEnumeration e = new EntriesEnumeration(this.m_dir);
        return e.hasMoreElements() ? e : null;
    }

    @Override
    public byte[] getEntryAsBytes(String name) throws IllegalStateException {
        name = this.getName(name);
        try {
            File file = this.getFile(name);
            return BundleCache.getSecureAction().fileExists(file) ? BundleCache.read(BundleCache.getSecureAction().getInputStream(file), file.length()) : null;
        }
        catch (Exception ex) {
            this.m_logger.log(1, "DirectoryContent: Unable to read bytes for file " + name + " from file " + new File(this.m_dir, name).getAbsolutePath(), ex);
            return null;
        }
    }

    @Override
    public InputStream getEntryAsStream(String name) throws IllegalStateException, IOException {
        name = this.getName(name);
        try {
            File file = this.getFile(name);
            return BundleCache.getSecureAction().fileExists(file) ? BundleCache.getSecureAction().getInputStream(file) : null;
        }
        catch (Exception ex) {
            this.m_logger.log(1, "DirectoryContent: Unable to create inputstream for file " + name + " from file " + new File(this.m_dir, name).getAbsolutePath(), ex);
            return null;
        }
    }

    private String getName(String name) {
        if (name.length() > 0 && name.charAt(0) == '/') {
            name = name.substring(1);
        }
        return name;
    }

    private File getFile(String name) throws IOException {
        File result = new File(this.m_dir, name);
        String canonicalPath = BundleCache.getSecureAction().getCanonicalPath(result);
        if (BundleCache.getSecureAction().isFileDirectory(result) && !canonicalPath.endsWith(File.separator)) {
            canonicalPath = canonicalPath + File.separator;
        }
        if (!canonicalPath.startsWith(this.m_canonicalRoot)) {
            throw new IOException("File outside the root: " + canonicalPath);
        }
        return result;
    }

    @Override
    public URL getEntryAsURL(String name) {
        if (this.hasEntry(name = this.getName(name))) {
            try {
                return BundleCache.getSecureAction().toURI(this.getFile(name)).toURL();
            }
            catch (IOException e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public long getContentTime(String name) {
        name = this.getName(name);
        File file = null;
        try {
            file = this.getFile(name);
        }
        catch (IOException e) {
            return 0L;
        }
        return BundleCache.getSecureAction().getLastModified(file);
    }

    @Override
    public Content getEntryAsContent(String entryName) {
        if (entryName.equals(".")) {
            return new DirectoryContent(this.m_logger, this.m_configMap, this.m_zipFactory, this.m_revisionLock, this.m_rootDir, this.m_dir);
        }
        String string = entryName = entryName.startsWith("/") ? entryName.substring(1) : entryName;
        if (entryName.trim().startsWith(".." + File.separatorChar) || entryName.contains(File.separator + ".." + File.separatorChar) || entryName.trim().endsWith(File.separator + "..") || entryName.trim().equals("..")) {
            return null;
        }
        File embedDir = new File(this.m_rootDir, this.m_dir.getName() + EMBEDDED_DIRECTORY);
        File file = null;
        try {
            file = this.getFile(entryName);
        }
        catch (IOException e) {
            return null;
        }
        if (BundleCache.getSecureAction().isFileDirectory(file)) {
            return new DirectoryContent(this.m_logger, this.m_configMap, this.m_zipFactory, this.m_revisionLock, this.m_rootDir, file);
        }
        if (BundleCache.getSecureAction().fileExists(file) && entryName.endsWith(".jar")) {
            File extractDir = new File(embedDir, entryName.lastIndexOf(47) >= 0 ? entryName.substring(0, entryName.lastIndexOf(47)) : entryName);
            return new JarContent(this.m_logger, this.m_configMap, this.m_zipFactory, this.m_revisionLock, extractDir, file, null);
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
        File libDir = new File(this.m_rootDir, this.m_dir.getName() + LIBRARY_DIRECTORY);
        File entryFile = null;
        try {
            entryFile = this.getFile(entryName);
        }
        catch (IOException e) {
            return null;
        }
        if (BundleCache.getSecureAction().fileExists(entryFile) && !BundleCache.getSecureAction().isFileDirectory(entryFile)) {
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
                        InputStream is = null;
                        try {
                            is = BundleCache.getSecureAction().getInputStream(entryFile);
                            BundleCache.copyStreamToFile(is, libFile);
                            String command = (String)this.m_configMap.get("org.osgi.framework.command.execpermission");
                            if (command != null) {
                                Properties props = new Properties();
                                props.setProperty("abspath", libFile.toString());
                                command = Util.substVars(command, "command", null, props);
                                Process p = BundleCache.getSecureAction().exec(command);
                                p.waitFor();
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
        return "DIRECTORY " + this.m_dir;
    }

    private static class EntriesEnumeration
    implements Enumeration {
        private final File m_dir;
        private final File[] m_children;
        private int m_counter = 0;

        public EntriesEnumeration(File dir) {
            this.m_dir = dir;
            this.m_children = this.listFilesRecursive(this.m_dir);
        }

        @Override
        public synchronized boolean hasMoreElements() {
            return this.m_children != null && this.m_counter < this.m_children.length;
        }

        public synchronized Object nextElement() {
            if (this.m_children == null || this.m_counter >= this.m_children.length) {
                throw new NoSuchElementException("No more entry paths.");
            }
            String abs = BundleCache.getSecureAction().getAbsolutePath(this.m_children[this.m_counter]).replace(File.separatorChar, '/');
            StringBuilder sb = new StringBuilder(abs);
            sb.delete(0, BundleCache.getSecureAction().getAbsolutePath(this.m_dir).length() + 1);
            if (BundleCache.getSecureAction().isFileDirectory(this.m_children[this.m_counter])) {
                sb.append('/');
            }
            ++this.m_counter;
            return sb.toString();
        }

        private File[] listFilesRecursive(File dir) {
            File[] children;
            File[] combined = children = BundleCache.getSecureAction().listDirectory(dir);
            if (children != null) {
                for (int i = 0; i < children.length; ++i) {
                    File[] grandchildren;
                    if (!BundleCache.getSecureAction().isFileDirectory(children[i]) || (grandchildren = this.listFilesRecursive(children[i])) == null || grandchildren.length <= 0) continue;
                    File[] tmp = new File[combined.length + grandchildren.length];
                    System.arraycopy(combined, 0, tmp, 0, combined.length);
                    System.arraycopy(grandchildren, 0, tmp, combined.length, grandchildren.length);
                    combined = tmp;
                }
            }
            return combined;
        }
    }
}

