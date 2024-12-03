/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.extras.api;

import com.atlassian.extras.api.AtlassianLicense;

public interface LicenseManager {
    public AtlassianLicense getLicense(String var1);

    public void clear();
}

