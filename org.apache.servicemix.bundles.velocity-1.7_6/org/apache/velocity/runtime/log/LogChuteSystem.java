/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.log;

import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogChute;
import org.apache.velocity.runtime.log.LogSystem;
import org.apache.velocity.util.StringUtils;

public class LogChuteSystem
implements LogChute {
    private LogSystem logSystem;

    protected LogChuteSystem(LogSystem wrapMe) {
        this.logSystem = wrapMe;
    }

    public void init(RuntimeServices rs) throws Exception {
        this.logSystem.init(rs);
    }

    public void log(int level, String message) {
        this.logSystem.logVelocityMessage(level, message);
    }

    public void log(int level, String message, Throwable t) {
        this.logSystem.logVelocityMessage(level, message);
        this.logSystem.logVelocityMessage(level, StringUtils.stackTrace(t));
    }

    public boolean isLevelEnabled(int level) {
        return true;
    }
}

