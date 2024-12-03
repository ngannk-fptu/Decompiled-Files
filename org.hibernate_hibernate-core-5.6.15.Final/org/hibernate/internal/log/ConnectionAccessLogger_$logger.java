/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.BasicLogger
 *  org.jboss.logging.DelegatingBasicLogger
 *  org.jboss.logging.Logger
 *  org.jboss.logging.Logger$Level
 */
package org.hibernate.internal.log;

import java.io.Serializable;
import java.util.Locale;
import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
import org.hibernate.internal.log.ConnectionAccessLogger;
import org.jboss.logging.BasicLogger;
import org.jboss.logging.DelegatingBasicLogger;
import org.jboss.logging.Logger;

public class ConnectionAccessLogger_$logger
extends DelegatingBasicLogger
implements ConnectionAccessLogger,
BasicLogger,
Serializable {
    private static final long serialVersionUID = 1L;
    private static final String FQCN = ConnectionAccessLogger_$logger.class.getName();
    private static final Locale LOCALE = Locale.ROOT;

    public ConnectionAccessLogger_$logger(Logger log) {
        super(log);
    }

    protected Locale getLoggingLocale() {
        return LOCALE;
    }

    @Override
    public final void informConnectionLocalTransactionForNonJtaDdl(JdbcConnectionAccess jdbcConnectionAccess) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.informConnectionLocalTransactionForNonJtaDdl$str(), (Object)jdbcConnectionAccess);
    }

    protected String informConnectionLocalTransactionForNonJtaDdl$str() {
        return "HHH10001501: Connection obtained from JdbcConnectionAccess [%s] for (non-JTA) DDL execution was not in auto-commit mode; the Connection 'local transaction' will be committed and the Connection will be set into auto-commit mode.";
    }
}

