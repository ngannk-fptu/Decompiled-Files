/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterChain
 *  javax.servlet.GenericFilter
 *  javax.servlet.ServletException
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.apache.catalina.ssi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.FilterChain;
import javax.servlet.GenericFilter;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.ssi.ByteArrayServletOutputStream;
import org.apache.catalina.ssi.ResponseIncludeWrapper;
import org.apache.catalina.ssi.SSIProcessor;
import org.apache.catalina.ssi.SSIServletExternalResolver;

public class SSIFilter
extends GenericFilter {
    private static final long serialVersionUID = 1L;
    protected int debug = 0;
    protected Long expires = null;
    protected boolean isVirtualWebappRelative = false;
    protected Pattern contentTypeRegEx = null;
    protected final Pattern shtmlRegEx = Pattern.compile("text/x-server-parsed-html(;.*)?");
    protected boolean allowExec = false;

    public void init() throws ServletException {
        if (this.getInitParameter("debug") != null) {
            this.debug = Integer.parseInt(this.getInitParameter("debug"));
        }
        this.contentTypeRegEx = this.getInitParameter("contentType") != null ? Pattern.compile(this.getInitParameter("contentType")) : this.shtmlRegEx;
        this.isVirtualWebappRelative = Boolean.parseBoolean(this.getInitParameter("isVirtualWebappRelative"));
        if (this.getInitParameter("expires") != null) {
            this.expires = Long.valueOf(this.getInitParameter("expires"));
        }
        this.allowExec = Boolean.parseBoolean(this.getInitParameter("allowExec"));
        if (this.debug > 0) {
            this.getServletContext().log("SSIFilter.init() SSI invoker started with 'debug'=" + this.debug);
        }
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse res = (HttpServletResponse)response;
        ByteArrayServletOutputStream basos = new ByteArrayServletOutputStream();
        ResponseIncludeWrapper responseIncludeWrapper = new ResponseIncludeWrapper(res, basos);
        chain.doFilter((ServletRequest)req, (ServletResponse)responseIncludeWrapper);
        responseIncludeWrapper.flushOutputStreamOrWriter();
        byte[] bytes = basos.toByteArray();
        String contentType = responseIncludeWrapper.getContentType();
        if (contentType != null && this.contentTypeRegEx.matcher(contentType).matches()) {
            String encoding = res.getCharacterEncoding();
            SSIServletExternalResolver ssiExternalResolver = new SSIServletExternalResolver(this.getServletContext(), req, res, this.isVirtualWebappRelative, this.debug, encoding);
            SSIProcessor ssiProcessor = new SSIProcessor(ssiExternalResolver, this.debug, this.allowExec);
            InputStreamReader reader = new InputStreamReader((InputStream)new ByteArrayInputStream(bytes), encoding);
            ByteArrayOutputStream ssiout = new ByteArrayOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter((OutputStream)ssiout, encoding));
            long lastModified = ssiProcessor.process(reader, responseIncludeWrapper.getLastModified(), writer);
            writer.flush();
            bytes = ssiout.toByteArray();
            if (this.expires != null) {
                res.setDateHeader("expires", new Date().getTime() + this.expires * 1000L);
            }
            if (lastModified > 0L) {
                res.setDateHeader("last-modified", lastModified);
            }
            res.setContentLength(bytes.length);
            Matcher shtmlMatcher = this.shtmlRegEx.matcher(responseIncludeWrapper.getContentType());
            if (shtmlMatcher.matches()) {
                String enc = shtmlMatcher.group(1);
                res.setContentType("text/html" + (enc != null ? enc : ""));
            }
        }
        ServletOutputStream out = null;
        try {
            out = res.getOutputStream();
        }
        catch (IllegalStateException illegalStateException) {
            // empty catch block
        }
        if (out == null) {
            res.getWriter().write(new String(bytes));
        } else {
            out.write(bytes);
        }
    }
}

