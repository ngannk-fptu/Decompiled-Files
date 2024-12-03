/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.impl.audit;

import com.atlassian.confluence.audit.AuditingContext;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.Nullable;

public class DefaultAuditingContext
implements AuditingContext {
    private final InheritableThreadLocal<AuditingHint> auditingHint = new InheritableThreadLocal();
    private final AutoCloseable skipAuditingAutoCloseable = this.auditingHint::remove;

    @Override
    public void executeWithoutAuditing(Runnable operation) {
        this.onlyAuditFor(null, operation);
    }

    @Override
    public void onlyAuditFor(String subjectKey, Runnable operation) {
        try {
            this.auditingHint.set(new AuditingHint(subjectKey));
            operation.run();
        }
        finally {
            this.auditingHint.remove();
        }
    }

    @Override
    public AutoCloseable noAuditing() {
        return this.noAuditing(null);
    }

    @Override
    public AutoCloseable noAuditing(String summaryKey) {
        this.auditingHint.set(new AuditingHint(summaryKey));
        return this.skipAuditingAutoCloseable;
    }

    @Override
    public boolean skipAuditing() {
        return this.auditingHint.get() != null;
    }

    @Override
    public boolean skipAuditing(@Nullable String summaryKey) {
        if (this.skipAuditing()) {
            String configuredKey = Optional.ofNullable((AuditingHint)this.auditingHint.get()).map(AuditingHint::getSubjectKey).orElse(null);
            return configuredKey == null || !configuredKey.equals(summaryKey);
        }
        return false;
    }

    private static class AuditingHint {
        private final @Nullable String subjectKey;

        public String getSubjectKey() {
            return this.subjectKey;
        }

        public AuditingHint(String subjectKey) {
            this.subjectKey = subjectKey;
        }
    }
}

