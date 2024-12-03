/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.loader.plan.build.internal;

import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.engine.FetchStrategy;
import org.hibernate.engine.FetchStyle;
import org.hibernate.engine.FetchTiming;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.CoreLogging;
import org.hibernate.loader.plan.build.internal.AbstractLoadPlanBuildingAssociationVisitationStrategy;
import org.hibernate.loader.plan.build.internal.LoadPlanImpl;
import org.hibernate.loader.plan.build.spi.ExpandingFetchSource;
import org.hibernate.loader.plan.spi.CollectionReturn;
import org.hibernate.loader.plan.spi.EntityReference;
import org.hibernate.loader.plan.spi.EntityReturn;
import org.hibernate.loader.plan.spi.LoadPlan;
import org.hibernate.loader.plan.spi.QuerySpaces;
import org.hibernate.loader.plan.spi.Return;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
import org.hibernate.persister.walking.spi.EncapsulatedEntityIdentifierDefinition;
import org.hibernate.persister.walking.spi.EntityIdentifierDefinition;
import org.hibernate.persister.walking.spi.NonEncapsulatedEntityIdentifierDefinition;
import org.hibernate.persister.walking.spi.WalkingException;
import org.hibernate.type.AssociationType;
import org.hibernate.type.CompositeType;
import org.hibernate.type.EmbeddedComponentType;
import org.hibernate.type.EntityType;
import org.hibernate.type.ForeignKeyDirection;
import org.hibernate.type.OneToOneType;
import org.hibernate.type.Type;
import org.jboss.logging.Logger;

