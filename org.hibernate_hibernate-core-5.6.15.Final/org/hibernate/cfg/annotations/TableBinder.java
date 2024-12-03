/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Index
 *  javax.persistence.UniqueConstraint
 *  org.jboss.logging.Logger
 */
package org.hibernate.cfg.annotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.persistence.Index;
import javax.persistence.UniqueConstraint;
import org.hibernate.AnnotationException;
import org.hibernate.AssertionFailure;
import org.hibernate.boot.model.naming.EntityNaming;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.ImplicitCollectionTableNameSource;
import org.hibernate.boot.model.naming.ImplicitJoinTableNameSource;
import org.hibernate.boot.model.naming.ImplicitNamingStrategy;
import org.hibernate.boot.model.naming.NamingStrategyHelper;
import org.hibernate.boot.model.source.spi.AttributePath;
import org.hibernate.boot.spi.InFlightMetadataCollector;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.cfg.BinderHelper;
import org.hibernate.cfg.Ejb3JoinColumn;
import org.hibernate.cfg.IndexOrUniqueKeySecondPass;
import org.hibernate.cfg.JPAIndexHolder;
import org.hibernate.cfg.ObjectNameSource;
import org.hibernate.cfg.UniqueConstraintHolder;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.collections.CollectionHelper;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.DependantValue;
import org.hibernate.mapping.JoinedSubclass;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Selectable;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.ToOne;
import org.hibernate.mapping.Value;
import org.jboss.logging.Logger;

