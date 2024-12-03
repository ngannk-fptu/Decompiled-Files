/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition;

import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.exception.CallerNotMemberException;
import com.hazelcast.spi.exception.PartitionMigratingException;
import com.hazelcast.spi.exception.RetryableException;
import com.hazelcast.spi.exception.TargetNotMemberException;
import com.hazelcast.spi.exception.WrongTargetException;
import java.util.logging.Level;

public final class ReplicaErrorLogger {
    private ReplicaErrorLogger() {
    }

    public static void log(Throwable e, ILogger logger) {
        if (e instanceof RetryableException) {
            Level level = Level.INFO;
            if (e instanceof CallerNotMemberException || e instanceof WrongTargetException || e instanceof TargetNotMemberException || e instanceof PartitionMigratingException) {
                level = Level.FINEST;
            }
            if (logger.isLoggable(level)) {
                logger.log(level, e.toString());
            }
        } else {
            logger.warning(e);
        }
    }
}

