/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.exception.ExceptionUtils
 *  org.slf4j.Logger
 */
package com.atlassian.confluence.extra.jira.cache;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;

public class CacheLoggingUtils {
    public static void log(@Nonnull Logger logger, @Nullable Throwable error, boolean isError) {
        if (error != null) {
            if (isError) {
                logger.error("Caching error: ", error);
            } else {
                logger.warn("Caching error: {}", (Object)ExceptionUtils.getRootCauseMessage((Throwable)error));
            }
        }
    }
}