public class FetchStyleLoadPlanBuildingAssociationVisitationStrategy
extends AbstractLoadPlanBuildingAssociationVisitationStrategy {
    private static final Logger log = CoreLogging.logger(FetchStyleLoadPlanBuildingAssociationVisitationStrategy.class);
    private final LoadQueryInfluencers loadQueryInfluencers;
    private final LockMode lockMode;
    private Return rootReturn;
    private boolean vetoHandleAssociationAttribute;

    public FetchStyleLoadPlanBuildingAssociationVisitationStrategy(SessionFactoryImplementor sessionFactory, LoadQueryInfluencers loadQueryInfluencers, LockMode lockMode) {
        super(sessionFactory);
        this.loadQueryInfluencers = loadQueryInfluencers;
        this.lockMode = lockMode;
    }

    @Override
    protected boolean supportsRootEntityReturns() {
        return true;
    }

    @Override
    protected boolean supportsRootCollectionReturns() {
        return true;
    }

    @Override
    protected void addRootReturn(Return rootReturn) {
        if (this.rootReturn != null) {
            throw new HibernateException("Root return already identified");
        }
        this.rootReturn = rootReturn;
    }

    @Override
    public void startingEntityIdentifier(EntityIdentifierDefinition identifierDefinition) {
        if (this.vetoHandleAssociationAttribute) {
            throw new WalkingException("vetoHandleAssociationAttribute is true when starting startingEntityIdentifier()");
        }
        this.vetoHandleAssociationAttribute = FetchStyleLoadPlanBuildingAssociationVisitationStrategy.shouldVetoHandleAssociationAttributeInId(this.rootReturn, identifierDefinition);
        super.startingEntityIdentifier(identifierDefinition);
    }

    @Override
    public void finishingEntityIdentifier(EntityIdentifierDefinition identifierDefinition) {
        super.finishingEntityIdentifier(identifierDefinition);
        if (this.vetoHandleAssociationAttribute != FetchStyleLoadPlanBuildingAssociationVisitationStrategy.shouldVetoHandleAssociationAttributeInId(this.rootReturn, identifierDefinition)) {
            throw new WalkingException("vetoHandleAssociationAttribute has unexpected value: " + this.vetoHandleAssociationAttribute);
        }
        this.vetoHandleAssociationAttribute = false;
    }

    private static boolean shouldVetoHandleAssociationAttributeInId(Return rootReturn, EntityIdentifierDefinition identifierDefinition) {
        NonEncapsulatedEntityIdentifierDefinition nonEncapsulated;
        EncapsulatedEntityIdentifierDefinition encapsulated;
        EntityIdentifierDefinition rootEntityIdentifierDefinition;
        return EntityReturn.class.isInstance(rootReturn) && (rootEntityIdentifierDefinition = ((EntityReturn)rootReturn).getEntityPersister().getEntityKeyDefinition()) == identifierDefinition && (rootEntityIdentifierDefinition.isEncapsulated() ? (encapsulated = (EncapsulatedEntityIdentifierDefinition)rootEntityIdentifierDefinition).getAttributeDefinition().getType().isComponentType() : (nonEncapsulated = (NonEncapsulatedEntityIdentifierDefinition)rootEntityIdentifierDefinition).getSeparateIdentifierMappingClass() == null);
    }

    @Override
    protected boolean handleAssociationAttribute(AssociationAttributeDefinition attributeDefinition) {
        return !this.vetoHandleAssociationAttribute && super.handleAssociationAttribute(attributeDefinition);
    }

    @Override
    public LoadPlan buildLoadPlan() {
        log.debug((Object)"Building LoadPlan...");
        if (EntityReturn.class.isInstance(this.rootReturn)) {
            return new LoadPlanImpl((EntityReturn)this.rootReturn, (QuerySpaces)this.getQuerySpaces());
        }
        if (CollectionReturn.class.isInstance(this.rootReturn)) {
            return new LoadPlanImpl((CollectionReturn)this.rootReturn, (QuerySpaces)this.getQuerySpaces());
        }
        throw new IllegalStateException("Unexpected root Return type : " + this.rootReturn);
    }

    @Override
    protected FetchStrategy determineFetchStrategy(AssociationAttributeDefinition attributeDefinition) {
        FetchStrategy fetchStrategy = attributeDefinition.determineFetchPlan(this.loadQueryInfluencers, this.currentPropertyPath);
        if (fetchStrategy.getTiming() == FetchTiming.IMMEDIATE && fetchStrategy.getStyle() == FetchStyle.JOIN) {
            fetchStrategy = this.adjustJoinFetchIfNeeded(attributeDefinition, fetchStrategy);
        }
        return fetchStrategy;
    }

    protected FetchStrategy adjustJoinFetchIfNeeded(AssociationAttributeDefinition attributeDefinition, FetchStrategy fetchStrategy) {
        if (this.lockMode.greaterThan(LockMode.READ)) {
            return new FetchStrategy(fetchStrategy.getTiming(), FetchStyle.SELECT);
        }
        Integer maxFetchDepth = this.sessionFactory().getSessionFactoryOptions().getMaximumFetchDepth();
        if (maxFetchDepth != null && this.currentDepth() > maxFetchDepth) {
            return new FetchStrategy(fetchStrategy.getTiming(), FetchStyle.SELECT);
        }
        ExpandingFetchSource currentSource = this.currentSource();
        AssociationType attributeType = attributeDefinition.getType();
        if (attributeType.isCollectionType()) {
            if (this.isTooManyCollections()) {
                return new FetchStrategy(fetchStrategy.getTiming(), FetchStyle.SELECT);
            }
            if (currentSource.resolveEntityReference() != null) {
                EmbeddedComponentType elementIdTypeEmbedded;
                EntityType elementType;
                Type elementIdType;
                CollectionPersister collectionPersister = (CollectionPersister)((Object)attributeDefinition.getType().getAssociatedJoinable(this.sessionFactory()));
                if (fetchStrategy.getTiming() == FetchTiming.IMMEDIATE && fetchStrategy.getStyle() == FetchStyle.JOIN && collectionPersister.isOneToMany() && collectionPersister.isInverse() && (elementIdType = ((EntityPersister)((Object)(elementType = (EntityType)collectionPersister.getElementType()).getAssociatedJoinable(this.sessionFactory()))).getIdentifierType()).isComponentType() && ((CompositeType)elementIdType).isEmbedded() && (elementIdTypeEmbedded = (EmbeddedComponentType)elementIdType).getSubtypes().length == 1 && elementIdTypeEmbedded.getPropertyNames()[0].equals(collectionPersister.getMappedByProperty())) {
                    return new FetchStrategy(fetchStrategy.getTiming(), FetchStyle.SELECT);
                }
            }
        }
        if (attributeType.isEntityType() && fetchStrategy.getTiming() == FetchTiming.IMMEDIATE && fetchStrategy.getStyle() == FetchStyle.JOIN) {
            EntityType entityType = (EntityType)attributeType;
            EntityReference currentEntityReference = currentSource.resolveEntityReference();
            if (currentEntityReference != null) {
                EntityType otherSideEntityType;
                EmbeddedComponentType associatedNonEncapsulatedIdentifierType;
                EntityPersister associatedEntityPersister;
                Type associatedIdentifierType;
                OneToOneType oneToOneType;
                String associatedUniqueKeyPropertyName;
                EntityPersister currentEntityPersister = currentEntityReference.getEntityPersister();
                if (entityType.isOneToOne() && entityType.getForeignKeyDirection() == ForeignKeyDirection.TO_PARENT && (associatedUniqueKeyPropertyName = (oneToOneType = (OneToOneType)attributeType).getIdentifierOrUniqueKeyPropertyName(this.sessionFactory())) == null && (associatedIdentifierType = (associatedEntityPersister = (EntityPersister)((Object)oneToOneType.getAssociatedJoinable(this.sessionFactory()))).getIdentifierType()).isComponentType() && ((CompositeType)associatedIdentifierType).isEmbedded() && (associatedNonEncapsulatedIdentifierType = (EmbeddedComponentType)associatedIdentifierType).getSubtypes().length == 1 && EntityType.class.isInstance(associatedNonEncapsulatedIdentifierType.getSubtypes()[0]) && (otherSideEntityType = (EntityType)associatedNonEncapsulatedIdentifierType.getSubtypes()[0]).isLogicalOneToOne() && otherSideEntityType.isReferenceToPrimaryKey() && otherSideEntityType.getAssociatedEntityName().equals(currentEntityPersister.getEntityName())) {
                    return new FetchStrategy(fetchStrategy.getTiming(), FetchStyle.SELECT);
                }
            }
        }
        return fetchStrategy;
    }

    @Override
    protected boolean isTooManyCollections() {
        return CollectionReturn.class.isInstance(this.rootReturn);
    }
}

