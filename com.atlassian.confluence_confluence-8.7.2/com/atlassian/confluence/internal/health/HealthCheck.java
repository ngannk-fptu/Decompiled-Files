/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.internal.health;

import com.atlassian.confluence.internal.health.HealthCheckResult;
import com.atlassian.confluence.internal.health.LifecyclePhase;
import java.util.Collection;
import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface HealthCheck {
    default public String getId() {
        return this.getClass().getName();
    }

    public @NonNull Collection<HealthCheck> getPrerequisites();

    public @NonNull List<HealthCheckResult> perform(LifecyclePhase var1);

    public boolean isApplicableFor(LifecyclePhase var1);
}

