/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.activeobjects.ao;

import com.atlassian.activeobjects.internal.Prefix;
import com.google.common.base.Preconditions;
import net.java.ao.SchemaConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PrefixedSchemaConfiguration
implements SchemaConfiguration {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Prefix prefix;

    public PrefixedSchemaConfiguration(Prefix prefix) {
        this.prefix = (Prefix)Preconditions.checkNotNull((Object)prefix);
    }

    @Override
    public final boolean shouldManageTable(String tableName, boolean caseSensitive) {
        boolean should = this.prefix.isStarting(tableName, caseSensitive);
        this.logger.debug("Active objects will {} manage table {}", (Object)(should ? "" : "NOT"), (Object)tableName);
        return should;
    }
}

