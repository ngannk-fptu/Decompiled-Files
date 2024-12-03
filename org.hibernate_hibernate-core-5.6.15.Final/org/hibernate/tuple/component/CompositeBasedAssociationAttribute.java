/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tuple.component;

import org.hibernate.FetchMode;
import org.hibernate.engine.FetchStrategy;
import org.hibernate.engine.FetchStyle;
import org.hibernate.engine.FetchTiming;
import org.hibernate.engine.spi.CascadeStyle;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.loader.PropertyPath;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.Joinable;
import org.hibernate.persister.spi.HydratedCompoundValueHandler;
import org.hibernate.persister.walking.internal.FetchStrategyHelper;
import org.hibernate.persister.walking.internal.StandardAnyTypeDefinition;
import org.hibernate.persister.walking.spi.AnyMappingDefinition;
import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
import org.hibernate.persister.walking.spi.AssociationKey;
import org.hibernate.persister.walking.spi.CollectionDefinition;
import org.hibernate.persister.walking.spi.EntityDefinition;
import org.hibernate.persister.walking.spi.WalkingException;
import org.hibernate.tuple.AbstractNonIdentifierAttribute;
import org.hibernate.tuple.BaselineAttributeInformation;
import org.hibernate.tuple.NonIdentifierAttribute;
import org.hibernate.tuple.component.AbstractCompositionAttribute;
import org.hibernate.type.AnyType;
import org.hibernate.type.AssociationType;

public class CompositeBasedAssociationAttribute
extends AbstractNonIdentifierAttribute
implements NonIdentifierAttribute,
AssociationAttributeDefinition {
    private final int subAttributeNumber;
    private final AssociationKey associationKey;
    private Joinable joinable;
    private HydratedCompoundValueHandler hydratedCompoundValueHandler;

    public CompositeBasedAssociationAttribute(AbstractCompositionAttribute source, SessionFactoryImplementor factory, int entityBasedAttributeNumber, String attributeName, AssociationType attributeType, BaselineAttributeInformation baselineInfo, int subAttributeNumber, AssociationKey associationKey) {
        super(source, factory, entityBasedAttributeNumber, attributeName, attributeType, baselineInfo);
        this.subAttributeNumber = subAttributeNumber;
        this.associationKey = associationKey;
    }

    @Override
    public AssociationType getType() {
        return (AssociationType)super.getType();
    }

    @Override
    public AbstractCompositionAttribute getSource() {
        return (AbstractCompositionAttribute)super.getSource();
    }

    protected Joinable getJoinable() {
        if (this.joinable == null) {
            this.joinable = this.getType().getAssociatedJoinable(this.sessionFactory());
        }
        return this.joinable;
    }

    @Override
    public AssociationKey getAssociationKey() {
        return this.associationKey;
    }

    @Override
    public AssociationAttributeDefinition.AssociationNature getAssociationNature() {
        if (this.getType().isAnyType()) {
            return AssociationAttributeDefinition.AssociationNature.ANY;
        }
        if (this.getJoinable().isCollection()) {
            return AssociationAttributeDefinition.AssociationNature.COLLECTION;
        }
        return AssociationAttributeDefinition.AssociationNature.ENTITY;
    }

    private boolean isAnyType() {
        return this.getAssociationNature() == AssociationAttributeDefinition.AssociationNature.ANY;
    }

    private boolean isEntityType() {
        return this.getAssociationNature() == AssociationAttributeDefinition.AssociationNature.ENTITY;
    }

    private boolean isCollection() {
        return this.getAssociationNature() == AssociationAttributeDefinition.AssociationNature.COLLECTION;
    }

    @Override
    public AnyMappingDefinition toAnyDefinition() {
        if (!this.isAnyType()) {
            throw new WalkingException("Cannot build AnyMappingDefinition from non-any-typed attribute");
        }
        return new StandardAnyTypeDefinition((AnyType)this.getType(), false);
    }

    @Override
    public EntityDefinition toEntityDefinition() {
        if (this.isCollection()) {
            throw new IllegalStateException("Cannot treat collection attribute as entity type");
        }
        if (this.isAnyType()) {
            throw new IllegalStateException("Cannot treat any-type attribute as entity type");
        }
        return (EntityPersister)((Object)this.getJoinable());
    }

    @Override
    public CollectionDefinition toCollectionDefinition() {
        if (this.isEntityType()) {
            throw new IllegalStateException("Cannot treat entity attribute as collection type");
        }
        if (this.isAnyType()) {
            throw new IllegalStateException("Cannot treat any-type attribute as collection type");
        }
        return (CollectionPersister)((Object)this.getJoinable());
    }

    @Override
    public FetchStrategy determineFetchPlan(LoadQueryInfluencers loadQueryInfluencers, PropertyPath propertyPath) {
        EntityPersister owningPersister = this.getSource().locateOwningPersister();
        FetchStyle style = FetchStrategyHelper.determineFetchStyleByProfile(loadQueryInfluencers, owningPersister, propertyPath, this.attributeNumber());
        if (style == null) {
            style = this.determineFetchStyleByMetadata(this.getFetchMode(), this.getType());
        }
        return new FetchStrategy(this.determineFetchTiming(style), style);
    }

    protected FetchStyle determineFetchStyleByMetadata(FetchMode fetchMode, AssociationType type) {
        return FetchStrategyHelper.determineFetchStyleByMetadata(fetchMode, type, this.sessionFactory());
    }

    private FetchTiming determineFetchTiming(FetchStyle style) {
        return FetchStrategyHelper.determineFetchTiming(style, this.getType(), this.sessionFactory());
    }

    @Override
    public CascadeStyle determineCascadeStyle() {
        return this.getCascadeStyle();
    }

    @Override
    public HydratedCompoundValueHandler getHydratedCompoundValueExtractor() {
        if (this.hydratedCompoundValueHandler == null) {
            this.hydratedCompoundValueHandler = new HydratedCompoundValueHandler(){

                @Override
                public Object extract(Object hydratedState) {
                    return ((Object[])hydratedState)[CompositeBasedAssociationAttribute.this.subAttributeNumber];
                }

                @Override
                public void inject(Object hydratedState, Object value) {
                    ((Object[])hydratedState)[((CompositeBasedAssociationAttribute)CompositeBasedAssociationAttribute.this).subAttributeNumber] = value;
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

