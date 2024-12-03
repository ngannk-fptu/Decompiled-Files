/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.BasicLogger
 *  org.jboss.logging.Logger
 *  org.jboss.logging.Logger$Level
 *  org.jboss.logging.annotations.Cause
 *  org.jboss.logging.annotations.LogMessage
 *  org.jboss.logging.annotations.Message
 *  org.jboss.logging.annotations.MessageLogger
 *  org.jboss.logging.annotations.ValidIdRange
 */
package org.hibernate.internal.log;

import java.sql.SQLException;
import java.util.Properties;
import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;
import org.jboss.logging.annotations.ValidIdRange;

@MessageLogger(projectCode="HHH")
@ValidIdRange(min=10001001, max=10001500)
public interface ConnectionPoolingLogger
extends BasicLogger {
    public static final ConnectionPoolingLogger CONNECTIONS_LOGGER = (ConnectionPoolingLogger)Logger.getMessageLogger(ConnectionPoolingLogger.class, (String)"org.hibernate.orm.connections.pooling");

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Connection properties: %s", id=10001001)
    public void connectionProperties(Properties var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Using Hibernate built-in connection pool (not for production use!)", id=10001002)
    public void usingHibernateBuiltInConnectionPool();

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Autocommit mode: %s", id=10001003)
    public void autoCommitMode(boolean var1);

    @Message(value="JDBC URL was not specified by property %s", id=10001004)
    public String jdbcUrlNotSpecified(String var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="using driver [%s] at URL [%s]", id=10001005)
    public void usingDriver(String var1, String var2);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="No JDBC Driver class was specified by property %s", id=10001006)
    public void jdbcDriverNotSpecified(String var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="JDBC isolation level: %s", id=10001007)
    public void jdbcIsolationLevel(String var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Cleaning up connection pool [%s]", id=10001008)
    public void cleaningUpConnectionPool(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Problem closing pooled connection", id=10001009)
    public void unableToClosePooledConnection(@Cause SQLException var1);
}

