/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.util.thread;

import java.security.PrivilegedAction;
import org.eclipse.jetty.util.security.SecurityUtils;

class PrivilegedThreadFactory {
    static <T extends Thread> T newThread(PrivilegedAction<T> creator) {
        return (T)((Thread)SecurityUtils.doPrivileged(creator));
    }

    private PrivilegedThreadFactory() {
    }
}

