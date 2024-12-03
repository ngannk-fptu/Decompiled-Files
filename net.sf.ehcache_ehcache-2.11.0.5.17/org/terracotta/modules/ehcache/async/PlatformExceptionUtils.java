/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.terracotta.toolkit.rejoin.RejoinException
 */
package org.terracotta.modules.ehcache.async;

import org.terracotta.toolkit.rejoin.RejoinException;

public class PlatformExceptionUtils {
    public static boolean isTCNRE(Throwable th) {
        return th.getClass().getName().equals("com.tc.exception.TCNotRunningException");
    }

    public static boolean isRejoinException(Throwable th) {
        return th instanceof RejoinException;
    }

    public static boolean shouldIgnore(Throwable th) {
        return PlatformExceptionUtils.isTCNRE(th) || PlatformExceptionUtils.isRejoinException(th);
    }
}

