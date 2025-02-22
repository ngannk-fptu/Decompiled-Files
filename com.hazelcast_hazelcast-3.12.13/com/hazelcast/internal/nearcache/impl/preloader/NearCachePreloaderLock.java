/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.nearcache.impl.preloader;

import com.hazelcast.core.HazelcastException;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.IOUtil;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;

class NearCachePreloaderLock {
    private final ILogger logger;
    private final File lockFile;
    private final FileChannel channel;
    private final FileLock lock;

    NearCachePreloaderLock(ILogger logger, String lockFilename) {
        this.logger = logger;
        this.lockFile = new File(lockFilename);
        this.channel = this.openChannel(this.lockFile);
        this.lock = this.acquireLock(this.lockFile, this.channel);
    }

    void release() {
        this.releaseInternal(this.lock, this.channel);
    }

    FileLock acquireLock(File lockFile, FileChannel channel) {
        FileLock fileLock = null;
        try {
            fileLock = channel.tryLock();
            if (fileLock != null) {
                FileLock fileLock2 = fileLock;
                return fileLock2;
            }
            try {
                throw new HazelcastException("Cannot acquire lock on " + lockFile.getAbsolutePath() + ". File is already being used by another Hazelcast instance.");
            }
            catch (OverlappingFileLockException e) {
                throw new HazelcastException("Cannot acquire lock on " + lockFile.getAbsolutePath() + ". File is already being used by this Hazelcast instance.", e);
            }
            catch (IOException e) {
                throw new HazelcastException("Unknown failure while acquiring lock on " + lockFile.getAbsolutePath(), e);
            }
        }
        finally {
            if (fileLock == null) {
                IOUtil.closeResource(channel);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void releaseInternal(FileLock lock, FileChannel channel) {
        try {
            lock.release();
            channel.close();
        }
        catch (IOException e) {
            this.logger.severe("Problem while releasing the lock and closing channel on " + this.lockFile, e);
        }
        finally {
            this.lockFile.deleteOnExit();
        }
    }

    private FileChannel openChannel(File lockFile) {
        try {
            return new RandomAccessFile(lockFile, "rw").getChannel();
        }
        catch (IOException e) {
            throw new HazelcastException("Cannot create lock file " + lockFile.getAbsolutePath(), e);
        }
    }
}

