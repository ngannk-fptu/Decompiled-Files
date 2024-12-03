/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.tuckey.web.filters.urlrewrite.gzip;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.tuckey.web.filters.urlrewrite.utils.Log;

public final class ResponseUtil {
    private static final Log LOG = Log.getLog(ResponseUtil.class);
    private static final int EMPTY_GZIPPED_CONTENT_SIZE = 20;

    private ResponseUtil() {
    }

    public static boolean shouldGzippedBodyBeZero(byte[] compressedBytes, HttpServletRequest request) {
        if (compressedBytes.length == 20) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(request.getRequestURL() + " resulted in an empty response.");
            }
            return true;
        }
        return false;
    }

    public static boolean shouldBodyBeZero(HttpServletRequest request, int responseStatus) {
        if (responseStatus == 204) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(request.getRequestURL() + " resulted in a " + 204 + " response. Removing message body in accordance with RFC2616.");
            }
            return true;
        }
        if (responseStatus == 304) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(request.getRequestURL() + " resulted in a " + 304 + " response. Removing message body in accordance with RFC2616.");
            }
            return true;
        }
        return false;
    }

    public static void addGzipHeader(HttpServletResponse response) {
        response.setHeader("Content-Encoding", "gzip");
    }
}

