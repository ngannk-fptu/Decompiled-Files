/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpServletResponseWrapper
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.filters;

import java.io.IOException;
import java.nio.charset.Charset;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.apache.catalina.filters.FilterBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class AddDefaultCharsetFilter
extends FilterBase {
    private final Log log = LogFactory.getLog(AddDefaultCharsetFilter.class);
    private static final String DEFAULT_ENCODING = "ISO-8859-1";
    private String encoding;

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    @Override
    protected Log getLogger() {
        return this.log;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        if (this.encoding == null || this.encoding.length() == 0 || this.encoding.equalsIgnoreCase("default")) {
            this.encoding = DEFAULT_ENCODING;
        } else if (this.encoding.equalsIgnoreCase("system")) {
            this.encoding = Charset.defaultCharset().name();
        } else if (!Charset.isSupported(this.encoding)) {
            throw new IllegalArgumentException(sm.getString("addDefaultCharset.unsupportedCharset", new Object[]{this.encoding}));
        }
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (response instanceof HttpServletResponse) {
            ResponseWrapper wrapped = new ResponseWrapper((HttpServletResponse)response, this.encoding);
            chain.doFilter(request, (ServletResponse)wrapped);
        } else {
            chain.doFilter(request, response);
        }
    }

    public static class ResponseWrapper
    extends HttpServletResponseWrapper {
        private String encoding;

        public ResponseWrapper(HttpServletResponse response, String encoding) {
            super(response);
            this.encoding = encoding;
        }

        public void setContentType(String contentType) {
            if (contentType != null && contentType.startsWith("text/")) {
                if (!contentType.contains("charset=")) {
                    super.setContentType(contentType + ";charset=" + this.encoding);
                } else {
                    super.setContentType(contentType);
                    this.encoding = this.getCharacterEncoding();
                }
            } else {
                super.setContentType(contentType);
            }
        }

        public void setHeader(String name, String value) {
            if (name.trim().equalsIgnoreCase("content-type")) {
                this.setContentType(value);
            } else {
                super.setHeader(name, value);
            }
        }

        public void addHeader(String name, String value) {
            if (name.trim().equalsIgnoreCase("content-type")) {
                this.setContentType(value);
            } else {
                super.addHeader(name, value);
            }
        }

        public void setCharacterEncoding(String charset) {
            super.setCharacterEncoding(charset);
            this.encoding = charset;
        }
    }
}

