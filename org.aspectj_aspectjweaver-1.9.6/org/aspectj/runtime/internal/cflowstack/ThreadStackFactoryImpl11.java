/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.runtime.internal.cflowstack;

import org.aspectj.runtime.internal.cflowstack.ThreadCounter;
import org.aspectj.runtime.internal.cflowstack.ThreadCounterImpl11;
import org.aspectj.runtime.internal.cflowstack.ThreadStack;
import org.aspectj.runtime.internal.cflowstack.ThreadStackFactory;
import org.aspectj.runtime.internal.cflowstack.ThreadStackImpl11;

public class ThreadStackFactoryImpl11
implements ThreadStackFactory {
    @Override
    public ThreadStack getNewThreadStack() {
        return new ThreadStackImpl11();
    }

    @Override
    public ThreadCounter getNewThreadCounter() {
        return new ThreadCounterImpl11();
    }
}

