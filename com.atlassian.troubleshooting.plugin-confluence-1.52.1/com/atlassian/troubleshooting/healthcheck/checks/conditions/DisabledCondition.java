/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.healthcheck.checks.conditions;

import com.atlassian.troubleshooting.api.healthcheck.SupportHealthCheckCondition;

public class DisabledCondition
implements SupportHealthCheckCondition {
    @Override
    public boolean shouldDisplay() {
        return false;
    }
}

