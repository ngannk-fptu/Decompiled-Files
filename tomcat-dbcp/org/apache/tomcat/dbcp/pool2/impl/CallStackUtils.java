/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.pool2.impl;

import java.security.AccessControlException;
import org.apache.tomcat.dbcp.pool2.impl.CallStack;
import org.apache.tomcat.dbcp.pool2.impl.SecurityManagerCallStack;
import org.apache.tomcat.dbcp.pool2.impl.ThrowableCallStack;

public final class CallStackUtils {
    private static boolean canCreateSecurityManager() {
        SecurityManager manager = System.getSecurityManager();
        if (manager == null) {
            return true;
        }
        try {
            manager.checkPermission(new RuntimePermission("createSecurityManager"));
            return true;
        }
        catch (AccessControlException ignored) {
            return false;
        }
    }

    @Deprecated
    public static CallStack newCallStack(String messageFormat, boolean useTimestamp) {
        return CallStackUtils.newCallStack(messageFormat, useTimestamp, false);
    }

    public static CallStack newCallStack(String messageFormat, boolean useTimestamp, boolean requireFullStackTrace) {
        return CallStackUtils.canCreateSecurityManager() && !requireFullStackTrace ? new SecurityManagerCallStack(messageFormat, useTimestamp) : new ThrowableCallStack(messageFormat, useTimestamp);
    }

    private CallStackUtils() {
    }
}

