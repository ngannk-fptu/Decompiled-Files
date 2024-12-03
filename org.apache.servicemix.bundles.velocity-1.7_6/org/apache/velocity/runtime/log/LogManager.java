/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.HoldingLogChute;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.runtime.log.LogChute;
import org.apache.velocity.runtime.log.LogChuteSystem;
import org.apache.velocity.runtime.log.LogSystem;
import org.apache.velocity.runtime.log.SystemLogChute;
import org.apache.velocity.util.ClassUtils;

public class LogManager {
    private static LogChute createLogChute(RuntimeServices rsvc) throws Exception {
        Log log = rsvc.getLog();
        Object o = rsvc.getProperty("runtime.log.logsystem");
        if (o != null) {
            if (o instanceof LogChute) {
                try {
                    ((LogChute)o).init(rsvc);
                    return (LogChute)o;
                }
                catch (Exception e) {
                    String msg = "Could not init runtime.log.logsystem " + o;
                    log.error(msg, e);
                    throw new VelocityException(msg, e);
                }
            }
            if (o instanceof LogSystem) {
                log.debug("LogSystem has been deprecated. Please use a LogChute implementation.");
                try {
                    LogChuteSystem chute = new LogChuteSystem((LogSystem)o);
                    chute.init(rsvc);
                    return chute;
                }
                catch (Exception e) {
                    String msg = "Could not init runtime.log.logsystem " + o;
                    log.error(msg, e);
                    throw new VelocityException(msg, e);
                }
            }
            String msg = o.getClass().getName() + " object set as runtime.log.logsystem is not a valid log implementation.";
            log.error(msg);
            throw new VelocityException(msg);
        }
        List<Object> classes = new ArrayList();
        Object obj = rsvc.getProperty("runtime.log.logsystem.class");
        if (obj instanceof List) {
            classes = (List)obj;
        } else if (obj instanceof String) {
            classes.add(obj);
        }
        Iterator ii = classes.iterator();
        while (ii.hasNext()) {
            String claz = (String)ii.next();
            if (claz == null || claz.length() <= 0) continue;
            log.debug("Trying to use logger class " + claz);
            try {
                o = ClassUtils.getNewInstance(claz);
                if (o instanceof LogChute) {
                    ((LogChute)o).init(rsvc);
                    log.debug("Using logger class " + claz);
                    return (LogChute)o;
                }
                if (o instanceof LogSystem) {
                    log.debug("LogSystem has been deprecated. Please use a LogChute implementation.");
                    LogChuteSystem chute = new LogChuteSystem((LogSystem)o);
                    chute.init(rsvc);
                    return chute;
                }
                String msg = "The specified logger class " + claz + " does not implement the " + LogChute.class.getName() + " interface.";
                log.error(msg);
                if (LogManager.isProbablyProvidedLogChute(claz)) {
                    log.error("This appears to be a ClassLoader issue.  Check for multiple Velocity jars in your classpath.");
                }
                throw new VelocityException(msg);
            }
            catch (NoClassDefFoundError ncdfe) {
                if (LogManager.isProbablyProvidedLogChute(claz)) {
                    log.debug("Target log system for " + claz + " is not available (" + ncdfe.toString() + ").  Falling back to next log system...");
                    continue;
                }
                log.debug("Couldn't find class " + claz + " or necessary supporting classes in classpath.", ncdfe);
            }
            catch (UnsupportedOperationException uoe) {
                if (LogManager.isProbablyProvidedLogChute(claz)) {
                    log.debug("Target log system for " + claz + " is not supported (" + uoe.toString() + ").  Falling back to next log system...");
                    continue;
                }
                log.debug("Couldn't find necessary resources for " + claz, uoe);
            }
            catch (Exception e) {
                String msg = "Failed to initialize an instance of " + claz + " with the current runtime configuration.";
                log.error(msg, e);
                throw new VelocityException(msg, e);
            }
        }
        SystemLogChute slc = new SystemLogChute();
        slc.init(rsvc);
        log.debug("Using SystemLogChute.");
        return slc;
    }

    private static boolean isProbablyProvidedLogChute(String claz) {
        if (claz == null) {
            return false;
        }
        return claz.startsWith("org.apache.velocity.runtime.log") && claz.endsWith("LogChute");
    }

    public static void updateLog(Log log, RuntimeServices rsvc) throws Exception {
        LogChute newLogChute = LogManager.createLogChute(rsvc);
        LogChute oldLogChute = log.getLogChute();
        log.setLogChute(newLogChute);
        if (oldLogChute instanceof HoldingLogChute) {
            HoldingLogChute hlc = (HoldingLogChute)oldLogChute;
            hlc.transferTo(newLogChute);
        }
    }
}

