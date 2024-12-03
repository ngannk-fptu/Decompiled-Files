/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.BasicLogger
 *  org.jboss.logging.Logger
 *  org.jboss.logging.Logger$Level
 *  org.jboss.logging.annotations.LogMessage
 *  org.jboss.logging.annotations.Message
 *  org.jboss.logging.annotations.MessageLogger
 *  org.jboss.logging.annotations.ValidIdRange
 */
package org.hibernate.internal.log;

import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;
import org.jboss.logging.annotations.ValidIdRange;

@MessageLogger(projectCode="HHH")
@ValidIdRange(min=10001501, max=10002000)
public interface ConnectionAccessLogger
extends BasicLogger {
    public static final String LOGGER_NAME = "org.hibernate.orm.connections.access";
    public static final ConnectionAccessLogger INSTANCE = (ConnectionAccessLogger)Logger.getMessageLogger(ConnectionAccessLogger.class, (String)"org.hibernate.orm.connections.access");

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Connection obtained from JdbcConnectionAccess [%s] for (non-JTA) DDL execution was not in auto-commit mode; the Connection 'local transaction' will be committed and the Connection will be set into auto-commit mode.", id=10001501)
    public void informConnectionLocalTransactionForNonJtaDdl(JdbcConnectionAccess var1);
}

