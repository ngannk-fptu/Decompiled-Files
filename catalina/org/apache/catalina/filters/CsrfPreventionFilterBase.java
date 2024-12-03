/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.filters;

import java.security.SecureRandom;
import java.util.Random;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.apache.catalina.filters.FilterBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public abstract class CsrfPreventionFilterBase
extends FilterBase {
    private final Log log = LogFactory.getLog(CsrfPreventionFilterBase.class);
    private String randomClass = SecureRandom.class.getName();
    private Random randomSource;
    private int denyStatus = 403;

    @Override
    protected Log getLogger() {
        return this.log;
    }

    public int getDenyStatus() {
        return this.denyStatus;
    }

    public void setDenyStatus(int denyStatus) {
        this.denyStatus = denyStatus;
    }

    public void setRandomClass(String randomClass) {
        this.randomClass = randomClass;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        try {
            Class<?> clazz = Class.forName(this.randomClass);
            this.randomSource = (Random)clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
        }
        catch (ReflectiveOperationException e) {
            ServletException se = new ServletException(sm.getString("csrfPrevention.invalidRandomClass", new Object[]{this.randomClass}), (Throwable)e);
            throw se;
        }
    }

    @Override
    protected boolean isConfigProblemFatal() {
        return true;
    }

    protected String generateNonce(HttpServletRequest request) {
        return this.generateNonce();
    }

    @Deprecated
    protected String generateNonce() {
        byte[] random = new byte[16];
        StringBuilder buffer = new StringBuilder();
        this.randomSource.nextBytes(random);
        for (byte b : random) {
            byte b1 = (byte)((b & 0xF0) >> 4);
            byte b2 = (byte)(b & 0xF);
            if (b1 < 10) {
                buffer.append((char)(48 + b1));
            } else {
                buffer.append((char)(65 + (b1 - 10)));
            }
            if (b2 < 10) {
                buffer.append((char)(48 + b2));
                continue;
            }
            buffer.append((char)(65 + (b2 - 10)));
        }
        return buffer.toString();
    }

    protected String getRequestedPath(HttpServletRequest request) {
        String path = request.getServletPath();
        if (request.getPathInfo() != null) {
            path = path + request.getPathInfo();
        }
        return path;
    }
}

