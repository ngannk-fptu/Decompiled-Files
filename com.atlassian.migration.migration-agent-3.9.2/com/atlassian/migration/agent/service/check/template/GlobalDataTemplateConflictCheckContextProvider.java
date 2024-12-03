/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.check.template;

import com.atlassian.migration.agent.service.check.CheckContextProvider;
import com.atlassian.migration.agent.service.check.ContextProviderUtil;
import com.atlassian.migration.agent.service.check.template.GlobalDataTemplateConflictContext;
import java.util.Map;

public class GlobalDataTemplateConflictCheckContextProvider
implements CheckContextProvider<GlobalDataTemplateConflictContext> {
    @Override
    public GlobalDataTemplateConflictContext apply(Map<String, Object> parameters) {
        return new GlobalDataTemplateConflictContext(ContextProviderUtil.getCloudId(parameters), ContextProviderUtil.getGlobalEntityType(parameters));
    }
}

