/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin.descriptor.web.conditions;

import com.atlassian.confluence.admin.criteria.WritableDirectoryExistsCriteria;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;

public class WritableDirectoryExistsCondition
extends BaseConfluenceCondition {
    private WritableDirectoryExistsCriteria writableDirectoryExistsCriteria;

    @Override
    protected boolean shouldDisplay(WebInterfaceContext context) {
        return this.writableDirectoryExistsCriteria.isMet();
    }

    public void setWritableDirectoryExistsCriteria(WritableDirectoryExistsCriteria writableDirectoryExistsCriteria) {
        this.writableDirectoryExistsCriteria = writableDirectoryExistsCriteria;
    }
}

