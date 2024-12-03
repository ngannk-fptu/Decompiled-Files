/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.velocity.runtime.RuntimeServices
 *  org.apache.velocity.runtime.log.LogChute
 */
package com.atlassian.templaterenderer.velocity.log;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogChute;

public class CommonsLogChute
implements LogChute {
    private final Log log = LogFactory.getLog(this.getClass());

    public void init(RuntimeServices arg0) throws Exception {
    }

    public boolean isLevelEnabled(int level) {
        switch (level) {
            case 0: {
                return this.log.isDebugEnabled();
            }
            case 3: {
                return this.log.isErrorEnabled();
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
        }
        return false;
    }

    public void log(int level, String message) {
        switch (level) {
            case 0: {
                this.log.debug((Object)message);
                break;
            }
            case 3: {
                this.log.error((Object)message);
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
            case 2: {
                this.log.warn((Object)message);
            }
        }
    }

    public void log(int level, String message, Throwable cause) {
        switch (level) {
            case 0: {
                this.log.debug((Object)message, cause);
                break;
            }
            case 3: {
                this.log.error((Object)message, cause);
                break;
            }
            case 1: {
                this.log.info((Object)message, cause);
                break;
            }
            case -1: {
                this.log.trace((Object)message, cause);
                break;
            }
            case 2: {
                this.log.warn((Object)message, cause);
            }
        }
    }
}

