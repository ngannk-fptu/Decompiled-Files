/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckContext
 */
package com.atlassian.migration.agent.service.check.group;

import com.atlassian.cmpt.check.base.CheckContext;
import java.util.List;

public class GroupNamesConflictContext
implements CheckContext {
    public final String cloudId;
    public final List<String> groups;

    public GroupNamesConflictContext(String cloudId, List<String> groups) {
        this.cloudId = cloudId;
        this.groups = groups;
    }
}

