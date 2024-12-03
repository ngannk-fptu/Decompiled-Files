/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.spi;

import java.util.Set;
import org.hibernate.MappingException;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.spi.MetadataBuildingOptions;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.mapping.MappedSuperclass;
import org.hibernate.query.spi.NamedQueryRepository;
import org.hibernate.type.TypeResolver;
import org.hibernate.type.spi.TypeConfiguration;

public interface MetadataImplementor
extends Metadata,
Mapping {
    public MetadataBuildingOptions getMetadataBuildingOptions();

    public TypeConfiguration getTypeConfiguration();

    @Deprecated
    public TypeResolver getTypeResolver();

    public NamedQueryRepository buildNamedQueryRepository(SessionFactoryImpl var1);

    public void validate() throws MappingException;

    public Set<MappedSuperclass> getMappedSuperclassMappingsCopy();

    public void initSessionFactory(SessionFactoryImplementor var1);
}

