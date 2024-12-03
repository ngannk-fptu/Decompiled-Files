/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.capabilities.api.CapabilityService
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugins.navlink.producer.capabilities.services;

import com.atlassian.plugins.navlink.producer.capabilities.ApplicationWithCapabilities;
import javax.annotation.Nonnull;

public interface CapabilityService
extends com.atlassian.plugins.capabilities.api.CapabilityService {
    @Nonnull
    public ApplicationWithCapabilities getHostApplication();
}

