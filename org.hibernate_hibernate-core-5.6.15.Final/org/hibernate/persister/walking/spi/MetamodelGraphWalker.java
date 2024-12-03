/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.persister.walking.spi;

import java.util.HashSet;
import java.util.Set;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.loader.PropertyPath;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.collection.QueryableCollection;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.walking.spi.AnyMappingDefinition;
import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
import org.hibernate.persister.walking.spi.AssociationKey;
import org.hibernate.persister.walking.spi.AssociationVisitationStrategy;
import org.hibernate.persister.walking.spi.AttributeDefinition;
import org.hibernate.persister.walking.spi.AttributeSource;
import org.hibernate.persister.walking.spi.CollectionDefinition;
import org.hibernate.persister.walking.spi.CollectionElementDefinition;
import org.hibernate.persister.walking.spi.CollectionIndexDefinition;
import org.hibernate.persister.walking.spi.CompositionDefinition;
import org.hibernate.persister.walking.spi.EncapsulatedEntityIdentifierDefinition;
import org.hibernate.persister.walking.spi.EntityDefinition;
import org.hibernate.persister.walking.spi.EntityIdentifierDefinition;
import org.hibernate.persister.walking.spi.NonEncapsulatedEntityIdentifierDefinition;
import org.hibernate.persister.walking.spi.WalkingException;
import org.hibernate.type.Type;
import org.jboss.logging.Logger;

public class MetamodelGraphWalker {
    private static final Logger log = Logger.getLogger(MetamodelGraphWalker.class);
    private final AssociationVisitationStrategy strategy;
    private final SessionFactoryImplementor factory;
    private PropertyPath currentPropertyPath = new PropertyPath();
    private final Set<AssociationKey> visitedAssociationKeys = new HashSet<AssociationKey>();

    public static void visitEntity(AssociationVisitationStrategy strategy, EntityPersister persister) {
        strategy.start();
        try {
            new MetamodelGraphWalker(strategy, persister.getFactory()).visitEntityDefinition(persister);
        }
        finally {
            strategy.finish();
        }
    }

    public static void visitCollection(AssociationVisitationStrategy strategy, CollectionPersister persister) {
        strategy.start();
        try {
            new MetamodelGraphWalker(strategy, persister.getFactory()).visitCollectionDefinition(persister);
        }
        finally {
            strategy.finish();
        }
    }

    public MetamodelGraphWalker(AssociationVisitationStrategy strategy, SessionFactoryImplementor factory) {
        this.strategy = strategy;
        this.factory = factory;
    }

