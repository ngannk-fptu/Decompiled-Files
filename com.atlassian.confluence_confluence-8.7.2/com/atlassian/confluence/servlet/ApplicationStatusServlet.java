/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.util.BootstrapUtils
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.BeansException
 *  org.springframework.context.ApplicationContext
 */
package com.atlassian.confluence.servlet;

import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.server.ApplicationState;
import com.atlassian.confluence.server.ApplicationStatusService;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

public class ApplicationStatusServlet
extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(ApplicationStatusServlet.class);
    private ApplicationStatusService applicationStatusService;

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ApplicationState state = this.getApplicationState(req);
        resp.setStatus(this.getStatus(state));
        resp.setContentType("application/json");
        resp.getWriter().append("{\"state\":\"").append(state.name()).append("\"}");
    }

    private int getStatus(ApplicationState state) {
        switch (state) {
            case ERROR: {
                return 500;
            }
            case RUNNING: 
            case FIRST_RUN: {
                return 200;
            }
        }
        return 503;
    }

    private ApplicationState getApplicationState(HttpServletRequest request) {
        ApplicationContext bootstrapContext;
        if (this.applicationStatusService == null && (bootstrapContext = BootstrapUtils.getBootstrapContext()) != null) {
            try {
                this.applicationStatusService = (ApplicationStatusService)bootstrapContext.getBean("applicationStatusService");
            }
            catch (BeansException e) {
                log.debug("Could not obtain ApplicationStatusService from Spring context ({})", (Object)e.getMessage());
            }
        }
        if (this.applicationStatusService != null) {
            return this.applicationStatusService.getState();
        }
        return ApplicationState.STARTING;
    }
}

