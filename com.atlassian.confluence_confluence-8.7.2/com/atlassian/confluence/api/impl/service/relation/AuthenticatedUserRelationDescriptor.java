/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.people.User
 *  com.atlassian.confluence.api.model.relations.Relatable
 *  com.atlassian.confluence.api.model.relations.RelationDescriptor
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 */
package com.atlassian.confluence.api.impl.service.relation;

import com.atlassian.confluence.api.impl.service.relation.DefaultValidatingRelationDescriptor;
import com.atlassian.confluence.api.model.people.User;
import com.atlassian.confluence.api.model.relations.Relatable;
import com.atlassian.confluence.api.model.relations.RelationDescriptor;
import com.atlassian.confluence.api.model.validation.SimpleValidationResult;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;

public class AuthenticatedUserRelationDescriptor<S extends User, T extends Relatable>
extends DefaultValidatingRelationDescriptor<S, T> {
    protected AuthenticatedUserRelationDescriptor(RelationDescriptor relationDescriptor) {
        super(relationDescriptor);
    }

    @Override
    public ValidationResult canRelate(S source, T target) {
        if (AuthenticatedUserThreadLocal.isAnonymousUser()) {
            return SimpleValidationResult.FORBIDDEN;
        }
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        if (!currentUser.getKey().equals(source.optionalUserKey().get())) {
            return SimpleValidationResult.FORBIDDEN;
        }
        return super.canRelate(source, target);
    }
}

