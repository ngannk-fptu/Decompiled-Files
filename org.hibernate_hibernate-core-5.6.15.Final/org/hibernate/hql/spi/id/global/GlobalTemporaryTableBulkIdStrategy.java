/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.spi.id.global;

import java.sql.PreparedStatement;
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
import org.hibernate.hql.spi.id.IdTableInfo;
import org.hibernate.hql.spi.id.IdTableSupport;
import org.hibernate.hql.spi.id.IdTableSupportStandardImpl;
import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
import org.hibernate.hql.spi.id.TableBasedDeleteHandlerImpl;
import org.hibernate.hql.spi.id.TableBasedUpdateHandlerImpl;
import org.hibernate.hql.spi.id.global.IdTableInfoImpl;
import org.hibernate.hql.spi.id.global.PreparationContextImpl;
import org.hibernate.hql.spi.id.local.AfterUseAction;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Table;
import org.hibernate.persister.entity.Queryable;
import org.hibernate.service.ServiceRegistry;

public class GlobalTemporaryTableBulkIdStrategy
extends AbstractMultiTableBulkIdStrategyImpl<IdTableInfoImpl, PreparationContextImpl>
implements MultiTableBulkIdStrategy {
    public static final String DROP_ID_TABLES = "hibernate.hql.bulk_id_strategy.global_temporary.drop_tables";
    public static final String SHORT_NAME = "global_temporary";
    private final AfterUseAction afterUseAction;
    private ServiceRegistry serviceRegistry;
    private boolean dropIdTables;
    private String[] dropTableStatements;

    public GlobalTemporaryTableBulkIdStrategy() {
        this(AfterUseAction.CLEAN);
    }

    public GlobalTemporaryTableBulkIdStrategy(AfterUseAction afterUseAction) {
        this(new IdTableSupportStandardImpl(){

            @Override
            public String getCreateIdTableCommand() {
                return "create global temporary table";
            }

            @Override
            public String getDropIdTableCommand() {
                return super.getDropIdTableCommand();
            }
        }, afterUseAction);
    }

    public GlobalTemporaryTableBulkIdStrategy(IdTableSupport idTableSupport, AfterUseAction afterUseAction) {
        super(idTableSupport);
        this.afterUseAction = afterUseAction;
        if (afterUseAction == AfterUseAction.DROP) {
            throw new IllegalArgumentException("DROP not supported as a after-use action for global temp table strategy");
        }
    }

    @Override
    protected PreparationContextImpl buildPreparationContext() {
        return new PreparationContextImpl();
    }

    @Override
    protected void initialize(MetadataBuildingOptions buildingOptions, SessionFactoryOptions sessionFactoryOptions) {
        StandardServiceRegistry serviceRegistry = buildingOptions.getServiceRegistry();
        ConfigurationService configService = serviceRegistry.getService(ConfigurationService.class);
        this.dropIdTables = configService.getSetting(DROP_ID_TABLES, StandardConverters.BOOLEAN, Boolean.valueOf(false));
    }

    @Override
    protected IdTableInfoImpl buildIdTableInfo(PersistentClass entityBinding, Table idTable, JdbcServices jdbcServices, MetadataImplementor metadata, PreparationContextImpl context, SqlStringGenerationContext sqlStringGenerationContext) {
        context.creationStatements.add(this.buildIdTableCreateStatement(idTable, metadata, sqlStringGenerationContext));
        if (this.dropIdTables) {
            context.dropStatements.add(this.buildIdTableDropStatement(idTable, sqlStringGenerationContext));
        }
        String renderedName = sqlStringGenerationContext.formatWithoutDefaults(idTable.getQualifiedTableName());
        return new IdTableInfoImpl(renderedName);
    }

    @Override
    protected void finishPreparation(JdbcServices jdbcServices, JdbcConnectionAccess connectionAccess, MetadataImplementor metadata, PreparationContextImpl context) {
        this.serviceRegistry = metadata.getDatabase().getServiceRegistry();
        IdTableHelper.INSTANCE.executeIdTableCreationStatements(context.creationStatements, jdbcServices, connectionAccess, this.serviceRegistry);
        this.dropTableStatements = this.dropIdTables ? context.dropStatements.toArray(new String[context.dropStatements.size()]) : null;
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
        return new TableBasedUpdateHandlerImpl(factory, walker, (IdTableInfo)this.getIdTableInfo(targetedPersister)){

            @Override
            protected void releaseFromUse(Queryable persister, SharedSessionContractImplementor session) {
                if (GlobalTemporaryTableBulkIdStrategy.this.afterUseAction == AfterUseAction.NONE) {
                    return;
                }
                GlobalTemporaryTableBulkIdStrategy.this.cleanUpRows(((IdTableInfoImpl)GlobalTemporaryTableBulkIdStrategy.this.getIdTableInfo(persister)).getQualifiedIdTableName(), session);
            }
        };
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void cleanUpRows(String tableName, SharedSessionContractImplementor session) {
        String sql = this.getIdTableSupport().getTruncateIdTableCommand() + " " + tableName;
        PreparedStatement ps = null;
        try {
            ps = session.getJdbcCoordinator().getStatementPreparer().prepareStatement(sql, false);
            session.getJdbcCoordinator().getResultSetReturn().executeUpdate(ps);
        }
        finally {
            if (ps != null) {
                try {
                    session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release(ps);
                }
                catch (Throwable throwable) {}
            }
        }
    }

    @Override
    public MultiTableBulkIdStrategy.DeleteHandler buildDeleteHandler(SessionFactoryImplementor factory, HqlSqlWalker walker) {
        DeleteStatement updateStatement = (DeleteStatement)walker.getAST();
        FromElement fromElement = updateStatement.getFromClause().getFromElement();
        Queryable targetedPersister = fromElement.getQueryable();
        return new TableBasedDeleteHandlerImpl(factory, walker, (IdTableInfo)this.getIdTableInfo(targetedPersister)){

            @Override
            protected void releaseFromUse(Queryable persister, SharedSessionContractImplementor session) {
                if (GlobalTemporaryTableBulkIdStrategy.this.afterUseAction == AfterUseAction.NONE) {
                    return;
                }
                GlobalTemporaryTableBulkIdStrategy.this.cleanUpRows(((IdTableInfoImpl)GlobalTemporaryTableBulkIdStrategy.this.getIdTableInfo(persister)).getQualifiedIdTableName(), session);
            }
        };
    }
}

