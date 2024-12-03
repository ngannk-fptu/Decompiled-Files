/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.api.model.relations;

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.people.User;
import com.atlassian.confluence.api.model.relations.AbstractRelationDescriptor;
import com.atlassian.confluence.api.model.relations.Relatable;
import com.atlassian.confluence.api.model.relations.RelationDescriptors;

public final class LikeRelationDescriptor<T extends Relatable>
extends AbstractRelationDescriptor<User, T> {
    public static final String NAME = "like";
    public static final LikeRelationDescriptor<Content> CONTENT_LIKE = new LikeRelationDescriptor<Content>(Content.class);

    private LikeRelationDescriptor(Class<T> targetClass) {
        super(NAME, User.class, targetClass);
    }

    static void register() {
        RelationDescriptors.registerBuiltIn(CONTENT_LIKE);
    }
}

