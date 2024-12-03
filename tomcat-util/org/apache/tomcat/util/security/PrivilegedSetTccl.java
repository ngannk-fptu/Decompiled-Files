/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.security;

import java.security.PrivilegedAction;

public class PrivilegedSetTccl
implements PrivilegedAction<Void> {
    private final ClassLoader cl;
    private final Thread t;

    @Deprecated
    public PrivilegedSetTccl(ClassLoader cl) {
        this(Thread.currentThread(), cl);
    }

    public PrivilegedSetTccl(Thread t, ClassLoader cl) {
        this.t = t;
        this.cl = cl;
    }

    @Override
    public Void run() {
        this.t.setContextClassLoader(this.cl);
        return null;
    }
}

