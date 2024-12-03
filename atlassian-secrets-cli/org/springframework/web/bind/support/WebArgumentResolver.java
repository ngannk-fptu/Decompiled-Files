/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.bind.support;

import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.NativeWebRequest;

@FunctionalInterface
public interface WebArgumentResolver {
    public static final Object UNRESOLVED = new Object();

    @Nullable
    public Object resolveArgument(MethodParameter var1, NativeWebRequest var2) throws Exception;
}

