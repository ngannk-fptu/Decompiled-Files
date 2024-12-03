/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.api.model.relations;

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.api.model.people.User;
import com.atlassian.confluence.api.model.relations.AbstractRelationDescriptor;
import com.atlassian.confluence.api.model.relations.Relatable;
import com.atlassian.confluence.api.model.relations.RelationDescriptors;

public final class FavouriteRelationDescriptor<T extends Relatable>
extends AbstractRelationDescriptor<User, T> {
    public static final String NAME = "favourite";
    public static final FavouriteRelationDescriptor<Content> CONTENT_FAVOURITE = new FavouriteRelationDescriptor<Content>(Content.class);
    public static final FavouriteRelationDescriptor<Space> SPACE_FAVOURITE = new FavouriteRelationDescriptor<Space>(Space.class);

    private FavouriteRelationDescriptor(Class<T> targetClass) {
        super(NAME, User.class, targetClass);
    }

    static void register() {
        RelationDescriptors.registerBuiltIn(CONTENT_FAVOURITE, SPACE_FAVOURITE);
    }
}

