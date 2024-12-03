/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.healthcheck.core;

import com.atlassian.healthcheck.core.HealthCheck;

public interface AddressedHealthCheck
extends HealthCheck {
    public String getName();

    public String getDescription();
}

