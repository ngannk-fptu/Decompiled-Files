/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.people.Group
 *  com.atlassian.confluence.api.nav.Navigation$GroupNav
 */
package com.atlassian.confluence.plugins.rest.navigation.impl;

import com.atlassian.confluence.api.model.people.Group;
import com.atlassian.confluence.api.nav.Navigation;
import com.atlassian.confluence.plugins.rest.navigation.impl.AbstractNav;
import com.atlassian.confluence.plugins.rest.navigation.impl.DelegatingPathBuilder;

class GroupNavBuilderImpl
extends DelegatingPathBuilder
implements Navigation.GroupNav {
    public GroupNavBuilderImpl(Group group, AbstractNav baseApiPathBuilder) {
        super("/group/" + group.getName(), baseApiPathBuilder);
    }
}

