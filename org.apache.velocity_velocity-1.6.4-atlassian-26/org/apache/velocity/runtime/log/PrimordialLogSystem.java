/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.log;

import org.apache.velocity.runtime.log.HoldingLogChute;
import org.apache.velocity.runtime.log.LogChuteSystem;
import org.apache.velocity.runtime.log.LogSystem;

public class PrimordialLogSystem
extends HoldingLogChute
implements LogSystem {
    @Override
    public void logVelocityMessage(int level, String message) {
        this.log(level, message);
    }

    public void dumpLogMessages(LogSystem newLogger) {
        this.transferTo(new LogChuteSystem(newLogger));
    }
}

