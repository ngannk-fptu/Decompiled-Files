/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.entity.AuditEntity
 *  javax.annotation.Nonnull
 */
package com.atlassian.audit.denylist;

import com.atlassian.audit.entity.AuditEntity;
import java.util.List;
import javax.annotation.Nonnull;

public interface ExcludedActionsService {
    public boolean shouldExclude(@Nonnull AuditEntity var1);

    @Nonnull
    public List<String> getExcludedActions();

    public void updateExcludedActions(List<String> var1, List<String> var2);

    public void replaceExcludedActions(List<String> var1);
}

