/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.cfg.context;

import org.hibernate.validator.cfg.context.GroupConversionTargetContext;
import org.hibernate.validator.internal.cfg.context.CascadableConstraintMappingContextImplBase;

class GroupConversionTargetContextImpl<C>
implements GroupConversionTargetContext<C> {
    private final C cascadableContext;
    private final Class<?> from;
    private final CascadableConstraintMappingContextImplBase<?> target;

    GroupConversionTargetContextImpl(Class<?> from, C cascadableContext, CascadableConstraintMappingContextImplBase<?> target) {
        this.from = from;
        this.cascadableContext = cascadableContext;
        this.target = target;
    }

    @Override
    public C to(Class<?> to) {
        this.target.addGroupConversion(this.from, to);
        return this.cascadableContext;
    }
}

