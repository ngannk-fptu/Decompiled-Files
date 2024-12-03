/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.log;

import org.apache.velocity.runtime.log.LogSystem;
import org.apache.velocity.runtime.log.NullLogChute;

public class NullLogSystem
extends NullLogChute
implements LogSystem {
    public void logVelocityMessage(int level, String message) {
    }
}

