/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalSpi
 */
package com.atlassian.confluence.api.model.relations;

import com.atlassian.annotations.ExperimentalSpi;
import com.atlassian.confluence.api.model.relations.Relatable;

@ExperimentalSpi
public interface RelationDescriptor<S extends Relatable, T extends Relatable> {
    public String getRelationName();

    public Class<S> getSourceClass();

    public Class<T> getTargetClass();
}

