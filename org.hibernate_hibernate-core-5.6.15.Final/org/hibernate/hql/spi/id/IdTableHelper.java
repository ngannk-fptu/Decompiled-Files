/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.hql.spi.id;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
import org.hibernate.mapping.JoinedSubclass;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Subclass;
import org.hibernate.mapping.UnionSubclass;
import org.hibernate.resource.transaction.spi.DdlTransactionIsolator;
import org.hibernate.resource.transaction.spi.TransactionCoordinatorBuilder;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.schema.internal.exec.GenerationTargetToDatabase;
import org.hibernate.tool.schema.internal.exec.JdbcContext;
import org.hibernate.tool.schema.spi.CommandAcceptanceException;
import org.jboss.logging.Logger;

public class IdTableHelper {
    private static final Logger log = Logger.getLogger(IdTableHelper.class);
    public static final IdTableHelper INSTANCE = new IdTableHelper();

    private IdTableHelper() {
    }

    public boolean needsIdTable(PersistentClass entityBinding) {
        Subclass subclassEntityBinding;
        if (entityBinding.getJoinClosureSpan() > 0) {
            return true;
        }
        RootClass rootEntityBinding = entityBinding.getRootClass();
        Iterator itr = rootEntityBinding.getSubclassIterator();
        return itr.hasNext() && ((subclassEntityBinding = (Subclass)itr.next()) instanceof JoinedSubclass || subclassEntityBinding instanceof UnionSubclass);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void executeIdTableCreationStatements(List<String> creationStatements, JdbcServices jdbcServices, JdbcConnectionAccess connectionAccess) {
        if (creationStatements == null || creationStatements.isEmpty()) {
            return;
        }
        try {
            Connection connection;
            try {
                connection = connectionAccess.obtainConnection();
            }
            catch (UnsupportedOperationException e) {
                log.debug((Object)"Unable to obtain JDBC connection; assuming ID tables already exist or wont be needed");
                return;
            }
            try {
                Statement statement = connection.createStatement();
                for (String creationStatement : creationStatements) {
                    try {
                        jdbcServices.getSqlStatementLogger().logStatement(creationStatement);
                        statement.execute(creationStatement);
                    }
                    catch (SQLException e) {
                        log.debugf("Error attempting to export id-table [%s] : %s", (Object)creationStatement, (Object)e.getMessage());
                    }
                }
                statement.close();
            }
            catch (SQLException e) {
                log.error((Object)"Unable to use JDBC Connection to create Statement", (Throwable)e);
            }
            finally {
                try {
                    connectionAccess.releaseConnection(connection);
                }
                catch (SQLException sQLException) {}
            }
        }
        catch (SQLException e) {
            log.error((Object)"Unable obtain JDBC Connection", (Throwable)e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void executeIdTableDropStatements(String[] dropStatements, JdbcServices jdbcServices, JdbcConnectionAccess connectionAccess) {
        if (dropStatements == null || dropStatements.length == 0) {
            return;
        }
        try {
            Connection connection = connectionAccess.obtainConnection();
            try (Statement statement = connection.createStatement();){
                for (String dropStatement : dropStatements) {
                    try {
                        jdbcServices.getSqlStatementLogger().logStatement(dropStatement);
                        statement.execute(dropStatement);
                    }
                    catch (SQLException e) {
                        log.debugf("Error attempting to cleanup id-table : [%s]", (Object)e.getMessage());
                    }
                }
            }
            catch (SQLException e) {
                log.error((Object)"Unable to use JDBC Connection to create Statement", (Throwable)e);
            }
            finally {
                try {
                    connectionAccess.releaseConnection(connection);
                }
                catch (SQLException sQLException) {}
            }
        }
        catch (SQLException e) {
            log.error((Object)"Unable obtain JDBC Connection", (Throwable)e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void executeIdTableCreationStatements(List<String> creationStatements, JdbcServices jdbcServices, JdbcConnectionAccess connectionAccess, ServiceRegistry serviceRegistry) {
        if (creationStatements == null || creationStatements.isEmpty()) {
            return;
        }
        GenerationTargetToDatabase target = new GenerationTargetToDatabase(this.getDdlTransactionIsolator(jdbcServices, connectionAccess, serviceRegistry));
        try {
            for (String createStatement : creationStatements) {
                try {
                    target.accept(createStatement);
                }
                catch (CommandAcceptanceException e) {
                    log.debugf("Error attempting to export id-table [%s] : %s", (Object)createStatement, (Object)e.getMessage());
                }
            }
        }
        finally {
            target.release();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void executeIdTableDropStatements(String[] dropStatements, JdbcServices jdbcServices, JdbcConnectionAccess connectionAccess, ServiceRegistry serviceRegistry) {
        if (dropStatements == null || dropStatements.length == 0) {
            return;
        }
        GenerationTargetToDatabase target = new GenerationTargetToDatabase(this.getDdlTransactionIsolator(jdbcServices, connectionAccess, serviceRegistry));
        try {
            for (String dropStatement : dropStatements) {
                try {
                    target.accept(dropStatement);
                }
                catch (CommandAcceptanceException e) {
                    log.debugf("Error attempting to drop id-table : [%s]", (Object)e.getMessage());
                }
            }
        }
        finally {
            target.release();
        }
    }

    public DdlTransactionIsolator getDdlTransactionIsolator(final JdbcServices jdbcServices, final JdbcConnectionAccess connectionAccess, final ServiceRegistry serviceRegistry) {
        return serviceRegistry.getService(TransactionCoordinatorBuilder.class).buildDdlTransactionIsolator(new JdbcContext(){

            @Override
            public JdbcConnectionAccess getJdbcConnectionAccess() {
                return connectionAccess;
            }

            @Override
            public Dialect getDialect() {
                return jdbcServices.getJdbcEnvironment().getDialect();
            }

            @Override
            public SqlStatementLogger getSqlStatementLogger() {
                return jdbcServices.getSqlStatementLogger();
            }

            @Override
            public SqlExceptionHelper getSqlExceptionHelper() {
                return jdbcServices.getSqlExceptionHelper();
            }

            @Override
            public ServiceRegistry getServiceRegistry() {
                return serviceRegistry;
            }
        });
    }
}

