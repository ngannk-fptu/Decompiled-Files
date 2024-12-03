/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mchange.v2.log.MLevel
 *  com.mchange.v2.log.MLog
 *  com.mchange.v2.log.MLogger
 */
package com.mchange.v2.resourcepool;

import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLog;
import com.mchange.v2.log.MLogger;
import com.mchange.v2.resourcepool.ResourcePoolException;

final class ResourcePoolUtils {
    static final MLogger logger = MLog.getLogger(ResourcePoolUtils.class);

    ResourcePoolUtils() {
    }

    static final ResourcePoolException convertThrowable(String msg, Throwable t) {
        if (logger.isLoggable(MLevel.FINE)) {
            logger.log(MLevel.FINE, "Converting throwable to ResourcePoolException...", t);
        }
        if (t instanceof ResourcePoolException) {
            return (ResourcePoolException)((Object)t);
        }
        return new ResourcePoolException(msg, t);
    }

    static final ResourcePoolException convertThrowable(Throwable t) {
        return ResourcePoolUtils.convertThrowable("Ouch! " + t.toString(), t);
    }
}

