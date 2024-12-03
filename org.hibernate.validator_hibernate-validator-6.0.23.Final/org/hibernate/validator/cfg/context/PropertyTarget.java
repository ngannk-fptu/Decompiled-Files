/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.cfg.context;

import java.lang.annotation.ElementType;
import org.hibernate.validator.cfg.context.PropertyConstraintMappingContext;

public interface PropertyTarget {
    public PropertyConstraintMappingContext property(String var1, ElementType var2);
}

