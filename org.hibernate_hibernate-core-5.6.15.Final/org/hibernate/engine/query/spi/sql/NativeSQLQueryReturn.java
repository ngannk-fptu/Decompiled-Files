/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.query.spi.sql;

public interface NativeSQLQueryReturn {
    public void traceLog(TraceLogger var1);

    public static interface TraceLogger {
        public void writeLine(String var1);
    }
}

