/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.diagnostics.internal.platform.analytics;

public interface EventFactory {
    default public boolean isEnabled() {
        return false;
    }
}

