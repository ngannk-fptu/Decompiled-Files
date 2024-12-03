/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.Condition
 */
package com.atlassian.confluence.compat.plugin.descriptor.web.conditions.accessmode;

import com.atlassian.confluence.compat.api.service.accessmode.AccessModeCompatService;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import java.util.Map;

public class ReadWriteAccessModeCondition
implements Condition {
    private final AccessModeCompatService accessModeCompatService;

    public ReadWriteAccessModeCondition(AccessModeCompatService accessModeCompatService) {
        this.accessModeCompatService = accessModeCompatService;
    }

    public void init(Map<String, String> params) throws PluginParseException {
    }

    public boolean shouldDisplay(Map<String, Object> context) {
        return !this.accessModeCompatService.isReadOnlyAccessModeEnabled();
    }
}

