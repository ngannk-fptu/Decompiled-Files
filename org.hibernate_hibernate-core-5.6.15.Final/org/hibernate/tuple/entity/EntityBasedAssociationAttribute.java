/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tuple.entity;

import org.hibernate.engine.FetchStrategy;
import org.hibernate.engine.FetchStyle;
import org.hibernate.engine.internal.JoinHelper;
import org.hibernate.engine.spi.CascadeStyle;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.loader.PropertyPath;
import org.hibernate.persister.collection.QueryableCollection;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.Joinable;
import org.hibernate.persister.entity.OuterJoinLoadable;
import org.hibernate.persister.spi.HydratedCompoundValueHandler;
import org.hibernate.persister.walking.internal.FetchStrategyHelper;
import org.hibernate.persister.walking.internal.StandardAnyTypeDefinition;
import org.hibernate.persister.walking.spi.AnyMappingDefinition;
import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
import org.hibernate.persister.walking.spi.AssociationKey;
import org.hibernate.persister.walking.spi.CollectionDefinition;
import org.hibernate.persister.walking.spi.EntityDefinition;
import org.hibernate.persister.walking.spi.WalkingException;
import org.hibernate.tuple.BaselineAttributeInformation;
import org.hibernate.tuple.entity.AbstractEntityBasedAttribute;
import org.hibernate.type.AnyType;
import org.hibernate.type.AssociationType;
import org.hibernate.type.ForeignKeyDirection;
import org.hibernate.type.Type;

public class EntityBasedAssociationAttribute
extends AbstractEntityBasedAttribute
implements AssociationAttributeDefinition {
    private Joinable joinable;
    private HydratedCompoundValueHandler hydratedCompoundValueHandler;

    public EntityBasedAssociationAttribute(EntityPersister source, SessionFactoryImplementor sessionFactory, int attributeNumber, String attributeName, AssociationType attributeType, BaselineAttributeInformation baselineInfo) {
        super(source, sessionFactory, attributeNumber, attributeName, (Type)attributeType, baselineInfo);
    }

    @Override
    public AssociationType getType() {
        return (AssociationType)super.getType();
    }

    @Override
    public AssociationKey getAssociationKey() {
        AssociationType type = this.getType();
        if (type.isAnyType()) {
            return new AssociationKey(JoinHelper.getLHSTableName(type, this.attributeNumber(), (OuterJoinLoadable)this.getSource()), JoinHelper.getLHSColumnNames(type, this.attributeNumber(), 0, (OuterJoinLoadable)this.getSource(), this.sessionFactory()));
        }
        Joinable joinable = type.getAssociatedJoinable(this.sessionFactory());
        if (type.getForeignKeyDirection() == ForeignKeyDirection.FROM_PARENT) {
            String[] lhsColumnNames;
            String lhsTableName;
            if (joinable.isCollection()) {
                QueryableCollection collectionPersister = (QueryableCollection)joinable;
                lhsTableName = collectionPersister.getTableName();
                lhsColumnNames = collectionPersister.getElementColumnNames();
            } else {
                OuterJoinLoadable entityPersister = (OuterJoinLoadable)this.source();
                lhsTableName = JoinHelper.getLHSTableName(type, this.attributeNumber(), entityPersister);
                lhsColumnNames = JoinHelper.getLHSColumnNames(type, this.attributeNumber(), entityPersister, this.sessionFactory());
            }
            return new AssociationKey(lhsTableName, lhsColumnNames);
        }
        return new AssociationKey(joinable.getTableName(), JoinHelper.getRHSColumnNames(type, this.sessionFactory()));
    }

    @Override
    public AssociationAttributeDefinition.AssociationNature getAssociationNature() {
        if (this.getType().isAnyType()) {
            return AssociationAttributeDefinition.AssociationNature.ANY;
        }
        if (this.getType().isCollectionType()) {
            return AssociationAttributeDefinition.AssociationNature.COLLECTION;
        }
        return AssociationAttributeDefinition.AssociationNature.ENTITY;
    }

    @Override
    public AnyMappingDefinition toAnyDefinition() {
        return new StandardAnyTypeDefinition((AnyType)this.getType(), this.getSource().getEntityMetamodel().getProperties()[this.attributeNumber()].isLazy());
    }

    protected Joinable getJoinable() {
        if (this.getAssociationNature() == AssociationAttributeDefinition.AssociationNature.ANY) {
            throw new WalkingException("Cannot resolve AnyType to a Joinable");
        }
        if (this.joinable == null) {
            this.joinable = this.getType().getAssociatedJoinable(this.sessionFactory());
        }
        return this.joinable;
    }

    @Override
    public EntityDefinition toEntityDefinition() {
        if (this.getAssociationNature() == AssociationAttributeDefinition.AssociationNature.ANY) {
            throw new WalkingException("Cannot treat any-type attribute as an entity type");
        }
        if (this.getAssociationNature() == AssociationAttributeDefinition.AssociationNature.COLLECTION) {
            throw new IllegalStateException("Cannot treat collection-valued attribute as entity type");
        }
        return (EntityPersister)((Object)this.getJoinable());
    }

    @Override
    public CollectionDefinition toCollectionDefinition() {
        if (this.getAssociationNature() == AssociationAttributeDefinition.AssociationNature.ANY) {
            throw new WalkingException("Cannot treat any-type attribute as a collection type");
        }
        if (this.getAssociationNature() == AssociationAttributeDefinition.AssociationNature.ENTITY) {
            throw new IllegalStateException("Cannot treat entity-valued attribute as collection type");
        }
        return (QueryableCollection)this.getJoinable();
    }

    @Override
    public FetchStrategy determineFetchPlan(LoadQueryInfluencers loadQueryInfluencers, PropertyPath propertyPath) {
        EntityPersister owningPersister = this.getSource().getEntityPersister();
        FetchStyle style = FetchStrategyHelper.determineFetchStyleByProfile(loadQueryInfluencers, owningPersister, propertyPath, this.attributeNumber());
        if (style == null) {
            style = FetchStrategyHelper.determineFetchStyleByMetadata(((OuterJoinLoadable)this.getSource().getEntityPersister()).getFetchMode(this.attributeNumber()), this.getType(), this.sessionFactory());
        }
        return new FetchStrategy(FetchStrategyHelper.determineFetchTiming(style, this.getType(), this.sessionFactory()), style);
    }

    @Override
    public CascadeStyle determineCascadeStyle() {
        return this.getSource().getEntityPersister().getPropertyCascadeStyles()[this.attributeNumber()];
    }

    @Override
    public HydratedCompoundValueHandler getHydratedCompoundValueExtractor() {
        if (this.hydratedCompoundValueHandler == null) {
            this.hydratedCompoundValueHandler = new HydratedCompoundValueHandler(){

                @Override
                public Object extract(Object hydratedState) {
                    return ((Object[])hydratedState)[EntityBasedAssociationAttribute.this.attributeNumber()];
                }

                @Override
                public void inject(Object hydratedState, Object value) {
                    ((Object[])hydratedState)[((EntityBasedAssociationAttribute)EntityBasedAssociationAttribute.this).attributeNumber()] = value;
                }
            };
        }
        return this.hydratedCompoundValueHandler;
    }

    @Override
    protected String loggableMetadata() {
        return super.loggableMetadata() + ",association";
    }
}

