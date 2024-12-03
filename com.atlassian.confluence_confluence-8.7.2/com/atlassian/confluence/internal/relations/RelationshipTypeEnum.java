/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.model.people.User
 *  com.atlassian.confluence.api.model.relations.RelationDescriptor
 */
package com.atlassian.confluence.internal.relations;

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.api.model.people.User;
import com.atlassian.confluence.api.model.relations.RelationDescriptor;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.user.ConfluenceUser;

public enum RelationshipTypeEnum {
    CONTENT2CONTENT,
    USER2CONTENT,
    USER2USER;


    public static RelationshipTypeEnum getRelationshipType(RelationDescriptor<?, ?> relationDescriptor) {
        return RelationshipTypeEnum.getRelationshipType(relationDescriptor, true);
    }

    public static RelationshipTypeEnum getRelationshipType(RelationDescriptor<?, ?> relationDescriptor, boolean throwOnError) {
        Class source = relationDescriptor.getSourceClass();
        Class target = relationDescriptor.getTargetClass();
        if (RelationshipTypeEnum.isUserClass(source)) {
            if (RelationshipTypeEnum.isUserClass(target)) {
                return USER2USER;
            }
            if (RelationshipTypeEnum.isContentClass(target)) {
                return USER2CONTENT;
            }
        } else if (RelationshipTypeEnum.isContentClass(source) && RelationshipTypeEnum.isContentClass(target)) {
            return CONTENT2CONTENT;
        }
        if (throwOnError) {
            throw new IllegalStateException(String.format("Unrecognised source / type combination.  Source: %s, Target: %s", source.getName(), target.getName()));
        }
        return null;
    }

    private static boolean isUserClass(Class<?> cls) {
        return User.class.isAssignableFrom(cls) || ConfluenceUser.class.isAssignableFrom(cls);
    }

    private static boolean isContentClass(Class<?> cls) {
        return Content.class.isAssignableFrom(cls) || Space.class.isAssignableFrom(cls) || ContentEntityObject.class.isAssignableFrom(cls);
    }
}

