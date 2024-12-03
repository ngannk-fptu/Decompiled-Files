/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.JoinColumn
 *  javax.persistence.PrimaryKeyJoinColumn
 *  org.hibernate.annotations.common.reflection.XClass
 */
package org.hibernate.cfg;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import javax.persistence.JoinColumn;
import javax.persistence.PrimaryKeyJoinColumn;
import org.hibernate.AnnotationException;
import org.hibernate.AssertionFailure;
import org.hibernate.MappingException;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinFormula;
import org.hibernate.annotations.common.reflection.XClass;
import org.hibernate.boot.model.naming.EntityNaming;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.ImplicitJoinColumnNameSource;
import org.hibernate.boot.model.naming.ImplicitNamingStrategy;
import org.hibernate.boot.model.naming.ImplicitPrimaryKeyJoinColumnNameSource;
import org.hibernate.boot.model.naming.ObjectNameNormalizer;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.boot.model.relational.Database;
import org.hibernate.boot.model.source.spi.AttributePath;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.cfg.BinderHelper;
import org.hibernate.cfg.Ejb3Column;
import org.hibernate.cfg.InheritanceState;
import org.hibernate.cfg.PropertyData;
import org.hibernate.cfg.PropertyHolder;
import org.hibernate.cfg.PropertyHolderBuilder;
import org.hibernate.cfg.RecoverableException;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Join;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Selectable;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.Value;

