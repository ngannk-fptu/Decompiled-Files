/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.confluence.impl.health;

import com.atlassian.confluence.internal.health.HealthCheck;
import com.google.common.collect.ImmutableList;
import java.util.Collection;

public abstract class AbstractHealthCheck
implements HealthCheck {
    private final Collection<HealthCheck> prerequisites;

    protected AbstractHealthCheck(Iterable<HealthCheck> prerequisites) {
        this.prerequisites = ImmutableList.copyOf(prerequisites);
    }

    @Override
    public final Collection<HealthCheck> getPrerequisites() {
        return this.prerequisites;
    }
}

