/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.tools.cache;

import java.util.zip.CRC32;
import org.aspectj.weaver.tools.Trace;
import org.aspectj.weaver.tools.TraceFactory;
import org.aspectj.weaver.tools.cache.CacheBacking;

public abstract class AbstractCacheBacking
implements CacheBacking {
    protected final Trace logger = TraceFactory.getTraceFactory().getTrace(this.getClass());

    protected AbstractCacheBacking() {
    }

    public static final long crc(byte[] bytes) {
        if (bytes == null || bytes.length <= 0) {
            return 0L;
        }
        CRC32 crc32 = new CRC32();
        crc32.update(bytes);
        return crc32.getValue();
    }
}

