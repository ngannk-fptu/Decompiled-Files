/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache.terracotta;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.sf.ehcache.DiskStorePathManager;
import net.sf.ehcache.util.PreferredLoaderObjectInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class RotatingSnapshotFile {
    private static final Logger LOG = LoggerFactory.getLogger(RotatingSnapshotFile.class);
    private static final String SUFFIX_OK = ".keySet";
    private static final String SUFFIX_PROGRESS = ".keySet.temp";
    private static final String SUFFIX_MOVE = ".keySet.old";
    private volatile boolean shutdownOnThreadInterrupted;
    private final String cacheName;
    private final Lock readLock;
    private final Lock writeLock;
    private final DiskStorePathManager diskStorePathManager;
    private final ClassLoader classLoader;

    RotatingSnapshotFile(DiskStorePathManager diskStorePathManager, String cacheName, ClassLoader classLoader) {
        ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
        this.readLock = rwl.readLock();
        this.writeLock = rwl.writeLock();
        this.diskStorePathManager = diskStorePathManager;
        this.cacheName = cacheName;
        this.classLoader = classLoader;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void writeAll(Iterable localKeys) throws IOException {
        this.writeLock.lock();
        long writtenKeys = 0L;
        try {
            File inProgress = this.newSnapshotFile();
            this.cleanUp(inProgress);
            if (!inProgress.createNewFile()) {
                throw new AssertionError((Object)("The file '" + inProgress.getAbsolutePath() + "' exists already!"));
            }
            FileOutputStream fileOutputStream = new FileOutputStream(inProgress);
            ObjectOutputStream oos = new ObjectOutputStream(fileOutputStream);
            try {
                for (Object localKey : localKeys) {
                    if (this.shutdownOnThreadInterrupted && Thread.currentThread().isInterrupted()) {
                        return;
                    }
                    oos.writeObject(localKey);
                    ++writtenKeys;
                }
            }
            finally {
                fileOutputStream.close();
            }
            this.swapForOldWithNewSnapshot(inProgress);
        }
        finally {
            LOG.info("Did a snapshot of " + writtenKeys + " local keys");
            this.writeLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    <T> Set<T> readAll() throws IOException {
        this.cleanUp();
        this.readLock.lock();
        try {
            File currentSnapshot = this.currentSnapshotFile();
            if (!currentSnapshot.exists()) {
                Set set = Collections.emptySet();
                return set;
            }
            HashSet<Object> values = new HashSet<Object>();
            FileInputStream fis = new FileInputStream(currentSnapshot);
            try {
                PreferredLoaderObjectInputStream ois = new PreferredLoaderObjectInputStream(fis, this.classLoader);
                boolean eof = false;
                while (!eof) {
                    try {
                        values.add(ois.readObject());
                    }
                    catch (Exception e) {
                        if (!(e instanceof EOFException)) continue;
                        eof = true;
                    }
                }
                try {
                    ois.close();
                }
                catch (IOException e) {
                    LOG.error("Error closing ObjectInputStream", (Throwable)e);
                    this.closeAndDeleteAssociatedFileOnFailure(fis, currentSnapshot);
                }
            }
            catch (IOException e) {
                this.closeAndDeleteAssociatedFileOnFailure(fis, currentSnapshot);
            }
            Set set = Collections.unmodifiableSet(values);
            return set;
        }
        finally {
            this.readLock.unlock();
        }
    }

    private void cleanUp() {
        if (this.requiresCleanUp()) {
            this.writeLock.lock();
            try {
                this.cleanUp(this.newSnapshotFile());
            }
            finally {
                this.writeLock.unlock();
            }
        }
    }

    private void cleanUp(File inProgress) {
        if (this.requiresCleanUp()) {
            File dest = this.currentSnapshotFile();
            if (dest.exists() && !inProgress.delete()) {
                throw new RuntimeException("Couldn't cleanup old file " + inProgress.getAbsolutePath());
            }
            File tempFile = this.tempSnapshotFile();
            if (tempFile.exists() && !tempFile.delete()) {
                throw new RuntimeException("Couldn't cleanup temp file " + tempFile.getAbsolutePath());
            }
            if (inProgress.exists() && !inProgress.renameTo(dest)) {
                throw new RuntimeException("Couldn't rename new snapshot: " + dest.getAbsolutePath());
            }
        }
    }

    private boolean requiresCleanUp() {
        return this.newSnapshotFile().exists();
    }

    private void swapForOldWithNewSnapshot(File inProgress) {
        File currentSnapshot = this.currentSnapshotFile();
        File tempFile = this.tempSnapshotFile();
        if (currentSnapshot.exists() && !currentSnapshot.renameTo(tempFile)) {
            throw new RuntimeException("Couldn't rename previous snapshot: " + currentSnapshot.getAbsolutePath());
        }
        if (!inProgress.renameTo(currentSnapshot)) {
            throw new RuntimeException("Couldn't rename new snapshot: " + currentSnapshot.getAbsolutePath());
        }
        if (tempFile.exists() && !tempFile.delete()) {
            throw new RuntimeException("Couldn't delete temp file " + tempFile.getAbsolutePath());
        }
    }

    File currentSnapshotFile() {
        return this.diskStorePathManager.getFile(this.cacheName, SUFFIX_OK);
    }

    File newSnapshotFile() {
        return this.diskStorePathManager.getFile(this.cacheName, SUFFIX_PROGRESS);
    }

    File tempSnapshotFile() {
        return this.diskStorePathManager.getFile(this.cacheName, SUFFIX_MOVE);
    }

    void setShutdownOnThreadInterrupted(boolean shutdownOnThreadInterrupted) {
        this.shutdownOnThreadInterrupted = shutdownOnThreadInterrupted;
    }

    private void closeAndDeleteAssociatedFileOnFailure(FileInputStream fis, File associatedFile) {
        block2: {
            try {
                fis.close();
            }
            catch (IOException e) {
                LOG.error("Couldn't close FileInputStream on {}, deleting the file!", (Object)associatedFile.getAbsolutePath(), (Object)e);
                if (!associatedFile.exists() || associatedFile.delete()) break block2;
                LOG.error("Couldn't delete file {}", (Object)associatedFile.getAbsolutePath(), (Object)e);
            }
        }
    }

    void snapshotNowOrWaitForCurrentToFinish(Set localKeys) throws IOException {
        if (this.writeLock.tryLock()) {
            try {
                this.writeAll(localKeys);
            }
            finally {
                this.writeLock.unlock();
            }
        } else {
            this.writeLock.lock();
            this.writeLock.unlock();
        }
    }
}

