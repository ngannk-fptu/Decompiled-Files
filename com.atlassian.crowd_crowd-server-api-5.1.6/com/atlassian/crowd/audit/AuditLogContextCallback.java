/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.audit;

@FunctionalInterface
public interface AuditLogContextCallback<T> {
    public T execute() throws Exception;
}

