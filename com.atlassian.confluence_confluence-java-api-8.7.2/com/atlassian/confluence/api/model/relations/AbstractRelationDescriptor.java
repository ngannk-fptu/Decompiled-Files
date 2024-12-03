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
import java.lang.reflect.ParameterizedType;

@ExperimentalSpi
public abstract class AbstractRelationDescriptor<S extends Relatable, T extends Relatable>
implements RelationDescriptor<S, T> {
    private final String relationName;
    private Class<S> sourceClass;
    private Class<T> targetClass;

    protected AbstractRelationDescriptor(String relationName) {
        this.relationName = relationName;
    }

    protected AbstractRelationDescriptor(String relationName, Class<S> sourceClass, Class<T> targetClass) {
        this(relationName);
        this.sourceClass = sourceClass;
        this.targetClass = targetClass;
    }

    @Override
    public Class<S> getSourceClass() {
        if (this.sourceClass == null) {
            this.sourceClass = this.getGenericTypeArgs(0);
        }
        return this.sourceClass;
    }

    @Override
    public Class<T> getTargetClass() {
        if (this.targetClass == null) {
            this.targetClass = this.getGenericTypeArgs(1);
        }
        return this.targetClass;
    }

    @Override
    public String getRelationName() {
        return this.relationName;
    }

    protected Class getGenericTypeArgs(int i) {
        ParameterizedType thisAbstractRelation = (ParameterizedType)this.getClass().getGenericSuperclass();
        return (Class)thisAbstractRelation.getActualTypeArguments()[i];
    }
}

