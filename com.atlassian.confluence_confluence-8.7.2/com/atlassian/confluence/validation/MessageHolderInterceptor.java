/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.spring.container.LazyComponentReference
 *  com.atlassian.util.concurrent.Supplier
 *  com.opensymphony.xwork2.Action
 *  com.opensymphony.xwork2.ActionInvocation
 *  com.opensymphony.xwork2.interceptor.Interceptor
 */
package com.atlassian.confluence.validation;

import com.atlassian.confluence.setup.SetupContext;
import com.atlassian.confluence.validation.MessageHolder;
import com.atlassian.confluence.validation.MessageHolderAware;
import com.atlassian.confluence.validation.MessageHolderFactory;
import com.atlassian.confluence.validation.MessageLoadingPreResultListener;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.spring.container.LazyComponentReference;
import com.atlassian.util.concurrent.Supplier;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

public class MessageHolderInterceptor
implements Interceptor {
    private final Supplier<MessageHolderFactory> holderFactory = new LazyComponentReference("messageHolderFactory");

    public void destroy() {
    }

    public void init() {
    }

    public String intercept(ActionInvocation actionInvocation) throws Exception {
        Action action = (Action)actionInvocation.getAction();
        if (action instanceof MessageHolderAware && this.getMessageHolderFactory() != null) {
            MessageHolderAware holderAware = (MessageHolderAware)action;
            MessageHolder holder = this.getMessageHolderFactory().newHolder();
            holderAware.setMessageHolder(holder);
            actionInvocation.addPreResultListener(MessageLoadingPreResultListener.getInstance());
        }
        return actionInvocation.invoke();
    }

    private MessageHolderFactory getMessageHolderFactory() {
        if (!ContainerManager.isContainerSetup()) {
            if (SetupContext.get() != null) {
                return (MessageHolderFactory)SetupContext.get().getBean("messageHolderFactory");
            }
            return null;
        }
        return (MessageHolderFactory)this.holderFactory.get();
    }
}

