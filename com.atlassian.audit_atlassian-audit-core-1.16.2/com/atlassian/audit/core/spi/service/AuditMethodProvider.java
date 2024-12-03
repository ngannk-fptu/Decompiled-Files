/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.audit.core.spi.service;

import javax.annotation.Nullable;

public interface AuditMethodProvider {
    @Nullable
    public String currentMethod();
}

