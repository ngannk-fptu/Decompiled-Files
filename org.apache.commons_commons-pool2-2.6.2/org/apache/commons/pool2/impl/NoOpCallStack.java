/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.pool2.impl;

import java.io.PrintWriter;
import org.apache.commons.pool2.impl.CallStack;

public class NoOpCallStack
implements CallStack {
    public static final CallStack INSTANCE = new NoOpCallStack();

    private NoOpCallStack() {
    }

    @Override
    public boolean printStackTrace(PrintWriter writer) {
        return false;
    }

    @Override
    public void fillInStackTrace() {
    }

    @Override
    public void clear() {
    }
}

