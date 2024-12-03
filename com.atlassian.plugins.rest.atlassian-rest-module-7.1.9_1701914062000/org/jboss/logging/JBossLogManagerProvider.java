/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logmanager.LogContext
 *  org.jboss.logmanager.Logger
 *  org.jboss.logmanager.Logger$AttachmentKey
 *  org.jboss.logmanager.MDC
 *  org.jboss.logmanager.NDC
 */
package org.jboss.logging;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.jboss.logging.JBossLogManagerLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.LoggerProvider;
import org.jboss.logmanager.LogContext;
import org.jboss.logmanager.Logger;
import org.jboss.logmanager.MDC;
import org.jboss.logmanager.NDC;

final class JBossLogManagerProvider
implements LoggerProvider {
    private static final Logger.AttachmentKey<Logger> KEY = new Logger.AttachmentKey();
    private static final Logger.AttachmentKey<ConcurrentMap<String, Logger>> LEGACY_KEY = new Logger.AttachmentKey();

    JBossLogManagerProvider() {
    }

    @Override
    public Logger getLogger(final String name) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            return AccessController.doPrivileged(new PrivilegedAction<Logger>(){

                @Override
                public Logger run() {
                    try {
                        return JBossLogManagerProvider.doGetLogger(name);
                    }
                    catch (NoSuchMethodError noSuchMethodError) {
                        return JBossLogManagerProvider.doLegacyGetLogger(name);
                    }
                }
            });
        }
        try {
            return JBossLogManagerProvider.doGetLogger(name);
        }
        catch (NoSuchMethodError noSuchMethodError) {
            return JBossLogManagerProvider.doLegacyGetLogger(name);
        }
    }

    private static Logger doLegacyGetLogger(String name) {
        Logger l;
        ConcurrentMap appearing;
        org.jboss.logmanager.Logger lmLogger = LogContext.getLogContext().getLogger("");
        ConcurrentMap<String, Logger> loggers = (ConcurrentHashMap)lmLogger.getAttachment(LEGACY_KEY);
        if (loggers == null && (appearing = (ConcurrentMap)lmLogger.attachIfAbsent(LEGACY_KEY, loggers = new ConcurrentHashMap())) != null) {
            loggers = appearing;
        }
        if ((l = (Logger)loggers.get(name)) != null) {
            return l;
        }
        org.jboss.logmanager.Logger logger2 = org.jboss.logmanager.Logger.getLogger((String)name);
        l = new JBossLogManagerLogger(name, logger2);
        Logger appearing2 = loggers.putIfAbsent(name, l);
        if (appearing2 == null) {
            return l;
        }
        return appearing2;
    }

    private static Logger doGetLogger(String name) {
        Logger l = (Logger)LogContext.getLogContext().getAttachment(name, KEY);
        if (l != null) {
            return l;
        }
        org.jboss.logmanager.Logger logger2 = org.jboss.logmanager.Logger.getLogger((String)name);
        Logger a = (Logger)logger2.attachIfAbsent(KEY, (Object)(l = new JBossLogManagerLogger(name, logger2)));
        if (a == null) {
            return l;
        }
        return a;
    }

    @Override
    public void clearMdc() {
        MDC.clear();
    }

    @Override
    public Object putMdc(String key, Object value) {
        return MDC.put((String)key, (String)String.valueOf(value));
    }

    @Override
    public Object getMdc(String key) {
        return MDC.get((String)key);
    }

    @Override
    public void removeMdc(String key) {
        MDC.remove((String)key);
    }

    @Override
    public Map<String, Object> getMdcMap() {
        return MDC.copy();
    }

    @Override
    public void clearNdc() {
        NDC.clear();
    }

    @Override
    public String getNdc() {
        return NDC.get();
    }

    @Override
    public int getNdcDepth() {
        return NDC.getDepth();
    }

    @Override
    public String popNdc() {
        return NDC.pop();
    }

    @Override
    public String peekNdc() {
        return NDC.get();
    }

    @Override
    public void pushNdc(String message) {
        NDC.push((String)message);
    }

    @Override
    public void setNdcMaxDepth(int maxDepth) {
        NDC.trimTo((int)maxDepth);
    }
}

