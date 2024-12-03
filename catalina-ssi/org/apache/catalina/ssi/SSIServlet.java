/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.apache.catalina.ssi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Locale;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.ssi.SSIProcessor;
import org.apache.catalina.ssi.SSIServletExternalResolver;
import org.apache.catalina.ssi.SSIServletRequestUtil;

public class SSIServlet
extends HttpServlet {
    private static final long serialVersionUID = 1L;
    protected int debug = 0;
    protected boolean buffered = false;
    protected Long expires = null;
    protected boolean isVirtualWebappRelative = false;
    protected String inputEncoding = null;
    protected String outputEncoding = "UTF-8";
    protected boolean allowExec = false;

    public void init() throws ServletException {
        if (this.getServletConfig().getInitParameter("debug") != null) {
            this.debug = Integer.parseInt(this.getServletConfig().getInitParameter("debug"));
        }
        this.isVirtualWebappRelative = Boolean.parseBoolean(this.getServletConfig().getInitParameter("isVirtualWebappRelative"));
        if (this.getServletConfig().getInitParameter("expires") != null) {
            this.expires = Long.valueOf(this.getServletConfig().getInitParameter("expires"));
        }
        this.buffered = Boolean.parseBoolean(this.getServletConfig().getInitParameter("buffered"));
        this.inputEncoding = this.getServletConfig().getInitParameter("inputEncoding");
        if (this.getServletConfig().getInitParameter("outputEncoding") != null) {
            this.outputEncoding = this.getServletConfig().getInitParameter("outputEncoding");
        }
        this.allowExec = Boolean.parseBoolean(this.getServletConfig().getInitParameter("allowExec"));
        if (this.debug > 0) {
            this.log("SSIServlet.init() SSI invoker started with 'debug'=" + this.debug);
        }
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        if (this.debug > 0) {
            this.log("SSIServlet.doGet()");
        }
        this.requestHandler(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        if (this.debug > 0) {
            this.log("SSIServlet.doPost()");
        }
        this.requestHandler(req, res);
    }

    protected void requestHandler(HttpServletRequest req, HttpServletResponse res) throws IOException {
        ServletContext servletContext = this.getServletContext();
        String path = SSIServletRequestUtil.getRelativePath(req);
        if (this.debug > 0) {
            this.log("SSIServlet.requestHandler()\nServing " + (this.buffered ? "buffered " : "unbuffered ") + "resource '" + path + "'");
        }
        if (path == null || path.toUpperCase(Locale.ENGLISH).startsWith("/WEB-INF") || path.toUpperCase(Locale.ENGLISH).startsWith("/META-INF")) {
            res.sendError(404);
            return;
        }
        URL resource = servletContext.getResource(path);
        if (resource == null) {
            res.sendError(404);
            return;
        }
        String resourceMimeType = servletContext.getMimeType(path);
        if (resourceMimeType == null) {
            resourceMimeType = "text/html";
        }
        res.setContentType(resourceMimeType + ";charset=" + this.outputEncoding);
        if (this.expires != null) {
            res.setDateHeader("Expires", new Date().getTime() + this.expires * 1000L);
        }
        this.processSSI(req, res, resource);
    }

    protected void processSSI(HttpServletRequest req, HttpServletResponse res, URL resource) throws IOException {
        SSIServletExternalResolver ssiExternalResolver = new SSIServletExternalResolver(this.getServletContext(), req, res, this.isVirtualWebappRelative, this.debug, this.inputEncoding);
        SSIProcessor ssiProcessor = new SSIProcessor(ssiExternalResolver, this.debug, this.allowExec);
        PrintWriter printWriter = null;
        StringWriter stringWriter = null;
        if (this.buffered) {
            stringWriter = new StringWriter();
            printWriter = new PrintWriter(stringWriter);
        } else {
            printWriter = res.getWriter();
        }
        URLConnection resourceInfo = resource.openConnection();
        InputStream resourceInputStream = resourceInfo.getInputStream();
        String encoding = resourceInfo.getContentEncoding();
        if (encoding == null) {
            encoding = this.inputEncoding;
        }
        InputStreamReader isr = encoding == null ? new InputStreamReader(resourceInputStream) : new InputStreamReader(resourceInputStream, encoding);
        try (BufferedReader bufferedReader = new BufferedReader(isr);){
            long lastModified = ssiProcessor.process(bufferedReader, resourceInfo.getLastModified(), printWriter);
            if (lastModified > 0L) {
                res.setDateHeader("last-modified", lastModified);
            }
            if (this.buffered) {
                printWriter.flush();
                String text = stringWriter.toString();
                res.getWriter().write(text);
            }
        }
    }
}

