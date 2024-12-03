/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.hibernate.bulk;

import com.atlassian.confluence.impl.hibernate.bulk.BulkAction;
import com.atlassian.confluence.impl.hibernate.bulk.BulkExecutionContext;

public interface HibernateBulkAction<CONTEXT extends BulkExecutionContext, TARGET> {
    public void execute(CONTEXT var1, TARGET var2, BulkAction<CONTEXT, TARGET> var3);
}

