/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.velocity.runtime.RuntimeServices
 *  org.apache.velocity.runtime.log.LogSystem
 */
package com.atlassian.confluence.util.velocity;

import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogSystem;

@Deprecated(forRemoval=true)
public class NullVelocityLogSystem
implements LogSystem {
    public void init(RuntimeServices runtimeServices) throws Exception {
    }

    public void logVelocityMessage(int maxResults, String string) {
    }
}

