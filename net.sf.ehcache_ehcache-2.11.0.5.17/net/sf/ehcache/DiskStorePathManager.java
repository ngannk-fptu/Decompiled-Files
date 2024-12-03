/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.HashSet;
import java.util.Set;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.config.DiskStoreConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DiskStorePathManager {
    private static final String AUTO_DISK_PATH_DIRECTORY_PREFIX = "ehcache_auto_created";
    private static final Logger LOG = LoggerFactory.getLogger(DiskStorePathManager.class);
    private static final String LOCK_FILE_NAME = ".ehcache-diskstore.lock";
    private static final int DEL = 127;
    private static final char ESCAPE = '%';
    private static final Set<Character> ILLEGALS = new HashSet<Character>();
    private final File initialPath;
    private final boolean defaultPath;
    private volatile DiskStorePath path;

    public DiskStorePathManager(String initialPath) {
        this.initialPath = new File(initialPath);
        this.defaultPath = false;
    }

    public DiskStorePathManager() {
        this.initialPath = new File(DiskStoreConfiguration.getDefaultPath());
        this.defaultPath = true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean resolveAndLockIfExists(String file) {
        if (this.path != null) {
            return this.getFile(file).exists();
        }
        DiskStorePathManager diskStorePathManager = this;
        synchronized (diskStorePathManager) {
            if (this.path != null) {
                return this.getFile(file).exists();
            }
            if (!this.initialPath.isDirectory()) {
                return false;
            }
            if (!new File(this.initialPath, file).exists()) {
                return false;
            }
            try {
                this.path = new DiskStorePath(this.initialPath, false, this.defaultPath);
            }
            catch (DiskstoreNotExclusiveException e) {
                throw new CacheException(e);
            }
            LOG.debug("Using diskstore path {}", (Object)this.path.getDiskStorePath());
            LOG.debug("Holding exclusive lock on {}", (Object)this.path.getLockFile());
            return true;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void resolveAndLockIfNeeded(boolean allowAutoCreate) throws DiskstoreNotExclusiveException {
        if (this.path != null) {
            return;
        }
        DiskStorePathManager diskStorePathManager = this;
        synchronized (diskStorePathManager) {
            if (this.path != null) {
                return;
            }
            File candidate = this.initialPath;
            boolean autoCreated = false;
            while (true) {
                if (!candidate.isDirectory() && !candidate.mkdirs()) {
                    throw new CacheException("Disk store path can't be created: " + candidate);
                }
                try {
                    this.path = new DiskStorePath(candidate, autoCreated, !autoCreated && this.defaultPath);
                    break;
                }
                catch (DiskstoreNotExclusiveException e) {
                    if (!allowAutoCreate) {
                        throw e;
                    }
                    autoCreated = true;
                    try {
                        candidate = File.createTempFile(AUTO_DISK_PATH_DIRECTORY_PREFIX, "diskstore", this.initialPath);
                        candidate.delete();
                    }
                    catch (IOException ioe) {
                        throw new CacheException(ioe);
                    }
                }
            }
            if (autoCreated) {
                LOG.warn("diskStorePath '" + this.initialPath + "' is already used by an existing CacheManager either in the same VM or in a different process.\nThe diskStore path for this CacheManager will be set to " + candidate + ".\nTo avoid this warning consider using the CacheManager factory methods to create a singleton CacheManager or specifying a separate ehcache configuration (ehcache.xml) for each CacheManager instance.");
            }
            LOG.debug("Using diskstore path {}", (Object)this.path.getDiskStorePath());
            LOG.debug("Holding exclusive lock on {}", (Object)this.path.getLockFile());
        }
    }

    private static String safeName(String name) {
        int len = name.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; ++i) {
            char c = name.charAt(i);
            if (c <= ' ' || c >= '\u007f' || c >= 'A' && c <= 'Z' || ILLEGALS.contains(Character.valueOf(c)) || c == '%') {
                sb.append('%');
                sb.append(String.format("%04x", c));
                continue;
            }
            sb.append(c);
        }
        return sb.toString();
    }

    private static void deleteFile(File f) {
        if (!f.delete()) {
            LOG.debug("Failed to delete file {}", (Object)f.getAbsolutePath());
        }
    }

    public boolean isAutoCreated() {
        DiskStorePath diskStorePath = this.path;
        if (diskStorePath == null) {
            throw new IllegalStateException();
        }
        return diskStorePath.isAutoCreated();
    }

    public boolean isDefault() {
        DiskStorePath diskStorePath = this.path;
        if (diskStorePath == null) {
            throw new IllegalStateException();
        }
        return diskStorePath.isDefault();
    }

    public synchronized void releaseLock() {
        try {
            if (this.path != null) {
                this.path.unlock();
            }
        }
        finally {
            this.path = null;
        }
    }

    public File getFile(String cacheName, String suffix) {
        return this.getFile(DiskStorePathManager.safeName(cacheName) + suffix);
    }

    public File getFile(String name) {
        try {
            this.resolveAndLockIfNeeded(true);
        }
        catch (DiskstoreNotExclusiveException e) {
            throw new CacheException(e);
        }
        File diskStorePath = this.path.getDiskStorePath();
        File file = new File(diskStorePath, name);
        for (File parent = file.getParentFile(); parent != null; parent = parent.getParentFile()) {
            if (!diskStorePath.equals(parent)) continue;
            return file;
        }
        throw new IllegalArgumentException("Attempted to access file outside the disk-store path");
    }

    static {
        ILLEGALS.add(Character.valueOf('/'));
        ILLEGALS.add(Character.valueOf('\\'));
        ILLEGALS.add(Character.valueOf('<'));
        ILLEGALS.add(Character.valueOf('>'));
        ILLEGALS.add(Character.valueOf(':'));
        ILLEGALS.add(Character.valueOf('\"'));
        ILLEGALS.add(Character.valueOf('|'));
        ILLEGALS.add(Character.valueOf('?'));
        ILLEGALS.add(Character.valueOf('*'));
        ILLEGALS.add(Character.valueOf('.'));
    }

    private static class DiskStorePath {
        private final FileLock directoryLock;
        private final File lockFile;
        private final File diskStorePath;
        private final boolean autoCreated;
        private final boolean defaultPath;

        DiskStorePath(File path, boolean autoCreated, boolean defaultPath) throws DiskstoreNotExclusiveException {
            FileLock dirLock;
            this.diskStorePath = path;
            this.autoCreated = autoCreated;
            this.defaultPath = defaultPath;
            this.lockFile = new File(path.getAbsoluteFile(), DiskStorePathManager.LOCK_FILE_NAME);
            this.lockFile.deleteOnExit();
            try {
                this.lockFile.createNewFile();
                if (!this.lockFile.exists()) {
                    throw new AssertionError((Object)("Failed to create lock file " + this.lockFile));
                }
                FileChannel lockFileChannel = new RandomAccessFile(this.lockFile, "rw").getChannel();
                dirLock = lockFileChannel.tryLock();
            }
            catch (OverlappingFileLockException ofle) {
                dirLock = null;
            }
            catch (IOException ioe) {
                throw new CacheException(ioe);
            }
            if (dirLock == null) {
                throw new DiskstoreNotExclusiveException(path.getAbsolutePath() + " is not exclusive.");
            }
            this.directoryLock = dirLock;
        }

        boolean isAutoCreated() {
            return this.autoCreated;
        }

        boolean isDefault() {
            return this.defaultPath;
        }

        File getDiskStorePath() {
            return this.diskStorePath;
        }

        File getLockFile() {
            return this.lockFile;
        }

        void unlock() {
            if (this.directoryLock != null && this.directoryLock.isValid()) {
                try {
                    this.directoryLock.release();
                    this.directoryLock.channel().close();
                    DiskStorePathManager.deleteFile(this.lockFile);
                }
                catch (IOException e) {
                    throw new CacheException("Failed to release disk store path's lock file:" + this.lockFile, e);
                }
            }
            if (this.autoCreated && this.diskStorePath.delete()) {
                LOG.debug("Deleted directory " + this.diskStorePath.getName());
            }
        }

        public String toString() {
            return this.diskStorePath.getAbsolutePath();
        }
    }

    private static class DiskstoreNotExclusiveException
    extends Exception {
        public DiskstoreNotExclusiveException() {
        }

        public DiskstoreNotExclusiveException(String message) {
            super(message);
        }
    }
}

