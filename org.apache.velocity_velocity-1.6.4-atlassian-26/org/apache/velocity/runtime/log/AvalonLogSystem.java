/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.log;

import org.apache.velocity.runtime.log.AvalonLogChute;
import org.apache.velocity.runtime.log.LogSystem;

public class AvalonLogSystem
extends AvalonLogChute
implements LogSystem {
    @Override
    public void logVelocityMessage(int level, String message) {
        this.log(level, message);
    }
}

