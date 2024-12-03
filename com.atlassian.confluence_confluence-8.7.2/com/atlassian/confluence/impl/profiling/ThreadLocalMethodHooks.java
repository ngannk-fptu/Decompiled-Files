/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.aop.MethodBeforeAdvice
 */
package com.atlassian.confluence.impl.profiling;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.MethodBeforeAdvice;

public final class ThreadLocalMethodHooks {
    private static final Logger log = LoggerFactory.getLogger(ThreadLocalMethodHooks.class);
    private static final ThreadLocal<Map<Method, MethodBeforeAdvice>> hooksRef = new ThreadLocal();

    private ThreadLocalMethodHooks() {
    }

    public static MethodBeforeAdvice advice() {
        return (method, args, target) -> {
            MethodBeforeAdvice hook;
            Map<Method, MethodBeforeAdvice> hooks = hooksRef.get();
            if (hooks != null && (hook = hooks.get(method)) != null) {
                log.debug("Invoking method hook for {}", (Object)method);
                hook.before(method, args, target);
            }
        };
    }

    public static void registerHook(Method method, MethodBeforeAdvice hook) {
        log.debug("Registering hook for {}", (Object)method);
        Map<Object, Object> hooks = hooksRef.get() == null ? new HashMap() : hooksRef.get();
        hooks.put(method, hook);
        hooksRef.set(hooks);
    }

    public static void unregisterHooks() {
        log.debug("Unregistering all hooks");
        hooksRef.remove();
    }
}

