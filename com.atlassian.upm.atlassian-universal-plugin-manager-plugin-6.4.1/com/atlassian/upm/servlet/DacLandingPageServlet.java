/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.upm.servlet;

import com.atlassian.upm.servlet.PluginManagerHandler;
import java.io.IOException;
import java.util.Objects;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DacLandingPageServlet
extends HttpServlet {
    private final PluginManagerHandler handler;

    public DacLandingPageServlet(PluginManagerHandler handler) {
        this.handler = Objects.requireNonNull(handler, "handler");
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        this.handler.handle(request, response, "dac-landing-page.vm", false);
    }
}

