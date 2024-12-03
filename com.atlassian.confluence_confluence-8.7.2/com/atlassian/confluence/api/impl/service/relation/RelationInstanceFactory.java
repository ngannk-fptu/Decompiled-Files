/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.relations.RelationDescriptor
 *  com.atlassian.confluence.api.model.relations.RelationDescriptors
 *  com.atlassian.confluence.api.model.relations.RelationInstance
 */
package com.atlassian.confluence.api.impl.service.relation;

import com.atlassian.confluence.api.impl.service.content.factory.ModelFactory;
import com.atlassian.confluence.api.impl.service.relation.RelatableFactory;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.relations.RelationDescriptor;
import com.atlassian.confluence.api.model.relations.RelationDescriptors;
import com.atlassian.confluence.api.model.relations.RelationInstance;
import com.atlassian.confluence.internal.relations.RelatableEntity;
import com.atlassian.confluence.internal.relations.dao.RelationEntity;

public class RelationInstanceFactory
extends ModelFactory<RelationEntity, RelationInstance> {
    private final RelatableFactory relatableFactory;

    public RelationInstanceFactory(RelatableFactory relatableFactory) {
        this.relatableFactory = relatableFactory;
    }

    @Override
    public RelationInstance buildFrom(RelationEntity hibernateObject, Expansions expansions) {
        return this.buildFrom((RelatableEntity)hibernateObject.getSourceContent(), hibernateObject.getRelationName(), (RelatableEntity)hibernateObject.getTargetContent(), expansions);
    }

    public RelationInstance buildFrom(RelatableEntity sourceEntity, String relationName, RelatableEntity targetEntity, Expansions expansions) {
        Object target = this.relatableFactory.buildFrom(targetEntity, expansions.getSubExpansions("target"));
        Object source = this.relatableFactory.buildFrom(sourceEntity, expansions.getSubExpansions("source"));
        RelationDescriptor relationDescriptor = RelationDescriptors.lookupBuiltinOrCreate(source.getClass(), (String)relationName, target.getClass());
        return RelationInstance.builder(source, (RelationDescriptor)relationDescriptor, target).build();
    }
}

