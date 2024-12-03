/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.exception.spi;

import java.lang.reflect.Constructor;
import java.sql.SQLException;
import java.util.Properties;
import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.hibernate.dialect.Dialect;
import org.hibernate.exception.GenericJDBCException;
import org.hibernate.exception.spi.Configurable;
import org.hibernate.exception.spi.SQLExceptionConverter;
import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.internal.util.StringHelper;
import org.jboss.logging.Logger;

public class SQLExceptionConverterFactory {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)SQLExceptionConverterFactory.class.getName());

    private SQLExceptionConverterFactory() {
    }

    public static SQLExceptionConverter buildSQLExceptionConverter(Dialect dialect, Properties properties) throws HibernateException {
        SQLExceptionConverter converter = null;
        String converterClassName = (String)properties.get("hibernate.jdbc.sql_exception_converter");
        if (StringHelper.isNotEmpty(converterClassName)) {
            converter = SQLExceptionConverterFactory.constructConverter(converterClassName, dialect.getViolatedConstraintNameExtracter());
        }
        if (converter == null) {
            LOG.trace("Using dialect defined converter");
            converter = dialect.buildSQLExceptionConverter();
        }
        if (converter instanceof Configurable) {
            try {
                ((Configurable)((Object)converter)).configure(properties);
            }
            catch (HibernateException e) {
                LOG.unableToConfigureSqlExceptionConverter(e);
                throw e;
            }
        }
        return converter;
    }

    public static SQLExceptionConverter buildMinimalSQLExceptionConverter() {
        return new SQLExceptionConverter(){

            @Override
            public JDBCException convert(SQLException sqlException, String message, String sql) {
                return new GenericJDBCException(message, sqlException, sql);
            }
        };
    }

    private static SQLExceptionConverter constructConverter(String converterClassName, ViolatedConstraintNameExtracter violatedConstraintNameExtracter) {
        try {
            Constructor<?>[] ctors;
            LOG.tracev("Attempting to construct instance of specified SQLExceptionConverter [{0}]", converterClassName);
            Class converterClass = ReflectHelper.classForName(converterClassName);
            for (Constructor<?> ctor : ctors = converterClass.getDeclaredConstructors()) {
                Class<?>[] parameterTypes = ctor.getParameterTypes();
                if (parameterTypes == null || ctor.getParameterCount() != 1 || !ViolatedConstraintNameExtracter.class.isAssignableFrom(parameterTypes[0])) continue;
                try {
                    return (SQLExceptionConverter)ctor.newInstance(violatedConstraintNameExtracter);
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            }
            return (SQLExceptionConverter)converterClass.newInstance();
        }
        catch (Throwable t) {
            LOG.unableToConstructSqlExceptionConverter(t);
            return null;
        }
    }
}

