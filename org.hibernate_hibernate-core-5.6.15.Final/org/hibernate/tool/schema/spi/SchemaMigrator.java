/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.spi;

import org.hibernate.Incubating;
import org.hibernate.boot.Metadata;
import org.hibernate.tool.schema.spi.ExecutionOptions;
import org.hibernate.tool.schema.spi.TargetDescriptor;

@Incubating
public interface SchemaMigrator {
    public void doMigration(Metadata var1, ExecutionOptions var2, TargetDescriptor var3);
}

