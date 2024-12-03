/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.migration.agent.service.extract;

import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;

public interface GroupExtractionService {
    public Set<String> getGroupsFromSpace(@Nonnull String var1);

    public Map<String, Set<String>> getGroupsFromSpaces(List<String> var1);

    public Set<String> getGroupsFromGlobalEntities();
}

