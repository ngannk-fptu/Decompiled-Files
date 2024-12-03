/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.util.BatchResult
 */
package com.atlassian.crowd.util.persistence.hibernate.batch;

import com.atlassian.crowd.util.BatchResult;
import com.atlassian.crowd.util.persistence.hibernate.batch.BulkAuditMapper;
import com.atlassian.crowd.util.persistence.hibernate.batch.HibernateOperation;
import java.util.Collection;

public interface BatchProcessor<S> {
    public <E> BatchResult<E> execute(HibernateOperation<S> var1, Collection<E> var2);

    public <E> BatchResult<E> execute(HibernateOperation<S> var1, Collection<E> var2, BulkAuditMapper<E> var3);
}

