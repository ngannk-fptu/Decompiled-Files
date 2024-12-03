/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.healthcheck.core;

import com.atlassian.healthcheck.core.AddressedHealthCheck;

public interface ExtendedHealthCheck
extends AddressedHealthCheck {
    public int getTimeOut();

    public String getKey();

    public String getTag();
}

