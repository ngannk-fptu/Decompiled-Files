/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalSpi
 *  javax.annotation.Nonnull
 */
package com.atlassian.audit.api;

import com.atlassian.annotations.ExperimentalSpi;
import com.atlassian.audit.entity.AuditEntity;
import java.util.List;
import javax.annotation.Nonnull;

@ExperimentalSpi
public interface AuditConsumer {
    public void accept(@Nonnull List<AuditEntity> var1);

    default public boolean isEnabled() {
        return true;
    }
}

