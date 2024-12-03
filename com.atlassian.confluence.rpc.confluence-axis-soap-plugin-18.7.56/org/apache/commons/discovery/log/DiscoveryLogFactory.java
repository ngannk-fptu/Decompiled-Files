/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.discovery.log;

import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Map;
import org.apache.commons.discovery.DiscoveryException;
import org.apache.commons.discovery.log.SimpleLog;
import org.apache.commons.discovery.tools.ClassUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Deprecated
public class DiscoveryLogFactory {
    private static LogFactory logFactory = null;
    private static final Map<Class<?>, Class<?>> classRegistry = new Hashtable();
    private static final Class<?>[] setLogParamClasses = new Class[]{Log.class};
    private static Log log = DiscoveryLogFactory._newLog(DiscoveryLogFactory.class);

    public static Log newLog(Class<?> clazz) {
        try {
            Method setLog = ClassUtils.findPublicStaticMethod(clazz, Void.TYPE, "setLog", setLogParamClasses);
            if (setLog == null) {
                String msg = "Internal Error: " + clazz.getName() + " required to implement 'public static void setLog(Log)'";
                log.fatal((Object)msg);
                throw new DiscoveryException(msg);
            }
        }
        catch (SecurityException se) {
            String msg = "Required Security Permissions not present";
            log.fatal((Object)msg, (Throwable)se);
            throw new DiscoveryException(msg, se);
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("Class meets requirements: " + clazz.getName()));
        }
        return DiscoveryLogFactory._newLog(clazz);
    }

    public static Log _newLog(Class<?> clazz) {
        classRegistry.put(clazz, clazz);
        return logFactory == null ? new SimpleLog(clazz.getName()) : logFactory.getInstance(clazz.getName());
    }

    public static void setLog(Log _log) {
        log = _log;
    }

    public static void setFactory(LogFactory factory) {
        if (logFactory == null) {
            logFactory = factory;
            for (Class<?> clazz : classRegistry.values()) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Reset Log for: " + clazz.getName()));
                }
                Method setLog = null;
                try {
                    setLog = clazz.getMethod("setLog", setLogParamClasses);
                }
                catch (Exception e) {
                    String msg = "Internal Error: pre-check for " + clazz.getName() + " failed?!";
                    log.fatal((Object)msg, (Throwable)e);
                    throw new DiscoveryException(msg, e);
                }
                Object[] setLogParam = new Object[]{factory.getInstance(clazz.getName())};
                try {
                    setLog.invoke(null, setLogParam);
                }
                catch (Exception e) {
                    String msg = "Internal Error: setLog failed for " + clazz.getName();
                    log.fatal((Object)msg, (Throwable)e);
                    throw new DiscoveryException(msg, e);
                }
            }
        }
    }
}

