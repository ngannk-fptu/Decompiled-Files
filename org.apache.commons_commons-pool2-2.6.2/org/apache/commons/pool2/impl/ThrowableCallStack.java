/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.pool2.impl;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.apache.commons.pool2.impl.CallStack;

public class ThrowableCallStack
implements CallStack {
    private final String messageFormat;
    private final DateFormat dateFormat;
    private volatile Snapshot snapshot;

    public ThrowableCallStack(String messageFormat, boolean useTimestamp) {
        this.messageFormat = messageFormat;
        this.dateFormat = useTimestamp ? new SimpleDateFormat(messageFormat) : null;
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
                message = this.dateFormat.format(snapshotRef.timestamp);
            }
        }
        writer.println(message);
        snapshotRef.printStackTrace(writer);
        return true;
    }

    @Override
    public void fillInStackTrace() {
        this.snapshot = new Snapshot();
    }

    @Override
    public void clear() {
        this.snapshot = null;
    }

    private static class Snapshot
    extends Throwable {
        private static final long serialVersionUID = 1L;
        private final long timestamp = System.currentTimeMillis();

        private Snapshot() {
        }
    }
}

