/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.ProgressMeter
 */
package com.atlassian.confluence.pages.persistence.dao.bulk.delete;

import com.atlassian.confluence.impl.hibernate.bulk.BulkExecutionContext;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.core.util.ProgressMeter;

public class BulkPageDeleteExecutionContext
implements BulkExecutionContext {
    private final ProgressMeter progressMeter;
    private final ConfluenceUser user;
    private final Page targetParent;

    public BulkPageDeleteExecutionContext(ProgressMeter progressMeter, ConfluenceUser user, Page targetParent) {
        this.progressMeter = progressMeter;
        this.user = user;
        this.targetParent = targetParent;
    }

    public ProgressMeter getProgressMeter() {
        return this.progressMeter;
    }

    public ConfluenceUser getUser() {
        return this.user;
    }

    public Page getTargetParent() {
        return this.targetParent;
    }
}

