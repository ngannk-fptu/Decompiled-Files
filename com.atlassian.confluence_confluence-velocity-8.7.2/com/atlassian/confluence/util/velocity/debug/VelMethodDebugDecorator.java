/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.velocity.util.introspection.VelMethod
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.util.velocity.debug;

import org.apache.velocity.util.introspection.VelMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VelMethodDebugDecorator
implements VelMethod {
    private static final Logger log = LoggerFactory.getLogger(VelMethodDebugDecorator.class);
    private final VelMethod delegate;

    public VelMethodDebugDecorator(VelMethod delegate) {
        this.delegate = delegate;
    }

    public Object invoke(Object o, Object[] params) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("invoking method [{}#{}]", (Object)o.getClass().getName(), (Object)this.getMethodName());
        }
        return this.delegate.invoke(o, params);
    }

    private String paramsToString(Object[] params) {
        int maxlength = 40;
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < params.length; ++i) {
            Object param;
            Object val;
            if (i > 0) {
                buf.append(",");
            }
            if (((String)(val = String.valueOf(param = params[i]))).length() > maxlength) {
                val = ((String)val).substring(0, maxlength - 3) + "...";
            }
            buf.append((String)val);
        }
        return buf.toString();
    }

    public boolean isCacheable() {
        return this.delegate.isCacheable();
    }

    public String getMethodName() {
        return this.delegate.getMethodName();
    }

    public Class getReturnType() {
        return this.delegate.getReturnType();
    }
}

