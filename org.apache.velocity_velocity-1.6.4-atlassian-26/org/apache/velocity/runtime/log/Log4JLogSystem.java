/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.log;

import org.apache.velocity.runtime.log.Log4JLogChute;
import org.apache.velocity.runtime.log.LogSystem;

public class Log4JLogSystem
extends Log4JLogChute
implements LogSystem {
    @Override
    public void logVelocityMessage(int level, String message) {
        this.log(level, message);
    }
}

