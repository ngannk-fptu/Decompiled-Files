/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.velocity.runtime.log;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogChute;

public class CommonsLogLogChute
implements LogChute {
    public static final String LOGCHUTE_COMMONS_LOG_NAME = "runtime.log.logsystem.commons.logging.name";
    public static final String DEFAULT_LOG_NAME = "org.apache.velocity";
    protected Log log;

    @Override
    public void init(RuntimeServices rs) throws Exception {
        String name = (String)rs.getProperty(LOGCHUTE_COMMONS_LOG_NAME);
        if (name == null) {
            name = DEFAULT_LOG_NAME;
        }
        this.log = LogFactory.getLog((String)name);
        this.log(0, "CommonsLogLogChute name is '" + name + "'");
    }

    @Override
    public void log(int level, String message) {
        switch (level) {
            case 2: {
                this.log.warn((Object)message);
                break;
            }
            case 1: {
                this.log.info((Object)message);
                break;
            }
            case -1: {
                this.log.trace((Object)message);
                break;
            }
            case 3: {
                this.log.error((Object)message);
                break;
            }
            default: {
                this.log.debug((Object)message);
            }
        }
    }

    @Override
    public void log(int level, String message, Throwable t) {
        switch (level) {
            case 2: {
                this.log.warn((Object)message, t);
                break;
            }
            case 1: {
                this.log.info((Object)message, t);
                break;
            }
            case -1: {
                this.log.trace((Object)message, t);
                break;
            }
            case 3: {
                this.log.error((Object)message, t);
                break;
            }
            default: {
                this.log.debug((Object)message, t);
            }
        }
    }

    @Override
    public boolean isLevelEnabled(int level) {
        switch (level) {
            case 0: {
                return this.log.isDebugEnabled();
            }
            case 1: {
                return this.log.isInfoEnabled();
            }
            case -1: {
                return this.log.isTraceEnabled();
            }
            case 2: {
                return this.log.isWarnEnabled();
            }
            case 3: {
                return this.log.isErrorEnabled();
            }
        }
        return true;
    }
}

