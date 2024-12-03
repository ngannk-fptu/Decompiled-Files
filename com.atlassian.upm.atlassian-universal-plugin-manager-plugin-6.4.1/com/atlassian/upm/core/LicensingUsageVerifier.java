/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 */
package com.atlassian.upm.core;

import com.atlassian.plugin.Plugin;

public interface LicensingUsageVerifier {
    public boolean usesLicensing(Plugin var1);

    public boolean isCarebearSpecificPlugin(Plugin var1);
}

