/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 */
package com.atlassian.confluence.impl.audit.handler;

import com.google.common.annotations.VisibleForTesting;
import java.util.Set;

@VisibleForTesting
public interface AuditHandlerDescription {
    public Set<String> getHandledMethodNames();

    public Set<String> getExcludedMethodNames();

    public Class<?> getHandledClass();
}

