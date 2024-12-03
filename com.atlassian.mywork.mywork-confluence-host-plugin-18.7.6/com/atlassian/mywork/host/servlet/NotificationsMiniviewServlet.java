/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.mywork.host.servlet;

import com.atlassian.mywork.host.servlet.ServletRenderer;
import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NotificationsMiniviewServlet
extends HttpServlet {
    private static final long serialVersionUID = 0L;
    private final transient ServletRenderer renderer;

    public NotificationsMiniviewServlet(ServletRenderer renderer) {
        this.renderer = renderer;
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        this.renderer.renderWithAnchor(req, resp, "templates/mywork.vm");
    }
}

