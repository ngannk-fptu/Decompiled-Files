/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Throwables
 */
package net.java.ao.sql;

import com.google.common.base.Throwables;

public class CallStackProvider {
    public String getCallStack() {
        String stackTrace = Throwables.getStackTraceAsString((Throwable)new Throwable());
        return stackTrace;
    }
}

