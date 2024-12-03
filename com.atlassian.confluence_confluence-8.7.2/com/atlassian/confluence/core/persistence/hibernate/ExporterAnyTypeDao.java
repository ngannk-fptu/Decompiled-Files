/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.core.persistence.hibernate;

import com.atlassian.confluence.core.persistence.AnyTypeDao;
import com.atlassian.confluence.core.persistence.hibernate.TransientHibernateHandle;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public interface ExporterAnyTypeDao
extends AnyTypeDao {
    @Deprecated
    default public List<TransientHibernateHandle> findAllPersistentObjectsHibernateHandles() {
        return this.findAllPersistentObjectsHibernateHandles(Collections.emptyList());
    }

    public List<TransientHibernateHandle> findAllPersistentObjectsHibernateHandles(Collection<Class<?>> var1);
}

