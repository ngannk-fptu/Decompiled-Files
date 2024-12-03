/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.people.Group
 *  com.atlassian.confluence.api.model.people.User
 *  com.atlassian.confluence.api.service.finder.SingleFetcher
 *  com.atlassian.confluence.api.service.people.GroupService$GroupFinder
 */
package com.atlassian.confluence.impl.service.finder.people;

import com.atlassian.confluence.api.model.people.Group;
import com.atlassian.confluence.api.model.people.User;
import com.atlassian.confluence.api.service.finder.SingleFetcher;
import com.atlassian.confluence.api.service.people.GroupService;
import com.atlassian.confluence.impl.service.finder.NoopFetcher;

public class NoopGroupFinder
extends NoopFetcher<Group>
implements GroupService.GroupFinder {
    public SingleFetcher<Group> withName(String groupName) {
        return this;
    }

    public GroupService.GroupFinder withMember(User person) {
        return this;
    }
}

