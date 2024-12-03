/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages.persistence.dao.bulk.copy;

import com.atlassian.confluence.impl.hibernate.bulk.BulkExecutionContext;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.util.SubProgressMeter;

public class BulkPageCopyExecutionContext
implements BulkExecutionContext {
    private long parentPageId;
    private SubProgressMeter progressMeter;

    public BulkPageCopyExecutionContext(SubProgressMeter progressMeter, Page parentPage) {
        this.progressMeter = progressMeter;
        this.parentPageId = parentPage.getId();
    }

    public BulkPageCopyExecutionContext(BulkPageCopyExecutionContext previousContext, Page parentPage) {
        this(previousContext.getProgressMeter(), parentPage);
    }

    public SubProgressMeter getProgressMeter() {
        return this.progressMeter;
    }

    public long getParentPageId() {
        return this.parentPageId;
    }
}

