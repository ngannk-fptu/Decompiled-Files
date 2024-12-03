/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.statistics.observer;

public interface OperationObserver<T extends Enum<T>> {
    public void begin();

    public void end(T var1);

    public void end(T var1, long ... var2);
}

