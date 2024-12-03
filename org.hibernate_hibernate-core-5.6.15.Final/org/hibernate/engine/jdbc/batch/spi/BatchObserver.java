/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.batch.spi;

public interface BatchObserver {
    public void batchExplicitlyExecuted();

    public void batchImplicitlyExecuted();
}

