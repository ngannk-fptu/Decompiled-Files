/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.log;

import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogChute;

public class NullLogChute
implements LogChute {
    @Override
    public void init(RuntimeServices rs) throws Exception {
    }

    @Override
    public void log(int level, String message) {
    }

    @Override
    public void log(int level, String message, Throwable t) {
    }

    @Override
    public boolean isLevelEnabled(int level) {
        return false;
    }
}

