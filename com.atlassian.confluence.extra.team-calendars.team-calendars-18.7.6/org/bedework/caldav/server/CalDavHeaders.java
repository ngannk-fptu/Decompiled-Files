/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package org.bedework.caldav.server;

import javax.servlet.http.HttpServletRequest;

public class CalDavHeaders {
    public static final String clientId = "Client-Id";
    public static final String runAs = "Run-As";

    public static boolean scheduleReply(HttpServletRequest req) {
        String hdrStr = req.getHeader("Schedule-Reply");
        if (hdrStr == null) {
            return true;
        }
        return "T".equals(hdrStr);
    }

    public static String getRunAs(HttpServletRequest req) {
        return req.getHeader(runAs);
    }

    public static String getClientId(HttpServletRequest req) {
        return req.getHeader(clientId);
    }

    public static boolean isSchedulingAssistant(HttpServletRequest req) {
        String cid = CalDavHeaders.getClientId(req);
        if (cid == null) {
            return false;
        }
        return "Jasig Scheduling Assistant".equals(cid);
    }
}