public class Ejb3JoinColumn
extends Ejb3Column {
    private String referencedColumn;
    private String mappedBy;
    private String mappedByPropertyName;
    private String mappedByTableName;
    private String mappedByEntityName;
    private String mappedByJpaEntityName;
    private boolean JPA2ElementCollection;
    private String manyToManyOwnerSideEntityName;
    public static final int NO_REFERENCE = 0;
    public static final int PK_REFERENCE = 1;
    public static final int NON_PK_REFERENCE = 2;

    public void setJPA2ElementCollection(boolean JPA2ElementCollection) {
        this.JPA2ElementCollection = JPA2ElementCollection;
    }

    public String getManyToManyOwnerSideEntityName() {
        return this.manyToManyOwnerSideEntityName;
    }

    public void setManyToManyOwnerSideEntityName(String manyToManyOwnerSideEntityName) {
        this.manyToManyOwnerSideEntityName = manyToManyOwnerSideEntityName;
    }

    public void setReferencedColumn(String referencedColumn) {
        this.referencedColumn = referencedColumn;
    }

    public String getMappedBy() {
        return this.mappedBy;
    }

    public void setMappedBy(String mappedBy) {
        this.mappedBy = mappedBy;
    }

    private Ejb3JoinColumn() {
        this.setMappedBy("");
    }

    private Ejb3JoinColumn(String sqlType, String name, String comment, boolean nullable, boolean unique, boolean insertable, boolean updatable, String referencedColumn, String secondaryTable, Map<String, Join> joins, PropertyHolder propertyHolder, String propertyName, String mappedBy, boolean isImplicit, MetadataBuildingContext buildingContext) {
        this.setImplicit(isImplicit);
        this.setSqlType(sqlType);
        this.setLogicalColumnName(name);
        this.setComment(comment);
        this.setNullable(nullable);
        this.setUnique(unique);
        this.setInsertable(insertable);
        this.setUpdatable(updatable);
        this.setExplicitTableName(secondaryTable);
        this.setPropertyHolder(propertyHolder);
        this.setJoins(joins);
        this.setBuildingContext(buildingContext);
        this.setPropertyName(BinderHelper.getRelativePath(propertyHolder, propertyName));
        this.bind();
        this.referencedColumn = referencedColumn;
        this.mappedBy = mappedBy;
    }

    public String getReferencedColumn() {
        return this.referencedColumn;
    }

    public static Ejb3JoinColumn[] buildJoinColumnsOrFormulas(JoinColumnOrFormula[] anns, String mappedBy, Map<String, Join> joins, PropertyHolder propertyHolder, String propertyName, MetadataBuildingContext buildingContext) {
        Ejb3JoinColumn[] joinColumns = new Ejb3JoinColumn[anns.length];
        for (int i = 0; i < anns.length; ++i) {
            JoinColumnOrFormula join = anns[i];
            JoinFormula formula = join.formula();
            joinColumns[i] = formula.value() != null && !formula.value().isEmpty() ? Ejb3JoinColumn.buildJoinFormula(formula, mappedBy, joins, propertyHolder, propertyName, buildingContext) : Ejb3JoinColumn.buildJoinColumns(new JoinColumn[]{join.column()}, null, mappedBy, joins, propertyHolder, propertyName, buildingContext)[0];
        }
        return joinColumns;
    }

    public static Ejb3JoinColumn buildJoinFormula(JoinFormula ann, String mappedBy, Map<String, Join> joins, PropertyHolder propertyHolder, String propertyName, MetadataBuildingContext buildingContext) {
        Ejb3JoinColumn formulaColumn = new Ejb3JoinColumn();
        formulaColumn.setFormula(ann.value());
        formulaColumn.setReferencedColumn(ann.referencedColumnName());
        formulaColumn.setBuildingContext(buildingContext);
        formulaColumn.setPropertyHolder(propertyHolder);
        formulaColumn.setJoins(joins);
        formulaColumn.setPropertyName(BinderHelper.getRelativePath(propertyHolder, propertyName));
        formulaColumn.bind();
        return formulaColumn;
    }

    public static Ejb3JoinColumn[] buildJoinColumns(JoinColumn[] anns, Comment comment, String mappedBy, Map<String, Join> joins, PropertyHolder propertyHolder, String propertyName, MetadataBuildingContext buildingContext) {
        return Ejb3JoinColumn.buildJoinColumnsWithDefaultColumnSuffix(anns, comment, mappedBy, joins, propertyHolder, propertyName, "", buildingContext);
    }

    public static Ejb3JoinColumn[] buildJoinColumnsWithDefaultColumnSuffix(JoinColumn[] anns, Comment comment, String mappedBy, Map<String, Join> joins, PropertyHolder propertyHolder, String propertyName, String suffixForDefaultColumnName, MetadataBuildingContext buildingContext) {
        JoinColumn[] actualColumns = propertyHolder.getOverriddenJoinColumn(StringHelper.qualify(propertyHolder.getPath(), propertyName));
        if (actualColumns == null) {
            actualColumns = anns;
        }
        if (actualColumns == null || actualColumns.length == 0) {
            return new Ejb3JoinColumn[]{Ejb3JoinColumn.buildJoinColumn(null, comment, mappedBy, joins, propertyHolder, propertyName, suffixForDefaultColumnName, buildingContext)};
        }
        int size = actualColumns.length;
        Ejb3JoinColumn[] result = new Ejb3JoinColumn[size];
        for (int index = 0; index < size; ++index) {
            result[index] = Ejb3JoinColumn.buildJoinColumn(actualColumns[index], comment, mappedBy, joins, propertyHolder, propertyName, suffixForDefaultColumnName, buildingContext);
        }
        return result;
    }

    private static Ejb3JoinColumn buildJoinColumn(JoinColumn ann, Comment comment, String mappedBy, Map<String, Join> joins, PropertyHolder propertyHolder, String propertyName, String suffixForDefaultColumnName, MetadataBuildingContext buildingContext) {
        if (ann != null) {
            if (!BinderHelper.isEmptyOrNullAnnotationValue(mappedBy)) {
                throw new AnnotationException("Illegal attempt to define a @JoinColumn with a mappedBy association: " + BinderHelper.getRelativePath(propertyHolder, propertyName));
            }
            Ejb3JoinColumn joinColumn = new Ejb3JoinColumn();
            joinColumn.setComment(comment != null ? comment.value() : null);
            joinColumn.setBuildingContext(buildingContext);
            joinColumn.setJoinAnnotation(ann, null);
            if (StringHelper.isEmpty(joinColumn.getLogicalColumnName()) && !StringHelper.isEmpty(suffixForDefaultColumnName)) {
                joinColumn.setLogicalColumnName(propertyName + suffixForDefaultColumnName);
            }
            joinColumn.setJoins(joins);
            joinColumn.setPropertyHolder(propertyHolder);
            joinColumn.setPropertyName(BinderHelper.getRelativePath(propertyHolder, propertyName));
            joinColumn.setImplicit(false);
            joinColumn.bind();
            return joinColumn;
        }
        Ejb3JoinColumn joinColumn = new Ejb3JoinColumn();
        joinColumn.setMappedBy(mappedBy);
        joinColumn.setJoins(joins);
        joinColumn.setPropertyHolder(propertyHolder);
        joinColumn.setPropertyName(BinderHelper.getRelativePath(propertyHolder, propertyName));
        if (!StringHelper.isEmpty(suffixForDefaultColumnName)) {
            joinColumn.setLogicalColumnName(propertyName + suffixForDefaultColumnName);
            joinColumn.setImplicit(false);
        } else {
            joinColumn.setImplicit(true);
        }
        joinColumn.setBuildingContext(buildingContext);
        joinColumn.bind();
        return joinColumn;
    }

    public void setJoinAnnotation(JoinColumn annJoin, String defaultName) {
        if (annJoin == null) {
            this.setImplicit(true);
        } else {
            this.setImplicit(false);
            if (!BinderHelper.isEmptyAnnotationValue(annJoin.columnDefinition())) {
                this.setSqlType(this.getBuildingContext().getObjectNameNormalizer().applyGlobalQuoting(annJoin.columnDefinition()));
            }
            if (!BinderHelper.isEmptyAnnotationValue(annJoin.name())) {
                this.setLogicalColumnName(annJoin.name());
            }
            this.setNullable(annJoin.nullable());
            this.setUnique(annJoin.unique());
            this.setInsertable(annJoin.insertable());
            this.setUpdatable(annJoin.updatable());
            this.setReferencedColumn(annJoin.referencedColumnName());
            if (BinderHelper.isEmptyAnnotationValue(annJoin.table())) {
                this.setExplicitTableName("");
            } else {
                Identifier logicalIdentifier = this.getBuildingContext().getMetadataCollector().getDatabase().toIdentifier(annJoin.table());
                Identifier physicalIdentifier = this.getBuildingContext().getBuildingOptions().getPhysicalNamingStrategy().toPhysicalTableName(logicalIdentifier, this.getBuildingContext().getMetadataCollector().getDatabase().getJdbcEnvironment());
                this.setExplicitTableName(physicalIdentifier.render(this.getBuildingContext().getMetadataCollector().getDatabase().getDialect()));
            }
        }
    }

    public static Ejb3JoinColumn buildJoinColumn(PrimaryKeyJoinColumn pkJoinAnn, JoinColumn joinAnn, Value identifier, Map<String, Join> joins, PropertyHolder propertyHolder, MetadataBuildingContext context) {
        ObjectNameNormalizer normalizer = context.getObjectNameNormalizer();
        Column col = (Column)identifier.getColumnIterator().next();
        String defaultName = context.getMetadataCollector().getLogicalColumnName(identifier.getTable(), col.getQuotedName());
        if (pkJoinAnn != null || joinAnn != null) {
            String referencedColumnName;
            String columnDefinition;
            String colName;
            if (pkJoinAnn != null) {
                colName = pkJoinAnn.name();
                columnDefinition = pkJoinAnn.columnDefinition();
                referencedColumnName = pkJoinAnn.referencedColumnName();
            } else {
                colName = joinAnn.name();
                columnDefinition = joinAnn.columnDefinition();
                referencedColumnName = joinAnn.referencedColumnName();
            }
            String sqlType = columnDefinition.isEmpty() ? null : normalizer.toDatabaseIdentifierText(columnDefinition);
            String name = colName != null && colName.isEmpty() ? normalizer.normalizeIdentifierQuotingAsString(defaultName) : context.getObjectNameNormalizer().normalizeIdentifierQuotingAsString(colName);
            return new Ejb3JoinColumn(sqlType, name, null, false, false, true, true, referencedColumnName, null, joins, propertyHolder, null, null, false, context);
        }
        defaultName = context.getObjectNameNormalizer().normalizeIdentifierQuotingAsString(defaultName);
        return new Ejb3JoinColumn(null, defaultName, null, false, false, true, true, null, null, joins, propertyHolder, null, null, true, context);
    }

    public void setPersistentClass(PersistentClass persistentClass, Map<String, Join> joins, Map<XClass, InheritanceState> inheritanceStatePerClass) {
        this.propertyHolder = PropertyHolderBuilder.buildPropertyHolder(persistentClass, joins, this.getBuildingContext(), inheritanceStatePerClass);
    }

    public static void checkIfJoinColumn(Object columns, PropertyHolder holder, PropertyData property) {
        if (!(columns instanceof Ejb3JoinColumn[])) {
            throw new AnnotationException("@Column cannot be used on an association property: " + holder.getEntityName() + "." + property.getPropertyName());
        }
    }

    public void copyReferencedStructureAndCreateDefaultJoinColumns(PersistentClass referencedEntity, Iterator columnIterator, SimpleValue value) {
        if (!this.isNameDeferred()) {
            throw new AssertionFailure("Building implicit column but the column is not implicit");
        }
        while (columnIterator.hasNext()) {
            Column synthCol = (Column)columnIterator.next();
            this.linkValueUsingDefaultColumnNaming(synthCol, referencedEntity, value);
        }
        this.setMappingColumn(null);
    }

    public void linkValueUsingDefaultColumnNaming(Column referencedColumn, PersistentClass referencedEntity, SimpleValue value) {
        String logicalReferencedColumn = this.getBuildingContext().getMetadataCollector().getLogicalColumnName(referencedEntity.getTable(), referencedColumn.getQuotedName());
        String columnName = this.buildDefaultColumnName(referencedEntity, logicalReferencedColumn);
        this.setLogicalColumnName(columnName);
        this.setReferencedColumn(logicalReferencedColumn);
        this.initMappingColumn(columnName, null, referencedColumn.getLength(), referencedColumn.getPrecision(), referencedColumn.getScale(), this.getMappingColumn() != null ? this.getMappingColumn().isNullable() : false, referencedColumn.getSqlType(), this.getMappingColumn() != null ? this.getMappingColumn().isUnique() : false, false);
        this.linkWithValue(value);
    }

    public void addDefaultJoinColumnName(PersistentClass referencedEntity, String logicalReferencedColumn) {
        String columnName = this.buildDefaultColumnName(referencedEntity, logicalReferencedColumn);
        this.getMappingColumn().setName(columnName);
        this.setLogicalColumnName(columnName);
    }

    private String buildDefaultColumnName(final PersistentClass referencedEntity, final String logicalReferencedColumn) {
        Identifier columnIdentifier;
        final Database database = this.getBuildingContext().getMetadataCollector().getDatabase();
        ImplicitNamingStrategy implicitNamingStrategy = this.getBuildingContext().getBuildingOptions().getImplicitNamingStrategy();
        PhysicalNamingStrategy physicalNamingStrategy = this.getBuildingContext().getBuildingOptions().getPhysicalNamingStrategy();
        boolean mappedBySide = this.mappedByTableName != null || this.mappedByPropertyName != null;
        boolean ownerSide = this.getPropertyName() != null;
        boolean isRefColumnQuoted = StringHelper.isQuoted(logicalReferencedColumn);
        if (mappedBySide) {
            final AttributePath attributePath = AttributePath.parse(this.mappedByPropertyName);
            final ImplicitJoinColumnNameSource.Nature implicitNamingNature = this.getPropertyHolder().isEntity() ? ImplicitJoinColumnNameSource.Nature.ENTITY : (this.JPA2ElementCollection ? ImplicitJoinColumnNameSource.Nature.ELEMENT_COLLECTION : ImplicitJoinColumnNameSource.Nature.ENTITY_COLLECTION);
            columnIdentifier = implicitNamingStrategy.determineJoinColumnName(new ImplicitJoinColumnNameSource(){
                private final EntityNaming entityNaming = new EntityNaming(){

                    @Override
                    public String getClassName() {
                        return referencedEntity.getClassName();
                    }

                    @Override
                    public String getEntityName() {
                        return referencedEntity.getEntityName();
                    }

                    @Override
                    public String getJpaEntityName() {
                        return referencedEntity.getJpaEntityName();
                    }
                };
                private final Identifier referencedTableName = this.getBuildingContext().getMetadataCollector().getDatabase().toIdentifier(Ejb3JoinColumn.access$000(Ejb3JoinColumn.this));

                @Override
                public ImplicitJoinColumnNameSource.Nature getNature() {
                    return implicitNamingNature;
                }

                @Override
                public EntityNaming getEntityNaming() {
                    return this.entityNaming;
                }

                @Override
                public AttributePath getAttributePath() {
                    return attributePath;
                }

                @Override
                public Identifier getReferencedTableName() {
                    return this.referencedTableName;
                }

                @Override
                public Identifier getReferencedColumnName() {
                    if (logicalReferencedColumn != null) {
                        return this.getBuildingContext().getMetadataCollector().getDatabase().toIdentifier(logicalReferencedColumn);
                    }
                    if (Ejb3JoinColumn.this.mappedByEntityName == null || Ejb3JoinColumn.this.mappedByPropertyName == null) {
                        return null;
                    }
                    PersistentClass mappedByEntityBinding = this.getBuildingContext().getMetadataCollector().getEntityBinding(Ejb3JoinColumn.this.mappedByEntityName);
                    Property mappedByProperty = mappedByEntityBinding.getProperty(Ejb3JoinColumn.this.mappedByPropertyName);
                    SimpleValue value = (SimpleValue)mappedByProperty.getValue();
                    Iterator<Selectable> selectableValues = value.getColumnIterator();
                    if (!selectableValues.hasNext()) {
                        throw new AnnotationException(String.format(Locale.ENGLISH, "mapped-by [%s] defined for attribute [%s] referenced an invalid property (no columns)", Ejb3JoinColumn.this.mappedByPropertyName, Ejb3JoinColumn.this.propertyHolder.getPath()));
                    }
                    Selectable selectable = selectableValues.next();
                    if (!Column.class.isInstance(selectable)) {
                        throw new AnnotationException(String.format(Locale.ENGLISH, "mapped-by [%s] defined for attribute [%s] referenced an invalid property (formula)", Ejb3JoinColumn.this.mappedByPropertyName, Ejb3JoinColumn.this.propertyHolder.getPath()));
                    }
                    if (selectableValues.hasNext()) {
                        throw new AnnotationException(String.format(Locale.ENGLISH, "mapped-by [%s] defined for attribute [%s] referenced an invalid property (multiple columns)", Ejb3JoinColumn.this.mappedByPropertyName, Ejb3JoinColumn.this.propertyHolder.getPath()));
                    }
                    return this.getBuildingContext().getMetadataCollector().getDatabase().toIdentifier(((Column)selectable).getQuotedName());
                }

                @Override
                public MetadataBuildingContext getBuildingContext() {
                    return Ejb3JoinColumn.this.getBuildingContext();
                }
            });
            if (isRefColumnQuoted || StringHelper.isQuoted(this.mappedByTableName)) {
                columnIdentifier = Identifier.quote(columnIdentifier);
            }
        } else if (ownerSide) {
            final String logicalTableName = this.getBuildingContext().getMetadataCollector().getLogicalTableName(referencedEntity.getTable());
            final ImplicitJoinColumnNameSource.Nature implicitNamingNature = this.JPA2ElementCollection ? ImplicitJoinColumnNameSource.Nature.ELEMENT_COLLECTION : (this.getPropertyHolder().isEntity() ? ImplicitJoinColumnNameSource.Nature.ENTITY : ImplicitJoinColumnNameSource.Nature.ENTITY_COLLECTION);
            columnIdentifier = this.getBuildingContext().getBuildingOptions().getImplicitNamingStrategy().determineJoinColumnName(new ImplicitJoinColumnNameSource(){
                private final EntityNaming entityNaming = new EntityNaming(){

                    @Override
                    public String getClassName() {
                        return referencedEntity.getClassName();
                    }

                    @Override
                    public String getEntityName() {
                        return referencedEntity.getEntityName();
                    }

                    @Override
                    public String getJpaEntityName() {
                        return referencedEntity.getJpaEntityName();
                    }
                };
                private final AttributePath attributePath = AttributePath.parse(Ejb3JoinColumn.this.getPropertyName());
                private final Identifier referencedTableName = this.getBuildingContext().getMetadataCollector().getDatabase().toIdentifier(logicalTableName);
                private final Identifier referencedColumnName = this.getBuildingContext().getMetadataCollector().getDatabase().toIdentifier(logicalReferencedColumn);

                @Override
                public ImplicitJoinColumnNameSource.Nature getNature() {
                    return implicitNamingNature;
                }

                @Override
                public EntityNaming getEntityNaming() {
                    return this.entityNaming;
                }

                @Override
                public AttributePath getAttributePath() {
                    return this.attributePath;
                }

                @Override
                public Identifier getReferencedTableName() {
                    return this.referencedTableName;
                }

                @Override
                public Identifier getReferencedColumnName() {
                    return this.referencedColumnName;
                }

                @Override
                public MetadataBuildingContext getBuildingContext() {
                    return Ejb3JoinColumn.this.getBuildingContext();
                }
            });
            if (columnIdentifier.getText().contains("_collection&&element_")) {
                columnIdentifier = Identifier.toIdentifier(columnIdentifier.getText().replace("_collection&&element_", "_"), columnIdentifier.isQuoted());
            }
            if (isRefColumnQuoted || StringHelper.isQuoted(logicalTableName)) {
                columnIdentifier = Identifier.quote(columnIdentifier);
            }
        } else {
            final Identifier logicalTableName = database.toIdentifier(this.getBuildingContext().getMetadataCollector().getLogicalTableName(referencedEntity.getTable()));
            columnIdentifier = implicitNamingStrategy.determinePrimaryKeyJoinColumnName(new ImplicitPrimaryKeyJoinColumnNameSource(){

                @Override
                public MetadataBuildingContext getBuildingContext() {
                    return Ejb3JoinColumn.this.getBuildingContext();
                }

                @Override
                public Identifier getReferencedTableName() {
                    return logicalTableName;
                }

                @Override
                public Identifier getReferencedPrimaryKeyColumnName() {
                    return database.toIdentifier(logicalReferencedColumn);
                }
            });
            if (!columnIdentifier.isQuoted() && (isRefColumnQuoted || logicalTableName.isQuoted())) {
                columnIdentifier = Identifier.quote(columnIdentifier);
            }
        }
        return physicalNamingStrategy.toPhysicalColumnName(columnIdentifier, database.getJdbcEnvironment()).render(database.getJdbcEnvironment().getDialect());
    }

    public void linkValueUsingAColumnCopy(Column column, SimpleValue value) {
        this.initMappingColumn(column.getQuotedName(), null, column.getLength(), column.getPrecision(), column.getScale(), this.getMappingColumn().isNullable(), column.getSqlType(), this.getMappingColumn().isUnique(), false);
        this.linkWithValue(value);
    }

    @Override
    protected void addColumnBinding(SimpleValue value) {
        if (StringHelper.isEmpty(this.mappedBy)) {
            boolean isLogicalColumnQuoted = StringHelper.isQuoted(this.getLogicalColumnName());
            ObjectNameNormalizer nameNormalizer = this.getBuildingContext().getObjectNameNormalizer();
            String logicalColumnName = nameNormalizer.normalizeIdentifierQuotingAsString(this.getLogicalColumnName());
            String referencedColumn = nameNormalizer.normalizeIdentifierQuotingAsString(this.getReferencedColumn());
            String unquotedLogColName = StringHelper.unquote(logicalColumnName);
            String unquotedRefColumn = StringHelper.unquote(referencedColumn);
            String logicalCollectionColumnName = StringHelper.isNotEmpty(unquotedLogColName) ? unquotedLogColName : this.getPropertyName() + '_' + unquotedRefColumn;
            logicalCollectionColumnName = this.getBuildingContext().getMetadataCollector().getDatabase().getJdbcEnvironment().getIdentifierHelper().toIdentifier(logicalCollectionColumnName, isLogicalColumnQuoted).render();
            this.getBuildingContext().getMetadataCollector().addColumnNameBinding(value.getTable(), logicalCollectionColumnName, this.getMappingColumn());
        }
    }

    public static int checkReferencedColumnsType(Ejb3JoinColumn[] columns, PersistentClass referencedEntity, MetadataBuildingContext context) {
        HashSet<Column> idColumns = new HashSet<Column>();
        Iterator<Selectable> idColumnsIt = referencedEntity.getKey().getColumnIterator();
        while (idColumnsIt.hasNext()) {
            idColumns.add((Column)idColumnsIt.next());
        }
        boolean isFkReferencedColumnName = false;
        boolean noReferencedColumn = true;
        if (columns.length == 0) {
            return 0;
        }
        Object columnOwner = BinderHelper.findColumnOwner(referencedEntity, columns[0].getReferencedColumn(), context);
        if (columnOwner == null) {
            try {
                throw new MappingException("Unable to find column with logical name: " + columns[0].getReferencedColumn() + " in " + referencedEntity.getTable() + " and its related supertables and secondary tables");
            }
            catch (MappingException e) {
                throw new RecoverableException(e.getMessage(), (Throwable)((Object)e));
            }
        }
        Table matchingTable = columnOwner instanceof PersistentClass ? ((PersistentClass)columnOwner).getTable() : ((Join)columnOwner).getTable();
        for (Ejb3JoinColumn ejb3Column : columns) {
            String referencedColumnName;
            String logicalReferencedColumnName = ejb3Column.getReferencedColumn();
            if (!StringHelper.isNotEmpty(logicalReferencedColumnName)) continue;
            try {
                referencedColumnName = context.getMetadataCollector().getPhysicalColumnName(matchingTable, logicalReferencedColumnName);
            }
            catch (MappingException me) {
                throw new MappingException("Unable to find column with logical name: " + logicalReferencedColumnName + " in " + matchingTable.getName());
            }
            noReferencedColumn = false;
            Column refCol = new Column(referencedColumnName);
            boolean contains = idColumns.contains(refCol);
            if (contains) continue;
            isFkReferencedColumnName = true;
            break;
        }
        if (isFkReferencedColumnName) {
            return 2;
        }
        if (noReferencedColumn) {
            return 0;
        }
        if (idColumns.size() != columns.length) {
            return 2;
        }
        return 1;
    }

    public void overrideFromReferencedColumnIfNecessary(Column column) {
        if (this.getMappingColumn() != null) {
            if (StringHelper.isEmpty(this.sqlType)) {
                this.sqlType = column.getSqlType();
                this.getMappingColumn().setSqlType(this.sqlType);
            }
            this.getMappingColumn().setLength(column.getLength());
            this.getMappingColumn().setPrecision(column.getPrecision());
            this.getMappingColumn().setScale(column.getScale());
        }
    }

    @Override
    public void redefineColumnName(String columnName, String propertyName, boolean applyNamingStrategy) {
        if (StringHelper.isNotEmpty(columnName)) {
            if (applyNamingStrategy) {
                this.getMappingColumn().setName(this.getBuildingContext().getBuildingOptions().getPhysicalNamingStrategy().toPhysicalColumnName(this.getBuildingContext().getMetadataCollector().getDatabase().toIdentifier(columnName), this.getBuildingContext().getMetadataCollector().getDatabase().getJdbcEnvironment()).render());
            } else {
                this.getMappingColumn().setName(columnName);
            }
        }
    }

    public static Ejb3JoinColumn[] buildJoinTableJoinColumns(JoinColumn[] annJoins, Map<String, Join> secondaryTables, PropertyHolder propertyHolder, String propertyName, String mappedBy, MetadataBuildingContext buildingContext) {
        Ejb3JoinColumn[] joinColumns;
        if (annJoins == null) {
            Ejb3JoinColumn currentJoinColumn = new Ejb3JoinColumn();
            currentJoinColumn.setImplicit(true);
            currentJoinColumn.setNullable(false);
            currentJoinColumn.setPropertyHolder(propertyHolder);
            currentJoinColumn.setJoins(secondaryTables);
            currentJoinColumn.setBuildingContext(buildingContext);
            currentJoinColumn.setPropertyName(BinderHelper.getRelativePath(propertyHolder, propertyName));
            currentJoinColumn.setMappedBy(mappedBy);
            currentJoinColumn.bind();
            joinColumns = new Ejb3JoinColumn[]{currentJoinColumn};
        } else {
            joinColumns = new Ejb3JoinColumn[annJoins.length];
            for (JoinColumn annJoin : annJoins) {
                Ejb3JoinColumn currentJoinColumn = new Ejb3JoinColumn();
                currentJoinColumn.setImplicit(true);
                currentJoinColumn.setPropertyHolder(propertyHolder);
                currentJoinColumn.setJoins(secondaryTables);
                currentJoinColumn.setBuildingContext(buildingContext);
                currentJoinColumn.setPropertyName(BinderHelper.getRelativePath(propertyHolder, propertyName));
                currentJoinColumn.setMappedBy(mappedBy);
                currentJoinColumn.setJoinAnnotation(annJoin, propertyName);
                currentJoinColumn.setNullable(false);
                currentJoinColumn.bind();
                joinColumns[index] = currentJoinColumn;
            }
        }
        return joinColumns;
    }

    public void setMappedBy(String entityName, String jpaEntityName, String logicalTableName, String mappedByProperty) {
        this.mappedByEntityName = entityName;
        this.mappedByJpaEntityName = jpaEntityName;
        this.mappedByTableName = logicalTableName;
        this.mappedByPropertyName = mappedByProperty;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Ejb3JoinColumn");
        sb.append("{logicalColumnName='").append(this.getLogicalColumnName()).append('\'');
        sb.append(", referencedColumn='").append(this.referencedColumn).append('\'');
        sb.append(", mappedBy='").append(this.mappedBy).append('\'');
        sb.append('}');
        return sb.toString();
    }

    static /* synthetic */ String access$000(Ejb3JoinColumn x0) {
        return x0.mappedByTableName;
    }
}

