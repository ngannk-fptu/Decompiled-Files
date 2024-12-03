/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.spi.id.local;

import org.hibernate.boot.TempTableDdlTransactionHandling;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.spi.MetadataBuildingOptions;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.config.spi.StandardConverters;
import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.hql.internal.ast.HqlSqlWalker;
import org.hibernate.hql.internal.ast.tree.DeleteStatement;
import org.hibernate.hql.internal.ast.tree.FromElement;
import org.hibernate.hql.internal.ast.tree.UpdateStatement;
import org.hibernate.hql.spi.id.AbstractMultiTableBulkIdStrategyImpl;
import org.hibernate.hql.spi.id.IdTableHelper;
import org.hibernate.hql.spi.id.IdTableSupport;
import org.hibernate.hql.spi.id.IdTableSupportStandardImpl;
import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
import org.hibernate.hql.spi.id.TableBasedDeleteHandlerImpl;
import org.hibernate.hql.spi.id.TableBasedUpdateHandlerImpl;
import org.hibernate.hql.spi.id.local.AfterUseAction;
import org.hibernate.hql.spi.id.local.Helper;
import org.hibernate.hql.spi.id.local.IdTableInfoImpl;
import org.hibernate.hql.spi.id.local.PreparationContextImpl;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Table;
import org.hibernate.persister.entity.Queryable;
import org.hibernate.service.ServiceRegistry;

public class LocalTemporaryTableBulkIdStrategy
extends AbstractMultiTableBulkIdStrategyImpl<IdTableInfoImpl, PreparationContextImpl>
implements MultiTableBulkIdStrategy {
    public static final String DROP_ID_TABLES = "hibernate.hql.bulk_id_strategy.local_temporary.drop_tables";
    public static final String SHORT_NAME = "local_temporary";
    private final AfterUseAction afterUseAction;
    private TempTableDdlTransactionHandling ddlTransactionHandling;
    private ServiceRegistry serviceRegistry;
    private boolean dropIdTables;
    private String[] dropTableStatements;

    public LocalTemporaryTableBulkIdStrategy() {
        this(new IdTableSupportStandardImpl(){

            @Override
            public String getCreateIdTableCommand() {
                return "create local temporary table";
            }
        }, AfterUseAction.DROP, null);
    }

    public LocalTemporaryTableBulkIdStrategy(IdTableSupport idTableSupport, AfterUseAction afterUseAction, TempTableDdlTransactionHandling ddlTransactionHandling) {
        super(idTableSupport);
        this.afterUseAction = afterUseAction;
        this.ddlTransactionHandling = ddlTransactionHandling;
    }

    @Override
    protected PreparationContextImpl buildPreparationContext() {
        return new PreparationContextImpl();
    }

    @Override
    protected void initialize(MetadataBuildingOptions buildingOptions, SessionFactoryOptions sessionFactoryOptions) {
        if (this.ddlTransactionHandling == null) {
            this.ddlTransactionHandling = sessionFactoryOptions.getTempTableDdlTransactionHandling();
        }
        StandardServiceRegistry serviceRegistry = buildingOptions.getServiceRegistry();
        ConfigurationService configService = serviceRegistry.getService(ConfigurationService.class);
        this.dropIdTables = configService.getSetting(DROP_ID_TABLES, StandardConverters.BOOLEAN, Boolean.valueOf(false));
    }

    @Override
    protected void finishPreparation(JdbcServices jdbcServices, JdbcConnectionAccess connectionAccess, MetadataImplementor metadata, PreparationContextImpl context) {
        this.serviceRegistry = metadata.getDatabase().getServiceRegistry();
        this.dropTableStatements = this.dropIdTables ? context.dropStatements.toArray(new String[context.dropStatements.size()]) : null;
    }

    @Override
    protected IdTableInfoImpl buildIdTableInfo(PersistentClass entityBinding, Table idTable, JdbcServices jdbcServices, MetadataImplementor metadata, PreparationContextImpl context, SqlStringGenerationContext sqlStringGenerationContext) {
        String dropStatement = this.buildIdTableDropStatement(idTable, sqlStringGenerationContext);
        if (this.dropIdTables) {
            context.dropStatements.add(dropStatement);
        }
        return new IdTableInfoImpl(sqlStringGenerationContext.formatWithoutDefaults(idTable.getQualifiedTableName()), this.buildIdTableCreateStatement(idTable, metadata, sqlStringGenerationContext), dropStatement);
    }

    @Override
    public void release(JdbcServices jdbcServices, JdbcConnectionAccess connectionAccess) {
        if (!this.dropIdTables) {
            return;
        }
        IdTableHelper.INSTANCE.executeIdTableDropStatements(this.dropTableStatements, jdbcServices, connectionAccess, this.serviceRegistry);
    }

    @Override
    public MultiTableBulkIdStrategy.UpdateHandler buildUpdateHandler(SessionFactoryImplementor factory, HqlSqlWalker walker) {
        UpdateStatement updateStatement = (UpdateStatement)walker.getAST();
        FromElement fromElement = updateStatement.getFromClause().getFromElement();
        Queryable targetedPersister = fromElement.getQueryable();
        final IdTableInfoImpl tableInfo = (IdTableInfoImpl)this.getIdTableInfo(targetedPersister);
        return new TableBasedUpdateHandlerImpl(factory, walker, tableInfo){

            @Override
            protected void prepareForUse(Queryable persister, SharedSessionContractImplementor session) {
                Helper.INSTANCE.createTempTable(tableInfo, LocalTemporaryTableBulkIdStrategy.this.ddlTransactionHandling, session);
            }

            @Override
            protected void releaseFromUse(Queryable persister, SharedSessionContractImplementor session) {
                Helper.INSTANCE.releaseTempTable(tableInfo, LocalTemporaryTableBulkIdStrategy.this.dropIdTables ? AfterUseAction.DROP : LocalTemporaryTableBulkIdStrategy.this.afterUseAction, LocalTemporaryTableBulkIdStrategy.this.ddlTransactionHandling, session);
            }
        };
    }

    @Override
    public MultiTableBulkIdStrategy.DeleteHandler buildDeleteHandler(SessionFactoryImplementor factory, HqlSqlWalker walker) {
        DeleteStatement updateStatement = (DeleteStatement)walker.getAST();
        FromElement fromElement = updateStatement.getFromClause().getFromElement();
        Queryable targetedPersister = fromElement.getQueryable();
        final IdTableInfoImpl tableInfo = (IdTableInfoImpl)this.getIdTableInfo(targetedPersister);
        return new TableBasedDeleteHandlerImpl(factory, walker, tableInfo){

            @Override
            protected void prepareForUse(Queryable persister, SharedSessionContractImplementor session) {
                Helper.INSTANCE.createTempTable(tableInfo, LocalTemporaryTableBulkIdStrategy.this.ddlTransactionHandling, session);
            }

            @Override
            protected void releaseFromUse(Queryable persister, SharedSessionContractImplementor session) {
                Helper.INSTANCE.releaseTempTable(tableInfo, LocalTemporaryTableBulkIdStrategy.this.dropIdTables ? AfterUseAction.DROP : LocalTemporaryTableBulkIdStrategy.this.afterUseAction, LocalTemporaryTableBulkIdStrategy.this.ddlTransactionHandling, session);
            }
        };
    }
}

