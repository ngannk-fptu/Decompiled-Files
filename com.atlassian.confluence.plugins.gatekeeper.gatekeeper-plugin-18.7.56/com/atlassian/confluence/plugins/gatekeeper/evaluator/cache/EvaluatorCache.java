/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.gatekeeper.evaluator.cache;

import com.atlassian.confluence.plugins.gatekeeper.model.event.TinyEvent;
import com.atlassian.confluence.plugins.gatekeeper.model.owner.GroupMembers;
import com.atlassian.confluence.plugins.gatekeeper.model.owner.TinyGroup;
import com.atlassian.confluence.plugins.gatekeeper.model.owner.TinyOwner;
import com.atlassian.confluence.plugins.gatekeeper.model.owner.TinyUser;
import com.atlassian.confluence.plugins.gatekeeper.model.space.SpacePermissions;
import com.atlassian.confluence.plugins.gatekeeper.model.space.TinySpace;
import java.util.List;

public interface EvaluatorCache {
    public List<TinySpace> getSpaces();

    public List<TinyUser> getUsers();

    public List<TinyGroup> getGroups();

    public void update(List<TinyEvent> var1);

    public boolean isGlobalAnonymousAccessEnabled();

    public SpacePermissions getSpacePermissions(String var1);

    public GroupMembers getGroupMembers(String var1);

    public boolean isUserConfluenceAdministrator(String var1);

    public TinyOwner getGroup(String var1);

    public TinyOwner getUser(String var1);

    public boolean hasUserCanUse(String var1);

    public boolean hasGroupCanUse(String var1);

    public String getMemoryUsage();
}

