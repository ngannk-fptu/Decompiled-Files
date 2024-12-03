/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckContext
 */
package com.atlassian.migration.agent.service.check.attachment;

import com.atlassian.cmpt.check.base.CheckContext;
import java.util.Set;

public class MissingAttachmentContext
implements CheckContext {
    public final Set<String> spaceKeys;

    public MissingAttachmentContext(Set<String> spaceKeys) {
        this.spaceKeys = spaceKeys;
    }
}

