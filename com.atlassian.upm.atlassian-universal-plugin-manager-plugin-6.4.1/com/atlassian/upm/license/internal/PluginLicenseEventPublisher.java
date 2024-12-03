/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.license.internal;

import com.atlassian.upm.api.license.event.PluginLicenseEvent;

public interface PluginLicenseEventPublisher {
    public void publish(PluginLicenseEvent var1);
}

