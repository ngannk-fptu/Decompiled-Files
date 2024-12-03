/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.services.s3.internal;

import com.amazonaws.annotation.ThreadSafe;
import com.amazonaws.services.s3.transfer.exception.FileLockException;
import com.amazonaws.util.IOUtils;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@ThreadSafe
public enum FileLocks {

    private static final boolean EXTERNAL_LOCK = false;
    private static final Log log = LogFactory.getLog(FileLocks.class);
    private static final Map<File, RandomAccessFile> lockedFiles = new TreeMap<File, RandomAccessFile>();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean lock(File file) {
        boolean locked;
        Map<File, RandomAccessFile> map = lockedFiles;
        synchronized (map) {
            if (lockedFiles.containsKey(file)) {
                return false;
            }
        }
        Object lock = null;
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(file, "rw");
            FileChannel fileChannel = raf.getChannel();
        }
        catch (Exception e) {
            IOUtils.closeQuietly(raf, log);
            throw new FileLockException(e);
        }
        Map<File, RandomAccessFile> map2 = lockedFiles;
        synchronized (map2) {
            RandomAccessFile prev = lockedFiles.put(file, raf);
            if (prev == null) {
                locked = true;
            } else {
                locked = false;
                lockedFiles.put(file, prev);
            }
        }
        if (locked) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Locked file " + file + " with " + lock));
            }
        } else {
            IOUtils.closeQuietly(raf, log);
        }
        return locked;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean isFileLocked(File file) {
        Map<File, RandomAccessFile> map = lockedFiles;
        synchronized (map) {
            return lockedFiles.containsKey(file);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean unlock(File file) {
        Map<File, RandomAccessFile> map = lockedFiles;
        synchronized (map) {
            RandomAccessFile raf = lockedFiles.get(file);
            if (raf == null) {
                return false;
            }
            IOUtils.closeQuietly(raf, log);
            lockedFiles.remove(file);
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("Unlocked file " + file));
        }
        return true;
    }
}

