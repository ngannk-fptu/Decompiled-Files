/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.lang;

import com.mchange.v2.lang.ThreadGroupUtils;
import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLog;
import com.mchange.v2.log.MLogger;
import java.lang.reflect.Method;

public final class ThreadUtils {
    private static final MLogger logger;
    static final Method holdsLock;

    public static void enumerateAll(Thread[] threadArray) {
        ThreadGroupUtils.rootThreadGroup().enumerate(threadArray);
    }

    public static Boolean reflectiveHoldsLock(Object object) {
        try {
            if (holdsLock == null) {
                return null;
            }
            return (Boolean)holdsLock.invoke(null, object);
        }
        catch (Exception exception) {
            if (logger.isLoggable(MLevel.FINER)) {
                logger.log(MLevel.FINER, "An Exception occurred while trying to call Thread.holdsLock( ... ) reflectively.", exception);
            }
            return null;
        }
    }

    private ThreadUtils() {
    }

    static {
        Method method;
        logger = MLog.getLogger(ThreadUtils.class);
        try {
            method = Thread.class.getMethod("holdsLock", Object.class);
        }
        catch (NoSuchMethodException noSuchMethodException) {
            method = null;
        }
        holdsLock = method;
    }
}

