/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.util.profiling.Ticker
 *  com.atlassian.util.profiling.Timers
 *  com.google.common.base.Supplier
 *  com.google.common.base.Suppliers
 *  com.opensymphony.xwork2.Action
 *  com.opensymphony.xwork2.ActionInvocation
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.impl.pages.actions.CommentAwareHelper;
import com.atlassian.confluence.pages.actions.CommentAware;
import com.atlassian.confluence.setup.struts.AbstractAwareInterceptor;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.util.profiling.Ticker;
import com.atlassian.util.profiling.Timers;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import java.util.Optional;

public class CommentAwareInterceptor
extends AbstractAwareInterceptor {
    private final Supplier<CommentAwareHelper> helper = Suppliers.memoize(() -> (CommentAwareHelper)ContainerManager.getComponent((String)"commentAwareHelper"));

    @Override
    public String intercept(ActionInvocation actionInvocation) throws Exception {
        try (Ticker ignored = Timers.start((String)"CommentAwareInterceptor.intercept()");){
            Action action = (Action)actionInvocation.getAction();
            if (action instanceof CommentAware) {
                this.getHelper().ifPresent(helper -> helper.configureCommentAware((CommentAware)action, this::getParameter, this.getUser()));
            }
        }
        return actionInvocation.invoke();
    }

    private Optional<CommentAwareHelper> getHelper() {
        if (ContainerManager.isContainerSetup()) {
            return Optional.of((CommentAwareHelper)this.helper.get());
        }
        return Optional.empty();
    }
}

