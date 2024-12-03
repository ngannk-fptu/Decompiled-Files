/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework.cache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Map;
import org.apache.felix.framework.Logger;
import org.apache.felix.framework.cache.BundleArchive;
import org.apache.felix.framework.util.SecureAction;
import org.apache.felix.framework.util.WeakZipFileFactory;
import org.osgi.framework.connect.ModuleConnector;

public class BundleCache {
    public static final String CACHE_BUFSIZE_PROP = "felix.cache.bufsize";
    public static final String CACHE_ROOTDIR_PROP = "felix.cache.rootdir";
    public static final String CACHE_LOCKING_PROP = "felix.cache.locking";
    public static final String CACHE_FILELIMIT_PROP = "felix.cache.filelimit";
    private static final ThreadLocal m_defaultBuffer = new ThreadLocal();
    private static volatile int DEFAULT_BUFFER = 65536;
    private static final transient String CACHE_DIR_NAME = "felix-cache";
    private static final transient String CACHE_ROOTDIR_DEFAULT = ".";
    private static final transient String CACHE_LOCK_NAME = "cache.lock";
    static final transient String BUNDLE_DIR_PREFIX = "bundle";
    private static final SecureAction m_secureAction = new SecureAction();
    private final Logger m_logger;
    private final Map m_configMap;
    private final WeakZipFileFactory m_zipFactory;
    private final Object m_lock;

    public BundleCache(Logger logger, Map configMap) throws Exception {
        this.m_logger = logger;
        this.m_configMap = configMap;
        int limit = 0;
        String limitStr = (String)this.m_configMap.get(CACHE_FILELIMIT_PROP);
        if (limitStr != null) {
            try {
                limit = Integer.parseInt(limitStr);
            }
            catch (NumberFormatException ex) {
                limit = 0;
            }
        }
        this.m_zipFactory = new WeakZipFileFactory(limit);
        File cacheDir = BundleCache.determineCacheDir(this.m_configMap);
        if (!BundleCache.getSecureAction().fileExists(cacheDir) && !BundleCache.getSecureAction().mkdirs(cacheDir)) {
            this.m_logger.log(1, "Unable to create cache directory: " + cacheDir);
            throw new RuntimeException("Unable to create cache directory.");
        }
        Object locking = this.m_configMap.get(CACHE_LOCKING_PROP);
        String string = locking = locking == null ? Boolean.TRUE.toString() : locking.toString().toLowerCase();
        if (locking.equals(Boolean.TRUE.toString())) {
            File lockFile = new File(cacheDir, CACHE_LOCK_NAME);
            FileChannel fc = null;
            try {
                fc = BundleCache.getSecureAction().getFileChannel(lockFile);
            }
            catch (Exception ex) {
                try {
                    if (fc != null) {
                        fc.close();
                    }
                }
                catch (Exception exception) {
                    // empty catch block
                }
                throw new Exception("Unable to create bundle cache lock file: " + ex);
            }
            try {
                this.m_lock = fc.tryLock();
            }
            catch (Exception ex) {
                throw new Exception("Unable to lock bundle cache: " + ex);
            }
        }
        this.m_lock = null;
    }

    public static Map<String, Object> getMainAttributes(Map<String, Object> headers, InputStream inputStream, long size) throws Exception {
        if (size > 0L) {
            return BundleCache.getMainAttributes(headers, inputStream, (int)(size < Integer.MAX_VALUE ? size : Integer.MAX_VALUE));
        }
        return headers;
    }

