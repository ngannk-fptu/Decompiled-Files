/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import java.util.Map;
import org.hibernate.boot.model.source.spi.ColumnSource;
import org.hibernate.boot.model.source.spi.HibernateTypeSource;

public interface CollectionIdSource {
    public ColumnSource getColumnSource();

    public HibernateTypeSource getTypeInformation();

    public String getGeneratorName();

    public Map<String, String> getParameters();
}

