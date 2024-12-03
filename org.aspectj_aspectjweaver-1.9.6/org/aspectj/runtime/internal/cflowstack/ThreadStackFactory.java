/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.runtime.internal.cflowstack;

import org.aspectj.runtime.internal.cflowstack.ThreadCounter;
import org.aspectj.runtime.internal.cflowstack.ThreadStack;

public interface ThreadStackFactory {
    public ThreadStack getNewThreadStack();

    public ThreadCounter getNewThreadCounter();
}

