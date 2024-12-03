/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.spi;

import org.hibernate.Incubating;
import org.hibernate.boot.Metadata;
import org.hibernate.tool.schema.spi.DelayedDropAction;
import org.hibernate.tool.schema.spi.ExecutionOptions;
import org.hibernate.tool.schema.spi.SourceDescriptor;
import org.hibernate.tool.schema.spi.TargetDescriptor;

@Incubating
public interface SchemaDropper {
    public void doDrop(Metadata var1, ExecutionOptions var2, SourceDescriptor var3, TargetDescriptor var4);

    public DelayedDropAction buildDelayedAction(Metadata var1, ExecutionOptions var2, SourceDescriptor var3);
}

