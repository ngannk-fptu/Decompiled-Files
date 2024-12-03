/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package com.atlassian.confluence.util.sandbox;

import com.atlassian.confluence.util.sandbox.SandboxCallbackContext;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Optional;

class DefaultSandboxCallbackContext
implements SandboxCallbackContext {
    private final Map<Class<?>, Object> callbackContextObjects;

    DefaultSandboxCallbackContext(Map<Class<?>, Object> callbackContextObjects) {
        this.callbackContextObjects = ImmutableMap.copyOf(callbackContextObjects);
    }

    @Override
    public <T> Optional<T> get(Class<T> type) {
        return Optional.ofNullable(type.cast(this.callbackContextObjects.get(type)));
    }
}

