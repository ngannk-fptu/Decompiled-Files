/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.velocity.runtime.RuntimeServices
 *  org.apache.velocity.runtime.log.LogSystem
 */
package org.apache.velocity.tools.generic.log;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogSystem;

@Deprecated
public class CommonsLogLogSystem
implements LogSystem {
    public static final String LOGSYSTEM_COMMONS_LOG_NAME = "runtime.log.logsystem.commons.logging.name";
    public static final String DEFAULT_LOG_NAME = "org.apache.velocity";
    protected Log log;

    public void init(RuntimeServices rs) throws Exception {
        String name = (String)rs.getProperty(LOGSYSTEM_COMMONS_LOG_NAME);
        if (name == null) {
            name = DEFAULT_LOG_NAME;
        }
        this.log = LogFactory.getLog((String)name);
        this.logVelocityMessage(0, "CommonsLogLogSystem name is '" + name + "'");
    }

    public void logVelocityMessage(int level, String message) {
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
}

