/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import java.util.List;
import org.hibernate.boot.model.source.spi.ColumnSource;
import org.hibernate.boot.model.source.spi.ForeignKeyContributingSource;
import org.hibernate.boot.model.source.spi.SubclassEntitySource;

public interface JoinedSubclassEntitySource
extends SubclassEntitySource,
ForeignKeyContributingSource {
    public List<ColumnSource> getPrimaryKeyColumnSources();
}

