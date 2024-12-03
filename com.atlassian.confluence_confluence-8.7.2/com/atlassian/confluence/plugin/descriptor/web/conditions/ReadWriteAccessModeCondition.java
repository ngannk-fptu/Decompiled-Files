/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.accessmode.AccessMode
 *  com.atlassian.confluence.api.service.accessmode.AccessModeService
 */
package com.atlassian.confluence.plugin.descriptor.web.conditions;

import com.atlassian.confluence.api.model.accessmode.AccessMode;
import com.atlassian.confluence.api.service.accessmode.AccessModeService;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;

public class ReadWriteAccessModeCondition
extends BaseConfluenceCondition {
    private AccessModeService accessModeService;

    @Override
    protected boolean shouldDisplay(WebInterfaceContext context) {
        return AccessMode.READ_WRITE.equals((Object)this.accessModeService.getAccessMode());
    }

    public void setAccessModeService(AccessModeService accessModeService) {
        this.accessModeService = accessModeService;
    }
}

