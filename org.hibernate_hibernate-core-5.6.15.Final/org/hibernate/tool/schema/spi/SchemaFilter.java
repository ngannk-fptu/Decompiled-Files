/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.spi;

import org.hibernate.Incubating;
import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.boot.model.relational.Sequence;
import org.hibernate.mapping.Table;

@Incubating
public interface SchemaFilter {
    public boolean includeNamespace(Namespace var1);

    public boolean includeTable(Table var1);

    public boolean includeSequence(Sequence var1);
}

