/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.core.servlet;

import java.io.IOException;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractNoOpServlet
extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(AbstractNoOpServlet.class);
    private static final String RECEIVED_UNEXPECTED_REQUEST = "NoOpServlet received an unexpected request.";
    private static final String UNABLE_TO_HANDLE_REQUEST = "Unable to handle request. Request is not a HttpServletRequest";

    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        log.warn(RECEIVED_UNEXPECTED_REQUEST);
        if (!(servletRequest instanceof HttpServletRequest) || !(servletResponse instanceof HttpServletResponse)) {
            log.error(UNABLE_TO_HANDLE_REQUEST);
            return;
        }
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        this.logUserInformation(request);
        this.logRequestInformation(request);
        response.sendError(404, "NoOpServlet received an unexpected request. More information is available in the log file.");
    }

    private void logUserInformation(HttpServletRequest request) {
        String username = this.getUserName(request);
        if (username != null) {
            log.warn("User: " + username);
        } else {
            log.warn("User: Anonymous (Not logged in)");
        }
    }

    protected abstract String getUserName(HttpServletRequest var1);

    private void logRequestInformation(HttpServletRequest request) {
        try {
            log.warn("Request Information");
            log.warn("- Request URL: " + request.getRequestURL());
            log.warn("- Query String: " + (request.getQueryString() == null ? "" : request.getQueryString()));
            log.warn("Request Attributes");
            Enumeration attributeNames = request.getAttributeNames();
            while (attributeNames.hasMoreElements()) {
                String name = (String)attributeNames.nextElement();
                Object attribute = request.getAttribute(name);
                log.warn("- " + name + ": " + (attribute == null ? "null" : attribute.toString()));
            }
        }
        catch (Throwable t) {
            log.error("Error rendering logging information" + t);
        }
    }
}

