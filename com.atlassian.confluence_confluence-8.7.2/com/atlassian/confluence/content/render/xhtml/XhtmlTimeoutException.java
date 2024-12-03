/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.misc.ConcurrentConversionUtil
 *  com.atlassian.util.concurrent.Timeout
 *  io.atlassian.util.concurrent.Timeout
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.util.misc.ConcurrentConversionUtil;
import io.atlassian.util.concurrent.Timeout;

public class XhtmlTimeoutException
extends XhtmlException {
    private final long allowedTimeInSeconds;
    private final long exceededTimeInMilliseconds;

    @Deprecated
    public static XhtmlTimeoutException forTimeout(com.atlassian.util.concurrent.Timeout timeout) {
        return XhtmlTimeoutException.createForTimeout(ConcurrentConversionUtil.toIoTimeout((com.atlassian.util.concurrent.Timeout)timeout));
    }

    public static XhtmlTimeoutException createForTimeout(Timeout timeout) {
        long exceededTimeInMilliseconds = timeout.getUnit().toMillis(-timeout.getTime());
        long allowedTimeInSeconds = timeout.getUnit().toSeconds(timeout.getTimeoutPeriod());
        return new XhtmlTimeoutException(XhtmlTimeoutException.timeoutMessage(allowedTimeInSeconds), allowedTimeInSeconds, exceededTimeInMilliseconds);
    }

    private XhtmlTimeoutException(String message, long allowedTimeInSeconds, long exceededTimeInMilliseconds) {
        super(message);
        this.allowedTimeInSeconds = allowedTimeInSeconds;
        this.exceededTimeInMilliseconds = exceededTimeInMilliseconds;
    }

    private static String timeoutMessage(long allowedTimeInSeconds) {
        return "Rendering this content exceeded the timeout of " + allowedTimeInSeconds + " seconds.";
    }

    public String getDetailedTimeoutMessage() {
        return this.getDetailedTimeoutMessage("Rendering this content");
    }

    public String getDetailedTimeoutMessage(String operation) {
        return operation + " exceeded the timeout of " + this.allowedTimeInSeconds + " seconds by " + this.exceededTimeInMilliseconds + " milliseconds";
    }

    public long getAllowedTimeInSeconds() {
        return this.allowedTimeInSeconds;
    }

    public long getExceededTimeInMilliseconds() {
        return this.exceededTimeInMilliseconds;
    }
}

