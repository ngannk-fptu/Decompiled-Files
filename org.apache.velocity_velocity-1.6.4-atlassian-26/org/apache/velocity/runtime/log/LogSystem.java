/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.log;

import org.apache.velocity.runtime.RuntimeServices;

public interface LogSystem {
    public static final boolean DEBUG_ON = true;
    public static final int DEBUG_ID = 0;
    public static final int INFO_ID = 1;
    public static final int WARN_ID = 2;
    public static final int ERROR_ID = 3;

    public void init(RuntimeServices var1) throws Exception;

    public void logVelocityMessage(int var1, String var2);
}

