/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.persister.walking.internal;

import java.util.Iterator;
import org.hibernate.FetchMode;
import org.hibernate.engine.FetchStrategy;
import org.hibernate.engine.FetchStyle;
import org.hibernate.engine.spi.CascadeStyle;
import org.hibernate.engine.spi.CascadeStyles;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.loader.PropertyPath;
import org.hibernate.persister.collection.QueryableCollection;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.OuterJoinLoadable;
import org.hibernate.persister.spi.HydratedCompoundValueHandler;
import org.hibernate.persister.walking.internal.FetchStrategyHelper;
import org.hibernate.persister.walking.internal.StandardAnyTypeDefinition;
import org.hibernate.persister.walking.spi.AnyMappingDefinition;
import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
import org.hibernate.persister.walking.spi.AssociationKey;
import org.hibernate.persister.walking.spi.AttributeDefinition;
import org.hibernate.persister.walking.spi.AttributeSource;
import org.hibernate.persister.walking.spi.CollectionDefinition;
import org.hibernate.persister.walking.spi.CompositeCollectionElementDefinition;
import org.hibernate.persister.walking.spi.CompositionDefinition;
import org.hibernate.persister.walking.spi.EntityDefinition;
import org.hibernate.persister.walking.spi.WalkingException;
import org.hibernate.type.AnyType;
import org.hibernate.type.AssociationType;
import org.hibernate.type.CompositeType;
import org.hibernate.type.Type;

public final class CompositionSingularSubAttributesHelper {
    private CompositionSingularSubAttributesHelper() {
    }

    public static Iterable<AttributeDefinition> getIdentifierSubAttributes(AbstractEntityPersister entityPersister) {
        return CompositionSingularSubAttributesHelper.getSingularSubAttributes(entityPersister, entityPersister, (CompositeType)entityPersister.getIdentifierType(), entityPersister.getTableName(), entityPersister.getRootTableIdentifierColumnNames());
    }

    public static Iterable<AttributeDefinition> getCompositeCollectionElementSubAttributes(CompositeCollectionElementDefinition compositionElementDefinition) {
        QueryableCollection collectionPersister = (QueryableCollection)compositionElementDefinition.getCollectionDefinition().getCollectionPersister();
        return CompositionSingularSubAttributesHelper.getSingularSubAttributes(compositionElementDefinition.getSource(), (OuterJoinLoadable)collectionPersister.getOwnerEntityPersister(), (CompositeType)collectionPersister.getElementType(), collectionPersister.getTableName(), collectionPersister.getElementColumnNames());
    }

    public static Iterable<AttributeDefinition> getCompositeCollectionIndexSubAttributes(CompositeCollectionElementDefinition compositionElementDefinition) {
        QueryableCollection collectionPersister = (QueryableCollection)compositionElementDefinition.getCollectionDefinition().getCollectionPersister();
        return CompositionSingularSubAttributesHelper.getSingularSubAttributes(compositionElementDefinition.getSource(), (OuterJoinLoadable)collectionPersister.getOwnerEntityPersister(), (CompositeType)collectionPersister.getIndexType(), collectionPersister.getTableName(), collectionPersister.toColumns("index"));
    }

