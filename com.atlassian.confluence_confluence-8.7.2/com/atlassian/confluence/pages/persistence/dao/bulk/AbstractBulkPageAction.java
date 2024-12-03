/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages.persistence.dao.bulk;

import com.atlassian.confluence.impl.hibernate.bulk.BulkAction;
import com.atlassian.confluence.impl.hibernate.bulk.BulkActionReportAware;
import com.atlassian.confluence.impl.hibernate.bulk.BulkExecutionContext;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.persistence.dao.bulk.DefaultBulkOptions;

public abstract class AbstractBulkPageAction<OPTIONS extends DefaultBulkOptions, CONTEXT extends BulkExecutionContext>
implements BulkAction<CONTEXT, Page>,
BulkActionReportAware {
    protected final OPTIONS options;

    public AbstractBulkPageAction(OPTIONS options) {
        this.options = options;
    }
}

