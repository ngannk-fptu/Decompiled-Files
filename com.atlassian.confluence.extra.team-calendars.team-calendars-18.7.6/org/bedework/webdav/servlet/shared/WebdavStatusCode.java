/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.webdav.servlet.shared;

import java.util.HashMap;

public class WebdavStatusCode {
    public static final int SC_MULTI_STATUS = 207;
    public static final int SC_FAILED_DEPENDENCY = 424;
    private static final HashMap<Integer, String> msgtext = new HashMap();

    private static void addmsg(int sc, String txt) {
        msgtext.put(sc, txt);
    }

    public static String getMessage(int sc) {
        String msg = msgtext.get(sc);
        if (msg == null) {
            msg = String.valueOf(sc);
        }
        return msg;
    }

    static {
        WebdavStatusCode.addmsg(207, "Multi-Status");
        WebdavStatusCode.addmsg(202, "accepted");
        WebdavStatusCode.addmsg(502, "bad_gateway");
        WebdavStatusCode.addmsg(400, "bad_request");
        WebdavStatusCode.addmsg(409, "conflict");
        WebdavStatusCode.addmsg(100, "continue");
        WebdavStatusCode.addmsg(201, "created");
        WebdavStatusCode.addmsg(417, "expectation_failed");
        WebdavStatusCode.addmsg(403, "forbidden");
        WebdavStatusCode.addmsg(302, "found");
        WebdavStatusCode.addmsg(504, "gateway_timeout");
        WebdavStatusCode.addmsg(410, "gone");
        WebdavStatusCode.addmsg(505, "http_version_not_supported");
        WebdavStatusCode.addmsg(500, "internal_server_error");
        WebdavStatusCode.addmsg(411, "length_required");
        WebdavStatusCode.addmsg(405, "method_not_allowed");
        WebdavStatusCode.addmsg(301, "moved_permanently");
        WebdavStatusCode.addmsg(302, "moved_temporarily");
        WebdavStatusCode.addmsg(300, "multiple_choices");
        WebdavStatusCode.addmsg(204, "no_content");
        WebdavStatusCode.addmsg(203, "non_authoritative_information");
        WebdavStatusCode.addmsg(406, "not_acceptable");
        WebdavStatusCode.addmsg(404, "not_found");
        WebdavStatusCode.addmsg(501, "not_implemented");
        WebdavStatusCode.addmsg(304, "not_modified");
        WebdavStatusCode.addmsg(200, "ok");
        WebdavStatusCode.addmsg(206, "partial_content");
        WebdavStatusCode.addmsg(402, "payment_required");
        WebdavStatusCode.addmsg(412, "precondition_failed");
        WebdavStatusCode.addmsg(407, "proxy_authentication_required");
        WebdavStatusCode.addmsg(413, "request_entity_too_large");
        WebdavStatusCode.addmsg(408, "request_timeout");
        WebdavStatusCode.addmsg(414, "request_uri_too_long");
        WebdavStatusCode.addmsg(416, "requested_range_not_satisfiable");
        WebdavStatusCode.addmsg(205, "reset_content");
        WebdavStatusCode.addmsg(303, "see_other");
        WebdavStatusCode.addmsg(503, "service_unavailable");
        WebdavStatusCode.addmsg(101, "switching_protocols");
        WebdavStatusCode.addmsg(307, "temporary_redirect");
        WebdavStatusCode.addmsg(401, "unauthorized");
        WebdavStatusCode.addmsg(415, "unsupported_media_type");
        WebdavStatusCode.addmsg(305, "use_proxy");
    }
}

