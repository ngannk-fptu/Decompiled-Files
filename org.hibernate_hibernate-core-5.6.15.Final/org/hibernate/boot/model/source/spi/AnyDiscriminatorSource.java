/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import java.util.Map;
import org.hibernate.boot.model.naming.ImplicitAnyDiscriminatorColumnNameSource;
import org.hibernate.boot.model.source.spi.HibernateTypeSource;
import org.hibernate.boot.model.source.spi.RelationalValueSource;

public interface AnyDiscriminatorSource
extends ImplicitAnyDiscriminatorColumnNameSource {
    public HibernateTypeSource getTypeSource();

    public RelationalValueSource getRelationalValueSource();

    public Map<String, String> getValueMappings();
}

