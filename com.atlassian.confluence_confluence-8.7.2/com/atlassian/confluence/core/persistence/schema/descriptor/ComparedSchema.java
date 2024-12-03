/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Iterables
 *  net.jcip.annotations.ThreadSafe
 */
package com.atlassian.confluence.core.persistence.schema.descriptor;

import com.atlassian.confluence.core.persistence.schema.api.SchemaComparison;
import com.atlassian.confluence.core.persistence.schema.api.TableSchemaComparison;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.Collections;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class ComparedSchema
implements SchemaComparison {
    private final Iterable<TableSchemaComparison> tables;
    private final Collection<String> warnings;

    public ComparedSchema(Iterable<TableSchemaComparison> tables, Collection<String> warnings) {
        this.tables = (Iterable)Preconditions.checkNotNull(tables);
        this.warnings = (Collection)Preconditions.checkNotNull(warnings);
    }

    @Override
    public Iterable<TableSchemaComparison> getTables() {
        return Iterables.unmodifiableIterable(this.tables);
    }

    @Override
    public Collection<String> getWarnings() {
        return Collections.unmodifiableCollection(this.warnings);
    }
}

