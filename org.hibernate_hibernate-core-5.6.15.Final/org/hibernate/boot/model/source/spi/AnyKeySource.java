/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import java.util.List;
import org.hibernate.boot.model.naming.ImplicitAnyKeyColumnNameSource;
import org.hibernate.boot.model.source.spi.HibernateTypeSource;
import org.hibernate.boot.model.source.spi.RelationalValueSource;

public interface AnyKeySource
extends ImplicitAnyKeyColumnNameSource {
    public HibernateTypeSource getTypeSource();

    public List<RelationalValueSource> getRelationalValueSources();
}

