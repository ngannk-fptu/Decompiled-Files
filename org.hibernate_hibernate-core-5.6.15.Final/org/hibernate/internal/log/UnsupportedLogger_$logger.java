/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 *  org.jboss.logging.Logger$Level
 */
package org.hibernate.internal.log;

import java.io.Serializable;
import java.util.Locale;
import org.hibernate.internal.log.UnsupportedLogger;
import org.jboss.logging.Logger;

public class UnsupportedLogger_$logger
implements UnsupportedLogger,
Serializable {
    private static final long serialVersionUID = 1L;
    private static final String FQCN = UnsupportedLogger_$logger.class.getName();
    protected final Logger log;
    private static final Locale LOCALE = Locale.ROOT;

    public UnsupportedLogger_$logger(Logger log) {
        this.log = log;
    }

    protected Locale getLoggingLocale() {
        return LOCALE;
    }

    @Override
    public final void usingLegacyClassnamesForProxies() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.usingLegacyClassnamesForProxies$str(), new Object[0]);
    }

    protected String usingLegacyClassnamesForProxies$str() {
        return "HHH90002001: Global configuration option 'hibernate.bytecode.enforce_legacy_proxy_classnames' was enabled. Generated proxies will use backwards compatible classnames. This option is unsupported and will be removed.";
    }
}

