/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.api.model.relations;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.annotations.Internal;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.people.User;
import com.atlassian.confluence.api.model.relations.AbstractRelationDescriptor;
import com.atlassian.confluence.api.model.relations.RelationDescriptors;

@ExperimentalApi
@Internal
public class CumulativeContributorRelationDescriptor
extends AbstractRelationDescriptor<User, Content> {
    public static final String NAME = "contributor";
    public static final CumulativeContributorRelationDescriptor CUMULATIVE_CONTRIBUTOR = new CumulativeContributorRelationDescriptor();

    protected CumulativeContributorRelationDescriptor() {
        super(NAME, User.class, Content.class);
    }

    static void register() {
        RelationDescriptors.registerBuiltIn(CUMULATIVE_CONTRIBUTOR);
    }
}

