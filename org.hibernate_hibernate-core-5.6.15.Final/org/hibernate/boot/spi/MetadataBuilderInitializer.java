/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.spi;

import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.registry.StandardServiceRegistry;

public interface MetadataBuilderInitializer {
    public void contribute(MetadataBuilder var1, StandardServiceRegistry var2);
}

