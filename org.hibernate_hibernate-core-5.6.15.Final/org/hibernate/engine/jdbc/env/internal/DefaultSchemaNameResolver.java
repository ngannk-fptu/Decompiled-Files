/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.engine.jdbc.env.internal;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.hibernate.HibernateException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.env.spi.SchemaNameResolver;
import org.jboss.logging.Logger;

public class DefaultSchemaNameResolver
implements SchemaNameResolver {
    private static final Logger log = Logger.getLogger(DefaultSchemaNameResolver.class);
    public static final DefaultSchemaNameResolver INSTANCE = new DefaultSchemaNameResolver();

    private DefaultSchemaNameResolver() {
    }

    private SchemaNameResolver determineAppropriateResolverDelegate(Connection connection) {
        try {
            Class<?> jdbcConnectionClass = connection.getClass();
            Method getSchemaMethod = jdbcConnectionClass.getMethod("getSchema", new Class[0]);
            if (getSchemaMethod != null && getSchemaMethod.getReturnType().equals(String.class)) {
                try {
                    connection.getSchema();
                    return new SchemaNameResolverJava17Delegate();
                }
                catch (AbstractMethodError e) {
                    log.debugf("Unable to use Java 1.7 Connection#getSchema", new Object[0]);
                    return SchemaNameResolverFallbackDelegate.INSTANCE;
                }
            }
            log.debugf("Unable to use Java 1.7 Connection#getSchema", new Object[0]);
            return SchemaNameResolverFallbackDelegate.INSTANCE;
        }
        catch (Exception ignore) {
            log.debugf("Unable to use Java 1.7 Connection#getSchema : An error occurred trying to resolve the connection default schema resolver: " + ignore.getMessage(), new Object[0]);
            return SchemaNameResolverFallbackDelegate.INSTANCE;
        }
    }

    @Override
    public String resolveSchemaName(Connection connection, Dialect dialect) throws SQLException {
        SchemaNameResolver delegate = this.determineAppropriateResolverDelegate(connection);
        return delegate.resolveSchemaName(connection, dialect);
    }

    public static class SchemaNameResolverFallbackDelegate
    implements SchemaNameResolver {
        public static final SchemaNameResolverFallbackDelegate INSTANCE = new SchemaNameResolverFallbackDelegate();

        @Override
        public String resolveSchemaName(Connection connection, Dialect dialect) throws SQLException {
            String command = dialect.getCurrentSchemaCommand();
            if (command == null) {
                throw new HibernateException("Use of DefaultSchemaNameResolver requires Dialect to provide the proper SQL statement/command but provided Dialect [" + dialect.getClass().getName() + "] did not return anything from Dialect#getCurrentSchemaCommand");
            }
            try (Statement statement = connection.createStatement();){
                String string;
                block13: {
                    ResultSet resultSet = statement.executeQuery(dialect.getCurrentSchemaCommand());
                    try {
                        String string2 = string = resultSet.next() ? resultSet.getString(1) : null;
                        if (resultSet == null) break block13;
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
                return string;
            }
        }
    }

    public static class SchemaNameResolverJava17Delegate
    implements SchemaNameResolver {
        @Override
        public String resolveSchemaName(Connection connection, Dialect dialect) throws SQLException {
            return connection.getSchema();
        }
    }
}

