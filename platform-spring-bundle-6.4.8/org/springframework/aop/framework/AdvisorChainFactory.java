/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.framework;

import java.lang.reflect.Method;
import java.util.List;
import org.springframework.aop.framework.Advised;
import org.springframework.lang.Nullable;

public interface AdvisorChainFactory {
    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Advised var1, Method var2, @Nullable Class<?> var3);
}

