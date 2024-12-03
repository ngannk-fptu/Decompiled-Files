/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.core.persistence.schema.api;

import com.atlassian.confluence.core.persistence.schema.api.TableSchemaComparison;
import java.util.Collection;

public interface SchemaComparison {
    public Iterable<TableSchemaComparison> getTables();

    public Collection<String> getWarnings();
}

