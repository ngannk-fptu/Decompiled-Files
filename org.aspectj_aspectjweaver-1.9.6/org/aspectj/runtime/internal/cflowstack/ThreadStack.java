/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.runtime.internal.cflowstack;

import java.util.Stack;

public interface ThreadStack {
    public Stack getThreadStack();

    public void removeThreadStack();
}

