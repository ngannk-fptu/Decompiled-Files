/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.api.healthcheck.exception;

import com.atlassian.troubleshooting.api.healthcheck.exception.InvalidHealthCheckFilterException;

public class SupportHealthCheckModuleDescriptorNotFoundException
extends InvalidHealthCheckFilterException {
    public SupportHealthCheckModuleDescriptorNotFoundException(String key) {
        super(String.format("Health check module descriptor with key '%s' was not found.", key));
    }
}

