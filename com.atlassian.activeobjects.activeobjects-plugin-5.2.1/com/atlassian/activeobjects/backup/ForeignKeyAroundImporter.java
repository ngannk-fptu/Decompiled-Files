/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.Iterables
 */
package com.atlassian.activeobjects.backup;

import com.atlassian.activeobjects.backup.ForeignKeyCreator;
import com.atlassian.dbexporter.Context;
import com.atlassian.dbexporter.ForeignKey;
import com.atlassian.dbexporter.Table;
import com.atlassian.dbexporter.importer.ImportConfiguration;
import com.atlassian.dbexporter.importer.NoOpAroundImporter;
import com.atlassian.dbexporter.node.NodeParser;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import java.util.Collection;

public final class ForeignKeyAroundImporter
extends NoOpAroundImporter {
    private final ForeignKeyCreator foreignKeyCreator;

    public ForeignKeyAroundImporter(ForeignKeyCreator foreignKeyCreator) {
        this.foreignKeyCreator = (ForeignKeyCreator)Preconditions.checkNotNull((Object)foreignKeyCreator);
    }

    @Override
    public void after(NodeParser node, ImportConfiguration configuration, Context context) {
        this.foreignKeyCreator.create(Iterables.concat((Iterable)Collections2.transform(context.getAll(Table.class), this.getForeignKeysFunction())), configuration.getEntityNameProcessor());
    }

    private Function<Table, Collection<ForeignKey>> getForeignKeysFunction() {
        return new Function<Table, Collection<ForeignKey>>(){

            public Collection<ForeignKey> apply(Table from) {
                return from.getForeignKeys();
            }
        };
    }
}

