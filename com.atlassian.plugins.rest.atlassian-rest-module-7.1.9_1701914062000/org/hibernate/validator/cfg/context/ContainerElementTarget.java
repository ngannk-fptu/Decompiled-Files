/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.cfg.context;

import org.hibernate.validator.Incubating;
import org.hibernate.validator.cfg.context.ContainerElementConstraintMappingContext;

@Incubating
public interface ContainerElementTarget {
    public ContainerElementConstraintMappingContext containerElementType();

    public ContainerElementConstraintMappingContext containerElementType(int var1, int ... var2);
}

