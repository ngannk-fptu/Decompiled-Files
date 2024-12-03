/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.build.internal.returns;

import org.hibernate.engine.FetchStrategy;
import org.hibernate.loader.plan.build.internal.returns.AbstractAnyReference;
import org.hibernate.loader.plan.spi.AnyAttributeFetch;
import org.hibernate.loader.plan.spi.AttributeFetch;
import org.hibernate.loader.plan.spi.EntityReference;
import org.hibernate.loader.plan.spi.FetchSource;
import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
import org.hibernate.persister.walking.spi.AttributeDefinition;
import org.hibernate.type.AnyType;

public class AnyAttributeFetchImpl
extends AbstractAnyReference
implements AnyAttributeFetch,
AttributeFetch {
    private final FetchSource fetchSource;
    private final AssociationAttributeDefinition fetchedAttribute;
    private final FetchStrategy fetchStrategy;

    public AnyAttributeFetchImpl(FetchSource fetchSource, AssociationAttributeDefinition fetchedAttribute, FetchStrategy fetchStrategy) {
        super(fetchSource.getPropertyPath().append(fetchedAttribute.getName()));
        this.fetchSource = fetchSource;
        this.fetchedAttribute = fetchedAttribute;
        this.fetchStrategy = fetchStrategy;
    }

    @Override
    public FetchSource getSource() {
        return this.fetchSource;
    }

    @Override
    public FetchStrategy getFetchStrategy() {
        return this.fetchStrategy;
    }

    @Override
    public AnyType getFetchedType() {
        return (AnyType)this.fetchedAttribute.getType();
    }

    @Override
    public boolean isNullable() {
        return this.fetchedAttribute.isNullable();
    }

    @Override
    public String[] toSqlSelectFragments(String alias) {
        return new String[0];
    }

    @Override
    public String getAdditionalJoinConditions() {
        return null;
    }

    @Override
    public EntityReference resolveEntityReference() {
        return this.fetchSource.resolveEntityReference();
    }

    @Override
    public AttributeDefinition getFetchedAttributeDefinition() {
        return this.fetchedAttribute;
    }
}