    private void visitEntityDefinition(EntityDefinition entityDefinition) {
        this.strategy.startingEntity(entityDefinition);
        try {
            AbstractEntityPersister persister = (AbstractEntityPersister)entityDefinition.getEntityPersister();
            this.visitIdentifierDefinition(entityDefinition.getEntityKeyDefinition());
            this.visitAttributes(entityDefinition, persister);
        }
        finally {
            this.strategy.finishingEntity(entityDefinition);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void visitIdentifierDefinition(EntityIdentifierDefinition identifierDefinition) {
        this.strategy.startingEntityIdentifier(identifierDefinition);
        try {
            if (identifierDefinition.isEncapsulated()) {
                EncapsulatedEntityIdentifierDefinition idAsEncapsulated = (EncapsulatedEntityIdentifierDefinition)identifierDefinition;
                AttributeDefinition idAttr = idAsEncapsulated.getAttributeDefinition();
                if (CompositionDefinition.class.isInstance(idAttr)) {
                    this.visitCompositeDefinition((CompositionDefinition)idAttr);
                }
            } else {
                this.visitCompositeDefinition((NonEncapsulatedEntityIdentifierDefinition)identifierDefinition);
            }
        }
        finally {
            this.strategy.finishingEntityIdentifier(identifierDefinition);
        }
    }

    private void visitAttributes(AttributeSource attributeSource, AbstractEntityPersister sourcePersister) {
        Iterable<AttributeDefinition> attributeDefinitions = attributeSource.getAttributes();
        if (attributeDefinitions == null) {
            return;
        }
        for (AttributeDefinition attributeDefinition : attributeDefinitions) {
            this.visitAttributeDefinition(attributeDefinition, sourcePersister);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void visitAttributeDefinition(AttributeDefinition attributeDefinition, AbstractEntityPersister sourcePersister) {
        block12: {
            PropertyPath subPath = this.currentPropertyPath.append(attributeDefinition.getName());
            log.debug((Object)("Visiting attribute path : " + subPath.getFullPath()));
            if (attributeDefinition.getType().isAssociationType()) {
                String[] columns;
                AssociationAttributeDefinition associationAttributeDefinition = (AssociationAttributeDefinition)attributeDefinition;
                AssociationKey associationKey = associationAttributeDefinition.getAssociationKey();
                if (this.isDuplicateAssociationKey(associationKey)) {
                    log.debug((Object)("Property path deemed to be circular : " + subPath.getFullPath()));
                    this.strategy.foundCircularAssociation(associationAttributeDefinition);
                    return;
                }
                if (sourcePersister != null && (columns = sourcePersister.toColumns(attributeDefinition.getName())).length == 0) {
                    return;
                }
            }
            boolean continueWalk = this.strategy.startingAttribute(attributeDefinition);
            try {
                if (!continueWalk) break block12;
                PropertyPath old = this.currentPropertyPath;
                this.currentPropertyPath = subPath;
                try {
                    Type attributeType = attributeDefinition.getType();
                    if (attributeType.isAssociationType()) {
                        this.visitAssociation((AssociationAttributeDefinition)attributeDefinition);
                    } else if (attributeType.isComponentType()) {
                        this.visitCompositeDefinition((CompositionDefinition)attributeDefinition);
                    }
                }
                finally {
                    this.currentPropertyPath = old;
                }
            }
            finally {
                this.strategy.finishingAttribute(attributeDefinition);
            }
        }
    }

    private void visitAssociation(AssociationAttributeDefinition attribute) {
        this.addAssociationKey(attribute.getAssociationKey());
        AssociationAttributeDefinition.AssociationNature nature = attribute.getAssociationNature();
        if (nature == AssociationAttributeDefinition.AssociationNature.ANY) {
            this.visitAnyDefinition(attribute.toAnyDefinition());
        } else if (nature == AssociationAttributeDefinition.AssociationNature.COLLECTION) {
            this.visitCollectionDefinition(attribute.toCollectionDefinition());
        } else {
            this.visitEntityDefinition(attribute.toEntityDefinition());
        }
    }

    private void visitAnyDefinition(AnyMappingDefinition anyDefinition) {
        this.strategy.foundAny(anyDefinition);
    }

    private void visitCompositeDefinition(CompositionDefinition compositionDefinition) {
        this.strategy.startingComposite(compositionDefinition);
        try {
            this.visitAttributes(compositionDefinition, null);
        }
        finally {
            this.strategy.finishingComposite(compositionDefinition);
        }
    }

    private void visitCollectionDefinition(CollectionDefinition collectionDefinition) {
        this.strategy.startingCollection(collectionDefinition);
        try {
            this.visitCollectionIndex(collectionDefinition);
            this.visitCollectionElements(collectionDefinition);
        }
        finally {
            this.strategy.finishingCollection(collectionDefinition);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void visitCollectionIndex(CollectionDefinition collectionDefinition) {
        CollectionIndexDefinition collectionIndexDefinition = collectionDefinition.getIndexDefinition();
        if (collectionIndexDefinition == null) {
            return;
        }
        this.strategy.startingCollectionIndex(collectionIndexDefinition);
        try {
            log.debug((Object)("Visiting index for collection :  " + this.currentPropertyPath.getFullPath()));
            this.currentPropertyPath = this.currentPropertyPath.append("<index>");
            try {
                Type collectionIndexType = collectionIndexDefinition.getType();
                if (collectionIndexType.isAnyType()) {
                    this.visitAnyDefinition(collectionIndexDefinition.toAnyMappingDefinition());
                } else if (collectionIndexType.isComponentType()) {
                    this.visitCompositeDefinition(collectionIndexDefinition.toCompositeDefinition());
                } else if (collectionIndexType.isAssociationType()) {
                    this.visitEntityDefinition(collectionIndexDefinition.toEntityDefinition());
                }
            }
            finally {
                this.currentPropertyPath = this.currentPropertyPath.getParent();
            }
        }
        finally {
            this.strategy.finishingCollectionIndex(collectionIndexDefinition);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void visitCollectionElements(CollectionDefinition collectionDefinition) {
        CollectionElementDefinition elementDefinition = collectionDefinition.getElementDefinition();
        this.strategy.startingCollectionElements(elementDefinition);
        try {
            Type collectionElementType = elementDefinition.getType();
            if (collectionElementType.isAnyType()) {
                this.visitAnyDefinition(elementDefinition.toAnyMappingDefinition());
            } else if (collectionElementType.isComponentType()) {
                this.visitCompositeDefinition(elementDefinition.toCompositeElementDefinition());
            } else if (collectionElementType.isEntityType()) {
                if (!collectionDefinition.getCollectionPersister().isOneToMany()) {
                    QueryableCollection queryableCollection = (QueryableCollection)collectionDefinition.getCollectionPersister();
                    this.addAssociationKey(new AssociationKey(queryableCollection.getTableName(), queryableCollection.getElementColumnNames()));
                }
                this.visitEntityDefinition(elementDefinition.toEntityDefinition());
            }
        }
        finally {
            this.strategy.finishingCollectionElements(elementDefinition);
        }
    }

    protected void addAssociationKey(AssociationKey associationKey) {
        if (!this.visitedAssociationKeys.add(associationKey)) {
            throw new WalkingException(String.format("Association has already been visited: %s", associationKey));
        }
        this.strategy.associationKeyRegistered(associationKey);
    }

    protected boolean isDuplicateAssociationKey(AssociationKey associationKey) {
        return this.visitedAssociationKeys.contains(associationKey) || this.strategy.isDuplicateAssociationKey(associationKey);
    }
}

