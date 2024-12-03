/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.bean.EntityObject
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.internal.persistence;

import com.atlassian.confluence.core.persistence.VersionedObjectDao;
import com.atlassian.confluence.internal.persistence.ObjectDaoInternal;
import com.atlassian.core.bean.EntityObject;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface VersionedObjectDaoInternal<T extends EntityObject>
extends VersionedObjectDao<T>,
ObjectDaoInternal<T> {
    public void saveEntity(T var1, @Nullable T var2);
}

