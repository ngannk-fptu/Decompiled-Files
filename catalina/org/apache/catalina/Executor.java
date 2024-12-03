/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina;

import java.util.concurrent.TimeUnit;
import org.apache.catalina.Lifecycle;

public interface Executor
extends java.util.concurrent.Executor,
Lifecycle {
    public String getName();

    @Deprecated
    public void execute(Runnable var1, long var2, TimeUnit var4);
}

