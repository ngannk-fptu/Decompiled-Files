/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.util.reflection;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;

@Deprecated
public class ReflectionProviderFactory {
    public static ReflectionProvider getInstance() {
        return ActionContext.getContext().getContainer().getInstance(ReflectionProvider.class);
    }
}

