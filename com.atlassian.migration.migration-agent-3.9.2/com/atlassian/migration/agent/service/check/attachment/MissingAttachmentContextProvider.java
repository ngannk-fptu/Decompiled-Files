/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 */
package com.atlassian.migration.agent.service.check.attachment;

import com.atlassian.migration.agent.service.check.CheckContextProvider;
import com.atlassian.migration.agent.service.check.ContextProviderUtil;
import com.atlassian.migration.agent.service.check.attachment.MissingAttachmentContext;
import com.google.common.collect.Sets;
import java.util.Map;

public class MissingAttachmentContextProvider
implements CheckContextProvider<MissingAttachmentContext> {
    @Override
    public MissingAttachmentContext apply(Map<String, Object> parameters) {
        Object[] keys = ContextProviderUtil.getSpaceKeys(parameters);
        return new MissingAttachmentContext(Sets.newHashSet((Object[])keys));
    }
}

