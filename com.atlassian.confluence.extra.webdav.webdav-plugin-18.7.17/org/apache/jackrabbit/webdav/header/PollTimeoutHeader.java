/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package org.apache.jackrabbit.webdav.header;

import javax.servlet.http.HttpServletRequest;
import org.apache.jackrabbit.webdav.header.TimeoutHeader;

public class PollTimeoutHeader
extends TimeoutHeader {
    public PollTimeoutHeader(long timeout) {
        super(timeout);
    }

    @Override
    public String getHeaderName() {
        return "PollTimeout";
    }

    public static PollTimeoutHeader parseHeader(HttpServletRequest request, long defaultValue) {
        String timeoutStr = request.getHeader("PollTimeout");
        long timeout = PollTimeoutHeader.parse(timeoutStr, defaultValue);
        return new PollTimeoutHeader(timeout);
    }
}

