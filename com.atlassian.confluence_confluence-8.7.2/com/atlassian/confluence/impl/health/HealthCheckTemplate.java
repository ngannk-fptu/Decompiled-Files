/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.health;

import com.atlassian.confluence.impl.health.AbstractHealthCheck;
import com.atlassian.confluence.internal.health.HealthCheck;
import com.atlassian.confluence.internal.health.HealthCheckResult;
import com.atlassian.confluence.internal.health.LifecyclePhase;
import java.util.List;
import java.util.Set;

public abstract class HealthCheckTemplate
extends AbstractHealthCheck {
    protected HealthCheckTemplate(Iterable<HealthCheck> prerequisites) {
        super(prerequisites);
    }

    @Override
    public final List<HealthCheckResult> perform(LifecyclePhase phase) {
        if (this.isApplicableFor(phase)) {
            return this.doPerform();
        }
        throw new UnsupportedOperationException("Unsupported phase " + phase);
    }

    @Override
    public final boolean isApplicableFor(LifecyclePhase phase) {
        return this.getApplicablePhases().contains((Object)phase);
    }

    protected abstract Set<LifecyclePhase> getApplicablePhases();

    protected abstract List<HealthCheckResult> doPerform();
}

