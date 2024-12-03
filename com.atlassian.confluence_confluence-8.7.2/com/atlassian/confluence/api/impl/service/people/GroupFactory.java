/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.people.Group
 *  com.atlassian.user.Group
 */
package com.atlassian.confluence.api.impl.service.people;

import com.atlassian.confluence.api.impl.service.content.factory.ModelFactory;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.user.Group;

public class GroupFactory
extends ModelFactory<Group, com.atlassian.confluence.api.model.people.Group> {
    @Override
    public com.atlassian.confluence.api.model.people.Group buildFrom(Group hibernateObject, Expansions expansions) {
        return new com.atlassian.confluence.api.model.people.Group(hibernateObject.getName());
    }
}

