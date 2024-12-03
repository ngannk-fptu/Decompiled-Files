/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.core.spi.service.CurrentUserProvider
 *  com.atlassian.audit.entity.AuditAuthor
 *  javax.annotation.Nonnull
 */
package com.atlassian.confluence.api.impl.service.audit;

import com.atlassian.audit.core.spi.service.CurrentUserProvider;
import com.atlassian.audit.entity.AuditAuthor;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.RequestCacheThreadLocal;
import javax.annotation.Nonnull;

public class ConfluenceAuditUserProvider
implements CurrentUserProvider {
    @Nonnull
    public AuditAuthor currentUser() {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (user != null) {
            return AuditAuthor.builder().type("user").id(user.getKey().getStringValue()).name(user.getFullName()).build();
        }
        if (RequestCacheThreadLocal.getRemoteAddress() == null) {
            return AuditAuthor.SYSTEM_AUTHOR;
        }
        return AuditAuthor.ANONYMOUS_AUTHOR;
    }
}

