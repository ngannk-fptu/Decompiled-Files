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

public class DepthHeader
implements Header,
DavConstants {
    private static Logger log = LoggerFactory.getLogger(DepthHeader.class);
    private final int depth;

    public DepthHeader(int depth) {
        if (depth != 0 && depth != 1 && depth != Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Invalid depth: " + depth);
        }
        this.depth = depth;
    }

    public DepthHeader(boolean isDeep) {
        this.depth = isDeep ? Integer.MAX_VALUE : 0;
    }

    public int getDepth() {
        return this.depth;
    }

    @Override
    public String getHeaderName() {
        return "Depth";
    }

    @Override
    public String getHeaderValue() {
        if (this.depth == 0 || this.depth == 1) {
            return String.valueOf(this.depth);
        }
        return "infinity";
    }

    public static DepthHeader parse(HttpServletRequest request, int defaultValue) {
        String headerValue = request.getHeader("Depth");
        if (headerValue == null || "".equals(headerValue)) {
            return new DepthHeader(defaultValue);
        }
        return new DepthHeader(DepthHeader.depthToInt(headerValue));
    }

    private static int depthToInt(String depth) {
        int d;
        if (depth.equalsIgnoreCase("infinity")) {
            d = Integer.MAX_VALUE;
        } else if (depth.equals("0")) {
            d = 0;
        } else if (depth.equals("1")) {
            d = 1;
        } else {
            throw new IllegalArgumentException("Invalid depth value: " + depth);
        }
        return d;
    }
}

