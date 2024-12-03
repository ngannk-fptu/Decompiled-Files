/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.MethodParameter
 *  org.springframework.lang.Nullable
 */
package org.springframework.web.method.support;

import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;

public interface AsyncHandlerMethodReturnValueHandler
extends HandlerMethodReturnValueHandler {
    public boolean isAsyncReturnValue(@Nullable Object var1, MethodParameter var2);
}

