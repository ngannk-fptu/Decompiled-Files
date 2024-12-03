/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.xwork2.ActionContext
 *  com.opensymphony.xwork2.util.ValueStack
 *  org.apache.struts2.util.ValueStackProvider
 */
package com.atlassian.confluence.velocity.context;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.util.ValueStackProvider;

public interface DefaultValueStackProvider
extends ValueStackProvider {
    default public ValueStack getValueStack() {
        return ActionContext.getContext().getValueStack();
    }
}

