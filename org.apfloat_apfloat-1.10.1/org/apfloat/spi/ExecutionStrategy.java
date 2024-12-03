/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.spi;

import java.util.concurrent.Future;

public interface ExecutionStrategy {
    public void wait(Future<?> var1);
}

