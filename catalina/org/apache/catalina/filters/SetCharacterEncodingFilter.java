/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.filters;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.catalina.filters.FilterBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class SetCharacterEncodingFilter
extends FilterBase {
    private final Log log = LogFactory.getLog(SetCharacterEncodingFilter.class);
    private String encoding = null;
    private boolean ignore = false;

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    public boolean isIgnore() {
        return this.ignore;
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String characterEncoding;
        if ((this.ignore || request.getCharacterEncoding() == null) && (characterEncoding = this.selectEncoding(request)) != null) {
            request.setCharacterEncoding(characterEncoding);
        }
        chain.doFilter(request, response);
    }

    @Override
    protected Log getLogger() {
        return this.log;
    }

    protected String selectEncoding(ServletRequest request) {
        return this.encoding;
    }
}

