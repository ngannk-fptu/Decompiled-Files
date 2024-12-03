/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.troubleshooting.stp.audit;

import com.atlassian.troubleshooting.stp.annotations.ConditionalOnMissingClass;
import com.atlassian.troubleshooting.stp.audit.Auditor;
import java.util.Map;
import javax.annotation.Nonnull;

@ConditionalOnMissingClass(value={"com.atlassian.audit.api.AuditService"})
public class AuditorNoop
implements Auditor {
    @Override
    public void audit(@Nonnull String summaryKey) {
    }

    @Override
    public void audit(@Nonnull String summaryKey, @Nonnull Map<String, String> extraAttributes) {
    }
}

