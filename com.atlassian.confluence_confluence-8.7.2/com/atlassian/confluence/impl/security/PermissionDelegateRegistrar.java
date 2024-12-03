/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.PostConstruct
 */
package com.atlassian.confluence.impl.security;

import com.atlassian.confluence.security.PermissionDelegate;
import com.atlassian.confluence.security.PermissionDelegateRegistry;
import java.util.Map;
import java.util.function.UnaryOperator;
import javax.annotation.PostConstruct;

final class PermissionDelegateRegistrar {
    private final PermissionDelegateRegistry registry;
    private final UnaryOperator<PermissionDelegate<?>> decorator;
    private final Map<String, PermissionDelegate<?>> delegates;

    PermissionDelegateRegistrar(PermissionDelegateRegistry registry, UnaryOperator<PermissionDelegate<?>> decorator, Map<String, PermissionDelegate<?>> delegates) {
        this.registry = registry;
        this.decorator = decorator;
        this.delegates = delegates;
    }

    @PostConstruct
    void register() {
        this.delegates.forEach(this::register);
    }

    private void register(String key, PermissionDelegate<?> delegate) {
        this.registry.register(key, (PermissionDelegate)this.decorator.apply(delegate));
    }
}

