/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tuple.component;

import java.util.Iterator;
import org.hibernate.engine.internal.JoinHelper;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.persister.collection.QueryableCollection;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.Joinable;
import org.hibernate.persister.entity.OuterJoinLoadable;
import org.hibernate.persister.walking.spi.AssociationKey;
import org.hibernate.persister.walking.spi.AttributeDefinition;
import org.hibernate.persister.walking.spi.AttributeSource;
import org.hibernate.persister.walking.spi.CompositionDefinition;
import org.hibernate.tuple.AbstractNonIdentifierAttribute;
import org.hibernate.tuple.BaselineAttributeInformation;
import org.hibernate.tuple.component.CompositeBasedAssociationAttribute;
import org.hibernate.tuple.component.CompositeBasedBasicAttribute;
import org.hibernate.tuple.component.CompositionBasedCompositionAttribute;
import org.hibernate.type.AssociationType;
import org.hibernate.type.CompositeType;
import org.hibernate.type.ForeignKeyDirection;
import org.hibernate.type.Type;

public abstract class AbstractCompositionAttribute
extends AbstractNonIdentifierAttribute
implements CompositionDefinition {
    private final int columnStartPosition;

    protected AbstractCompositionAttribute(AttributeSource source, SessionFactoryImplementor sessionFactory, int entityBasedAttributeNumber, String attributeName, CompositeType attributeType, int columnStartPosition, BaselineAttributeInformation baselineInfo) {
        super(source, sessionFactory, entityBasedAttributeNumber, attributeName, attributeType, baselineInfo);
        this.columnStartPosition = columnStartPosition;
    }

    @Override
    public CompositeType getType() {
        return (CompositeType)super.getType();
    }

    @Override
    public Iterable<AttributeDefinition> getAttributes() {
        return new Iterable<AttributeDefinition>(){

            @Override
            public Iterator<AttributeDefinition> iterator() {
                return new Iterator<AttributeDefinition>(){
                    private final int numberOfAttributes;
                    private int currentSubAttributeNumber;
                    private int currentColumnPosition;
                    {
                        this.numberOfAttributes = AbstractCompositionAttribute.this.getType().getSubtypes().length;
                        this.currentColumnPosition = AbstractCompositionAttribute.this.columnStartPosition;
                    }

                    @Override
                    public boolean hasNext() {
                        return this.currentSubAttributeNumber < this.numberOfAttributes;
                    }

                    @Override
                    public AttributeDefinition next() {
                        boolean nullable;
                        int subAttributeNumber = this.currentSubAttributeNumber++;
                        String name = AbstractCompositionAttribute.this.getType().getPropertyNames()[subAttributeNumber];
                        Type type = AbstractCompositionAttribute.this.getType().getSubtypes()[subAttributeNumber];
                        int columnPosition = this.currentColumnPosition;
                        this.currentColumnPosition += type.getColumnSpan(AbstractCompositionAttribute.this.sessionFactory());
                        CompositeType cType = AbstractCompositionAttribute.this.getType();
                        boolean bl = nullable = cType.getPropertyNullability() == null || cType.getPropertyNullability()[subAttributeNumber];
                        if (type.isAssociationType()) {
                            AssociationKey associationKey;
                            AssociationType aType = (AssociationType)type;
                            if (aType.isAnyType()) {
                                associationKey = new AssociationKey(JoinHelper.getLHSTableName(aType, AbstractCompositionAttribute.this.attributeNumber(), (OuterJoinLoadable)AbstractCompositionAttribute.this.locateOwningPersister()), JoinHelper.getLHSColumnNames(aType, AbstractCompositionAttribute.this.attributeNumber(), columnPosition, (OuterJoinLoadable)AbstractCompositionAttribute.this.locateOwningPersister(), AbstractCompositionAttribute.this.sessionFactory()));
                            } else if (aType.getForeignKeyDirection() == ForeignKeyDirection.FROM_PARENT) {
                                String[] lhsColumnNames;
                                String lhsTableName;
                                Joinable joinable = aType.getAssociatedJoinable(AbstractCompositionAttribute.this.sessionFactory());
                                if (joinable.isCollection()) {
                                    QueryableCollection collectionPersister = (QueryableCollection)joinable;
                                    lhsTableName = collectionPersister.getTableName();
                                    lhsColumnNames = collectionPersister.getElementColumnNames();
                                } else {
                                    OuterJoinLoadable entityPersister = (OuterJoinLoadable)AbstractCompositionAttribute.this.locateOwningPersister();
                                    lhsTableName = JoinHelper.getLHSTableName(aType, AbstractCompositionAttribute.this.attributeNumber(), entityPersister);
                                    lhsColumnNames = JoinHelper.getLHSColumnNames(aType, AbstractCompositionAttribute.this.attributeNumber(), columnPosition, entityPersister, AbstractCompositionAttribute.this.sessionFactory());
                                }
                                associationKey = new AssociationKey(lhsTableName, lhsColumnNames);
                            } else {
                                Joinable joinable = aType.getAssociatedJoinable(AbstractCompositionAttribute.this.sessionFactory());
                                associationKey = new AssociationKey(joinable.getTableName(), JoinHelper.getRHSColumnNames(aType, AbstractCompositionAttribute.this.sessionFactory()));
                            }
                            return new CompositeBasedAssociationAttribute(AbstractCompositionAttribute.this, AbstractCompositionAttribute.this.sessionFactory(), AbstractCompositionAttribute.this.attributeNumber(), name, (AssociationType)type, new BaselineAttributeInformation.Builder().setInsertable(AbstractCompositionAttribute.this.isInsertable()).setUpdateable(AbstractCompositionAttribute.this.isUpdateable()).setNullable(nullable).setDirtyCheckable(true).setVersionable(AbstractCompositionAttribute.this.isVersionable()).setCascadeStyle(AbstractCompositionAttribute.this.getType().getCascadeStyle(subAttributeNumber)).setFetchMode(AbstractCompositionAttribute.this.getType().getFetchMode(subAttributeNumber)).createInformation(), subAttributeNumber, associationKey);
                        }
                        if (type.isComponentType()) {
                            return new CompositionBasedCompositionAttribute(AbstractCompositionAttribute.this, AbstractCompositionAttribute.this.sessionFactory(), AbstractCompositionAttribute.this.attributeNumber(), name, (CompositeType)type, columnPosition, new BaselineAttributeInformation.Builder().setInsertable(AbstractCompositionAttribute.this.isInsertable()).setUpdateable(AbstractCompositionAttribute.this.isUpdateable()).setNullable(nullable).setDirtyCheckable(true).setVersionable(AbstractCompositionAttribute.this.isVersionable()).setCascadeStyle(AbstractCompositionAttribute.this.getType().getCascadeStyle(subAttributeNumber)).setFetchMode(AbstractCompositionAttribute.this.getType().getFetchMode(subAttributeNumber)).createInformation());
                        }
                        return new CompositeBasedBasicAttribute(AbstractCompositionAttribute.this, AbstractCompositionAttribute.this.sessionFactory(), subAttributeNumber, name, type, new BaselineAttributeInformation.Builder().setInsertable(AbstractCompositionAttribute.this.isInsertable()).setUpdateable(AbstractCompositionAttribute.this.isUpdateable()).setNullable(nullable).setDirtyCheckable(true).setVersionable(AbstractCompositionAttribute.this.isVersionable()).setCascadeStyle(AbstractCompositionAttribute.this.getType().getCascadeStyle(subAttributeNumber)).setFetchMode(AbstractCompositionAttribute.this.getType().getFetchMode(subAttributeNumber)).createInformation());
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException("Remove operation not supported here");
                    }
                };
            }
        };
    }

    protected abstract EntityPersister locateOwningPersister();

    @Override
    protected String loggableMetadata() {
        return super.loggableMetadata() + ",composition";
    }
}

