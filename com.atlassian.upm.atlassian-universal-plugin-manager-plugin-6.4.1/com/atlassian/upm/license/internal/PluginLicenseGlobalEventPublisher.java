/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.license.internal;

import com.atlassian.upm.license.internal.PluginLicenseEventPublisher;
import com.atlassian.upm.license.internal.PluginLicenseGlobalEvent;

public interface PluginLicenseGlobalEventPublisher
extends PluginLicenseEventPublisher {
    public void publishGlobal(PluginLicenseGlobalEvent var1);
}

