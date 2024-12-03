/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.group.Group
 *  com.google.common.collect.ListMultimap
 */
package com.atlassian.crowd.manager.directory.nestedgroups;

import com.atlassian.crowd.model.group.Group;
import com.google.common.collect.ListMultimap;
import java.util.Collection;

@FunctionalInterface
public interface MultipleGroupsProvider {
    public ListMultimap<String, Group> getDirectlyRelatedGroups(Collection<String> var1) throws Exception;
}

