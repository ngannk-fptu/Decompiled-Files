/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import org.hibernate.EntityMode;
import org.hibernate.boot.model.Caching;
import org.hibernate.boot.model.source.spi.DiscriminatorSource;
import org.hibernate.boot.model.source.spi.EntitySource;
import org.hibernate.boot.model.source.spi.IdentifierSource;
import org.hibernate.boot.model.source.spi.InheritanceType;
import org.hibernate.boot.model.source.spi.MultiTenancySource;
import org.hibernate.boot.model.source.spi.VersionAttributeSource;
import org.hibernate.engine.OptimisticLockStyle;

public interface EntityHierarchySource {
    public EntitySource getRoot();

    public InheritanceType getHierarchyInheritanceType();

    public IdentifierSource getIdentifierSource();

    public VersionAttributeSource getVersionAttributeSource();

    public DiscriminatorSource getDiscriminatorSource();

    public MultiTenancySource getMultiTenancySource();

    public EntityMode getEntityMode();

    public boolean isMutable();

    public boolean isExplicitPolymorphism();

    public String getWhere();

    public String getRowId();

    public OptimisticLockStyle getOptimisticLockStyle();

    public Caching getCaching();

    public Caching getNaturalIdCaching();
}

