/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.audit.entity.AuditEvent
 *  com.atlassian.audit.entity.AuditEvent$Builder
 *  com.atlassian.audit.entity.CoverageArea
 *  com.atlassian.diagnostics.util.CallingBundleResolver
 *  javax.annotation.Nonnull
 */
package com.atlassian.audit.core.ecosystem;

import com.atlassian.audit.api.AuditService;
import com.atlassian.audit.core.ecosystem.BundleDetector;
import com.atlassian.audit.entity.AuditEvent;
import com.atlassian.audit.entity.CoverageArea;
import com.atlassian.diagnostics.util.CallingBundleResolver;
import java.util.Objects;
import javax.annotation.Nonnull;

public class CallerAwareAuditService
implements AuditService {
    private final AuditService delegate;
    private final BundleDetector bundleDetector;
    private final CallingBundleResolver callingBundleResolver;

    public CallerAwareAuditService(BundleDetector bundleDetector, CallingBundleResolver callingBundleResolver, AuditService delegate) {
        this.bundleDetector = bundleDetector;
        this.callingBundleResolver = callingBundleResolver;
        this.delegate = delegate;
    }

    public void audit(@Nonnull AuditEvent event) {
        Objects.requireNonNull(event, "event");
        this.delegate.audit(this.maybeRewrite(event));
    }

    private AuditEvent maybeRewrite(AuditEvent auditEvent) {
        CoverageArea area = auditEvent.getArea();
        if (area == CoverageArea.ECOSYSTEM || this.isInternal()) {
            return auditEvent;
        }
        return new AuditEvent.Builder(auditEvent).area(CoverageArea.ECOSYSTEM).build();
    }

    private boolean isInternal() {
        return this.callingBundleResolver.getCallingBundle().map(this.bundleDetector::isInternal).orElse(true);
    }
}

