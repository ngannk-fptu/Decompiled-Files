/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.BasicLogger
 *  org.jboss.logging.DelegatingBasicLogger
 *  org.jboss.logging.Logger
 *  org.jboss.logging.Logger$Level
 */
package org.hibernate.bytecode.enhance.spi.interceptor;

import java.io.Serializable;
import java.util.Locale;
import org.hibernate.bytecode.enhance.spi.interceptor.BytecodeInterceptorLogging;
import org.jboss.logging.BasicLogger;
import org.jboss.logging.DelegatingBasicLogger;
import org.jboss.logging.Logger;

public class BytecodeInterceptorLogging_$logger
extends DelegatingBasicLogger
implements BytecodeInterceptorLogging,
BasicLogger,
Serializable {
    private static final long serialVersionUID = 1L;
    private static final String FQCN = BytecodeInterceptorLogging_$logger.class.getName();
    private static final Locale LOCALE = Locale.ROOT;

    public BytecodeInterceptorLogging_$logger(Logger log) {
        super(log);
    }

    protected Locale getLoggingLocale() {
        return LOCALE;
    }

    @Override
    public final void lazyGroupIgnoredForToOne(String ownerName, String attributeName, String requestedLazyGroup) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.lazyGroupIgnoredForToOne$str(), (Object)ownerName, (Object)attributeName, (Object)requestedLazyGroup);
    }

    protected String lazyGroupIgnoredForToOne$str() {
        return "HHH90005901: `%s#%s` was mapped with explicit lazy-group (`%s`).  Hibernate will ignore the lazy-group - this is generally not a good idea for to-one associations as it would lead to 2 separate SQL selects to initialize the association.  This is expected to be improved in future versions of Hibernate";
    }
}

