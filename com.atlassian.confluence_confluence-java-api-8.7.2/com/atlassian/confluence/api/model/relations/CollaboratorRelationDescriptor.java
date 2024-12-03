/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.api.model.relations;

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.people.User;
import com.atlassian.confluence.api.model.relations.AbstractRelationDescriptor;
import com.atlassian.confluence.api.model.relations.RelationDescriptors;

public final class CollaboratorRelationDescriptor
extends AbstractRelationDescriptor<User, Content> {
    public static final String NAME = "collaborator";
    public static final CollaboratorRelationDescriptor COLLABORATOR = new CollaboratorRelationDescriptor();

    private CollaboratorRelationDescriptor() {
        super(NAME, User.class, Content.class);
    }

    static void register() {
        RelationDescriptors.registerBuiltIn(COLLABORATOR);
    }
}

