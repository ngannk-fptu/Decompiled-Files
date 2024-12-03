/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.persister.walking.spi;

import org.hibernate.loader.plan.spi.FetchSource;
import org.hibernate.persister.walking.spi.AnyMappingDefinition;
import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
import org.hibernate.persister.walking.spi.AssociationKey;
import org.hibernate.persister.walking.spi.AttributeDefinition;
import org.hibernate.persister.walking.spi.CollectionDefinition;
import org.hibernate.persister.walking.spi.CollectionElementDefinition;
import org.hibernate.persister.walking.spi.CollectionIndexDefinition;
import org.hibernate.persister.walking.spi.CompositionDefinition;
import org.hibernate.persister.walking.spi.EntityDefinition;
import org.hibernate.persister.walking.spi.EntityIdentifierDefinition;

public interface AssociationVisitationStrategy {
    public void start();

    public void finish();

    public void startingEntity(EntityDefinition var1);

    public void finishingEntity(EntityDefinition var1);

    public void startingEntityIdentifier(EntityIdentifierDefinition var1);

    public void finishingEntityIdentifier(EntityIdentifierDefinition var1);

    public void startingCollection(CollectionDefinition var1);

    public void finishingCollection(CollectionDefinition var1);

    public void startingCollectionIndex(CollectionIndexDefinition var1);

    public void finishingCollectionIndex(CollectionIndexDefinition var1);

    public void startingCollectionElements(CollectionElementDefinition var1);

    public void finishingCollectionElements(CollectionElementDefinition var1);

    public void startingComposite(CompositionDefinition var1);

    public void finishingComposite(CompositionDefinition var1);

    public boolean startingAttribute(AttributeDefinition var1);

    public void finishingAttribute(AttributeDefinition var1);

    public void foundAny(AnyMappingDefinition var1);

    public void associationKeyRegistered(AssociationKey var1);

    public FetchSource registeredFetchSource(AssociationKey var1);

    public void foundCircularAssociation(AssociationAttributeDefinition var1);

    public boolean isDuplicateAssociationKey(AssociationKey var1);
}

