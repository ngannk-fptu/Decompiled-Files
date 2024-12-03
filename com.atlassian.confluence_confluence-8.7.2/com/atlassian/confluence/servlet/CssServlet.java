/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.io.ByteStreams
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.io.FilenameUtils
 *  org.apache.velocity.util.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.servlet;

import com.google.common.io.ByteStreams;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FilenameUtils;
import org.apache.velocity.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CssServlet
extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(CssServlet.class);

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        if (path.startsWith("/s/")) {
            path = this.stripResourcePrefix(path);
        }
        ServletContext servletContext = request.getSession().getServletContext();
        if (path.startsWith("/styles/")) {
            String forwardPath = FilenameUtils.removeExtension((String)path) + ".action";
            log.debug("Forwarding request for stylesheet at [{}] to Struts at [{}]", (Object)StringUtils.normalizePath((String)path), (Object)forwardPath);
            servletContext.getRequestDispatcher(forwardPath).forward((ServletRequest)request, (ServletResponse)response);
        } else {
            try (InputStream stylesheet = servletContext.getResourceAsStream(path);){
                if (stylesheet == null) {
                    log.debug("No static stylesheet found at [{}]", (Object)StringUtils.normalizePath((String)path));
                    response.sendError(404);
                    return;
                }
                log.debug("Sending static stylesheet response for [{}]", (Object)StringUtils.normalizePath((String)path));
                response.setContentType("text/css");
                ByteStreams.copy((InputStream)stylesheet, (OutputStream)response.getOutputStream());
            }
        }
    }

    private String stripResourcePrefix(String path) {
        Pattern pattern = Pattern.compile("^/s/.*/_(/.*)");
        Matcher match = pattern.matcher(path);
        return match.group(1);
    }
}

