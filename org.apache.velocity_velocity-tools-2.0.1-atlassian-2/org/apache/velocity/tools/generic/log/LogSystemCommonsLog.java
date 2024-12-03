/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.velocity.app.VelocityEngine
 */
package org.apache.velocity.tools.generic.log;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.generic.log.LogChuteCommonsLog;

@Deprecated
public class LogSystemCommonsLog
extends LogChuteCommonsLog {
    public static void setVelocityEngine(VelocityEngine target) {
        LogSystemCommonsLog.setVelocityLog(target.getLog());
    }
}

