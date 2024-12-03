/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.config;

import org.apache.tika.parser.ParseContext;

public class TikaTaskTimeout {
    private final long timeoutMillis;

    public TikaTaskTimeout(long timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
    }

    public long getTimeoutMillis() {
        return this.timeoutMillis;
    }

    public static long getTimeoutMillis(ParseContext context, long defaultTimeoutMillis) {
        TikaTaskTimeout tikaTaskTimeout = context.get(TikaTaskTimeout.class);
        if (tikaTaskTimeout == null) {
            return defaultTimeoutMillis;
        }
        return tikaTaskTimeout.getTimeoutMillis();
    }
}

