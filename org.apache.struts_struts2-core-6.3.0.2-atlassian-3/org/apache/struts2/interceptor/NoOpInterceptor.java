/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NoOpInterceptor
extends AbstractInterceptor {
    private static final Logger LOG = LogManager.getLogger(NoOpInterceptor.class);

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        LOG.trace("Passing invocation forward");
        return invocation.invoke();
    }
}

