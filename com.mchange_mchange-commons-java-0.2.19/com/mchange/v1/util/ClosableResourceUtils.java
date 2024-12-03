/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.util;

import com.mchange.v1.util.ClosableResource;
import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLog;
import com.mchange.v2.log.MLogger;

public final class ClosableResourceUtils {
    private static final MLogger logger = MLog.getLogger(ClosableResourceUtils.class);

    public static Exception attemptClose(ClosableResource closableResource) {
        try {
            if (closableResource != null) {
                closableResource.close();
            }
            return null;
        }
        catch (Exception exception) {
            if (logger.isLoggable(MLevel.WARNING)) {
                logger.log(MLevel.WARNING, "CloseableResource close FAILED.", exception);
            }
            return exception;
        }
    }

    private ClosableResourceUtils() {
    }
}

