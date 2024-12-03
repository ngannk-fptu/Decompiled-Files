/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.security;

import java.security.PrivilegedAction;

public class PrivilegedGetTccl
implements PrivilegedAction<ClassLoader> {
    private final Thread currentThread;

    @Deprecated
    public PrivilegedGetTccl() {
        this(Thread.currentThread());
    }

    public PrivilegedGetTccl(Thread currentThread) {
        this.currentThread = currentThread;
    }

    @Override
    public ClassLoader run() {
        return this.currentThread.getContextClassLoader();
    }
}

