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

import com.atlassian.upm.rest.UpmUriBuilder;
import com.atlassian.upm.servlet.PluginManagerHandler;
import java.io.IOException;
import java.net.URI;
import java.util.Objects;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class PluginRequestsServlet
extends HttpServlet {
    private final PluginManagerHandler handler;
    private final UpmUriBuilder uriBuilder;

    public PluginRequestsServlet(PluginManagerHandler handler, UpmUriBuilder uriBuilder) {
        this.handler = Objects.requireNonNull(handler, "handler");
        this.uriBuilder = Objects.requireNonNull(uriBuilder, "uriBuilder");
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        URI uri = this.handler.getUri(request);
        if (uri.toASCIIString().startsWith(this.uriBuilder.buildUpmPluginRequestsUri().toASCIIString())) {
            response.sendRedirect(uri.toASCIIString().replace(this.uriBuilder.buildUpmPluginRequestsUri().toASCIIString(), this.uriBuilder.buildUpmMarketplaceUri().toASCIIString()));
        } else {
            response.sendRedirect(this.uriBuilder.buildUpmMarketplaceUri().toASCIIString());
        }
    }
}

