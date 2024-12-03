/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.confluence.api.model.relations;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.relations.AbstractRelationDescriptor;
import com.atlassian.confluence.api.model.relations.Relatable;

@ExperimentalApi
public final class NamedRelationDescriptor<S extends Relatable, T extends Relatable>
extends AbstractRelationDescriptor<S, T> {
    public NamedRelationDescriptor(String relationName, Class<S> sourceClass, Class<T> targetClass) {
        super(relationName, sourceClass, targetClass);
    }
}

