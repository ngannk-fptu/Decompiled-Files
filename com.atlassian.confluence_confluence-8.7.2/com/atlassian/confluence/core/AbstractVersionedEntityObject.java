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
import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.core.VersionChildOwnerPolicy;
import com.atlassian.confluence.core.Versioned;
import com.atlassian.confluence.internal.persistence.ObjectDaoInternal;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class AbstractVersionedEntityObject
extends ConfluenceEntityObject
implements Versioned,
Cloneable {
    protected static final int INITIAL_VERSION = 1;
    private int version = 1;
    private AbstractVersionedEntityObject originalVersion;
    private int hibernateVersion = 1;

    @Override
    public int getVersion() {
        return this.version;
    }

    @Override
    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public boolean isNew() {
        return this.getVersion() == 1;
    }

    private ConfluenceEntityObject getOriginalVersion() {
        return this.originalVersion;
    }

    @Override
    public void setOriginalVersion(Versioned originalVersion) {
        this.originalVersion = (AbstractVersionedEntityObject)originalVersion;
    }

    @Override
    public Versioned getLatestVersion() {
        return this.originalVersion == null ? this : this.originalVersion;
    }

    @Override
    public void convertToHistoricalVersion() {
        this.setId(0L);
    }

    @Override
    @Deprecated
    public void applyChildVersioningPolicy(Versioned versionToPromote, ObjectDao dao) {
    }

    @Override
    public void applyChildVersioningPolicy(@Nullable Versioned versionToPromote, ObjectDaoInternal<?> dao) {
    }

    @Override
    public boolean isLatestVersion() {
        return this.getOriginalVersion() == null;
    }

    @Override
    public VersionChildOwnerPolicy getVersionChildPolicy(ContentType contentType) {
        return VersionChildOwnerPolicy.currentVersion;
    }

    private int getHibernateVersion() {
        return this.hibernateVersion;
    }

    protected String getConfluenceRevision() {
        return String.valueOf(this.hibernateVersion);
    }

    private void setHibernateVersion(int hibernateVersion) {
        this.hibernateVersion = hibernateVersion;
    }
}

