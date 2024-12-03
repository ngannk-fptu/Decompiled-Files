/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.confluence.internal.index;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.core.persistence.hibernate.HibernateHandle;
import com.atlassian.confluence.internal.index.ReindexProgress;
import java.util.List;

@ExperimentalApi
@FunctionalInterface
public interface BatchIndexer {
    public void index(List<HibernateHandle> var1, ReindexProgress var2);
}

