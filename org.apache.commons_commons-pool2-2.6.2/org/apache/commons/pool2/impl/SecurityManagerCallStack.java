/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.pool2.impl;

import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.pool2.impl.CallStack;

public class SecurityManagerCallStack
implements CallStack {
    private final String messageFormat;
    private final DateFormat dateFormat;
    private final PrivateSecurityManager securityManager;
    private volatile Snapshot snapshot;

    public SecurityManagerCallStack(String messageFormat, boolean useTimestamp) {
        this.messageFormat = messageFormat;
        this.dateFormat = useTimestamp ? new SimpleDateFormat(messageFormat) : null;
        this.securityManager = AccessController.doPrivileged(new PrivilegedAction<PrivateSecurityManager>(){

            @Override
            public PrivateSecurityManager run() {
                return new PrivateSecurityManager();
            }
        });
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
                message = this.dateFormat.format(snapshotRef.timestamp);
            }
        }
        writer.println(message);
        for (WeakReference reference : snapshotRef.stack) {
            writer.println(reference.get());
        }
        return true;
    }

    @Override
    public void fillInStackTrace() {
        this.snapshot = new Snapshot(this.securityManager.getCallStack());
    }

    @Override
    public void clear() {
        this.snapshot = null;
    }

    private static class Snapshot {
        private final long timestamp = System.currentTimeMillis();
        private final List<WeakReference<Class<?>>> stack;

        private Snapshot(List<WeakReference<Class<?>>> stack) {
            this.stack = stack;
        }
    }

    private static class PrivateSecurityManager
    extends SecurityManager {
        private PrivateSecurityManager() {
        }

        private List<WeakReference<Class<?>>> getCallStack() {
            Class<?>[] classes = this.getClassContext();
            ArrayList stack = new ArrayList(classes.length);
            for (Class<?> klass : classes) {
                stack.add(new WeakReference(klass));
            }
            return stack;
        }
    }
}