    static byte[] read(InputStream input, long size) throws Exception {
        return BundleCache.read(input, size <= Integer.MAX_VALUE ? (int)size : Integer.MAX_VALUE);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    static byte[] read(InputStream input, int size) throws Exception {
        if (size <= 0) {
            return new byte[0];
        }
        byte[] result = new byte[size];
        Exception exception = null;
        for (int i = input.read(result, 0, size); i != -1 && i < size; i += input.read(result, i, size - i)) {
        }
        try {
            input.close();
        }
        catch (Exception ex) {
            throw exception != null ? exception : ex;
        }
        catch (Exception ex) {
            try {
                exception = ex;
            }
            catch (Throwable throwable) {
                try {
                    input.close();
                }
                catch (Exception ex2) {
                    throw exception != null ? exception : ex2;
                }
                throw throwable;
            }
            try {
                input.close();
            }
            catch (Exception ex3) {
                throw exception != null ? exception : ex3;
            }
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Map<String, Object> getMainAttributes(Map<String, Object> headers, InputStream inputStream, int size) throws Exception {
        if (size <= 0) {
            inputStream.close();
            return headers;
        }
        SoftReference ref = (SoftReference)m_defaultBuffer.get();
        byte[] bytes = null;
        if (ref != null) {
            bytes = (byte[])ref.get();
        }
        if (bytes == null) {
            bytes = new byte[size + 1 > DEFAULT_BUFFER ? size + 1 : DEFAULT_BUFFER];
            m_defaultBuffer.set(new SoftReference<byte[]>(bytes));
        } else if (size + 1 > bytes.length) {
            bytes = new byte[size + 1];
            m_defaultBuffer.set(new SoftReference<byte[]>(bytes));
        }
        try {
            for (int i = inputStream.read(bytes); i < size; i += inputStream.read(bytes, i, bytes.length - i)) {
            }
        }
        finally {
            inputStream.close();
        }
        bytes[size++] = 10;
        String key = null;
        int last = 0;
        int current = 0;
        for (int i = 0; i < size; ++i) {
            if (bytes[i] == 13 && i + 1 < size && bytes[i + 1] == 10) continue;
            if (bytes[i] == 10 && i + 1 < size && bytes[i + 1] == 32) {
                ++i;
                continue;
            }
            if (key == null && bytes[i] == 58) {
                key = new String(bytes, last, current - last, "UTF-8");
                if (i + 1 < size && bytes[i + 1] == 32) {
                    last = current + 1;
                    continue;
                }
                throw new Exception("Manifest error: Missing space separator - " + key);
            }
            if (bytes[i] == 10) {
                if (last == current && key == null) break;
                String value = new String(bytes, last, current - last, "UTF-8");
                if (key == null) {
                    throw new Exception("Manifest error: Missing attribute name - " + value);
                }
                if (headers.put(key.intern(), value) != null) {
                    throw new Exception("Manifest error: Duplicate attribute name - " + key);
                }
                last = current;
                key = null;
                continue;
            }
            bytes[current++] = bytes[i];
        }
        return headers;
    }

    public synchronized void release() {
        if (this.m_lock != null) {
            try {
                ((FileLock)this.m_lock).release();
                ((FileLock)this.m_lock).channel().close();
            }
            catch (Exception ex) {
                this.m_logger.log(2, "Exception releasing bundle cache.", ex);
            }
        }
    }

    public File getCacheDir() {
        return BundleCache.determineCacheDir(this.m_configMap);
    }

    static SecureAction getSecureAction() {
        return m_secureAction;
    }

    public synchronized void delete() throws Exception {
        File cacheDir = BundleCache.determineCacheDir(this.m_configMap);
        BundleCache.deleteDirectoryTree(cacheDir);
    }

    public BundleArchive[] getArchives(ModuleConnector connectFactory) throws Exception {
        try {
            String sBufSize = (String)this.m_configMap.get(CACHE_BUFSIZE_PROP);
            if (sBufSize != null) {
                DEFAULT_BUFFER = Integer.parseInt(sBufSize);
            }
        }
        catch (NumberFormatException sBufSize) {
            // empty catch block
        }
        File cacheDir = BundleCache.determineCacheDir(this.m_configMap);
        ArrayList<BundleArchive> archiveList = new ArrayList<BundleArchive>();
        File[] children = BundleCache.getSecureAction().listDirectory(cacheDir);
        for (int i = 0; children != null && i < children.length; ++i) {
            if (!children[i].getName().startsWith(BUNDLE_DIR_PREFIX) || children[i].getName().equals(BUNDLE_DIR_PREFIX + Long.toString(0L))) continue;
            try {
                archiveList.add(new BundleArchive(this.m_logger, this.m_configMap, this.m_zipFactory, connectFactory, children[i]));
                continue;
            }
            catch (Exception ex) {
                this.m_logger.log(1, "Error reloading cached bundle, removing it: " + children[i], ex);
                BundleCache.deleteDirectoryTree(children[i]);
            }
        }
        return archiveList.toArray(new BundleArchive[archiveList.size()]);
    }

    public BundleArchive create(long id, int startLevel, String location, InputStream is, ModuleConnector connectFactory) throws Exception {
        File cacheDir = BundleCache.determineCacheDir(this.m_configMap);
        File archiveRootDir = new File(cacheDir, BUNDLE_DIR_PREFIX + Long.toString(id));
        try {
            BundleArchive ba = new BundleArchive(this.m_logger, this.m_configMap, this.m_zipFactory, connectFactory, archiveRootDir, id, startLevel, location, is);
            return ba;
        }
        catch (Exception ex) {
            if (m_secureAction.fileExists(archiveRootDir) && !BundleCache.deleteDirectoryTree(archiveRootDir)) {
                this.m_logger.log(1, "Unable to delete the archive directory: " + archiveRootDir);
            }
            throw ex;
        }
    }

    public File getSystemBundleDataFile(String fileName) throws Exception {
        String dataDirPath;
        File sbDir = new File(BundleCache.determineCacheDir(this.m_configMap), BUNDLE_DIR_PREFIX + Long.toString(0L));
        if (!(BundleCache.getSecureAction().fileExists(sbDir) || BundleCache.getSecureAction().mkdirs(sbDir) || BundleCache.getSecureAction().fileExists(sbDir))) {
            this.m_logger.log(1, "Unable to create system bundle directory.");
            throw new IOException("Unable to create system bundle directory.");
        }
        File dataFile = new File(sbDir, fileName);
        String dataFilePath = BundleCache.getSecureAction().getCanonicalPath(dataFile);
        if (!dataFilePath.equals(dataDirPath = BundleCache.getSecureAction().getCanonicalPath(sbDir)) && !dataFilePath.startsWith(dataDirPath + File.separatorChar)) {
            throw new IllegalArgumentException("The data file must be inside the data dir.");
        }
        return dataFile;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static void copyStreamToFile(InputStream is, File outputFile) throws IOException {
        SoftReference ref = (SoftReference)m_defaultBuffer.get();
        byte[] bytes = null;
        if (ref != null) {
            bytes = (byte[])ref.get();
        }
        if (bytes == null) {
            bytes = new byte[DEFAULT_BUFFER];
            m_defaultBuffer.set(new SoftReference<byte[]>(bytes));
        }
        OutputStream os = null;
        try {
            os = BundleCache.getSecureAction().getOutputStream(outputFile);
            int i = is.read(bytes);
            while (i != -1) {
                os.write(bytes, 0, i);
                i = is.read(bytes);
            }
        }
        finally {
            try {
                if (is != null) {
                    is.close();
                }
            }
            finally {
                if (os != null) {
                    os.close();
                }
            }
        }
    }

    static boolean deleteDirectoryTree(File target) {
        if (!BundleCache.deleteDirectoryTreeRecursive(target)) {
            System.gc();
            System.gc();
            return BundleCache.deleteDirectoryTreeRecursive(target);
        }
        return true;
    }

    private static File determineCacheDir(Map configMap) {
        File cacheDir;
        String cacheDirStr = (String)configMap.get("org.osgi.framework.storage");
        String rootDirStr = (String)configMap.get(CACHE_ROOTDIR_PROP);
        String string = rootDirStr = rootDirStr == null ? CACHE_ROOTDIR_DEFAULT : rootDirStr;
        if (cacheDirStr != null) {
            cacheDir = new File(cacheDirStr);
            if (!cacheDir.isAbsolute()) {
                cacheDir = new File(rootDirStr, cacheDirStr);
            }
        } else {
            cacheDir = new File(rootDirStr, CACHE_DIR_NAME);
        }
        return cacheDir;
    }

    private static boolean deleteDirectoryTreeRecursive(File target) {
        File[] files;
        if (!BundleCache.getSecureAction().fileExists(target)) {
            return true;
        }
        if (BundleCache.getSecureAction().isFileDirectory(target) && (files = BundleCache.getSecureAction().listDirectory(target)) != null) {
            for (int i = 0; i < files.length; ++i) {
                BundleCache.deleteDirectoryTreeRecursive(files[i]);
            }
        }
        return BundleCache.getSecureAction().deleteFile(target);
    }
}

