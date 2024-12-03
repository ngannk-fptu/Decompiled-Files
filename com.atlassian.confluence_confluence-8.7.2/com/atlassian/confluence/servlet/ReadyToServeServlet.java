/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.util.BootstrapUtils
 *  javax.servlet.GenericServlet
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.servlet;

import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.server.MutableApplicationStatusService;
import java.io.IOException;
import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ReadyToServeServlet
extends GenericServlet {
    private static final Logger log = LoggerFactory.getLogger((String)"com.atlassian.confluence.lifecycle");

    public void init() throws ServletException {
        log.info("Confluence is ready to serve");
        ((MutableApplicationStatusService)BootstrapUtils.getBootstrapContext().getBean("applicationStatusService")).notifyServletsLoaded();
    }

    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        throw new ServletException("ReadyToServeServlet should not be receiving service requests");
    }
}

