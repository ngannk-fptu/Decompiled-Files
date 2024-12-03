/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.MethodParameter
 *  org.springframework.lang.Nullable
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

