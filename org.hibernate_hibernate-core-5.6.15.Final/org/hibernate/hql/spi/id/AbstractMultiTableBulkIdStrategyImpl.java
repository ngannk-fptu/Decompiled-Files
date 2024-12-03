/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.spi.id;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.hibernate.QueryException;
import org.hibernate.boot.model.relational.QualifiedTableName;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.boot.spi.MetadataBuildingOptions;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.hql.spi.id.IdTableHelper;
import org.hibernate.hql.spi.id.IdTableInfo;
import org.hibernate.hql.spi.id.IdTableSupport;
import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Table;
import org.hibernate.persister.entity.Queryable;

public abstract class AbstractMultiTableBulkIdStrategyImpl<TT extends IdTableInfo, CT extends PreparationContext>
implements MultiTableBulkIdStrategy {
    private final IdTableSupport idTableSupport;
    private Map<String, TT> idTableInfoMap = new HashMap<String, TT>();

    public AbstractMultiTableBulkIdStrategyImpl(IdTableSupport idTableSupport) {
        this.idTableSupport = idTableSupport;
    }

    public IdTableSupport getIdTableSupport() {
        return this.idTableSupport;
    }

    @Override
    public final void prepare(JdbcServices jdbcServices, JdbcConnectionAccess connectionAccess, MetadataImplementor metadata, SessionFactoryOptions sessionFactoryOptions, SqlStringGenerationContext sqlStringGenerationContext) {
        CT context = this.buildPreparationContext();
        this.initialize(metadata.getMetadataBuildingOptions(), sessionFactoryOptions);
        JdbcEnvironment jdbcEnvironment = jdbcServices.getJdbcEnvironment();
        for (PersistentClass entityBinding : metadata.getEntityBindings()) {
            if (!IdTableHelper.INSTANCE.needsIdTable(entityBinding)) continue;
            QualifiedTableName idTableName = this.determineIdTableName(jdbcEnvironment, entityBinding);
            Table idTable = new Table(idTableName.getCatalogName(), idTableName.getSchemaName(), idTableName.getTableName(), false);
            idTable.setComment("Used to hold id values for the " + entityBinding.getEntityName() + " entity");
            Iterator<Column> itr = entityBinding.getTable().getPrimaryKey().getColumnIterator();
            while (itr.hasNext()) {
                Column column = itr.next();
                idTable.addColumn(column.clone());
            }
            this.augmentIdTableDefinition(idTable);
            TT idTableInfo = this.buildIdTableInfo(entityBinding, idTable, jdbcServices, metadata, context, sqlStringGenerationContext);
            this.idTableInfoMap.put(entityBinding.getEntityName(), idTableInfo);
        }
        this.finishPreparation(jdbcServices, connectionAccess, metadata, context);
    }

    protected CT buildPreparationContext() {
        return null;
    }

    protected void initialize(MetadataBuildingOptions buildingOptions, SessionFactoryOptions sessionFactoryOptions) {
    }

    protected QualifiedTableName determineIdTableName(JdbcEnvironment jdbcEnvironment, PersistentClass entityBinding) {
        String entityPrimaryTableName = entityBinding.getTable().getName();
        String idTableName = this.getIdTableSupport().generateIdTableName(entityPrimaryTableName);
        return new QualifiedTableName(null, null, jdbcEnvironment.getIdentifierHelper().toIdentifier(idTableName));
    }

    protected void augmentIdTableDefinition(Table idTable) {
    }

    protected abstract TT buildIdTableInfo(PersistentClass var1, Table var2, JdbcServices var3, MetadataImplementor var4, CT var5, SqlStringGenerationContext var6);

    protected String buildIdTableCreateStatement(Table idTable, MetadataImplementor metadata, SqlStringGenerationContext sqlStringGenerationContext) {
        Dialect dialect = sqlStringGenerationContext.getDialect();
        StringBuilder buffer = new StringBuilder(this.getIdTableSupport().getCreateIdTableCommand()).append(' ').append(this.formatIdTableName(idTable.getQualifiedTableName(), sqlStringGenerationContext)).append(" (");
        Iterator<Column> itr = idTable.getColumnIterator();
        while (itr.hasNext()) {
            Column column = itr.next();
            buffer.append(column.getQuotedName(dialect)).append(' ');
            buffer.append(column.getSqlType(dialect, metadata));
            int sqlTypeCode = column.getSqlTypeCode() != null ? column.getSqlTypeCode().intValue() : column.getSqlTypeCode(metadata);
            String columnAnnotation = dialect.getCreateTemporaryTableColumnAnnotation(sqlTypeCode);
            if (!columnAnnotation.isEmpty()) {
                buffer.append(" ").append(columnAnnotation);
            }
            if (column.isNullable()) {
                buffer.append(dialect.getNullColumnString());
            } else {
                buffer.append(" not null");
            }
            if (!itr.hasNext()) continue;
            buffer.append(", ");
        }
        buffer.append(") ");
        if (this.getIdTableSupport().getCreateIdTableStatementOptions() != null) {
            buffer.append(this.getIdTableSupport().getCreateIdTableStatementOptions());
        }
        return buffer.toString();
    }

    protected String buildIdTableDropStatement(Table idTable, SqlStringGenerationContext sqlStringGenerationContext) {
        return this.getIdTableSupport().getDropIdTableCommand() + " " + this.formatIdTableName(idTable.getQualifiedTableName(), sqlStringGenerationContext);
    }

    protected String formatIdTableName(QualifiedTableName qualifiedTableName, SqlStringGenerationContext sqlStringGenerationContext) {
        return sqlStringGenerationContext.formatWithoutDefaults(qualifiedTableName);
    }

    protected void finishPreparation(JdbcServices jdbcServices, JdbcConnectionAccess connectionAccess, MetadataImplementor metadata, CT context) {
    }

    protected TT getIdTableInfo(Queryable targetedPersister) {
        return this.getIdTableInfo(targetedPersister.getEntityName());
    }

    protected TT getIdTableInfo(String entityName) {
        IdTableInfo tableInfo = (IdTableInfo)this.idTableInfoMap.get(entityName);
        if (tableInfo == null) {
            throw new QueryException("Entity does not have an id table for multi-table handling : " + entityName);
        }
        return (TT)tableInfo;
    }

    public static interface PreparationContext {
    }
}

