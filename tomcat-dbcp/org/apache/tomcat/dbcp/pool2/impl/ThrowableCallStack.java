/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.pool2.impl;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.apache.tomcat.dbcp.pool2.impl.CallStack;

public class ThrowableCallStack
implements CallStack {
    private final String messageFormat;
    private final DateFormat dateFormat;
    private volatile Snapshot snapshot;

    public ThrowableCallStack(String messageFormat, boolean useTimestamp) {
        this.messageFormat = messageFormat;
        this.dateFormat = useTimestamp ? new SimpleDateFormat(messageFormat) : null;
    }

    @Override
    public void clear() {
        this.snapshot = null;
    }

    @Override
    public void fillInStackTrace() {
        this.snapshot = new Snapshot();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized boolean printStackTrace(PrintWriter writer) {
        String message;
        Snapshot snapshotRef = this.snapshot;
        if (snapshotRef == null) {
            return false;
        }
        if (this.dateFormat == null) {
            message = this.messageFormat;
        } else {
            DateFormat dateFormat = this.dateFormat;
            synchronized (dateFormat) {
                message = this.dateFormat.format(snapshotRef.timestampMillis);
            }
        }
        writer.println(message);
        snapshotRef.printStackTrace(writer);
        return true;
    }

    private static class Snapshot
    extends Throwable {
        private static final long serialVersionUID = 1L;
        private final long timestampMillis = System.currentTimeMillis();

        private Snapshot() {
        }
    }
}

