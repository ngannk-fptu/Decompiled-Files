/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterChain
 *  javax.servlet.GenericFilter
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.apache.tomcat.websocket.server;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.GenericFilter;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.tomcat.websocket.server.UpgradeUtil;
import org.apache.tomcat.websocket.server.WsMappingResult;
import org.apache.tomcat.websocket.server.WsServerContainer;

public class WsFilter
extends GenericFilter {
    private static final long serialVersionUID = 1L;
    private transient WsServerContainer sc;

    public void init() throws ServletException {
        this.sc = (WsServerContainer)this.getServletContext().getAttribute("javax.websocket.server.ServerContainer");
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (!this.sc.areEndpointsRegistered() || !UpgradeUtil.isWebSocketUpgradeRequest(request, response)) {
            chain.doFilter(request, response);
            return;
        }
        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse resp = (HttpServletResponse)response;
        String pathInfo = req.getPathInfo();
        String path = pathInfo == null ? req.getServletPath() : req.getServletPath() + pathInfo;
        WsMappingResult mappingResult = this.sc.findMapping(path);
        if (mappingResult == null) {
            chain.doFilter(request, response);
            return;
        }
        UpgradeUtil.doUpgrade(this.sc, req, resp, mappingResult.getConfig(), mappingResult.getPathParams());
    }
}

