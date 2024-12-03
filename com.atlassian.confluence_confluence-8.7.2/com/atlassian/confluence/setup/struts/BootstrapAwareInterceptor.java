/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.profiling.Ticker
 *  com.atlassian.util.profiling.Timers
 *  com.opensymphony.xwork2.Action
 *  com.opensymphony.xwork2.ActionInvocation
 */
package com.atlassian.confluence.setup.struts;

import com.atlassian.confluence.pages.actions.beans.BootstrapAware;
import com.atlassian.confluence.setup.struts.AbstractAwareInterceptor;
import com.atlassian.util.profiling.Ticker;
import com.atlassian.util.profiling.Timers;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;

public class BootstrapAwareInterceptor
extends AbstractAwareInterceptor {
    @Override
    public String intercept(ActionInvocation actionInvocation) throws Exception {
        try (Ticker ignored = Timers.start((String)(this.getClass().getSimpleName() + ".intercept()"));){
            Action action = (Action)actionInvocation.getAction();
            if (action instanceof BootstrapAware) {
                ((BootstrapAware)action).bootstrap();
            }
        }
        return actionInvocation.invoke();
    }
}

