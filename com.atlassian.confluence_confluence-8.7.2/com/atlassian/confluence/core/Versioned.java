/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bucket.core.persistence.ObjectDao
 *  com.atlassian.confluence.api.model.content.ContentType
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.core;

import bucket.core.persistence.ObjectDao;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.core.VersionChildOwnerPolicy;
import com.atlassian.confluence.internal.persistence.ObjectDaoInternal;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface Versioned {
    public int getVersion();

    public void setVersion(int var1);

    public void convertToHistoricalVersion();

    @Deprecated
    public void applyChildVersioningPolicy(@Nullable Versioned var1, ObjectDao var2);

    public void applyChildVersioningPolicy(@Nullable Versioned var1, ObjectDaoInternal<?> var2);

    public boolean isNew();

    public Versioned getLatestVersion();

    @Deprecated
    public void setOriginalVersion(Versioned var1);

    public boolean isLatestVersion();

    public VersionChildOwnerPolicy getVersionChildPolicy(ContentType var1);
}

