/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.persister.walking.spi;

import org.hibernate.engine.FetchStrategy;
import org.hibernate.engine.spi.CascadeStyle;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.loader.PropertyPath;
import org.hibernate.persister.spi.HydratedCompoundValueHandler;
import org.hibernate.persister.walking.spi.AnyMappingDefinition;
import org.hibernate.persister.walking.spi.AssociationKey;
import org.hibernate.persister.walking.spi.AttributeDefinition;
import org.hibernate.persister.walking.spi.CollectionDefinition;
import org.hibernate.persister.walking.spi.EntityDefinition;
import org.hibernate.type.AssociationType;

public interface AssociationAttributeDefinition
extends AttributeDefinition {
    @Override
    public AssociationType getType();

    public AssociationKey getAssociationKey();

    public AssociationNature getAssociationNature();

    public EntityDefinition toEntityDefinition();

    public CollectionDefinition toCollectionDefinition();

    public AnyMappingDefinition toAnyDefinition();

    public FetchStrategy determineFetchPlan(LoadQueryInfluencers var1, PropertyPath var2);

    public CascadeStyle determineCascadeStyle();

    public HydratedCompoundValueHandler getHydratedCompoundValueExtractor();

    public static enum AssociationNature {
        ANY,
        ENTITY,
        COLLECTION;

    }
}

