/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.spi.id.persistent;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.relational.QualifiedTableName;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.spi.MetadataBuildingOptions;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.config.spi.StandardConverters;
import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.hql.internal.ast.HqlSqlWalker;
import org.hibernate.hql.internal.ast.tree.DeleteStatement;
import org.hibernate.hql.internal.ast.tree.FromElement;
import org.hibernate.hql.internal.ast.tree.UpdateStatement;
import org.hibernate.hql.spi.id.AbstractMultiTableBulkIdStrategyImpl;
import org.hibernate.hql.spi.id.IdTableHelper;
import org.hibernate.hql.spi.id.IdTableInfo;
import org.hibernate.hql.spi.id.IdTableSupport;
import org.hibernate.hql.spi.id.IdTableSupportStandardImpl;
import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
import org.hibernate.hql.spi.id.persistent.DeleteHandlerImpl;
import org.hibernate.hql.spi.id.persistent.IdTableInfoImpl;
import org.hibernate.hql.spi.id.persistent.PreparationContextImpl;
import org.hibernate.hql.spi.id.persistent.UpdateHandlerImpl;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Table;
import org.hibernate.persister.entity.Queryable;

public class PersistentTableBulkIdStrategy
extends AbstractMultiTableBulkIdStrategyImpl<IdTableInfoImpl, PreparationContextImpl>
implements MultiTableBulkIdStrategy {
    public static final String SHORT_NAME = "persistent";
    public static final String DROP_ID_TABLES = "hibernate.hql.bulk_id_strategy.persistent.drop_tables";
    public static final String SCHEMA = "hibernate.hql.bulk_id_strategy.persistent.schema";
    public static final String CATALOG = "hibernate.hql.bulk_id_strategy.persistent.catalog";
    private Identifier catalog;
    private Identifier schema;
    private boolean dropIdTables;
    private String[] dropTableStatements;

    public PersistentTableBulkIdStrategy() {
        this(IdTableSupportStandardImpl.INSTANCE);
    }

    public PersistentTableBulkIdStrategy(IdTableSupport idTableSupport) {
        super(idTableSupport);
    }

    @Override
    protected PreparationContextImpl buildPreparationContext() {
        return new PreparationContextImpl();
    }

    @Override
    protected void initialize(MetadataBuildingOptions buildingOptions, SessionFactoryOptions sessionFactoryOptions) {
        StandardServiceRegistry serviceRegistry = buildingOptions.getServiceRegistry();
        JdbcEnvironment jdbcEnvironment = serviceRegistry.getService(JdbcEnvironment.class);
        ConfigurationService configService = serviceRegistry.getService(ConfigurationService.class);
        String catalogName = configService.getSetting(CATALOG, StandardConverters.STRING, configService.getSetting("hibernate.default_catalog", StandardConverters.STRING));
        String schemaName = configService.getSetting(SCHEMA, StandardConverters.STRING, configService.getSetting("hibernate.default_schema", StandardConverters.STRING));
        this.catalog = jdbcEnvironment.getIdentifierHelper().toIdentifier(catalogName);
        this.schema = jdbcEnvironment.getIdentifierHelper().toIdentifier(schemaName);
        this.dropIdTables = configService.getSetting(DROP_ID_TABLES, StandardConverters.BOOLEAN, Boolean.valueOf(false));
    }

    @Override
    protected QualifiedTableName determineIdTableName(JdbcEnvironment jdbcEnvironment, PersistentClass entityBinding) {
        return new QualifiedTableName(this.catalog, this.schema, super.determineIdTableName(jdbcEnvironment, entityBinding).getTableName());
    }

    @Override
    protected void augmentIdTableDefinition(Table idTable) {
        Column sessionIdColumn = new Column("hib_sess_id");
        sessionIdColumn.setSqlType("CHAR(36)");
        sessionIdColumn.setSqlTypeCode(12);
        sessionIdColumn.setComment("Used to hold the Hibernate Session identifier");
        idTable.addColumn(sessionIdColumn);
    }

    @Override
    protected IdTableInfoImpl buildIdTableInfo(PersistentClass entityBinding, Table idTable, JdbcServices jdbcServices, MetadataImplementor metadata, PreparationContextImpl context, SqlStringGenerationContext sqlStringGenerationContext) {
        String renderedName = sqlStringGenerationContext.formatWithoutDefaults(idTable.getQualifiedTableName());
        context.creationStatements.add(this.buildIdTableCreateStatement(idTable, metadata, sqlStringGenerationContext));
        if (this.dropIdTables) {
            context.dropStatements.add(this.buildIdTableDropStatement(idTable, sqlStringGenerationContext));
        }
        return new IdTableInfoImpl(renderedName);
    }

    @Override
    protected void finishPreparation(JdbcServices jdbcServices, JdbcConnectionAccess connectionAccess, MetadataImplementor metadata, PreparationContextImpl context) {
        IdTableHelper.INSTANCE.executeIdTableCreationStatements(context.creationStatements, jdbcServices, connectionAccess);
        this.dropTableStatements = this.dropIdTables ? context.dropStatements.toArray(new String[context.dropStatements.size()]) : null;
    }

    @Override
    public MultiTableBulkIdStrategy.UpdateHandler buildUpdateHandler(SessionFactoryImplementor factory, HqlSqlWalker walker) {
        UpdateStatement updateStatement = (UpdateStatement)walker.getAST();
        FromElement fromElement = updateStatement.getFromClause().getFromElement();
        Queryable targetedPersister = fromElement.getQueryable();
        return new UpdateHandlerImpl(factory, walker, (IdTableInfo)this.getIdTableInfo(targetedPersister));
    }

    @Override
    public MultiTableBulkIdStrategy.DeleteHandler buildDeleteHandler(SessionFactoryImplementor factory, HqlSqlWalker walker) {
        DeleteStatement updateStatement = (DeleteStatement)walker.getAST();
        FromElement fromElement = updateStatement.getFromClause().getFromElement();
        Queryable targetedPersister = fromElement.getQueryable();
        return new DeleteHandlerImpl(factory, walker, (IdTableInfo)this.getIdTableInfo(targetedPersister));
    }

    @Override
    public void release(JdbcServices jdbcServices, JdbcConnectionAccess connectionAccess) {
        if (!this.dropIdTables) {
            return;
        }
        IdTableHelper.INSTANCE.executeIdTableDropStatements(this.dropTableStatements, jdbcServices, connectionAccess);
    }
}

