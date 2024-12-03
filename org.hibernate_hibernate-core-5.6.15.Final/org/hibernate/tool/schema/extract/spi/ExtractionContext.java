/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.extract.spi;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.hibernate.Incubating;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.relational.QualifiedSequenceName;
import org.hibernate.boot.model.relational.QualifiedTableName;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.schema.extract.spi.SequenceInformation;
import org.hibernate.tool.schema.extract.spi.TableInformation;

@Incubating
public interface ExtractionContext {
    public ServiceRegistry getServiceRegistry();

    public JdbcEnvironment getJdbcEnvironment();

    public SqlStringGenerationContext getSqlStringGenerationContext();

    public Connection getJdbcConnection();

    public DatabaseMetaData getJdbcDatabaseMetaData();

    @Incubating
    default public <T> T getQueryResults(String queryString, Object[] positionalParameters, ResultSetProcessor<T> resultSetProcessor) throws SQLException {
        try (PreparedStatement statement = this.getJdbcConnection().prepareStatement(queryString);){
            T t;
            block14: {
                if (positionalParameters != null) {
                    for (int i = 0; i < positionalParameters.length; ++i) {
                        statement.setObject(i + 1, positionalParameters[i]);
                    }
                }
                ResultSet resultSet = statement.executeQuery();
                try {
                    t = resultSetProcessor.process(resultSet);
                    if (resultSet == null) break block14;
                }
                catch (Throwable throwable) {
                    if (resultSet != null) {
                        try {
                            resultSet.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                resultSet.close();
            }
            return t;
        }
    }

    public Identifier getDefaultCatalog();

    public Identifier getDefaultSchema();

    public DatabaseObjectAccess getDatabaseObjectAccess();

    public void cleanup();

    public static abstract class EmptyExtractionContext
    implements ExtractionContext {
        @Override
        public ServiceRegistry getServiceRegistry() {
            return null;
        }

        @Override
        public JdbcEnvironment getJdbcEnvironment() {
            return null;
        }

        @Override
        public SqlStringGenerationContext getSqlStringGenerationContext() {
            return null;
        }

        @Override
        public Connection getJdbcConnection() {
            return null;
        }

        @Override
        public DatabaseMetaData getJdbcDatabaseMetaData() {
            return null;
        }

        @Override
        public Identifier getDefaultCatalog() {
            return null;
        }

        @Override
        public Identifier getDefaultSchema() {
            return null;
        }

        @Override
        public DatabaseObjectAccess getDatabaseObjectAccess() {
            return null;
        }

        @Override
        public void cleanup() {
        }
    }

    @Incubating
    public static interface DatabaseObjectAccess {
        public TableInformation locateTableInformation(QualifiedTableName var1);

        public SequenceInformation locateSequenceInformation(QualifiedSequenceName var1);
    }

    @Incubating
    public static interface ResultSetProcessor<T> {
        public T process(ResultSet var1) throws SQLException;
    }
}

