/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.BasicLogger
 *  org.jboss.logging.DelegatingBasicLogger
 *  org.jboss.logging.Logger
 *  org.jboss.logging.Logger$Level
 */
package org.hibernate.annotations.common.util.impl;

import java.io.Serializable;
import java.util.Locale;
import org.hibernate.annotations.common.util.impl.Log;
import org.jboss.logging.BasicLogger;
import org.jboss.logging.DelegatingBasicLogger;
import org.jboss.logging.Logger;

public class Log_$logger
extends DelegatingBasicLogger
implements Log,
BasicLogger,
Serializable {
    private static final long serialVersionUID = 1L;
    private static final String FQCN = Log_$logger.class.getName();
    private static final Locale LOCALE = Locale.ROOT;
    private static final String version = "HCANN000001: Hibernate Commons Annotations {%1$s}";
    private static final String assertionFailure = "HCANN000002: An assertion failure occurred (this may indicate a bug in Hibernate)";

    public Log_$logger(Logger log) {
        super(log);
    }

    protected Locale getLoggingLocale() {
        return LOCALE;
    }

    @Override
    public final void version(String version) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.version$str(), (Object)version);
    }

    protected String version$str() {
        return version;
    }

    @Override
    public final void assertionFailure(Throwable t) {
        this.log.logf(FQCN, Logger.Level.ERROR, t, this.assertionFailure$str(), new Object[0]);
    }

    protected String assertionFailure$str() {
        return assertionFailure;
    }
}

