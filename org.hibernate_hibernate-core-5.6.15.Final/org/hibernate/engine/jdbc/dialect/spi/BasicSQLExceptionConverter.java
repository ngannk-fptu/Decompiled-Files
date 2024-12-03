/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.dialect.spi;

import java.sql.SQLException;
import org.hibernate.JDBCException;
import org.hibernate.exception.internal.SQLStateConverter;
import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;

public class BasicSQLExceptionConverter {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(BasicSQLExceptionConverter.class);
    public static final BasicSQLExceptionConverter INSTANCE = new BasicSQLExceptionConverter();
    public static final String MSG = LOG.unableToQueryDatabaseMetadata();
    private static final SQLStateConverter CONVERTER = new SQLStateConverter(new ConstraintNameExtracter());

    public JDBCException convert(SQLException sqlException) {
        return CONVERTER.convert(sqlException, MSG, null);
    }

    private static class ConstraintNameExtracter
    implements ViolatedConstraintNameExtracter {
        private ConstraintNameExtracter() {
        }

        @Override
        public String extractConstraintName(SQLException sqle) {
            return "???";
        }
    }
}

