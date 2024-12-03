/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.logging.LoggingContext
 *  com.opensymphony.xwork2.Action
 *  com.opensymphony.xwork2.ActionInvocation
 *  com.opensymphony.xwork2.interceptor.Interceptor
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.actions.PageAware;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.actions.SpaceAware;
import com.atlassian.confluence.util.logging.LoggingContext;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingContextInterceptor
implements Interceptor {
    private static final Logger log = LoggerFactory.getLogger(LoggingContextInterceptor.class);

    public void destroy() {
    }

    public void init() {
    }

    public String intercept(ActionInvocation actionInvocation) throws Exception {
        try {
            this.buildLoggingContext(actionInvocation);
        }
        catch (Exception ex) {
            log.error("Exception while building logging context", (Throwable)ex);
        }
        try {
            String string = actionInvocation.invoke();
            return string;
        }
        finally {
            this.cleanLoggingContext();
        }
    }

    protected void buildLoggingContext(ActionInvocation actionInvocation) {
        Space space;
        AbstractPage page;
        Action action = (Action)actionInvocation.getAction();
        if (action instanceof PageAware && (page = ((PageAware)action).getPage()) != null) {
            LoggingContext.put((String)"page", (Object)page.getIdAsString());
        }
        if (action instanceof SpaceAware && (space = ((SpaceAware)action).getSpace()) != null) {
            LoggingContext.put((String)"space", (Object)Long.toString(space.getId()));
        }
        LoggingContext.put((String)"action", (Object)actionInvocation.getInvocationContext().getActionName());
    }

    protected void cleanLoggingContext() {
        LoggingContext.remove((String[])new String[]{"action"});
        LoggingContext.remove((String[])new String[]{"space"});
        LoggingContext.remove((String[])new String[]{"page"});
    }
}

