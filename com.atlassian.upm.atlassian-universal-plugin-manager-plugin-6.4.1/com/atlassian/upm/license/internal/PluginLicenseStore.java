/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.license.internal;

import com.atlassian.upm.api.util.Option;
import java.util.List;

public interface PluginLicenseStore {
    public Option<String> getPluginLicense(String var1);

    public List<String> getPluginLicenses();

    public Option<String> setPluginLicense(String var1, String var2);

    public Option<String> removePluginLicense(String var1);
}

