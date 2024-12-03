/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.util.compression;

import java.util.zip.Deflater;
import org.eclipse.jetty.util.component.Container;
import org.eclipse.jetty.util.compression.CompressionPool;
import org.eclipse.jetty.util.thread.ThreadPool;

public class DeflaterPool
extends CompressionPool<Deflater> {
    private final int compressionLevel;
    private final boolean nowrap;

    public DeflaterPool(int capacity, int compressionLevel, boolean nowrap) {
        super(capacity);
        this.compressionLevel = compressionLevel;
        this.nowrap = nowrap;
    }

    @Override
    protected Deflater newPooled() {
        return new Deflater(this.compressionLevel, this.nowrap);
    }

    @Override
    protected void end(Deflater deflater) {
        deflater.end();
    }

    @Override
    protected void reset(Deflater deflater) {
        deflater.reset();
    }

    public static DeflaterPool ensurePool(Container container) {
        DeflaterPool pool = container.getBean(DeflaterPool.class);
        if (pool != null) {
            return pool;
        }
        int capacity = 1024;
        ThreadPool.SizedThreadPool threadPool = container.getBean(ThreadPool.SizedThreadPool.class);
        if (threadPool != null) {
            capacity = threadPool.getMaxThreads();
        }
        pool = new DeflaterPool(capacity, -1, true);
        container.addBean(pool, true);
        return pool;
    }
}

