/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package org.postgresql.core;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.postgresql.PGProperty;
import org.postgresql.core.PGStream;
import org.postgresql.core.QueryExecutor;
import org.postgresql.core.v3.ConnectionFactoryImpl;
import org.postgresql.util.GT;
import org.postgresql.util.HostSpec;
import org.postgresql.util.PSQLException;
import org.postgresql.util.PSQLState;

public abstract class ConnectionFactory {
    private static final Logger LOGGER = Logger.getLogger(ConnectionFactory.class.getName());

    public static QueryExecutor openConnection(HostSpec[] hostSpecs, Properties info) throws SQLException {
        ConnectionFactoryImpl connectionFactory;
        QueryExecutor queryExecutor;
        String protoName = PGProperty.PROTOCOL_VERSION.getOrDefault(info);
        if ((protoName == null || protoName.isEmpty() || "3".equals(protoName)) && (queryExecutor = ((ConnectionFactory)(connectionFactory = new ConnectionFactoryImpl())).openConnectionImpl(hostSpecs, info)) != null) {
            return queryExecutor;
        }
        throw new PSQLException(GT.tr("A connection could not be made using the requested protocol {0}.", protoName), PSQLState.CONNECTION_UNABLE_TO_CONNECT);
    }

    public abstract QueryExecutor openConnectionImpl(HostSpec[] var1, Properties var2) throws SQLException;

    protected void closeStream(@Nullable PGStream newStream) {
        if (newStream != null) {
            try {
                newStream.close();
            }
            catch (IOException e) {
                LOGGER.log(Level.WARNING, "Failed to closed stream with error: {0}", e);
            }
        }
    }
}

