/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.cfg.context;

import org.hibernate.validator.cfg.context.GroupConversionTargetContext;

public interface Cascadable<C extends Cascadable<C>> {
    public C valid();

    public GroupConversionTargetContext<C> convertGroup(Class<?> var1);
}

