/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.async;

import com.mchange.v2.async.AsynchronousRunner;
import com.mchange.v2.async.RunnableQueue;

public interface Queuable
extends AsynchronousRunner {
    public RunnableQueue asRunnableQueue();
}

