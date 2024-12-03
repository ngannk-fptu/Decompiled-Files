/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.group.Group
 */
package com.atlassian.crowd.manager.directory.nestedgroups;

import com.atlassian.crowd.model.group.Group;
import java.util.List;

@FunctionalInterface
public interface SingleGroupProvider {
    public List<Group> getDirectlyRelatedGroups(String var1) throws Exception;
}

