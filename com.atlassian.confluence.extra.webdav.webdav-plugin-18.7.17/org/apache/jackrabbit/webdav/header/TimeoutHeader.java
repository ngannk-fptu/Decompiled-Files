/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.header;

import javax.servlet.http.HttpServletRequest;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.header.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeoutHeader
implements Header,
DavConstants {
    private static Logger log = LoggerFactory.getLogger(TimeoutHeader.class);
    private final long timeout;

    public TimeoutHeader(long timeout) {
        this.timeout = timeout;
    }

    @Override
    public String getHeaderName() {
        return "Timeout";
    }

    @Override
    public String getHeaderValue() {
        if (this.timeout == Integer.MAX_VALUE) {
            return "Infinite";
        }
        return "Second-" + this.timeout / 1000L;
    }

    public long getTimeout() {
        return this.timeout;
    }

    public static TimeoutHeader parse(HttpServletRequest request, long defaultValue) {
        String timeoutStr = request.getHeader("Timeout");
        long timeout = TimeoutHeader.parse(timeoutStr, defaultValue);
        return new TimeoutHeader(timeout);
    }

    public static long parse(String timeoutStr, long defaultValue) {
        long timeout = defaultValue;
        if (timeoutStr != null && timeoutStr.length() > 0) {
            int secondsInd = timeoutStr.indexOf("Second-");
            if (secondsInd >= 0) {
                int i;
                for (i = secondsInd += 7; i < timeoutStr.length() && Character.isDigit(timeoutStr.charAt(i)); ++i) {
                }
                try {
                    timeout = 1000L * Long.parseLong(timeoutStr.substring(secondsInd, i));
                }
                catch (NumberFormatException ignore) {
                    log.error("Invalid timeout format: " + timeoutStr);
                }
            } else if (timeoutStr.equalsIgnoreCase("Infinite")) {
                timeout = Integer.MAX_VALUE;
            }
        }
        return timeout;
    }
}

