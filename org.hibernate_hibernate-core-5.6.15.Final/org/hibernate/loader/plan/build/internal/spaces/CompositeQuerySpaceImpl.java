/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.build.internal.spaces;

import org.hibernate.loader.plan.build.internal.spaces.AbstractExpandingSourceQuerySpace;
import org.hibernate.loader.plan.build.internal.spaces.CompositePropertyMapping;
import org.hibernate.loader.plan.build.spi.ExpandingCompositeQuerySpace;
import org.hibernate.loader.plan.build.spi.ExpandingQuerySpaces;
import org.hibernate.loader.plan.spi.QuerySpace;
import org.hibernate.persister.entity.PropertyMapping;

public class CompositeQuerySpaceImpl
extends AbstractExpandingSourceQuerySpace
implements ExpandingCompositeQuerySpace {
    private final CompositePropertyMapping compositeSubPropertyMapping;

    public CompositeQuerySpaceImpl(CompositePropertyMapping compositeSubPropertyMapping, String uid, ExpandingQuerySpaces querySpaces, boolean canJoinsBeRequired) {
        super(uid, QuerySpace.Disposition.COMPOSITE, querySpaces, canJoinsBeRequired);
        this.compositeSubPropertyMapping = compositeSubPropertyMapping;
    }

    @Override
    public PropertyMapping getPropertyMapping() {
        return this.compositeSubPropertyMapping;
    }

    @Override
    public String[] toAliasedColumns(String alias, String propertyName) {
        return this.compositeSubPropertyMapping.toColumns(alias, propertyName);
    }
}

