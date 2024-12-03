/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.relations.CollaboratorRelationDescriptor
 *  com.atlassian.confluence.api.model.relations.RelationDescriptor
 *  com.atlassian.confluence.api.model.relations.ValidatingRelationDescriptor
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.confluence.api.impl.service.relation;

import com.atlassian.confluence.api.impl.service.relation.AuthenticatedUserRelationDescriptor;
import com.atlassian.confluence.api.impl.service.relation.DefaultValidatingRelationDescriptor;
import com.atlassian.confluence.api.model.relations.CollaboratorRelationDescriptor;
import com.atlassian.confluence.api.model.relations.RelationDescriptor;
import com.atlassian.confluence.api.model.relations.ValidatingRelationDescriptor;
import com.google.common.collect.ImmutableList;
import java.util.List;

public class ValidatingRelationDescriptorRegistry {
    private static final List<String> authenticatedUserDescriptors = ImmutableList.of((Object)CollaboratorRelationDescriptor.COLLABORATOR.getRelationName());

    ValidatingRelationDescriptor getValidatingDescriptor(RelationDescriptor relationDescriptor) {
        if (relationDescriptor instanceof ValidatingRelationDescriptor) {
            return (ValidatingRelationDescriptor)relationDescriptor;
        }
        if (authenticatedUserDescriptors.contains(relationDescriptor.getRelationName())) {
            return new AuthenticatedUserRelationDescriptor(relationDescriptor);
        }
        return new DefaultValidatingRelationDescriptor(relationDescriptor);
    }
}

