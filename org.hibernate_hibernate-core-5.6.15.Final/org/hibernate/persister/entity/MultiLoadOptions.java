/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.persister.entity;

import org.hibernate.LockOptions;

public interface MultiLoadOptions {
    public boolean isSessionCheckingEnabled();

    public boolean isSecondLevelCacheCheckingEnabled();

    public boolean isReturnOfDeletedEntitiesEnabled();

    public boolean isOrderReturnEnabled();

    public LockOptions getLockOptions();

    public Integer getBatchSize();
}

