/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.pool2.impl;

import java.io.PrintWriter;
import org.apache.tomcat.dbcp.pool2.impl.CallStack;

public class NoOpCallStack
implements CallStack {
    public static final CallStack INSTANCE = new NoOpCallStack();

    private NoOpCallStack() {
    }

    @Override
    public void clear() {
    }

    @Override
    public void fillInStackTrace() {
    }

    @Override
    public boolean printStackTrace(PrintWriter writer) {
        return false;
    }
}

