/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Appender
 *  org.apache.log4j.Layout
 *  org.apache.log4j.Level
 *  org.apache.log4j.Logger
 *  org.apache.log4j.PatternLayout
 *  org.apache.log4j.Priority
 *  org.apache.log4j.RollingFileAppender
 */
package org.apache.velocity.runtime.log;

import java.io.IOException;
import java.lang.reflect.Field;
import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Priority;
import org.apache.log4j.RollingFileAppender;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogChute;
import org.apache.velocity.util.ExceptionUtils;

public class Log4JLogChute
implements LogChute {
    public static final String RUNTIME_LOG_LOG4J_LOGGER = "runtime.log.logsystem.log4j.logger";
    public static final String RUNTIME_LOG_LOG4J_LOGGER_LEVEL = "runtime.log.logsystem.log4j.logger.level";
    private RuntimeServices rsvc = null;
    private boolean hasTrace = false;
    private RollingFileAppender appender = null;
    protected Logger logger = null;

    public void init(RuntimeServices rs) throws Exception {
        this.rsvc = rs;
        String name = (String)this.rsvc.getProperty(RUNTIME_LOG_LOG4J_LOGGER);
        if (name != null) {
            this.logger = Logger.getLogger((String)name);
            this.log(0, "Log4JLogChute using logger '" + name + '\'');
        } else {
            this.logger = Logger.getLogger((String)this.getClass().getName());
            String file = this.rsvc.getString("runtime.log");
            if (file != null && file.length() > 0) {
                this.initAppender(file);
            }
        }
        String lvl = this.rsvc.getString(RUNTIME_LOG_LOG4J_LOGGER_LEVEL);
        if (lvl != null) {
            Level level = Level.toLevel((String)lvl);
            this.logger.setLevel(level);
        }
        try {
            Field traceLevel = Level.class.getField("TRACE");
            this.hasTrace = true;
        }
        catch (NoSuchFieldException e) {
            this.log(0, "The version of log4j being used does not support the \"trace\" level.");
        }
    }

    private void initAppender(String file) throws Exception {
        try {
            PatternLayout layout = new PatternLayout("%d - %m%n");
            this.appender = new RollingFileAppender((Layout)layout, file, true);
            this.appender.setMaxBackupIndex(1);
            this.appender.setMaximumFileSize(100000L);
            this.logger.setAdditivity(false);
            this.logger.addAppender((Appender)this.appender);
            this.log(0, "Log4JLogChute initialized using file '" + file + '\'');
        }
        catch (IOException ioe) {
            this.rsvc.getLog().error("Could not create file appender '" + file + '\'', ioe);
            throw ExceptionUtils.createRuntimeException("Error configuring Log4JLogChute : ", ioe);
        }
    }

    public void log(int level, String message) {
        switch (level) {
            case 2: {
                this.logger.warn((Object)message);
                break;
            }
            case 1: {
                this.logger.info((Object)message);
                break;
            }
            case -1: {
                if (this.hasTrace) {
                    this.logger.trace((Object)message);
                    break;
                }
                this.logger.debug((Object)message);
                break;
            }
            case 3: {
                this.logger.error((Object)message);
                break;
            }
            default: {
                this.logger.debug((Object)message);
            }
        }
    }

    public void log(int level, String message, Throwable t) {
        switch (level) {
            case 2: {
                this.logger.warn((Object)message, t);
                break;
            }
            case 1: {
                this.logger.info((Object)message, t);
                break;
            }
            case -1: {
                if (this.hasTrace) {
                    this.logger.trace((Object)message, t);
                    break;
                }
                this.logger.debug((Object)message, t);
                break;
            }
            case 3: {
                this.logger.error((Object)message, t);
                break;
            }
            default: {
                this.logger.debug((Object)message, t);
            }
        }
    }

    public boolean isLevelEnabled(int level) {
        switch (level) {
            case 0: {
                return this.logger.isDebugEnabled();
            }
            case 1: {
                return this.logger.isInfoEnabled();
            }
            case -1: {
                if (this.hasTrace) {
                    return this.logger.isTraceEnabled();
                }
                return this.logger.isDebugEnabled();
            }
            case 2: {
                return this.logger.isEnabledFor((Priority)Level.WARN);
            }
            case 3: {
                return this.logger.isEnabledFor((Priority)Level.ERROR);
            }
        }
        return true;
    }

    protected void finalize() throws Throwable {
        this.shutdown();
    }

    public void shutdown() {
        if (this.appender != null) {
            this.logger.removeAppender((Appender)this.appender);
            this.appender.close();
            this.appender = null;
        }
    }
}

