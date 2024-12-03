/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.spi;

import org.hibernate.type.spi.TypeConfiguration;

public interface TypeConfigurationAware {
    public TypeConfiguration getTypeConfiguration();

    public void setTypeConfiguration(TypeConfiguration var1);
}

