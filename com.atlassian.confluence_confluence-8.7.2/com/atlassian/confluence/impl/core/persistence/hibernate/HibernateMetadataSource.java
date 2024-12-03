/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.boot.Metadata
 *  org.hibernate.mapping.Table
 */
package com.atlassian.confluence.impl.core.persistence.hibernate;

import java.util.Collection;
import org.hibernate.boot.Metadata;
import org.hibernate.mapping.Table;

public interface HibernateMetadataSource {
    public Metadata getMetadata();

    default public Collection<Table> getTableMappings() {
        return this.getMetadata().collectTableMappings();
    }
}

