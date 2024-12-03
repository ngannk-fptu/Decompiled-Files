/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.opensymphony.sitemesh.Content
 *  com.opensymphony.sitemesh.webapp.SiteMeshWebAppContext
 *  com.opensymphony.sitemesh.webapp.decorator.NoDecorator
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.util.profiling;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.util.GeneralUtil;
import com.opensymphony.sitemesh.Content;
import com.opensymphony.sitemesh.webapp.SiteMeshWebAppContext;
import com.opensymphony.sitemesh.webapp.decorator.NoDecorator;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.function.Supplier;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceSitemeshNoDecorator
extends NoDecorator {
    private static Logger logger = LoggerFactory.getLogger(ConfluenceSitemeshNoDecorator.class);
    private Supplier<Charset> confluenceDefaultCharsetSupplier = () -> GeneralUtil.getDefaultCharset();

    @VisibleForTesting
    public void setConfluenceDefaultCharsetSupplier(Supplier<Charset> confluenceDefaultCharsetSupplier) {
        this.confluenceDefaultCharsetSupplier = confluenceDefaultCharsetSupplier;
    }

    protected void render(Content content, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext, SiteMeshWebAppContext webAppContext) throws IOException, ServletException {
        if (webAppContext.isUsingStream()) {
            logger.debug("Render content using Stream");
            PrintWriter writer = this.getPrintWriter((OutputStream)response.getOutputStream());
            content.writeOriginal((Writer)writer);
            writer.flush();
            response.getOutputStream().flush();
        } else {
            logger.debug("Render content using Writer");
            PrintWriter writer = response.getWriter();
            content.writeOriginal((Writer)writer);
            response.getWriter().flush();
        }
    }

    private PrintWriter getPrintWriter(OutputStream outputStream) {
        Charset charset = this.confluenceDefaultCharsetSupplier.get();
        if (Charset.defaultCharset().equals(charset)) {
            logger.debug("Confluence will use OS default Charset {}", (Object)charset);
        }
        return new PrintWriter(new OutputStreamWriter(outputStream, charset));
    }
}

