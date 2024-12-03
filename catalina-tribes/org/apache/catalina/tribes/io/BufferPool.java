/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.tribes.io;

import org.apache.catalina.tribes.io.BufferPool15Impl;
import org.apache.catalina.tribes.io.XByteBuffer;
import org.apache.catalina.tribes.util.StringManager;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class BufferPool {
    private static final Log log = LogFactory.getLog(BufferPool.class);
    public static final int DEFAULT_POOL_SIZE = 0x6400000;
    protected static final StringManager sm = StringManager.getManager(BufferPool.class);
    protected static volatile BufferPool instance = null;
    protected final BufferPoolAPI pool;

    private BufferPool(BufferPoolAPI pool) {
        this.pool = pool;
    }

    public XByteBuffer getBuffer(int minSize, boolean discard) {
        if (this.pool != null) {
            return this.pool.getBuffer(minSize, discard);
        }
        return new XByteBuffer(minSize, discard);
    }

    public void returnBuffer(XByteBuffer buffer) {
        if (this.pool != null) {
            this.pool.returnBuffer(buffer);
        }
    }

    public void clear() {
        if (this.pool != null) {
            this.pool.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static BufferPool getBufferPool() {
        if (instance != null) return instance;
        Class<BufferPool> clazz = BufferPool.class;
        synchronized (BufferPool.class) {
            if (instance != null) return instance;
            BufferPool15Impl pool = new BufferPool15Impl();
            pool.setMaxSize(0x6400000);
            log.info((Object)sm.getString("bufferPool.created", Integer.toString(0x6400000), pool.getClass().getName()));
            instance = new BufferPool(pool);
            // ** MonitorExit[var0] (shouldn't be in output)
            return instance;
        }
    }

    public static interface BufferPoolAPI {
        public void setMaxSize(int var1);

        public XByteBuffer getBuffer(int var1, boolean var2);

        public void returnBuffer(XByteBuffer var1);

        public void clear();
    }
}

