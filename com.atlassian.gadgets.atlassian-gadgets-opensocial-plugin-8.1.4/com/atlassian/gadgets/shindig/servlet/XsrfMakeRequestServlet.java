/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.shindig.gadgets.servlet.MakeRequestServlet
 */
package com.atlassian.gadgets.shindig.servlet;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.shindig.gadgets.servlet.MakeRequestServlet;

public class XsrfMakeRequestServlet
extends MakeRequestServlet {
    public static final String X_ATLASSIAN_TOKEN = "X-Atlassian-Token";
    public static final String NO_CHECK = "no-check";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!NO_CHECK.equals(request.getHeader(X_ATLASSIAN_TOKEN))) {
            response.sendError(404, "XSRF check failed");
            return;
        }
        super.doGet(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!NO_CHECK.equals(request.getHeader(X_ATLASSIAN_TOKEN))) {
            response.sendError(404, "XSRF check failed");
            return;
        }
        super.doPost(request, response);
    }
}

