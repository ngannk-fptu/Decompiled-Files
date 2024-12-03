/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.log;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogChute;

public class JdkLogChute
implements LogChute {
    public static final String RUNTIME_LOG_JDK_LOGGER = "runtime.log.logsystem.jdk.logger";
    public static final String RUNTIME_LOG_JDK_LOGGER_LEVEL = "runtime.log.logsystem.jdk.logger.level";
    public static final String DEFAULT_LOG_NAME = "org.apache.velocity";
    protected Logger logger = null;

    public void init(RuntimeServices rs) {
        String name = (String)rs.getProperty(RUNTIME_LOG_JDK_LOGGER);
        if (name == null) {
            name = DEFAULT_LOG_NAME;
        }
        this.logger = Logger.getLogger(name);
        String lvl = rs.getString(RUNTIME_LOG_JDK_LOGGER_LEVEL);
        if (lvl != null) {
            Level level = Level.parse(lvl);
            this.logger.setLevel(level);
            this.log(0, "JdkLogChute will use logger '" + name + '\'' + " at level '" + level + '\'');
        }
    }

    protected Level getJdkLevel(int level) {
        switch (level) {
            case 2: {
                return Level.WARNING;
            }
            case 1: {
                return Level.INFO;
            }
            case 0: {
                return Level.FINE;
            }
            case -1: {
                return Level.FINEST;
            }
            case 3: {
                return Level.SEVERE;
            }
        }
        return Level.FINER;
    }

    public void log(int level, String message) {
        this.log(level, message, null);
    }

    public void log(int level, String message, Throwable t) {
        Level jdkLevel = this.getJdkLevel(level);
        if (t == null) {
            this.logger.log(jdkLevel, message);
        } else {
            this.logger.log(jdkLevel, message, t);
        }
    }

    public boolean isLevelEnabled(int level) {
        Level jdkLevel = this.getJdkLevel(level);
        return this.logger.isLoggable(jdkLevel);
    }
}

