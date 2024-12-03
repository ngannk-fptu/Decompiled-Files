/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.LazyComponentReference
 *  com.atlassian.util.profiling.Ticker
 *  com.atlassian.util.profiling.Timers
 *  com.opensymphony.xwork2.Action
 *  com.opensymphony.xwork2.ActionInvocation
 *  org.apache.struts2.ServletActionContext
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.impl.pages.actions.PageAwareHelper;
import com.atlassian.confluence.pages.actions.PageAware;
import com.atlassian.confluence.setup.struts.AbstractAwareInterceptor;
import com.atlassian.spring.container.LazyComponentReference;
import com.atlassian.util.profiling.Ticker;
import com.atlassian.util.profiling.Timers;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import java.util.function.Supplier;
import org.apache.struts2.ServletActionContext;

public class PageAwareInterceptor
extends AbstractAwareInterceptor {
    private static final String PAGE_NOT_FOUND = "pagenotfound";
    private final Supplier<PageAwareHelper> helperRef = new LazyComponentReference("pageAwareHelper");

    @Override
    public String intercept(ActionInvocation actionInvocation) throws Exception {
        try (Ticker ignored = Timers.start((String)"PageAwareInterceptor.intercept()");){
            Action action = (Action)actionInvocation.getAction();
            if (action instanceof PageAware) {
                PageAware pageAware = (PageAware)action;
                PageAwareHelper.Result result = this.helperRef.get().configure(pageAware, ServletActionContext.getRequest(), this::getParameter);
                switch (result) {
                    case NOT_PERMITTED: {
                        String string = "notpermitted";
                        return string;
                    }
                    case PAGE_NOT_PERMITTED: {
                        String string = "pagenotpermitted";
                        return string;
                    }
                    case PAGE_NOT_FOUND: {
                        String string = PAGE_NOT_FOUND;
                        return string;
                    }
                    case READ_ONLY: {
                        String string = "readonly";
                        return string;
                    }
                    case OK: {
                        String string = actionInvocation.invoke();
                        return string;
                    }
                }
                throw new IllegalStateException("Unhandled result type: " + result);
            }
            String string = actionInvocation.invoke();
            return string;
        }
    }
}

