/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  org.apache.commons.io.IOUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.cache.filecache.impl;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.plugin.cache.filecache.Cache;
import com.atlassian.plugin.cache.filecache.impl.OneStreamCache;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.BooleanSupplier;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class StreamsCache {
    private static final String CLIENT_ABORT_EXCEPTION = "org.apache.catalina.connector.ClientAbortException";
    private final Object lock = new Object();
    private Logger log = LoggerFactory.getLogger(OneStreamCache.class);
    private int concurrentCount;
    private State state = State.UNCACHED;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected static void streamFromFile(File file, OutputStream out) {
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            IOUtils.copyLarge((InputStream)in, (OutputStream)out);
            out.flush();
        }
        catch (IOException e) {
            block4: {
                try {
                    if (e.getClass().getName().equals(CLIENT_ABORT_EXCEPTION)) break block4;
                    throw new RuntimeException(e);
                }
                catch (Throwable throwable) {
                    IOUtils.closeQuietly(in);
                    throw throwable;
                }
            }
            IOUtils.closeQuietly((InputStream)in);
        }
        IOUtils.closeQuietly((InputStream)in);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void deleteWhenPossible(Runnable callback) {
        Object object = this.lock;
        synchronized (object) {
            if (this.state == State.UNCACHED) {
                this.state = State.DELETED;
            } else if (this.state == State.CACHED) {
                this.state = State.NEEDSDELETE;
            }
            if (this.state == State.NEEDSDELETE && this.concurrentCount == 0) {
                callback.run();
                this.state = State.DELETED;
            }
        }
    }

    public abstract void stream(OutputStream var1, Cache.StreamProvider var2);

    public abstract void streamTwo(OutputStream var1, OutputStream var2, Cache.TwoStreamProvider var3);

    public abstract void deleteWhenPossible();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected boolean doEnter(Runnable streamCallback, BooleanSupplier checkFilesCallback) {
        Object object = this.lock;
        synchronized (object) {
            boolean useCache = false;
            if (this.state == State.UNCACHED) {
                useCache = this.tryStreamCallback(streamCallback);
            } else if (this.state == State.CACHED) {
                boolean isFileStillValid = checkFilesCallback.getAsBoolean();
                useCache = !isFileStillValid ? this.tryStreamCallback(streamCallback) : true;
            } else if (this.state == State.NEEDSDELETE) {
                useCache = true;
            }
            ++this.concurrentCount;
            return useCache;
        }
    }

    private boolean tryStreamCallback(Runnable streamCallback) {
        try {
            streamCallback.run();
            this.state = State.CACHED;
            return true;
        }
        catch (Exception e) {
            this.log.warn("Problem caching to disk, skipping cache for this entry", (Throwable)e);
            this.state = State.UNCACHED;
            return false;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void doExit(Runnable callback) {
        Object object = this.lock;
        synchronized (object) {
            --this.concurrentCount;
            if (this.state == State.NEEDSDELETE && this.concurrentCount == 0) {
                callback.run();
                this.state = State.DELETED;
            }
        }
    }

    protected OutputStream createWriteStream(File file) throws FileNotFoundException {
        return new FileOutputStream(file);
    }

    @VisibleForTesting
    public void setLogger(Logger log) {
        this.log = log;
    }

    static enum State {
        UNCACHED,
        CACHED,
        NEEDSDELETE,
        DELETED;

    }
}

