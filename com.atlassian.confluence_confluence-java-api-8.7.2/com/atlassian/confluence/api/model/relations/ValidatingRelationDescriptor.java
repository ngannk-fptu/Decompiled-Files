/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalSpi
 */
package com.atlassian.confluence.api.model.relations;

import com.atlassian.annotations.ExperimentalSpi;
import com.atlassian.confluence.api.model.relations.Relatable;
import com.atlassian.confluence.api.model.relations.RelationDescriptor;
import com.atlassian.confluence.api.model.validation.ValidationResult;

@ExperimentalSpi
public interface ValidatingRelationDescriptor<S extends Relatable, T extends Relatable>
extends RelationDescriptor<S, T> {
    public ValidationResult canRelate(S var1, T var2);
}

