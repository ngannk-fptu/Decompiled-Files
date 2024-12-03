/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.cfg.context;

import org.hibernate.validator.cfg.context.TypeConstraintMappingContext;

public interface TypeTarget {
    public <C> TypeConstraintMappingContext<C> type(Class<C> var1);
}

