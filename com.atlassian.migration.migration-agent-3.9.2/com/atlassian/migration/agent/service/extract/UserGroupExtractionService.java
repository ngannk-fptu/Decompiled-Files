/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.extract;

import java.util.List;
import java.util.Set;

public interface UserGroupExtractionService {
    public Set<String> getUsersFromSpaces(List<String> var1);

    public Set<String> getGroupsFromSpaces(List<String> var1);
}

