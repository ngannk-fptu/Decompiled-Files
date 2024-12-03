/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.confluence.extra.jira.util;

import javax.ws.rs.core.Response;

public class ResponseUtil {
    public static Response buildUnauthorizedResponse(String oAuthenticationUri) {
        return Response.status((Response.Status)Response.Status.UNAUTHORIZED).header("WWW-Authenticate", (Object)("OAuth realm=\"" + oAuthenticationUri + "\"")).build();
    }
}

