/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.velocity.runtime.RuntimeServices
 *  org.apache.velocity.runtime.log.LogSystem
 */
package org.apache.velocity.tools.view.servlet;

import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogSystem;
import org.apache.velocity.tools.view.ServletLogChute;

@Deprecated
public class ServletLogger
extends ServletLogChute
implements LogSystem {
    @Override
    public void init(RuntimeServices rs) throws Exception {
        super.init(rs);
        this.log(2, "ServletLogger has been deprecated. Use " + super.getClass().getName() + " instead.");
    }

    public void logVelocityMessage(int level, String message) {
        this.log(level, message);
    }
}

