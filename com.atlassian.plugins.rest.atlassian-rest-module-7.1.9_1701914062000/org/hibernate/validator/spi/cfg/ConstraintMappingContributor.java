/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.spi.cfg;

import org.hibernate.validator.cfg.ConstraintMapping;

public interface ConstraintMappingContributor {
    public void createConstraintMappings(ConstraintMappingBuilder var1);

    public static interface ConstraintMappingBuilder {
        public ConstraintMapping addConstraintMapping();
    }
}

