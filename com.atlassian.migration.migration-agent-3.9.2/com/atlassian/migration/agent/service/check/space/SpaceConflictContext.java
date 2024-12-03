/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckContext
 */
package com.atlassian.migration.agent.service.check.space;

import com.atlassian.cmpt.check.base.CheckContext;
import java.util.Set;

public class SpaceConflictContext
implements CheckContext {
    public final Set<String> spaceKeys;
    public final String cloudId;

    public SpaceConflictContext(String cloudId, Set<String> spaceKeys) {
        this.spaceKeys = spaceKeys;
        this.cloudId = cloudId;
    }
}

