/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package org.postgresql.replication.fluent.logical;

import java.util.Properties;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.postgresql.replication.fluent.CommonOptions;

public interface LogicalReplicationOptions
extends CommonOptions {
    @Override
    public @Nullable String getSlotName();

    public Properties getSlotOptions();
}