public class TableBinder {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)TableBinder.class.getName());
    MetadataBuildingContext buildingContext;
    private String schema;
    private String catalog;
    private String name;
    private boolean isAbstract;
    private List<UniqueConstraintHolder> uniqueConstraints;
    String constraints;
    private String ownerEntityTable;
    private String associatedEntityTable;
    private String propertyName;
    private String ownerClassName;
    private String ownerEntity;
    private String ownerJpaEntity;
    private String associatedClassName;
    private String associatedEntity;
    private String associatedJpaEntity;
    private boolean isJPA2ElementCollection;
    private List<JPAIndexHolder> jpaIndexHolders;

    public void setBuildingContext(MetadataBuildingContext buildingContext) {
        this.buildingContext = buildingContext;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAbstract(boolean anAbstract) {
        this.isAbstract = anAbstract;
    }

    public void setUniqueConstraints(UniqueConstraint[] uniqueConstraints) {
        this.uniqueConstraints = TableBinder.buildUniqueConstraintHolders(uniqueConstraints);
    }

    public void setJpaIndex(Index[] jpaIndex) {
        this.jpaIndexHolders = TableBinder.buildJpaIndexHolder(jpaIndex);
    }

    public void setConstraints(String constraints) {
        this.constraints = constraints;
    }

    public void setJPA2ElementCollection(boolean isJPA2ElementCollection) {
        this.isJPA2ElementCollection = isJPA2ElementCollection;
    }

    public Table bind() {
        final Identifier ownerEntityTableNameIdentifier = this.toIdentifier(this.ownerEntityTable);
        final String unquotedOwnerTable = StringHelper.unquote(this.ownerEntityTable);
        final String unquotedAssocTable = StringHelper.unquote(this.associatedEntityTable);
        ObjectNameSource nameSource = this.buildNameContext();
        final boolean ownerEntityTableQuoted = StringHelper.isQuoted(this.ownerEntityTable);
        final boolean associatedEntityTableQuoted = StringHelper.isQuoted(this.associatedEntityTable);
        NamingStrategyHelper namingStrategyHelper = new NamingStrategyHelper(){

            @Override
            public Identifier determineImplicitName(final MetadataBuildingContext buildingContext) {
                ImplicitNamingStrategy namingStrategy = buildingContext.getBuildingOptions().getImplicitNamingStrategy();
                Identifier name = TableBinder.this.isJPA2ElementCollection ? namingStrategy.determineCollectionTableName(new ImplicitCollectionTableNameSource(){
                    private final EntityNaming entityNaming = new EntityNaming(){

                        @Override
                        public String getClassName() {
                            return TableBinder.this.ownerClassName;
                        }

                        @Override
                        public String getEntityName() {
                            return TableBinder.this.ownerEntity;
                        }

                        @Override
                        public String getJpaEntityName() {
                            return TableBinder.this.ownerJpaEntity;
                        }
                    };

                    @Override
                    public Identifier getOwningPhysicalTableName() {
                        return ownerEntityTableNameIdentifier;
                    }

                    @Override
                    public EntityNaming getOwningEntityNaming() {
                        return this.entityNaming;
                    }

                    @Override
                    public AttributePath getOwningAttributePath() {
                        return AttributePath.parse(TableBinder.this.propertyName);
                    }

                    @Override
                    public MetadataBuildingContext getBuildingContext() {
                        return buildingContext;
                    }
                }) : namingStrategy.determineJoinTableName(new ImplicitJoinTableNameSource(){
                    private final EntityNaming owningEntityNaming = new EntityNaming(){

                        @Override
                        public String getClassName() {
                            return TableBinder.this.ownerClassName;
                        }

                        @Override
                        public String getEntityName() {
                            return TableBinder.this.ownerEntity;
                        }

                        @Override
                        public String getJpaEntityName() {
                            return TableBinder.this.ownerJpaEntity;
                        }
                    };
                    private final EntityNaming nonOwningEntityNaming = new EntityNaming(){

                        @Override
                        public String getClassName() {
                            return TableBinder.this.associatedClassName;
                        }

                        @Override
                        public String getEntityName() {
                            return TableBinder.this.associatedEntity;
                        }

                        @Override
                        public String getJpaEntityName() {
                            return TableBinder.this.associatedJpaEntity;
                        }
                    };

                    @Override
                    public String getOwningPhysicalTableName() {
                        return unquotedOwnerTable;
                    }

                    @Override
                    public EntityNaming getOwningEntityNaming() {
                        return this.owningEntityNaming;
                    }

                    @Override
                    public String getNonOwningPhysicalTableName() {
                        return unquotedAssocTable;
                    }

                    @Override
                    public EntityNaming getNonOwningEntityNaming() {
                        return this.nonOwningEntityNaming;
                    }

                    @Override
                    public AttributePath getAssociationOwningAttributePath() {
                        return AttributePath.parse(TableBinder.this.propertyName);
                    }

                    @Override
                    public MetadataBuildingContext getBuildingContext() {
                        return buildingContext;
                    }
                });
                if (ownerEntityTableQuoted || associatedEntityTableQuoted) {
                    name = Identifier.quote(name);
                }
                return name;
            }

            @Override
            public Identifier handleExplicitName(String explicitName, MetadataBuildingContext buildingContext) {
                return buildingContext.getMetadataCollector().getDatabase().toIdentifier(explicitName);
            }

            @Override
            public Identifier toPhysicalName(Identifier logicalName, MetadataBuildingContext buildingContext) {
                return buildingContext.getBuildingOptions().getPhysicalNamingStrategy().toPhysicalTableName(logicalName, buildingContext.getMetadataCollector().getDatabase().getJdbcEnvironment());
            }
        };
        return TableBinder.buildAndFillTable(this.schema, this.catalog, nameSource, namingStrategyHelper, this.isAbstract, this.uniqueConstraints, this.jpaIndexHolders, this.constraints, this.buildingContext, null, null);
    }

    private Identifier toIdentifier(String tableName) {
        return this.buildingContext.getMetadataCollector().getDatabase().getJdbcEnvironment().getIdentifierHelper().toIdentifier(tableName);
    }

    private ObjectNameSource buildNameContext() {
        if (this.name != null) {
            return new AssociationTableNameSource(this.name, null);
        }
        Identifier logicalName = this.isJPA2ElementCollection ? this.buildingContext.getBuildingOptions().getImplicitNamingStrategy().determineCollectionTableName(new ImplicitCollectionTableNameSource(){
            private final EntityNaming owningEntityNaming = new EntityNaming(){

                @Override
                public String getClassName() {
                    return TableBinder.this.ownerClassName;
                }

                @Override
                public String getEntityName() {
                    return TableBinder.this.ownerEntity;
                }

                @Override
                public String getJpaEntityName() {
                    return TableBinder.this.ownerJpaEntity;
                }
            };

            @Override
            public Identifier getOwningPhysicalTableName() {
                return TableBinder.this.toIdentifier(TableBinder.this.ownerEntityTable);
            }

            @Override
            public EntityNaming getOwningEntityNaming() {
                return this.owningEntityNaming;
            }

            @Override
            public AttributePath getOwningAttributePath() {
                return AttributePath.parse(TableBinder.this.propertyName);
            }

            @Override
            public MetadataBuildingContext getBuildingContext() {
                return TableBinder.this.buildingContext;
            }
        }) : this.buildingContext.getBuildingOptions().getImplicitNamingStrategy().determineJoinTableName(new ImplicitJoinTableNameSource(){
            private final EntityNaming owningEntityNaming = new EntityNaming(){

                @Override
                public String getClassName() {
                    return TableBinder.this.ownerClassName;
                }

                @Override
                public String getEntityName() {
                    return TableBinder.this.ownerEntity;
                }

                @Override
                public String getJpaEntityName() {
                    return TableBinder.this.ownerJpaEntity;
                }
            };
            private final EntityNaming nonOwningEntityNaming = new EntityNaming(){

                @Override
                public String getClassName() {
                    return TableBinder.this.associatedClassName;
                }

                @Override
                public String getEntityName() {
                    return TableBinder.this.associatedEntity;
                }

                @Override
                public String getJpaEntityName() {
                    return TableBinder.this.associatedJpaEntity;
                }
            };

            @Override
            public String getOwningPhysicalTableName() {
                return TableBinder.this.ownerEntityTable;
            }

            @Override
            public EntityNaming getOwningEntityNaming() {
                return this.owningEntityNaming;
            }

            @Override
            public String getNonOwningPhysicalTableName() {
                return TableBinder.this.associatedEntityTable;
            }

            @Override
            public EntityNaming getNonOwningEntityNaming() {
                return this.nonOwningEntityNaming;
            }

            @Override
            public AttributePath getAssociationOwningAttributePath() {
                return AttributePath.parse(TableBinder.this.propertyName);
            }

            @Override
            public MetadataBuildingContext getBuildingContext() {
                return TableBinder.this.buildingContext;
            }
        });
        return new AssociationTableNameSource(this.name, logicalName.render());
    }

    public static Table buildAndFillTable(String schema, String catalog, ObjectNameSource nameSource, NamingStrategyHelper namingStrategyHelper, boolean isAbstract, List<UniqueConstraintHolder> uniqueConstraints, List<JPAIndexHolder> jpaIndexHolders, String constraints, MetadataBuildingContext buildingContext, String subselect, InFlightMetadataCollector.EntityTableXref denormalizedSuperTableXref) {
        Identifier logicalName = StringHelper.isNotEmpty(nameSource.getExplicitName()) ? namingStrategyHelper.handleExplicitName(nameSource.getExplicitName(), buildingContext) : namingStrategyHelper.determineImplicitName(buildingContext);
        return TableBinder.buildAndFillTable(schema, catalog, logicalName, isAbstract, uniqueConstraints, jpaIndexHolders, constraints, buildingContext, subselect, denormalizedSuperTableXref);
    }

    public static Table buildAndFillTable(String schema, String catalog, Identifier logicalName, boolean isAbstract, List<UniqueConstraintHolder> uniqueConstraints, List<JPAIndexHolder> jpaIndexHolders, String constraints, MetadataBuildingContext buildingContext, String subselect, InFlightMetadataCollector.EntityTableXref denormalizedSuperTableXref) {
        schema = BinderHelper.isEmptyOrNullAnnotationValue(schema) ? null : schema;
        catalog = BinderHelper.isEmptyOrNullAnnotationValue(catalog) ? null : catalog;
        Table table = denormalizedSuperTableXref != null ? buildingContext.getMetadataCollector().addDenormalizedTable(schema, catalog, logicalName.render(), isAbstract, subselect, denormalizedSuperTableXref.getPrimaryTable()) : buildingContext.getMetadataCollector().addTable(schema, catalog, logicalName.render(), subselect, isAbstract);
        if (CollectionHelper.isNotEmpty(uniqueConstraints)) {
            buildingContext.getMetadataCollector().addUniqueConstraintHolders(table, uniqueConstraints);
        }
        if (CollectionHelper.isNotEmpty(jpaIndexHolders)) {
            buildingContext.getMetadataCollector().addJpaIndexHolders(table, jpaIndexHolders);
        }
        if (constraints != null) {
            table.addCheckConstraint(constraints);
        }
        buildingContext.getMetadataCollector().addTableNameBinding(logicalName, table);
        return table;
    }

    private static String extract(Identifier identifier) {
        if (identifier == null) {
            return null;
        }
        return identifier.render();
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static void bindFk(PersistentClass referencedEntity, PersistentClass destinationEntity, Ejb3JoinColumn[] columns, SimpleValue value, boolean unique, MetadataBuildingContext buildingContext) {
        PersistentClass associatedClass = destinationEntity != null ? destinationEntity : (columns[0].getPropertyHolder() == null ? null : columns[0].getPropertyHolder().getPersistentClass());
        String mappedByProperty = columns[0].getMappedBy();
        if (StringHelper.isNotEmpty(mappedByProperty)) {
            Iterator<Selectable> mappedByColumns;
            LOG.debugf("Retrieving property %s.%s", associatedClass.getEntityName(), mappedByProperty);
            Property property = associatedClass.getRecursiveProperty(columns[0].getMappedBy());
            if (property.getValue() instanceof Collection) {
                Collection collection = (Collection)property.getValue();
                Value element = collection.getElement();
                if (element == null) {
                    throw new AnnotationException("Illegal use of mappedBy on both sides of the relationship: " + associatedClass.getEntityName() + "." + mappedByProperty);
                }
                mappedByColumns = element.getColumnIterator();
            } else {
                mappedByColumns = property.getValue().getColumnIterator();
            }
            while (mappedByColumns.hasNext()) {
                Column column = (Column)mappedByColumns.next();
                columns[0].overrideFromReferencedColumnIfNecessary(column);
                columns[0].linkValueUsingAColumnCopy(column, value);
            }
        } else if (columns[0].isImplicit()) {
            Iterator<Selectable> idColumns = referencedEntity instanceof JoinedSubclass ? referencedEntity.getKey().getColumnIterator() : referencedEntity.getIdentifier().getColumnIterator();
            while (idColumns.hasNext()) {
                Column column = (Column)idColumns.next();
                columns[0].linkValueUsingDefaultColumnNaming(column, referencedEntity, value);
                columns[0].overrideFromReferencedColumnIfNecessary(column);
            }
        } else {
            int fkEnum = Ejb3JoinColumn.checkReferencedColumnsType(columns, referencedEntity, buildingContext);
            if (2 == fkEnum) {
                String referencedPropertyName;
                if (value instanceof ToOne) {
                    referencedPropertyName = ((ToOne)value).getReferencedPropertyName();
                } else {
                    if (!(value instanceof DependantValue)) throw new AssertionFailure("Do a property ref on an unexpected Value type: " + value.getClass().getName());
                    String propertyName = columns[0].getPropertyName();
                    if (propertyName == null) throw new AnnotationException("SecondaryTable JoinColumn cannot reference a non primary key");
                    Collection collection = (Collection)referencedEntity.getRecursiveProperty(propertyName).getValue();
                    referencedPropertyName = collection.getReferencedPropertyName();
                }
                if (referencedPropertyName == null) {
                    throw new AssertionFailure("No property ref found while expected");
                }
                Property synthProp = referencedEntity.getReferencedProperty(referencedPropertyName);
                if (synthProp == null) {
                    throw new AssertionFailure("Cannot find synthProp: " + referencedEntity.getEntityName() + "." + referencedPropertyName);
                }
                TableBinder.linkJoinColumnWithValueOverridingNameIfImplicit(referencedEntity, synthProp.getColumnIterator(), columns, value);
            } else if (0 == fkEnum) {
                if (columns.length != referencedEntity.getIdentifier().getColumnSpan()) {
                    throw new AnnotationException("A Foreign key refering " + referencedEntity.getEntityName() + " from " + associatedClass.getEntityName() + " has the wrong number of column. should be " + referencedEntity.getIdentifier().getColumnSpan());
                }
                TableBinder.linkJoinColumnWithValueOverridingNameIfImplicit(referencedEntity, referencedEntity.getIdentifier().getColumnIterator(), columns, value);
            } else {
                Iterator<Selectable> idColItr = referencedEntity.getKey().getColumnIterator();
                Table table = referencedEntity.getTable();
                if (!idColItr.hasNext()) {
                    LOG.debug("No column in the identifier!");
                }
                while (idColItr.hasNext()) {
                    boolean match = false;
                    Column col = (Column)idColItr.next();
                    String colName = col.getQuotedName(buildingContext.getMetadataCollector().getDatabase().getJdbcEnvironment().getDialect());
                    for (Ejb3JoinColumn joinCol : columns) {
                        String referencedColumn = joinCol.getReferencedColumn();
                        referencedColumn = buildingContext.getMetadataCollector().getPhysicalColumnName(table, referencedColumn);
                        if (!referencedColumn.equalsIgnoreCase(colName)) continue;
                        if (joinCol.isNameDeferred()) {
                            joinCol.linkValueUsingDefaultColumnNaming(col, referencedEntity, value);
                        } else {
                            joinCol.linkWithValue(value);
                        }
                        joinCol.overrideFromReferencedColumnIfNecessary(col);
                        match = true;
                        break;
                    }
                    if (match) continue;
                    throw new AnnotationException("Column name " + col.getName() + " of " + referencedEntity.getEntityName() + " not found in JoinColumns.referencedColumnName");
                }
            }
        }
        value.createForeignKey();
        if (!unique) return;
        TableBinder.createUniqueConstraint(value);
    }

    public static void linkJoinColumnWithValueOverridingNameIfImplicit(PersistentClass referencedEntity, Iterator columnIterator, Ejb3JoinColumn[] columns, SimpleValue value) {
        for (Ejb3JoinColumn joinCol : columns) {
            Column synthCol = (Column)columnIterator.next();
            if (joinCol.isNameDeferred()) {
                joinCol.linkValueUsingDefaultColumnNaming(synthCol, referencedEntity, value);
                continue;
            }
            joinCol.linkWithValue(value);
            joinCol.overrideFromReferencedColumnIfNecessary(synthCol);
        }
    }

    public static void createUniqueConstraint(Value value) {
        Iterator<Selectable> iter = value.getColumnIterator();
        ArrayList<Column> cols = new ArrayList<Column>();
        while (iter.hasNext()) {
            cols.add((Column)iter.next());
        }
        value.getTable().createUniqueKey(cols);
    }

    public static void addIndexes(Table hibTable, org.hibernate.annotations.Index[] indexes, MetadataBuildingContext buildingContext) {
        for (org.hibernate.annotations.Index index : indexes) {
            buildingContext.getMetadataCollector().addSecondPass(new IndexOrUniqueKeySecondPass(hibTable, index.name(), index.columnNames(), buildingContext));
        }
    }

    public static void addIndexes(Table hibTable, Index[] indexes, MetadataBuildingContext buildingContext) {
        buildingContext.getMetadataCollector().addJpaIndexHolders(hibTable, TableBinder.buildJpaIndexHolder(indexes));
    }

    public static List<JPAIndexHolder> buildJpaIndexHolder(Index[] indexes) {
        ArrayList<JPAIndexHolder> holders = new ArrayList<JPAIndexHolder>(indexes.length);
        for (Index index : indexes) {
            holders.add(new JPAIndexHolder(index));
        }
        return holders;
    }

    @Deprecated
    public static List<String[]> buildUniqueConstraints(UniqueConstraint[] constraintsArray) {
        ArrayList<String[]> result = new ArrayList<String[]>();
        if (constraintsArray.length != 0) {
            for (UniqueConstraint uc : constraintsArray) {
                result.add(uc.columnNames());
            }
        }
        return result;
    }

    public static List<UniqueConstraintHolder> buildUniqueConstraintHolders(UniqueConstraint[] annotations) {
        List<UniqueConstraintHolder> result;
        if (annotations == null || annotations.length == 0) {
            result = Collections.emptyList();
        } else {
            result = new ArrayList<UniqueConstraintHolder>(CollectionHelper.determineProperSizing(annotations.length));
            for (UniqueConstraint uc : annotations) {
                result.add(new UniqueConstraintHolder().setName(uc.name()).setColumns(uc.columnNames()));
            }
        }
        return result;
    }

    public void setDefaultName(String ownerClassName, String ownerEntity, String ownerJpaEntity, String ownerEntityTable, String associatedClassName, String associatedEntity, String associatedJpaEntity, String associatedEntityTable, String propertyName) {
        this.ownerClassName = ownerClassName;
        this.ownerEntity = ownerEntity;
        this.ownerJpaEntity = ownerJpaEntity;
        this.ownerEntityTable = ownerEntityTable;
        this.associatedClassName = associatedClassName;
        this.associatedEntity = associatedEntity;
        this.associatedJpaEntity = associatedJpaEntity;
        this.associatedEntityTable = associatedEntityTable;
        this.propertyName = propertyName;
        this.name = null;
    }

    private static class AssociationTableNameSource
    implements ObjectNameSource {
        private final String explicitName;
        private final String logicalName;

        private AssociationTableNameSource(String explicitName, String logicalName) {
            this.explicitName = explicitName;
            this.logicalName = logicalName;
        }

        @Override
        public String getExplicitName() {
            return this.explicitName;
        }

        @Override
        public String getLogicalName() {
            return this.logicalName;
        }
    }
}

