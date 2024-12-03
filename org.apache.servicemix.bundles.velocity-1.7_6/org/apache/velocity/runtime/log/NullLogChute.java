/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.log;

import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogChute;

public class NullLogChute
implements LogChute {
    public void init(RuntimeServices rs) throws Exception {
    }

    public void log(int level, String message) {
    }

    public void log(int level, String message, Throwable t) {
    }

    public boolean isLevelEnabled(int level) {
        return false;
    }
}

