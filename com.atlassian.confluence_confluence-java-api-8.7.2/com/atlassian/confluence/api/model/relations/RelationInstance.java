/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.confluence.api.model.relations;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.relations.Relatable;
import com.atlassian.confluence.api.model.relations.RelationDescriptor;

@ExperimentalApi
public class RelationInstance<S extends Relatable, T extends Relatable> {
    private final T target;
    private final S source;
    private final RelationDescriptor<S, T> relationDescriptor;

    private RelationInstance(RelationInstanceBuilder<S, T> builder) {
        this.source = ((RelationInstanceBuilder)builder).source;
        this.relationDescriptor = ((RelationInstanceBuilder)builder).relationDescriptor;
        this.target = ((RelationInstanceBuilder)builder).target;
    }

    public T getTarget() {
        return this.target;
    }

    public S getSource() {
        return this.source;
    }

    public RelationDescriptor<S, T> getRelationDescriptor() {
        return this.relationDescriptor;
    }

    public static <S extends Relatable, T extends Relatable> RelationInstanceBuilder<S, T> builder(S source, RelationDescriptor<S, T> relationDescriptor, T target) {
        return new RelationInstanceBuilder(source, relationDescriptor, target, null);
    }

    public static class RelationInstanceBuilder<S extends Relatable, T extends Relatable> {
        private T target;
        private S source;
        private RelationDescriptor<S, T> relationDescriptor;

        private RelationInstanceBuilder(S source, RelationDescriptor<S, T> relationDescriptor, T target) {
            this.source = source;
            this.relationDescriptor = relationDescriptor;
            this.target = target;
        }

        public RelationInstance<S, T> build() {
            return new RelationInstance(this);
        }

        /* synthetic */ RelationInstanceBuilder(Relatable x0, RelationDescriptor x1, Relatable x2, 1 x3) {
            this(x0, x1, x2);
        }
    }
}

