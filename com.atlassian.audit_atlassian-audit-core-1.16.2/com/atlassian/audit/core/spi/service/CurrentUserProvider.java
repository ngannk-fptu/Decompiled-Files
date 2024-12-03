/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.entity.AuditAuthor
 *  javax.annotation.Nonnull
 */
package com.atlassian.audit.core.spi.service;

import com.atlassian.audit.entity.AuditAuthor;
import javax.annotation.Nonnull;

public interface CurrentUserProvider {
    @Nonnull
    public AuditAuthor currentUser();
}