    private static Iterable<AttributeDefinition> getSingularSubAttributes(final AttributeSource source, final OuterJoinLoadable ownerEntityPersister, final CompositeType compositeType, final String lhsTableName, final String[] lhsColumns) {
        return new Iterable<AttributeDefinition>(){

            @Override
            public Iterator<AttributeDefinition> iterator() {
                return new Iterator<AttributeDefinition>(){
                    private final int numberOfAttributes;
                    private int currentSubAttributeNumber;
                    private int currentColumnPosition;
                    {
                        this.numberOfAttributes = compositeType.getSubtypes().length;
                    }

                    @Override
                    public boolean hasNext() {
                        return this.currentSubAttributeNumber < this.numberOfAttributes;
                    }

                    @Override
                    public AttributeDefinition next() {
                        int subAttributeNumber = this.currentSubAttributeNumber++;
                        final String name = compositeType.getPropertyNames()[subAttributeNumber];
                        final Type type = compositeType.getSubtypes()[subAttributeNumber];
                        final FetchMode fetchMode = compositeType.getFetchMode(subAttributeNumber);
                        int columnPosition = this.currentColumnPosition;
                        int columnSpan = type.getColumnSpan(ownerEntityPersister.getFactory());
                        final String[] subAttributeLhsColumns = ArrayHelper.slice(lhsColumns, columnPosition, columnSpan);
                        boolean[] propertyNullability = compositeType.getPropertyNullability();
                        final boolean nullable = propertyNullability == null || propertyNullability[subAttributeNumber];
                        this.currentColumnPosition += columnSpan;
                        if (type.isAssociationType()) {
                            final AssociationType aType = (AssociationType)type;
                            return new AssociationAttributeDefinition(){

                                @Override
                                public AssociationKey getAssociationKey() {
                                    return new AssociationKey(lhsTableName, subAttributeLhsColumns);
                                }

                                @Override
                                public AssociationAttributeDefinition.AssociationNature getAssociationNature() {
                                    if (type.isAnyType()) {
                                        return AssociationAttributeDefinition.AssociationNature.ANY;
                                    }
                                    return AssociationAttributeDefinition.AssociationNature.ENTITY;
                                }

                                @Override
                                public EntityDefinition toEntityDefinition() {
                                    if (this.getAssociationNature() != AssociationAttributeDefinition.AssociationNature.ENTITY) {
                                        throw new WalkingException("Cannot build EntityDefinition from non-entity-typed attribute");
                                    }
                                    return (EntityPersister)((Object)aType.getAssociatedJoinable(ownerEntityPersister.getFactory()));
                                }

                                @Override
                                public AnyMappingDefinition toAnyDefinition() {
                                    if (this.getAssociationNature() != AssociationAttributeDefinition.AssociationNature.ANY) {
                                        throw new WalkingException("Cannot build AnyMappingDefinition from non-any-typed attribute");
                                    }
                                    return new StandardAnyTypeDefinition((AnyType)aType, false);
                                }

                                @Override
                                public CollectionDefinition toCollectionDefinition() {
                                    throw new WalkingException("A collection cannot be mapped to a composite ID sub-attribute.");
                                }

                                @Override
                                public FetchStrategy determineFetchPlan(LoadQueryInfluencers loadQueryInfluencers, PropertyPath propertyPath) {
                                    FetchStyle style = FetchStrategyHelper.determineFetchStyleByMetadata(fetchMode, (AssociationType)type, ownerEntityPersister.getFactory());
                                    return new FetchStrategy(FetchStrategyHelper.determineFetchTiming(style, this.getType(), ownerEntityPersister.getFactory()), style);
                                }

                                @Override
                                public CascadeStyle determineCascadeStyle() {
                                    return CascadeStyles.NONE;
                                }

                                @Override
                                public HydratedCompoundValueHandler getHydratedCompoundValueExtractor() {
                                    return null;
                                }

                                @Override
                                public String getName() {
                                    return name;
                                }

                                @Override
                                public AssociationType getType() {
                                    return aType;
                                }

                                @Override
                                public boolean isNullable() {
                                    return nullable;
                                }

                                @Override
                                public AttributeSource getSource() {
                                    return source;
                                }
                            };
                        }
                        if (type.isComponentType()) {
                            return new CompositionDefinition(){

                                @Override
                                public String getName() {
                                    return name;
                                }

                                @Override
                                public CompositeType getType() {
                                    return (CompositeType)type;
                                }

                                @Override
                                public boolean isNullable() {
                                    return nullable;
                                }

                                @Override
                                public AttributeSource getSource() {
                                    return source;
                                }

                                @Override
                                public Iterable<AttributeDefinition> getAttributes() {
                                    return CompositionSingularSubAttributesHelper.getSingularSubAttributes(this, ownerEntityPersister, (CompositeType)type, lhsTableName, subAttributeLhsColumns);
                                }
                            };
                        }
                        return new AttributeDefinition(){

                            @Override
                            public String getName() {
                                return name;
                            }

                            @Override
                            public Type getType() {
                                return type;
                            }

                            @Override
                            public boolean isNullable() {
                                return nullable;
                            }

                            @Override
                            public AttributeSource getSource() {
                                return source;
                            }
                        };
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException("Remove operation not supported here");
                    }
                };
            }
        };
    }
}

