/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.pool2.impl;

import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.tomcat.dbcp.pool2.impl.CallStack;

public class SecurityManagerCallStack
implements CallStack {
    private final String messageFormat;
    private final DateFormat dateFormat;
    private final PrivateSecurityManager securityManager;
    private volatile Snapshot snapshot;

    public SecurityManagerCallStack(String messageFormat, boolean useTimestamp) {
        this.messageFormat = messageFormat;
        this.dateFormat = useTimestamp ? new SimpleDateFormat(messageFormat) : null;
        this.securityManager = AccessController.doPrivileged(() -> new PrivateSecurityManager());
    }

    @Override
    public void clear() {
        this.snapshot = null;
    }

    @Override
    public void fillInStackTrace() {
        this.snapshot = new Snapshot(this.securityManager.getCallStack());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean printStackTrace(PrintWriter writer) {
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
        snapshotRef.stack.forEach(reference -> writer.println(reference.get()));
        return true;
    }

    private static class PrivateSecurityManager
    extends SecurityManager {
        private PrivateSecurityManager() {
        }

        private List<WeakReference<Class<?>>> getCallStack() {
            Stream<WeakReference> map = Stream.of(this.getClassContext()).map(WeakReference::new);
            return map.collect(Collectors.toList());
        }
    }

    private static class Snapshot {
        private final long timestampMillis = System.currentTimeMillis();
        private final List<WeakReference<Class<?>>> stack;

        private Snapshot(List<WeakReference<Class<?>>> stack) {
            this.stack = stack;
        }
    }
}

